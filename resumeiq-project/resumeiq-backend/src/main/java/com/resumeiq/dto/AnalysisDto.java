package com.resumeiq.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AnalysisDto {
    private Long    id;
    private int     atsScore;
    private String  jobTitle;
    private String  skillsJson;
    private String  keywordsJson;
    private String  suggestionsJson;
    private String  careersJson;
    private Integer matchScore;
    private String  createdAt;
}
