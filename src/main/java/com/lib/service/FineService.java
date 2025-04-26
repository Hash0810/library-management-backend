package com.lib.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lib.dto.FineHistory;
import com.lib.model.Fine;
import com.lib.model.Transaction;
import com.lib.model.User;
import com.lib.repository.FineRepository;
import com.lib.repository.TransactionRepository;

@Service
public class FineService {
    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Fine calculateFine(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        LocalDate dueDate = transaction.getBorrowDate().plusDays(14);
        LocalDate returnDate = transaction.getReturnDate();

        if (returnDate != null && returnDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            double fineAmount = daysLate * 2.0;  // Example: $2 fine per day

            Fine fine = new Fine();
            fine.setTransaction(transaction);
            fine.setAmount(fineAmount);
            fine.setPaid(false);
            fine.setReason("Late return for " + transaction.getBook().getBookName());

            return fineRepository.save(fine);
        }

        return null;  // No fine if returned on time
    }

    public List<Fine> getUserFines(User user) {
        return fineRepository.findByUserIdAndIsPaidFalse(user);
    }

    public List<FineHistory> getFinesByUsername(String username) {
        return fineRepository.findByUser_Username(username)
            .stream()
            .map(fine -> new FineHistory(
                fine.getTransaction().getBook().getBookName(),
                fine.getAmount(),
                fine.getReason()))
            .collect(Collectors.toList());
    }
    public boolean existsByTransactionId(Integer transactionId) {
        return fineRepository.existsByTransaction_Id(transactionId);
    }

}
