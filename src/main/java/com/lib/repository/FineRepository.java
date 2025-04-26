package com.lib.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lib.model.Fine;
import com.lib.model.User;

@Repository
public interface FineRepository extends JpaRepository<Fine, Integer> {
    List<Fine> findByUserIdAndIsPaidFalse(User user);
    List<Fine> findByUser_Username(String username);
    boolean existsByTransaction_Id(Integer transactionId);
}

