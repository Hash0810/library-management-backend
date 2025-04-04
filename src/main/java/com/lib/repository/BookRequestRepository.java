package com.lib.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lib.model.BookRequest;
import com.lib.model.RequestStatus;
import java.util.List;
@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, Integer> {
    List<BookRequest> findByStatus(RequestStatus status);
}
