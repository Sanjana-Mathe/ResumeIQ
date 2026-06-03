package com.resumeiq.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String  token;
    private String  type = "Bearer";
    private Long    id;
    private String  name;
    private String  email;
    private String  plan;
    private int     analysesCount;
    private int     chatsCount;
    private String  joinedAt;
}
