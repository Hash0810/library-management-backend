package com.lib.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Librarian extends User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
