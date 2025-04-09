package com.lib.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.lib.service.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lib.model.Book;
import com.lib.model.Fine;
import com.lib.service.BookService;
import com.lib.service.FineService;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/librarian")
public class LibrarianController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private FineService fineService;
    
    @Autowired
    private LibrarianService librarianService;

    @PostMapping("/addBook")
    public ResponseEntity<String> addBook(@RequestBody Map<String, Object> payload) {
        Integer userId =  ((Number)payload.get("userId")).intValue();
    
        // Convert the bookObj to Book using ObjectMapper
        Book book = new ObjectMapper().convertValue(payload.get("book"), Book.class);
    
        if (userId == null || book == null) {
            return ResponseEntity.badRequest().body("Missing userId or book data");
        }
    
        
        bookService.addBook(book, userId);
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
    @GetMapping("/genres")
    public ResponseEntity<List<String>> getGenres() {
        List<String> genres = bookService.getAllGenres();
        return ResponseEntity.ok(genres);
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
    @GetMapping("/sendReceipt/{username}")
    public ResponseEntity<String> sendReceipt(@PathVariable String username) {
        try {
            String result = librarianService.sendReceiptToUser(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error sending receipt: " + e.getMessage());
        }
    }
    @GetMapping("/receipt/{username}")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable String username) {
        try {
            byte[] pdf = librarianService.generateUserReceipt(username);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Book_Receipt.pdf");
    
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


}

