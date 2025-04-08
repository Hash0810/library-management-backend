package com.lib.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

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

    public SpringOpenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
        message.put("content", prompt); // No escaping needed!
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

        return responseEntity.getBody();
    }
}
