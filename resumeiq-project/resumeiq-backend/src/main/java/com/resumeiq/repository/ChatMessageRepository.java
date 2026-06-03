package com.resumeiq.repository;

import com.resumeiq.model.ChatMessage;
import com.resumeiq.model.ChatMessage.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserIdAndProviderOrderByCreatedAtAsc(Long userId, Provider provider);
    List<ChatMessage> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
}
