package com.resumeiq.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatMessageDto {
    private Long   id;
    private String provider;
    private String role;
    private String content;
    private String createdAt;
}
