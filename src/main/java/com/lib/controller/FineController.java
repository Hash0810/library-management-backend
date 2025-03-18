package com.lib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lib.model.Fine;
import com.lib.service.FineService;

@RestController
@RequestMapping("/fine")
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping("/calculateFine")
    public ResponseEntity<Fine> calculateFine(@RequestParam Integer transactionId) {
        Fine fine = fineService.calculateFine(transactionId);
        return fine != null ? ResponseEntity.ok(fine) : ResponseEntity.noContent().build();
    }
}
