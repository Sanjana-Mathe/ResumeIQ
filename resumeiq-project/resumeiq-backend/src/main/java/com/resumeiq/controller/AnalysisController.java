package com.resumeiq.controller;

import com.resumeiq.dto.*;
import com.resumeiq.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * POST /api/analysis/save
     * Header: Authorization: Bearer <token>
     * Body: { "atsScore": 78, "jobTitle": "Software Engineer", ... }
     * Returns: saved analysis with ID
     */
    @PostMapping("/save")
    public ResponseEntity<AnalysisDto> saveAnalysis(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AnalysisSaveRequest req) {
        return ResponseEntity.ok(
                analysisService.saveAnalysis(userDetails.getUsername(), req));
    }

    /**
     * GET /api/analysis/history
     * Header: Authorization: Bearer <token>
     * Returns: all past analyses (newest first)
     */
    @GetMapping("/history")
    public ResponseEntity<List<AnalysisDto>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                analysisService.getHistory(userDetails.getUsername()));
    }

    /**
     * GET /api/analysis/{id}
     * Header: Authorization: Bearer <token>
     * Returns: single analysis by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisDto> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(
                analysisService.getById(userDetails.getUsername(), id));
    }

    /**
     * DELETE /api/analysis/{id}
     * Header: Authorization: Bearer <token>
     * Deletes a single analysis
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAnalysis(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        analysisService.deleteAnalysis(userDetails.getUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Analysis deleted."));
    }
}
