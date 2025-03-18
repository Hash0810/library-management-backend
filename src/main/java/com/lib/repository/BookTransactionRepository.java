package com.lib.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lib.model.BookTransaction;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Integer> {
	List<BookTransaction> findByUser_Username(String username);
	Optional<BookTransaction> findByBook_IdAndUser_UsernameAndReturnDateIsNull(Integer bookId, String username);
}
