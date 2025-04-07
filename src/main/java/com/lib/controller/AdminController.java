package com.lib.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lib.model.Book;
import com.lib.model.Role;
import com.lib.model.User;
import com.lib.service.BookService;
import com.lib.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    // Endpoint to add a user (Student, Teacher, Librarian) by the Admin
    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@RequestBody User user) throws UnsupportedEncodingException {
        String responseMessage = userService.createUserWithRole(user);
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    // Endpoint to add an admin by the Admin
    @PostMapping("/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody User user) throws UnsupportedEncodingException {
        user.setRole(Role.ADMIN);  // Ensure role is ADMIN
        String responseMessage = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    // Endpoint to verify OTP for admin signup
    @PostMapping("/verify-admin-otp")
    public ResponseEntity<String> verifyAdminSignupOtp(@RequestParam String email, @RequestParam String otp) {
        String responseMessage = userService.verifyRegistrationOtp(email, otp);
        return ResponseEntity.ok(responseMessage);
    }

    // Test endpoint to check if the controller is working
    @GetMapping("/test")
    public String test() {
        return "AdminController is working!";
    }
}
