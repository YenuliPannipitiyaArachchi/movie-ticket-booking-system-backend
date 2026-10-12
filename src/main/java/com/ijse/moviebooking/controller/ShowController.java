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

@RestController @RequestMapping("/api/shows") @RequiredArgsConstructor
public class ShowController {
    private final ShowService shows;

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> create(@Valid @RequestBody ShowRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(shows.create(r)); }

    /** Optional filters: ?movieId=&theatreId=&date=yyyy-MM-dd&status= (default status: SCHEDULED) */
    @GetMapping
    public List<ShowResponse> getAll(@RequestParam(required = false) Long movieId, @RequestParam(required = false) Long theatreId,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                     @RequestParam(defaultValue = "SCHEDULED") ShowStatus status) {
        return shows.search(movieId, theatreId, date, status);
    }

    @GetMapping("/movie/{movieId}")
    public List<ShowResponse> forMovie(@PathVariable Long movieId) { return shows.search(movieId, null, null, ShowStatus.SCHEDULED); }

    /** Includes bookedSeats so the UI can render seat availability. */
    @GetMapping("/{id}")
    public ShowResponse getById(@PathVariable Long id) { return shows.getById(id); }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ShowResponse update(@PathVariable Long id, @Valid @RequestBody ShowRequest r) { return shows.update(id, r); }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { shows.delete(id); return ResponseEntity.noContent().build(); }
}
