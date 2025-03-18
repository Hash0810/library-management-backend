package com.lib.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lib.model.Book;
import com.lib.model.Transaction;
import com.lib.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookService bookService;

    public Transaction issueBook(Integer userId, Integer bookId) {
        Book book = bookService.findById(bookId);
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setUserId(userId);  // Assuming userId is set directly
        transaction.setBorrowDate(LocalDate.now());
        transaction.setReturned(false);
        bookService.updateBookAvailability(bookId, false);

        return transactionRepository.save(transaction);
    }

    public Transaction returnBook(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setReturned(true);
        transaction.setReturnDate(LocalDate.now());
        bookService.updateBookAvailability(transaction.getBook().getId(), true);

        return transactionRepository.save(transaction);
    }
}


