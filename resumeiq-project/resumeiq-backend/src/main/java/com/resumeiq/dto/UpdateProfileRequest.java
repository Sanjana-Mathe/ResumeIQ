package com.resumeiq.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;
}
