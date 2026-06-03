package com.resumeiq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// ─── User profile DTO ────────────────────────────────────
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserDto {
    private Long   id;
    private String name;
    private String email;
    private String plan;
    private int    analysesCount;
    private int    chatsCount;
    private String joinedAt;
}
