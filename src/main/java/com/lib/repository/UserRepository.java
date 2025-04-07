package com.lib.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lib.model.Role;
import com.lib.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findByRole(Role role);
}
