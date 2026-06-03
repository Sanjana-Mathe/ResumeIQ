package com.resumeiq.controller;

import com.resumeiq.dto.*;
import com.resumeiq.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Body: { "name": "Vaishnavi", "email": "v@example.com", "password": "pass1234" }
     * Returns: JWT token + user info
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    /**
     * POST /api/auth/login
     * Body: { "email": "v@example.com", "password": "pass1234" }
     * Returns: JWT token + user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
