package com.lib.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lib.service.AIService;
import com.lib.service.SpringOpenService;
@RestController
@RequestMapping("/api/ai")
public class AIController {

    
    private AIService aiService;

    
    private final SpringOpenService springOpenService;
    
    @Autowired
    public AIController(SpringOpenService springOpenService) {
        this.springOpenService = springOpenService;
    }
    
    @GetMapping("/chat")
    public ResponseEntity<String> sendMessage(@RequestParam(name="prompt") String prompt) {
        return ResponseEntity.ok(springOpenService.getAIResponse(prompt));
    }
}

