package com.resumeiq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    @NotNull(message = "Provider is required")
    private String provider;   // "CLAUDE" or "META"
}
