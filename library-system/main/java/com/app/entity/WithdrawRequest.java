package com.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "withdraw_requests")
public class WithdrawRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double amount;
    private String upiId; // User's payment address

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime processedAt;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}