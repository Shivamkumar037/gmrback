package com.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Double balance = 0.0;
    private Double totalEarned = 0.0;
    private Double totalWithdrawn = 0.0;

    private LocalDateTime lastUpdated = LocalDateTime.now();
}