package com.lib.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class SpringOpenService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public SpringOpenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAIResponse(String prompt) {
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Set up request body
        String requestBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

        // Create HTTP entity
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send request to OpenAI API
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            apiUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // Return the response content
        return responseEntity.getBody();
    }
}
