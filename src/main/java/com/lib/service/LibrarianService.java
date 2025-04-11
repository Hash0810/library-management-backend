package com.lib.service;

import com.lib.repository.BookTransactionRepository;
import com.lib.repository.UserRepository;
import com.lib.service.ReceiptService;
import com.lib.service.EmailService;
import com.lib.model.BookTransaction;
import com.lib.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibrarianService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookTransactionRepository bookTransactionRepository;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private EmailService emailService;

    public String sendReceiptToUser(String username) throws IOException, DocumentException, WriterException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<BookTransaction> transactions = bookTransactionRepository
                .findByUser_Username(username, pageable)
                .getContent();

        // Filter only today's transactions
        List<BookTransaction> todaysTransactions = transactions.stream()
                .filter(tx -> tx.getBorrowDate().isEqual(LocalDate.now()))
                .collect(Collectors.toList());

        if (todaysTransactions.isEmpty()) {
            throw new RuntimeException("No books borrowed today. Receipt not sent.");
        }

        byte[] pdf = receiptService.generateReceiptPdf(todaysTransactions);
        emailService.sendReceiptWithPDF(user.getEmail(), pdf, "Book_Receipt.pdf");

        return "Today's receipt sent to email successfully.";
    }

    public byte[] generateUserReceipt(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<BookTransaction> transactions = bookTransactionRepository
                .findByUser_Username(username, pageable)
                .getContent();

        // Filter only today's transactions
        List<BookTransaction> todaysTransactions = transactions.stream()
                .filter(tx -> tx.getBorrowDate().isEqual(LocalDate.now()))
                .collect(Collectors.toList());

        if (todaysTransactions.isEmpty()) {
            throw new RuntimeException("No books borrowed today. Cannot generate receipt.");
        }

        try {
            return receiptService.generateReceiptPdf(todaysTransactions);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF receipt: " + e.getMessage(), e);
        }
    }
}
