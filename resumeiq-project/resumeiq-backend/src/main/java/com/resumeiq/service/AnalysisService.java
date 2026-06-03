package com.resumeiq.service;

import com.resumeiq.dto.*;
import com.resumeiq.model.*;
import com.resumeiq.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ResumeAnalysisRepository analysisRepo;
    private final UserRepository           userRepo;
    private final UserService              userService;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    // ── Save a new analysis result ───────────────────────
    public AnalysisDto saveAnalysis(String email, AnalysisSaveRequest req) {
        User user = userService.findByEmail(email);

        ResumeAnalysis analysis = ResumeAnalysis.builder()
                .user(user)
                .atsScore(req.getAtsScore())
                .jobTitle(req.getJobTitle())
                .resumeText(req.getResumeText() != null
                        ? req.getResumeText().substring(0, Math.min(req.getResumeText().length(), 500))
                        : null)
                .skillsJson(req.getSkillsJson())
                .keywordsJson(req.getKeywordsJson())
                .suggestionsJson(req.getSuggestionsJson())
                .careersJson(req.getCareersJson())
                .matchScore(req.getMatchScore())
                .build();

        analysis = analysisRepo.save(analysis);

        // Increment user analyses counter
        user.setAnalysesCount(user.getAnalysesCount() + 1);
        userRepo.save(user);

        return toDto(analysis);
    }

    // ── Get all analyses for a user ──────────────────────
    public List<AnalysisDto> getHistory(String email) {
        User user = userService.findByEmail(email);
        return analysisRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── Get a single analysis ────────────────────────────
    public AnalysisDto getById(String email, Long id) {
        User user = userService.findByEmail(email);
        ResumeAnalysis analysis = analysisRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found."));
        if (!analysis.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied.");
        }
        return toDto(analysis);
    }

    // ── Delete an analysis ───────────────────────────────
    public void deleteAnalysis(String email, Long id) {
        User user = userService.findByEmail(email);
        ResumeAnalysis analysis = analysisRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found."));
        if (!analysis.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied.");
        }
        analysisRepo.delete(analysis);
    }

    private AnalysisDto toDto(ResumeAnalysis a) {
        return AnalysisDto.builder()
                .id(a.getId())
                .atsScore(a.getAtsScore())
                .jobTitle(a.getJobTitle())
                .skillsJson(a.getSkillsJson())
                .keywordsJson(a.getKeywordsJson())
                .suggestionsJson(a.getSuggestionsJson())
                .careersJson(a.getCareersJson())
                .matchScore(a.getMatchScore())
                .createdAt(a.getCreatedAt().format(FMT))
                .build();
    }
}
