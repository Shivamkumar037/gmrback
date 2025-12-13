package com.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // Keeping ID simple for history
    
    // Can be NULL if it's a Referral Bonus, otherwise links to Task
    private Long taskId; 

    private String description; // e.g., "Referral Bonus from User X" or "Task: Watch Video"
    
    private Double rewardAmount;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type; // TASK, REFERRAL, WITHDRAWAL

    private LocalDateTime completedAt = LocalDateTime.now();

    public enum TransactionType {
        TASK_COMPLETED,
        REFERRAL_BONUS,
        WITHDRAWAL
    }
}