package com.resumeiq.controller;

import com.resumeiq.dto.*;
import com.resumeiq.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * POST /api/chat/send
     * Header: Authorization: Bearer <token>
     * Body: { "message": "How do I improve my resume?", "provider": "CLAUDE" }
     * Returns: AI assistant reply (also saved to DB)
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChatRequest req) {
        return ResponseEntity.ok(chatService.sendMessage(userDetails.getUsername(), req));
    }

    /**
     * GET /api/chat/history?provider=CLAUDE
     * Header: Authorization: Bearer <token>
     * Returns: full conversation history for that provider
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDto>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "CLAUDE") String provider) {
        return ResponseEntity.ok(chatService.getHistory(userDetails.getUsername(), provider));
    }

    /**
     * DELETE /api/chat/history?provider=CLAUDE
     * Header: Authorization: Bearer <token>
     * Clears chat history for a provider
     */
    @DeleteMapping("/history")
    public ResponseEntity<Map<String, String>> clearHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "CLAUDE") String provider) {
        chatService.clearHistory(userDetails.getUsername(), provider);
        return ResponseEntity.ok(Map.of("message", "Chat history cleared."));
    }
}
