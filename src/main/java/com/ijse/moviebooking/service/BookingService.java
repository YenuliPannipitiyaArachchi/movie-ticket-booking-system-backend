package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.Dtos.*;
import com.ijse.moviebooking.entity.*;
import com.ijse.moviebooking.enums.*;
import com.ijse.moviebooking.exception.*;
import com.ijse.moviebooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Slf4j @Service @RequiredArgsConstructor @Transactional
public class BookingService {
    private static final int SEATS_PER_ROW = 10;

    private final BookingRepository bookings;
    private final ShowRepository shows;
    private final UserRepository users;
    private final PaymentRepository payments;

    static boolean isAdmin(Authentication a) { return a.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN")); }

    private BookingResponse toResponse(Booking b) {
        Show s = b.getShow();
        String payStatus = payments.findByBookingId(b.getId()).map(p -> p.getStatus().name()).orElse(null);
        return new BookingResponse(b.getId(), s.getId(), s.getMovie().getTitle(), s.getTheatre().getName(), s.getShowDate(),
                s.getShowTime(), b.getSeatNumbers(), b.getNumberOfTickets(), b.getTotalAmount(), b.getBookingDate(),
                b.getStatus(), payStatus, b.getUser().getEmail());
    }

    public BookingResponse create(String email, BookingRequest r) {
        User user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Show show = shows.lockById(r.showId()).orElseThrow(() -> new ResourceNotFoundException("Show not found: " + r.showId()));

        if (show.getStatus() == ShowStatus.CANCELLED) throw new BusinessException("A cancelled show cannot accept bookings");
        if (show.getStatus() == ShowStatus.COMPLETED) throw new BusinessException("A completed show cannot accept new bookings");
        if (LocalDateTime.of(show.getShowDate(), show.getShowTime()).isBefore(LocalDateTime.now()))
            throw new BusinessException("This show has already started");

        List<String> seats = r.seatNumbers().stream().map(x -> x.trim().toUpperCase()).toList();
        if (new HashSet<>(seats).size() != seats.size()) throw new BusinessException("Duplicate seat numbers in request");
        if (seats.size() != r.numberOfTickets()) throw new BusinessException("Number of tickets must match the number of selected seats");
        seats.forEach(x -> validateSeat(x, show.getTheatre().getCapacity()));

        Set<String> taken = new HashSet<>(bookings.findBookedSeats(show.getId()));
        List<String> clash = seats.stream().filter(taken::contains).toList();
        if (!clash.isEmpty()) throw new ConflictException("Seats already booked: " + String.join(", ", clash));

        Booking b = new Booking();
        b.setUser(user);
        b.setShow(show);
        b.setSeatNumbers(new ArrayList<>(seats));
        b.setNumberOfTickets(seats.size());
        b.setTotalAmount(show.getTicketPrice().multiply(BigDecimal.valueOf(seats.size())));
        b.setBookingDate(LocalDateTime.now());
        b.setStatus(BookingStatus.PENDING);
        b = bookings.save(b);
        log.info("Booking {} created by {} for show {} seats {}", b.getId(), email, show.getId(), seats);
        return toResponse(b);
    }

    /** Seat ids look like A1..J10: letter = row, number = 1..10; must fit the theatre capacity. */
    private void validateSeat(String seat, int capacity) {
        if (!seat.matches("[A-Z]\\d{1,2}")) throw new BusinessException("Invalid seat number: " + seat);
        int row = seat.charAt(0) - 'A';
        int num = Integer.parseInt(seat.substring(1));
        if (num < 1 || num > SEATS_PER_ROW || row * SEATS_PER_ROW + num > capacity)
            throw new BusinessException("Seat " + seat + " does not exist in this theatre");
    }

    @Transactional(readOnly = true)
    public BookingResponse getById(Long id, Authentication auth) { return toResponse(findAccessible(id, auth)); }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMine(String email) {
        return bookings.findByUserEmailOrderByBookingDateDesc(email).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAll() { return bookings.findAllByOrderByBookingDateDesc().stream().map(this::toResponse).toList(); }

    public BookingResponse cancel(Long id, Authentication auth) {
        Booking b = findAccessible(id, auth);
        if (b.getStatus() == BookingStatus.CANCELLED) throw new BusinessException("Booking is already cancelled");
        Show s = b.getShow();
        if (!isAdmin(auth) && LocalDateTime.of(s.getShowDate(), s.getShowTime()).isBefore(LocalDateTime.now()))
            throw new BusinessException("Bookings for shows that have already started cannot be cancelled");
        markCancelled(b);
        log.info("Booking {} cancelled by {}", id, auth.getName());
        return toResponse(b);
    }

    public BookingResponse updateStatus(Long id, BookingStatus status) {
        Booking b = bookings.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        if (status == BookingStatus.CANCELLED) markCancelled(b); else b.setStatus(status);
        log.info("Admin set booking {} status to {}", id, status);
        return toResponse(b);
    }

    private void markCancelled(Booking b) {
        b.setStatus(BookingStatus.CANCELLED);
        payments.findByBookingId(b.getId()).filter(p -> p.getStatus() == PaymentStatus.COMPLETED).ifPresent(p -> {
            p.setStatus(PaymentStatus.REFUNDED);
            log.info("Payment {} refunded because booking {} was cancelled", p.getId(), b.getId());
        });
    }

    private Booking findAccessible(Long id, Authentication auth) {
        Booking b = bookings.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        if (!isAdmin(auth) && !b.getUser().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Not your booking");
        return b;
    }
}
