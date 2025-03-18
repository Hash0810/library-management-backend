package com.lib.model;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
@Entity
public class Student extends User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private boolean hasOutStandingFines;
	@OneToMany(mappedBy = "student")
	private List<Transaction> transactions;
	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isHasOutStandingFines() {
		return hasOutStandingFines;
	}

	public void setHasOutStandingFines(boolean hasOutStandingFines) {
		this.hasOutStandingFines = hasOutStandingFines;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
}
