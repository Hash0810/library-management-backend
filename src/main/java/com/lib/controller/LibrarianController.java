package com.lib.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lib.model.Book;
import com.lib.model.Fine;
import com.lib.service.BookService;
import com.lib.service.FineService;

@RestController
@RequestMapping("/librarian")
public class LibrarianController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private FineService fineService;

    @PostMapping("/addBook")
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        bookService.addBook(book);
        return ResponseEntity.ok("Book added successfully.");
    }

    @PutMapping("/updateBookStatus")
    public ResponseEntity<String> updateBookStatus(@RequestParam Integer bookId, @RequestParam boolean isAvailable) {
        bookService.updateBookAvailability(bookId, isAvailable);
        return ResponseEntity.ok("Book availability updated.");
    }
    @PutMapping("/issueBook")
    public ResponseEntity<String> issueBook(@RequestParam Integer bookId, @RequestParam Integer userId) {
        boolean success = bookService.issueBook(bookId, userId);
        if (success) {
            return ResponseEntity.ok("Book issued successfully.");
        }
        return ResponseEntity.badRequest().body("Book could not be issued.");
    }
    @PostMapping("/categorizeBook")
    public ResponseEntity<String> categorizeBook(@RequestBody Book book) {
        String category = bookService.categorizeBookUsingAI(book);
        return ResponseEntity.ok("Book categorized as: " + category);
    }
    @PostMapping("/manageFine")
    public ResponseEntity<String> manageFine(@RequestParam Integer transactionId) {
        Fine fine = fineService.calculateFine(transactionId);
        if (fine != null) {
            return ResponseEntity.ok("Fine calculated: $" + fine.getAmount());
        }
        return ResponseEntity.ok("No fine was calculated.");
    }
    @GetMapping("/bookInventory")
    public ResponseEntity<List<Book>> getBookInventory() {
    	List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books != null ? books : new ArrayList<>());
    }
    @PutMapping("/approveRequest")
    public ResponseEntity<String> approveBookRequest(@RequestBody Map<String, Integer> requestBody) {
        Integer requestId = requestBody.get("requestId");
    
        String responseMessage = bookService.approveBookRequest(requestId);
        return ResponseEntity.ok(responseMessage);
    }
    
    @PutMapping("/rejectRequest")
    public ResponseEntity<String> rejectBookRequest(@RequestBody Map<String, Integer> requestBody) {
        Integer requestId = requestBody.get("requestId");
    
        String responseMessage = bookService.rejectBookRequest(requestId);
        return ResponseEntity.ok(responseMessage);
    }


}

