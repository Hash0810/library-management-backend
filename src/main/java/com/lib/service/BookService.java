package com.lib.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import com.lib.model.Book;
import com.lib.model.BookTransaction;
import com.lib.model.User;
import com.lib.model.BookRequest;
import com.lib.model.RequestStatus;
import com.lib.repository.BookRepository;
import com.lib.repository.BookRequestRepository;
import com.lib.repository.BookTransactionRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private BookTransactionRepository bookTransactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRequestRepository bookRequestRepository;
    public Book findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
    }

    public Book addBook(Book book) {
        book.setAvailable(true);  // Assuming books are available when added
        return bookRepository.save(book);
    }

    public void updateBookAvailability(Integer bookId, boolean isAvailable) {
        Book book = findById(bookId);
        book.setAvailable(isAvailable);
        bookRepository.save(book);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailable(true); // Assuming 'findByAvailable' is implemented in BookRepository
    }
    public boolean issueBook(Integer bookId, Integer userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        if (!book.isAvailable()) {
            return false; // Book already issued
        }

        book.setAvailable(false);
        book.setBorrowedBy(String.valueOf(userId)); // Storing user ID as String
        bookRepository.save(book);
        return true;
    }

    public String categorizeBookUsingAI(Book book) {
        String prompt = "Categorize the following book based on its title " +
                book.getBookName();
        
        String category = aiService.ask(prompt); // AI API Call
        book.setGenre(category);
        bookRepository.save(book);
        return category;
    }
    @Transactional
    public String borrowBook(Integer bookId, String username) {
        Book book = findById(bookId);
        User student = userService.findByUsername(username);
    
        if (student == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
    
        if (book.getCopies() <= 0) {
            return "Book is currently unavailable.";
        }
    
        // Create a new book request for approval
        BookRequest bookRequest = new BookRequest();
        bookRequest.setBook(book);
        bookRequest.setStudent(student);
        bookRequest.setStatus(RequestStatus.PENDING); // Waiting for librarian approval
    
        bookRequestRepository.save(bookRequest);
    
        return "Book request submitted successfully. Awaiting librarian approval.";
    }

    @Transactional
    public String returnBook(String username, Integer bookId) {
        Book book = findById(bookId);
        if (book.isAvailable()) {
            throw new RuntimeException("Book with ID " + bookId + " is already available.");
        }

        // ✅ Find the active transaction
        BookTransaction transaction = bookTransactionRepository
            .findByBook_IdAndUser_UsernameAndReturnDateIsNull(bookId, username)
            .orElseThrow(() -> new RuntimeException("No active transaction found for this book and user."));

        // ✅ Update return date
        transaction.setReturnDate(LocalDate.now());
        bookTransactionRepository.save(transaction);

        // ✅ Make the book available again
        book.setAvailable(true);
        book.setBorrowedBy(null); // Clear borrower info
        bookRepository.save(book);

        return "Book returned successfully.";
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public Page<BookTransaction> getBorrowedBooksByUsername(String username, int page, int size,Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookTransaction> transactions = bookTransactionRepository.findByUser_Username(username, pageable);

        transactions.forEach(transaction -> {
            if (transaction.getReturnDate() == null) {
                transaction.setStatus("Not Returned");
            } else {
                transaction.setStatus("Returned on " + transaction.getReturnDate().toString());
            }
        });

        return transactions;
    }
    @Transactional
    public String approveBookRequest(Integer requestId) {
        BookRequest bookRequest = bookRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Book request not found with ID: " + requestId));
    
        Book book = bookRequest.getBook();
        User student = bookRequest.getStudent();
    
        if (book.getCopies() <= 0) {
            return "Book is unavailable.";
        }
    
        // ✅ Approve request
        bookRequest.setStatus(RequestStatus.APPROVED);
        bookRequestRepository.save(bookRequest);
    
        // ✅ Update book availability
        book.setCopies(book.getCopies() - 1);
        if (book.getCopies() == 0) {
            book.setAvailable(false);
        }
        bookRepository.save(book);
    
        // ✅ Create a transaction record
        BookTransaction transaction = new BookTransaction(student, book, LocalDate.now(), null);
        bookTransactionRepository.save(transaction);
    
        return "Book request approved. Book issued to " + student.getUsername();
    }
    @Transactional
    public String rejectBookRequest(Integer requestId) {
        BookRequest bookRequest = bookRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Book request not found with ID: " + requestId));
    
        // ✅ Reject request
        bookRequest.setStatus(RequestStatus.REJECTED);
        bookRequestRepository.save(bookRequest);
    
        return "Book request rejected.";
    }
}
