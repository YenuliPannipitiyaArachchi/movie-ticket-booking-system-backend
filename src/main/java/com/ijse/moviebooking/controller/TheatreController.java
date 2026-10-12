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

@RestController @RequestMapping("/api/theatres") @RequiredArgsConstructor
public class TheatreController {
    private final TheatreService theatres;

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheatreResponse> create(@Valid @RequestBody TheatreRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(theatres.create(r)); }

    @GetMapping
    public List<TheatreResponse> getAll() { return theatres.getAll(); }

    @GetMapping("/{id}")
    public TheatreResponse getById(@PathVariable Long id) { return theatres.getById(id); }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public TheatreResponse update(@PathVariable Long id, @Valid @RequestBody TheatreRequest r) { return theatres.update(id, r); }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { theatres.delete(id); return ResponseEntity.noContent().build(); }
}
