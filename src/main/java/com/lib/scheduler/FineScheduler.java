package com.lib.scheduler;

import com.lib.model.BookTransaction;
import com.lib.model.Fine;
import com.lib.model.User;
import com.lib.repository.BookTransactionRepository;
import com.lib.service.EmailService;
import com.lib.service.FineService;
import com.lib.service.ReceiptService;

import com.itextpdf.text.DocumentException;
import com.google.zxing.WriterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class FineScheduler {

    @Autowired
    private BookTransactionRepository bookTransactionRepository;

    @Autowired
    private FineService fineService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReceiptService receiptService;

    // Runs daily at 9 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void checkOverdueBooksAndSendFines() {
        List<BookTransaction> activeTransactions = bookTransactionRepository.findByReturnDateIsNull();

        for (BookTransaction tx : activeTransactions) {
            LocalDate dueDate = tx.getBorrowDate().plusDays(3);
            if (LocalDate.now().isAfter(dueDate)) {
                // Check if fine already exists for this transaction
                boolean fineExists = fineService.existsByTransactionId(tx.getId());
                if (!fineExists) {
                    Fine fine = fineService.calculateFine(tx.getId());
                    if (fine != null) {
                        sendFineEmail(tx.getUser(), fine);
                    }
                }
            }
        }
    }

    private void sendFineEmail(User user, Fine fine) {
        try {
            byte[] pdf = receiptService.generateFinePDF(user, fine);
            emailService.sendFinePDF(user.getEmail(),pdf,"fine_receipt.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
