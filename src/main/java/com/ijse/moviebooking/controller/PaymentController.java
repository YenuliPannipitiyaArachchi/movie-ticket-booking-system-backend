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

@RestController @RequestMapping("/api/payments") @RequiredArgsConstructor
public class PaymentController {
    private final PaymentService payments;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest r, Authentication a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payments.create(r, a));
    }

    /** success=false simulates a failed gateway response. */
    @PostMapping("/{id}/process")
    public PaymentResponse process(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean success, Authentication a) {
        return payments.process(id, success, a);
    }

    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponse> getAll() { return payments.getAll(); }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id, Authentication a) { return payments.getById(id, a); }

    @GetMapping("/{id}/status")
    public PaymentStatusResponse status(@PathVariable Long id, Authentication a) { return payments.getStatus(id, a); }

    @GetMapping("/booking/{bookingId}")
    public PaymentResponse forBooking(@PathVariable Long bookingId, Authentication a) { return payments.getByBooking(bookingId, a); }
}
