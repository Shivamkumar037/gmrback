package com.gmr.repository;

import com.gmr.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Fetch history for a specific user, ordered by latest first
    List<Transaction> findByUserIdOrderByTimestampDesc(Long userId);
}