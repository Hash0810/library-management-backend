package com.lib.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
@Entity
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private Transaction transaction; // Link to the transaction that incurred the fine

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private double amount;
    private boolean isPaid;
    private String reason;

    private LocalDate issuedDate;
    private LocalDate dueDate;

    @ManyToOne
    private BookTransaction bookTransaction;
    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BookTransaction getBookTransaction() {
        return bookTransaction;
    }

    public void setTransaction(BookTransaction bookTransaction) {
        this.bookTransaction = bookTransaction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public LocalDate getIssuedDate() {
        return issuedDate;
    }
    
    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

}
