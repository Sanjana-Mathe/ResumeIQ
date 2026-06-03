package com.resumeiq.service;

import com.resumeiq.dto.*;
import com.resumeiq.model.User;
import com.resumeiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ── Get current user profile ─────────────────────────
    public UserDto getProfile(String email) {
        User user = findByEmail(email);
        return toDto(user);
    }

    // ── Update name ──────────────────────────────────────
    public UserDto updateProfile(String email, UpdateProfileRequest req) {
        User user = findByEmail(email);
        user.setName(req.getName());
        userRepository.save(user);
        return toDto(user);
    }

    // ── Upgrade to Premium ───────────────────────────────
    public UserDto upgradeToPremium(String email) {
        User user = findByEmail(email);
        if (user.getPlan() == User.Plan.PREMIUM) {
            throw new IllegalStateException("User is already on Premium plan.");
        }
        user.setPlan(User.Plan.PREMIUM);
        userRepository.save(user);
        return toDto(user);
    }

    // ── Internal helpers ─────────────────────────────────
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
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
