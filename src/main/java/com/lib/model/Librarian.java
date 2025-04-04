package com.lib.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("LIBRARIAN")
public class Librarian extends User {
	
}
