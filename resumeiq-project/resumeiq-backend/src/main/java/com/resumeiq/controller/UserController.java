package com.resumeiq.controller;

import com.resumeiq.dto.*;
import com.resumeiq.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me
     * Header: Authorization: Bearer <token>
     * Returns: current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    /**
     * PUT /api/users/me
     * Header: Authorization: Bearer <token>
     * Body: { "name": "New Name" }
     * Returns: updated profile
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), req));
    }

    /**
     * POST /api/users/upgrade
     * Header: Authorization: Bearer <token>
     * Upgrades the user plan to PREMIUM
     */
    @PostMapping("/upgrade")
    public ResponseEntity<UserDto> upgradeToPremium(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.upgradeToPremium(userDetails.getUsername()));
    }
}
