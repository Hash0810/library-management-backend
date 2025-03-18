package com.lib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lib.model.Transaction;
import com.lib.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/issueBook")
    public ResponseEntity<Transaction> issueBook(@RequestParam Integer userId, @RequestParam Integer bookId) {
        return new ResponseEntity<>(transactionService.issueBook(userId, bookId), HttpStatus.OK);
    }

    @PutMapping("/returnBook/{transactionId}")
    public ResponseEntity<Transaction> returnBook(@PathVariable Integer transactionId) {
        return new ResponseEntity<>(transactionService.returnBook(transactionId), HttpStatus.OK);
    }
}
