package com.lib.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BookRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;  // The user requesting the book

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // The requested book

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, APPROVED, REJECTED

    private LocalDateTime requestDate;

    public BookRequest() {
        this.requestDate = LocalDateTime.now();
        this.status = RequestStatus.PENDING; // Default status
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getRequestDate() { return requestDate; }
}
