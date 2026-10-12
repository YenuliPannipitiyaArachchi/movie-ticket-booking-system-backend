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
public class PaymentService {
    private final PaymentRepository payments;
    private final BookingRepository bookings;

    static PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getBooking().getId(), p.getAmount(), p.getMethod(), p.getStatus(), p.getPaymentDate());
    }

    /** Creates a PENDING payment for a booking (or reuses an unfinished one). */
    public PaymentResponse create(PaymentRequest r, Authentication auth) {
        Booking b = bookings.findById(r.bookingId()).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + r.bookingId()));
        checkOwner(b, auth);
        if (b.getStatus() == BookingStatus.CANCELLED) throw new BusinessException("Cancelled bookings cannot be paid for");
        Payment p = payments.findByBookingId(b.getId()).orElse(null);
        if (p != null && p.getStatus() == PaymentStatus.COMPLETED) throw new BusinessException("This booking has already been paid");
        if (p == null) {
            p = new Payment();
            p.setBooking(b);
            p.setAmount(b.getTotalAmount());
        }
        p.setMethod(r.method());
        p.setStatus(PaymentStatus.PENDING);
        p = payments.save(p);
        log.info("Payment {} created for booking {} ({})", p.getId(), b.getId(), r.method());
        return toResponse(p);
    }

    /** Simulated gateway: success=true completes the payment and confirms the booking; false marks it FAILED. */
    public PaymentResponse process(Long id, boolean success, Authentication auth) {
        Payment p = findAccessible(id, auth);
        Booking b = p.getBooking();
        if (p.getStatus() == PaymentStatus.COMPLETED) throw new BusinessException("This payment has already been completed");
        if (p.getStatus() == PaymentStatus.REFUNDED) throw new BusinessException("Refunded payments cannot be processed again");
        if (b.getStatus() == BookingStatus.CANCELLED) throw new BusinessException("Cancelled bookings cannot be paid for");
        if (success) {
            p.setStatus(PaymentStatus.COMPLETED);
            p.setPaymentDate(LocalDateTime.now());
            b.setStatus(BookingStatus.CONFIRMED);
            log.info("Payment {} completed; booking {} confirmed", id, b.getId());
        } else {
            p.setStatus(PaymentStatus.FAILED);
            log.warn("Payment {} failed; booking {} stays {}", id, b.getId(), b.getStatus());
        }
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id, Authentication auth) { return toResponse(findAccessible(id, auth)); }

    @Transactional(readOnly = true)
    public PaymentStatusResponse getStatus(Long id, Authentication auth) {
        Payment p = findAccessible(id, auth);
        return new PaymentStatusResponse(p.getId(), p.getStatus());
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByBooking(Long bookingId, Authentication auth) {
        Payment p = payments.findByBookingId(bookingId).orElseThrow(() -> new ResourceNotFoundException("No payment found for booking " + bookingId));
        checkOwner(p.getBooking(), auth);
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAll() { return payments.findAll().stream().map(PaymentService::toResponse).toList(); }

    private Payment findAccessible(Long id, Authentication auth) {
        Payment p = payments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        checkOwner(p.getBooking(), auth);
        return p;
    }

    private void checkOwner(Booking b, Authentication auth) {
        if (!BookingService.isAdmin(auth) && !b.getUser().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Not your booking");
    }
}
