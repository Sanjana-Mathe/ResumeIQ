package com.resumeiq.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analyses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer atsScore;

    @Column
    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String resumeText;        // first 500 chars stored for reference

    @Column(columnDefinition = "JSON")
    private String skillsJson;        // ["Java","Spring","SQL", ...]

    @Column(columnDefinition = "JSON")
    private String keywordsJson;      // matched / missing keywords

    @Column(columnDefinition = "JSON")
    private String suggestionsJson;   // improvement roadmap items

    @Column(columnDefinition = "JSON")
    private String careersJson;       // career path matches

    @Column
    private Integer matchScore;       // JD match % (nullable if no JD)

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
