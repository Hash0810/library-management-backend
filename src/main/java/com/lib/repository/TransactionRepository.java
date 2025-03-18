package com.lib.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lib.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	List<Transaction> findByUserId(Integer userId);
    List<Transaction> findByBookIdAndIsReturnedFalse(Integer bookId);
	// Custom query methods can be added if needed
}
