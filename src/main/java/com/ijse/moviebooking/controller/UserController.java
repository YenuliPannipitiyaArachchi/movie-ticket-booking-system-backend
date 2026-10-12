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

@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserService users;

    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() { return users.getAll(); }

    /** Current user profile (used by the React profile page). */
    @GetMapping("/me")
    public UserResponse me(Authentication a) { return users.getByEmail(a.getName()); }

    @PutMapping("/me")
    public UserResponse updateMe(@Valid @RequestBody UserUpdateRequest r, Authentication a) { return users.updateMe(a.getName(), r); }

    @GetMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(@PathVariable Long id) { return users.getById(id); }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest r) { return users.update(id, r); }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { users.delete(id); return ResponseEntity.noContent().build(); }
}
