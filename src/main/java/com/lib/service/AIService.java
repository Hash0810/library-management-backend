package com.lib.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class AIService {

     @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public AIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @param prompt - the question you are expecting to ask ChatGPT
     * @return the response in JSON format
     */

    public String ask(String prompt) {
        HttpEntity<String> entity = new HttpEntity<>(buildMessageBody(model, prompt), buildOpenAIHeaders());
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // Log rate limiting headers if present
        HttpHeaders headers = response.getHeaders();
        System.out.println("X-Ratelimit-Limit: " + headers.getFirst("X-Ratelimit-Limit"));
        System.out.println("X-Ratelimit-Remaining: " + headers.getFirst("X-Ratelimit-Remaining"));
        System.out.println("X-Ratelimit-Reset: " + headers.getFirst("X-Ratelimit-Reset"));

        return response.getBody();
    }

    private HttpHeaders buildOpenAIHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private String buildMessageBody(String model, String prompt) {
        return String.format("{ \"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", model, prompt);
    }
}
