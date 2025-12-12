package com.gmr.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Withdrawal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal amount;

    // Bank Details stored as JSON or simple String for simplicity
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status; // PENDING, APPROVED, REJECTED

    @CreationTimestamp
    private LocalDateTime requestedAt;
}

