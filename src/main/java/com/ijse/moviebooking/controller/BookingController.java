package com.ijse.moviebooking.controller;

import com.ijse.moviebooking.dto.Dtos.*;
import com.ijse.moviebooking.enums.*;
import com.ijse.moviebooking.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController @RequestMapping("/api/bookings") @RequiredArgsConstructor
public class BookingController {
    private final BookingService bookings;

    @PostMapping @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest r, Authentication a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookings.create(a.getName(), r));
    }

    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponse> getAll() { return bookings.getAll(); }

    @GetMapping("/my-bookings")
    public List<BookingResponse> mine(Authentication a) { return bookings.getMine(a.getName()); }

    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Long id, Authentication a) { return bookings.getById(id, a); }

    @PutMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id, Authentication a) { return bookings.cancel(id, a); }

    @PutMapping("/{id}/status") @PreAuthorize("hasRole('ADMIN')")
    public BookingResponse updateStatus(@PathVariable Long id, @Valid @RequestBody BookingStatusRequest r) { return bookings.updateStatus(id, r.status()); }
}
