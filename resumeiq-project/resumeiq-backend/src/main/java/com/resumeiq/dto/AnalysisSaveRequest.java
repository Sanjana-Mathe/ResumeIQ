package com.resumeiq.dto;

import lombok.*;

// ─── Save a new analysis result ──────────────────────────
@Getter @Setter
public class AnalysisSaveRequest {
    private int    atsScore;
    private String jobTitle;
    private String resumeText;      // first ~500 chars
    private String skillsJson;      // JSON array string
    private String keywordsJson;    // JSON object string
    private String suggestionsJson; // JSON array string
    private String careersJson;     // JSON array string
    private Integer matchScore;     // nullable
}
