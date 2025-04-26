package com.lib.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import com.lib.model.BookTransaction;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Integer> {
	Page<BookTransaction> findByUser_Username(String username, Pageable pageable);
	List<BookTransaction> findByBook_IdAndUser_UsernameAndReturnDateIsNull(Integer bookId, String username);
	long countByBook_IdAndReturnDateIsNull(Integer bookId);
	List<BookTransaction> findByReturnDateIsNull();
}
