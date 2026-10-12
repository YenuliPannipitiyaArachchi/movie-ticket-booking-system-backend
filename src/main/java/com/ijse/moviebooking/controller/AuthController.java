package com.ijse.moviebooking.controller;

import com.ijse.moviebooking.dto.Dtos.*;
import com.ijse.moviebooking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.signUp(r));
    }

    @PostMapping("/signin")
    public AuthResponse signIn(@Valid @RequestBody SignInRequest r) {
        return auth.signIn(r);
    }
}