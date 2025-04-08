package com.lib.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class SpringOpenService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SpringOpenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper(); // Initialize Jackson
    }

    public String getAIResponse(String prompt) {
        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Request body as map
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);

        body.put("messages", messages);

        // Send request
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        try {
            JsonNode root = objectMapper.readTree(responseEntity.getBody());
            return root.path("choices").get(0).path("message").path("content").asText().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
