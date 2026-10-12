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
public class MovieService {
    private final MovieRepository movies;

    static MovieResponse toResponse(Movie m) {
        return new MovieResponse(m.getId(), m.getTitle(), m.getDescription(), m.getDurationMinutes(), m.getLanguage(),
                m.getGenre(), m.getReleaseDate(), m.getPosterUrl(), m.getRating(), m.getStatus());
    }

    private void apply(Movie m, MovieRequest r) {
        m.setTitle(r.title().trim());
        m.setDescription(r.description());
        m.setDurationMinutes(r.durationMinutes());
        m.setLanguage(r.language());
        m.setGenre(r.genre().trim());
        m.setReleaseDate(r.releaseDate());
        m.setPosterUrl(r.posterUrl() == null || r.posterUrl().isBlank() ? null : r.posterUrl());
        m.setRating(r.rating());
        if (r.status() != null) m.setStatus(r.status());
    }

    public MovieResponse create(MovieRequest r) {
        Movie m = new Movie();
        apply(m, r);
        m = movies.save(m);
        log.info("Movie created: {} (id={})", m.getTitle(), m.getId());
        return toResponse(m);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> search(String title, String language, MovieStatus status) {
        String t = title == null || title.isBlank() ? null : title.trim();
        String l = language == null || language.isBlank() ? null : language.trim();
        return movies.search(t, l, status).stream().map(MovieService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getById(Long id) { return toResponse(find(id)); }

    public MovieResponse update(Long id, MovieRequest r) {
        Movie m = find(id);
        apply(m, r);
        log.info("Movie updated: id={}", id);
        return toResponse(m);
    }

    public void delete(Long id) {
        movies.delete(find(id));
        movies.flush();
        log.info("Movie deleted: id={}", id);
    }

    private Movie find(Long id) { return movies.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id)); }
}
