package com.lib.dto;

import java.time.LocalDate;

public class BorrowedBookHistory {
    private String title;
    private String author;
    private LocalDate borrowedDate;
    private LocalDate returnedDate;

    public BorrowedBookHistory(String title, String author, LocalDate borrowedDate, LocalDate returnedDate) {
        this.title = title;
        this.author = author;
        this.borrowedDate = borrowedDate;
        this.returnedDate = returnedDate;
    }
    // Getters and setters

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDate getBorrowedDate() {
		return borrowedDate;
	}

	public void setBorrowedDate(LocalDate borrowedDate) {
		this.borrowedDate = borrowedDate;
	}

	public LocalDate getReturnedDate() {
		return returnedDate;
	}

	public void setReturnedDate(LocalDate returnedDate) {
		this.returnedDate = returnedDate;
	}
}

