package com.resumeiq.repository;

import com.resumeiq.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    List<ResumeAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
}
