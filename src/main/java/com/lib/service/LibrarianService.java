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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE); // large enough to get all
        List<BookTransaction> transactions = bookTransactionRepository.findByUser_Username(username, pageable).getContent();
        
        if (transactions.isEmpty()) {
            throw new RuntimeException("No transactions found for user");
        }

        byte[] pdf = receiptService.generateReceiptPdf(transactions);
        emailService.sendReceiptWithPDF(user.getEmail(), pdf, "Book_Receipt.pdf");

        return "Receipt sent to email successfully.";
    }
}
