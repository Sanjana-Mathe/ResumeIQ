package com.resumeiq.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.resumeiq.dto.*;
import com.resumeiq.model.*;
import com.resumeiq.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository        userRepo;
    private final UserService           userService;

    @Value("${app.anthropic.api-key}")
    private String anthropicApiKey;

    @Value("${app.anthropic.model}")
    private String model;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Send a message & get AI reply ────────────────────
    public ChatMessageDto sendMessage(String email, ChatRequest req) {
        User user = userService.findByEmail(email);

        // Check free-plan limit (5 messages per session — you can make this daily)
        if (user.getPlan() == User.Plan.FREE) {
            long count = chatRepo.countByUserId(user.getId());
            if (count >= 50) {   // lifetime free limit; adjust as needed
                throw new IllegalStateException("Free message limit reached. Upgrade to Premium.");
            }
        }

        ChatMessage.Provider provider = ChatMessage.Provider.valueOf(req.getProvider().toUpperCase());

        // Save user message
        ChatMessage userMsg = ChatMessage.builder()
                .user(user)
                .provider(provider)
                .role(ChatMessage.Role.USER)
                .content(req.getMessage())
                .build();
        chatRepo.save(userMsg);

        // Load conversation history for context
        List<ChatMessage> history = chatRepo
                .findByUserIdAndProviderOrderByCreatedAtAsc(user.getId(), provider);

        // Call Anthropic API
        String aiReply = callAnthropicApi(req.getMessage(), history, provider);

        // Save assistant message
        ChatMessage botMsg = ChatMessage.builder()
                .user(user)
                .provider(provider)
                .role(ChatMessage.Role.ASSISTANT)
                .content(aiReply)
                .build();
        chatRepo.save(botMsg);

        // Increment user chat count
        user.setChatsCount(user.getChatsCount() + 1);
        userRepo.save(user);

        return toDto(botMsg);
    }

    // ── Get chat history for a provider ──────────────────
    public List<ChatMessageDto> getHistory(String email, String provider) {
        User user = userService.findByEmail(email);
        ChatMessage.Provider p = ChatMessage.Provider.valueOf(provider.toUpperCase());
        return chatRepo.findByUserIdAndProviderOrderByCreatedAtAsc(user.getId(), p)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── Clear chat history ────────────────────────────────
    public void clearHistory(String email, String provider) {
        User user = userService.findByEmail(email);
        ChatMessage.Provider p = ChatMessage.Provider.valueOf(provider.toUpperCase());
        List<ChatMessage> msgs = chatRepo.findByUserIdAndProviderOrderByCreatedAtAsc(user.getId(), p);
        chatRepo.deleteAll(msgs);
    }

    // ── Call Anthropic /v1/messages ───────────────────────
    private String callAnthropicApi(String userMessage,
                                    List<ChatMessage> history,
                                    ChatMessage.Provider provider) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Build messages array from history
            ArrayNode messages = objectMapper.createArrayNode();
            for (ChatMessage msg : history) {
                ObjectNode m = objectMapper.createObjectNode();
                m.put("role", msg.getRole() == ChatMessage.Role.USER ? "user" : "assistant");
                m.put("content", msg.getContent());
                messages.add(m);
            }

            // System prompt based on provider
            String systemPrompt = provider == ChatMessage.Provider.CLAUDE
                ? "You are Claude AI, a helpful career advisor on ResumeIQ. " +
                  "Help users with resume writing, ATS optimization, career advice, and interview prep. " +
                  "Be concise (2-4 sentences), professional, and always respond to what the user asked."
                : "You are Meta AI, an upbeat career coach on ResumeIQ. " +
                  "Help users with resume tips, LinkedIn, networking, and job hunting. " +
                  "Be encouraging, concise (2-4 sentences), and use occasional relevant emojis. " +
                  "Always respond directly to what the user asked.";

            // Build request body
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", 512);
            body.put("system", systemPrompt);
            body.set("messages", messages);

            // HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.anthropic.com/v1/messages", entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("content").get(0).path("text").asText();

        } catch (Exception e) {
            log.error("Anthropic API call failed: {}", e.getMessage());
            return "I'm having trouble connecting right now. Please try again in a moment.";
        }
    }

    private ChatMessageDto toDto(ChatMessage msg) {
        return ChatMessageDto.builder()
                .id(msg.getId())
                .provider(msg.getProvider().name())
                .role(msg.getRole().name())
                .content(msg.getContent())
                .createdAt(msg.getCreatedAt().format(FMT))
                .build();
    }
}
