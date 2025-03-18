package com.lib.dto;

public class FineHistory {
    private String bookTitle;
    private double amount;
    private String reason;

    public FineHistory(String bookTitle, double amount, String reason) {
        this.bookTitle = bookTitle;
        this.amount = amount;
        this.reason = reason;
    }
    // Getters and setters

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
