package com.resumeiq.service;

import com.resumeiq.dto.*;
import com.resumeiq.model.User;
import com.resumeiq.repository.UserRepository;
import com.resumeiq.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository     userRepository;
    private final PasswordEncoder    passwordEncoder;
    private final JwtUtil            jwtUtil;
    private final AuthenticationManager authManager;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ── Register ─────────────────────────────────────────
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail().toLowerCase())
                .password(passwordEncoder.encode(req.getPassword()))
                .plan(User.Plan.FREE)
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return buildResponse(user, token);
    }

    // ── Login ────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail().toLowerCase(), req.getPassword()));
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Incorrect email or password.");
        }

        User user = userRepository.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        String token = jwtUtil.generateToken(user.getEmail());
        return buildResponse(user, token);
    }

    // ── Helper ───────────────────────────────────────────
    private AuthResponse buildResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .plan(user.getPlan().name())
                .analysesCount(user.getAnalysesCount())
                .chatsCount(user.getChatsCount())
                .joinedAt(user.getJoinedAt().format(FMT))
                .build();
    }
}
