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
public class ShowService {
    private final ShowRepository shows;
    private final MovieRepository movies;
    private final TheatreRepository theatres;
    private final BookingRepository bookings;

    private ShowResponse toResponse(Show s, boolean withSeats) {
        return new ShowResponse(s.getId(), s.getMovie().getId(), s.getMovie().getTitle(), s.getTheatre().getId(),
                s.getTheatre().getName(), s.getShowDate(), s.getShowTime(), s.getTicketPrice(), s.getStatus(),
                s.getTheatre().getCapacity(), withSeats ? bookings.findBookedSeats(s.getId()) : List.of());
    }

    private void apply(Show s, ShowRequest r) {
        Movie m = movies.findById(r.movieId()).orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + r.movieId()));
        Theatre t = theatres.findById(r.theatreId()).orElseThrow(() -> new ResourceNotFoundException("Theatre not found: " + r.theatreId()));
        if (m.getStatus() == MovieStatus.ENDED) throw new BusinessException("Cannot schedule shows for a movie that has ENDED");
        if (t.getStatus() != TheatreStatus.ACTIVE) throw new BusinessException("Shows can only be created for an ACTIVE theatre");
        boolean clash = s.getId() == null
                ? shows.existsByTheatreIdAndShowDateAndShowTime(t.getId(), r.showDate(), r.showTime())
                : shows.existsByTheatreIdAndShowDateAndShowTimeAndIdNot(t.getId(), r.showDate(), r.showTime(), s.getId());
        if (clash) throw new ConflictException("This theatre already has a show at that date and time");
        s.setMovie(m);
        s.setTheatre(t);
        s.setShowDate(r.showDate());
        s.setShowTime(r.showTime());
        s.setTicketPrice(r.ticketPrice());
        if (r.status() != null) s.setStatus(r.status());
    }

    public ShowResponse create(ShowRequest r) {
        Show s = new Show();
        apply(s, r);
        s = shows.save(s);
        log.info("Show created: id={} movie={} theatre={}", s.getId(), r.movieId(), r.theatreId());
        return toResponse(s, false);
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> search(Long movieId, Long theatreId, LocalDate date, ShowStatus status) {
        return shows.search(movieId, theatreId, date, status).stream().map(s -> toResponse(s, false)).toList();
    }

    @Transactional(readOnly = true)
    public ShowResponse getById(Long id) { return toResponse(find(id), true); }

    public ShowResponse update(Long id, ShowRequest r) {
        Show s = find(id);
        apply(s, r);
        log.info("Show updated: id={}", id);
        return toResponse(s, false);
    }

    public void delete(Long id) {
        shows.delete(find(id));
        shows.flush();
        log.info("Show deleted: id={}", id);
    }

    private Show find(Long id) { return shows.findById(id).orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id)); }
}
