package com.gmr.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_referral", columnList = "referralCode")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String referralCode;

    private String referredBy;

    @Column(nullable = false)
    private BigDecimal walletBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalWithdrawn = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String role = "ROLE_USER";

    // --- ADD THIS FIELD TO FIX THE ERROR ---
    @Column(name = "active", nullable = false)
    private boolean active = true;
}