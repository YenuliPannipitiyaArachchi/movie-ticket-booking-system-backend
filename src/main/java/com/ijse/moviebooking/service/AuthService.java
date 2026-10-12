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
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final com.ijse.moviebooking.security.JwtService jwt;

    public UserResponse signUp(SignUpRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ConflictException("Email is already registered");
        User u = new User();
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));
        u.setPhone(r.phone() == null || r.phone().isBlank() ? null : r.phone());
        u.setRole(Role.CUSTOMER); // public sign-up always creates customers
        u = users.save(u);
        log.info("New customer registered: {}", email);
        return UserService.toResponse(u);
    }

    @Transactional(readOnly = true)
    public AuthResponse signIn(SignInRequest r) {
        String email = r.email().trim().toLowerCase();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, r.password()));
        User u = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        log.info("User signed in: {} ({})", email, u.getRole());
        return new AuthResponse(jwt.generate(email, u.getRole().name()), u.getRole().name(), u.getName(), u.getEmail());
    }
}
