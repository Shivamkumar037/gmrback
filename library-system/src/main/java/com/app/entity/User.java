package com.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String mobile;

    private String password;

    private String fullName;
    private String email;
    private String profileImage;

    @Column(unique = true, nullable = false)
    private String referralCode;

    private String referredBy;

    // --- Wallet Fields ---
    @Column(nullable = false)
    private Double walletBalance = 0.0;

    @Column(nullable = false)
    private Double totalEarned = 0.0;

    // 🔥 NEW FIX: Adding this field with default value 0.0
    @Column(nullable = false)
    private Double totalWithdrawn = 0.0;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}