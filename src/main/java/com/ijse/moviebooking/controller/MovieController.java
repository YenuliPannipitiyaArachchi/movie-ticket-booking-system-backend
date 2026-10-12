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

@RestController @RequestMapping("/api/movies") @RequiredArgsConstructor
public class MovieController {
    private final MovieService movies;

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(movies.create(r)); }

    /** Optional filters: ?title=&language=&status= */
    @GetMapping
    public List<MovieResponse> getAll(@RequestParam(required = false) String title, @RequestParam(required = false) String language,
                                      @RequestParam(required = false) MovieStatus status) { return movies.search(title, language, status); }

    @GetMapping("/{id}")
    public MovieResponse getById(@PathVariable Long id) { return movies.getById(id); }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest r) { return movies.update(id, r); }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { movies.delete(id); return ResponseEntity.noContent().build(); }
}
