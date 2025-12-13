package com.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    
    private Double rewardAmount;
    
    private String imageUrl; // For task icon/banner
    
    private String actionUrl; // Where user goes (e.g., YouTube link)

    private boolean active = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}