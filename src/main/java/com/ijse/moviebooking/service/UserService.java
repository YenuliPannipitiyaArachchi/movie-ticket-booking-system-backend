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
public class UserService {
    private final UserRepository users;

    static UserResponse toResponse(User u) { return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getRole()); }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() { return users.findAll().stream().map(UserService::toResponse).toList(); }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) { return toResponse(find(id)); }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        return toResponse(users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    /** Admin update: may also change the role. */
    public UserResponse update(Long id, UserUpdateRequest r) {
        User u = find(id);
        u.setName(r.name().trim());
        u.setPhone(r.phone());
        if (r.role() != null) u.setRole(r.role());
        log.info("Admin updated user {}", id);
        return toResponse(u);
    }

    /** Self update: name and phone only. */
    public UserResponse updateMe(String email, UserUpdateRequest r) {
        User u = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setName(r.name().trim());
        u.setPhone(r.phone());
        return toResponse(u);
    }

    public void delete(Long id) {
        users.delete(find(id));
        users.flush();
        log.info("User {} deleted", id);
    }

    private User find(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)); }
}
