package com.lib.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lib.model.Book;
import com.lib.model.BookTransaction;
import com.lib.model.User;
import com.lib.repository.BookRepository;
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

        if (!book.isAvailable()) {
            throw new RuntimeException("Book with ID " + bookId + " is not available for borrowing.");
        }

        book.setAvailable(false);
        book.setBorrowedBy(username);
        bookRepository.save(book);

        // ✅ Ensure user exists
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        // ✅ CREATE A NEW BOOK TRANSACTION
        BookTransaction transaction = new BookTransaction(user, book, LocalDate.now(), null); // returnDate is null initially
        bookTransactionRepository.save(transaction); // Save transaction

        return "Book borrowed successfully.";
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
    public List<BookTransaction> getBorrowedBooksByUsername(String username) {
    	 List<BookTransaction> transactions = bookTransactionRepository.findByUser_Username(username);
    	 for(BookTransaction transaction:transactions)
    	 {
    		 if(transaction.getReturnDate()== null)
    		 {
    			 transaction.setStatus("Not Returned");
    		 }
    		 else
    		 {
    			 transaction.setStatus("Returned on "+ transaction.getReturnDate().toString());
    		 }
    	 }
    	 return transactions;
    }
}
