package com.lib.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import com.lib.dto.EmailRequest;
import com.lib.dto.FineHistory;
import com.lib.dto.LoginRequest;
import com.lib.dto.PasswordResetRequest;
import com.lib.model.Book;
import com.lib.model.BookTransaction;
import com.lib.model.Role;
import com.lib.model.User;
import com.lib.service.BookService;
import com.lib.service.FineService;
import com.lib.service.UserService;
import com.lib.util.JwtUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private FineService fineService;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user and send OTP for verification
   @PostMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        User user = userService.findByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) throws UnsupportedEncodingException {
        String responseMessage = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @PostMapping("/admin-signup")
    public ResponseEntity<String> adminSignup(@RequestBody User user) throws UnsupportedEncodingException {
        // Setting admin role using enum

        user.setRole(Role.ADMIN);
        System.out.println("User role set to: " + user.getRole());
        String responseMessage = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    // Verify OTP for registration
    @PostMapping("/verify-signup-otp")
    public ResponseEntity<String> verifySignupOtp(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String otp = requestData.get("otp");
        String responseMessage = userService.verifyRegistrationOtp(email, otp);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/verify-admin-signup-otp")
    public ResponseEntity<String> verifyAdminSignupOtp(@RequestParam String email, @RequestParam String otp) {
        String responseMessage = userService.verifyRegistrationOtp(email, otp);
        return ResponseEntity.ok(responseMessage);
    }

    // Login a user and send OTP for login verification
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) throws Exception {
        Map<String, String> responseMessage = userService.loginUser(loginRequest.getUsername(),
                loginRequest.getPassword());
        return ResponseEntity.ok(responseMessage);
    }

    // Verify OTP for login
    @PostMapping("/verify-login-otp")
    public ResponseEntity<String> verifyLoginOtp(@RequestBody Map<String, String> requestData) {
        try {
            // Retrieve email associated with the username
            String email = requestData.get("email");
            String otp = requestData.get("otp");
            // Verify the OTP (assuming you have an OTPService for validation)
            boolean isOtpValid = userService.verifyLoginOtp(email, otp);

            if (isOtpValid) {
                return ResponseEntity.ok("Login OTP verification successful");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during OTP verification");
        }
    }

    // Initiate password reset process and send OTP
    @PostMapping("/reset-password-initiate")
    public ResponseEntity<String> initiatePasswordReset(@Valid @RequestBody EmailRequest emailRequest)
            throws UnsupportedEncodingException {
        String responseMessage = userService.initiatePasswordReset(emailRequest.getEmail());
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/verify-reset-password-otp")
    public ResponseEntity<String> verifyResetPasswordOtp(
            @RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String otp = requestData.get("otp");
        String responseMessage = userService.verifyResetPasswordOtp(email, otp);
        return ResponseEntity.ok(responseMessage);
    }

    // Reset password with OTP verification
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody PasswordResetRequest pwdrsRequest) {
        String responseMessage = userService.resetPassword(pwdrsRequest.getEmail(), pwdrsRequest.getNewPassword());
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/books/available")
    public ResponseEntity<List<Book>> viewAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @PostMapping("/books/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody Map<String, Object> requestBody) {
        String username = (String) requestBody.get("username");
        Integer bookId = (Integer) requestBody.get("bookId");
    
        String responseMessage = bookService.borrowBook(bookId, username);
        return ResponseEntity.ok(responseMessage);
    }


    @PostMapping("/books/return")
    public ResponseEntity<String> returnBook(@RequestBody Map<String, Object> requestMap) {
        String username = (String) requestMap.get("username");
        Integer bookId = (Integer) requestMap.get("bookId");
    
        String responseMessage = bookService.returnBook(username, bookId);
        return ResponseEntity.ok(responseMessage);
    }


    @GetMapping("/get-user-details")
    public ResponseEntity<User> getUserDetails(@RequestParam String email) {
        try {
            User user = userService.findByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/verify-session")
    public ResponseEntity<Map<String, String>> verifySession(@RequestParam String email) {
        try {
            User user = userService.verifySession(email);
            if (user != null) {
                Map<String, String> response = Map.of(
                        "email", user.getEmail(),
                        "role", user.getRole().toString());
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            System.err.println("Error during session verification: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/book-history")
    public ResponseEntity<Page<BookTransaction>> getBorrowingHistory(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        int page = (int) request.getOrDefault("page", 0);  // Default to page 0
        int size = (int) request.getOrDefault("size", 10); // Default page size 10
        String sortBy = (String) request.getOrDefault("sortBy", "borrowDate"); // Default sort field
        String sortOrder = (String) request.getOrDefault("sortOrder", "asc"); // Default sort order
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Page<BookTransaction> history = bookService.getBorrowedBooksByUsername(username, page, size, sort); // Include sort parameter
        return ResponseEntity.ok(history);
    }

    @GetMapping("/fine-history")
    public ResponseEntity<List<FineHistory>> getFineHistory(@RequestParam String username) {
        List<FineHistory> fines = fineService.getFinesByUsername(username);
        return ResponseEntity.ok(fines != null ? fines : new ArrayList<>());
    }
    @GetMapping("/librarians")
    public ResponseEntity<List<User>> getLibrarians() {
        List<User> librarians = userService.getUsersByRole(Role.LIBRARIAN);
        return ResponseEntity.ok(librarians);
    }
    @PostMapping("/test-jwt")
    public ResponseEntity<Map<String, String>> testJwt(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String token = jwtUtil.generateToken(username);
    
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }


}
