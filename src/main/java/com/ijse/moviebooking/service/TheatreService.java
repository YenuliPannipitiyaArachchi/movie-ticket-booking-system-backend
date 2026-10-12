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
public class TheatreService {
    private final TheatreRepository theatres;

    static TheatreResponse toResponse(Theatre t) { return new TheatreResponse(t.getId(), t.getName(), t.getLocation(), t.getCapacity(), t.getStatus()); }

    private void apply(Theatre t, TheatreRequest r) {
        t.setName(r.name().trim());
        t.setLocation(r.location().trim());
        t.setCapacity(r.capacity());
        if (r.status() != null) t.setStatus(r.status());
    }

    public TheatreResponse create(TheatreRequest r) {
        Theatre t = new Theatre();
        apply(t, r);
        t = theatres.save(t);
        log.info("Theatre created: {} (id={})", t.getName(), t.getId());
        return toResponse(t);
    }

    @Transactional(readOnly = true)
    public List<TheatreResponse> getAll() { return theatres.findAll().stream().map(TheatreService::toResponse).toList(); }

    @Transactional(readOnly = true)
    public TheatreResponse getById(Long id) { return toResponse(find(id)); }

    public TheatreResponse update(Long id, TheatreRequest r) {
        Theatre t = find(id);
        apply(t, r);
        log.info("Theatre updated: id={}", id);
        return toResponse(t);
    }

    public void delete(Long id) {
        theatres.delete(find(id));
        theatres.flush();
        log.info("Theatre deleted: id={}", id);
    }

    private Theatre find(Long id) { return theatres.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theatre not found: " + id)); }
}
