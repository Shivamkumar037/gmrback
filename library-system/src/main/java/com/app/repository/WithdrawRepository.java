package com.app.repository;

import com.app.entity.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WithdrawRepository extends JpaRepository<WithdrawRequest, Long> {
    List<WithdrawRequest> findByUserIdOrderByRequestedAtDesc(Long userId); // User history
    List<WithdrawRequest> findByStatus(WithdrawRequest.Status status); // For Admin
}