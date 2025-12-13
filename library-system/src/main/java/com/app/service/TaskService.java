package com.app.service;

import com.app.entity.*;
import com.app.exception.BadRequestException;
import com.app.exception.ResourceNotFound;
import com.app.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskHistoryRepository taskHistoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;

    public List<Task> getAllActiveTasks() {
        return taskRepository.findByActiveTrue();
    }

    @Transactional
    public TaskHistory completeTask(Long userId, Long taskId) {
        // 1. Validate User and Task
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));

        // 2. Check Daily Limit (Max 3 tasks per day)
        // We filter history list in memory or use a custom query. Here is a simple stream filter.
        long tasksToday = taskHistoryRepository.findByUserIdOrderByCompletedAtDesc(userId).stream()
                .filter(h -> h.getType() == TaskHistory.TransactionType.TASK_COMPLETED)
                .filter(h -> h.getCompletedAt().toLocalDate().isEqual(LocalDate.now()))
                .count();

        if (tasksToday >= 3) {
            throw new BadRequestException("Daily limit reached! You can only complete 3 tasks per day.");
        }

        // 3. Credit Wallet
        Wallet wallet = walletRepository.findByUser(user).orElseThrow(() -> new ResourceNotFound("Wallet not found"));
        wallet.setBalance(wallet.getBalance() + task.getRewardAmount());
        wallet.setTotalEarned(wallet.getTotalEarned() + task.getRewardAmount());
        walletRepository.save(wallet);

        // 4. Save History
        TaskHistory history = new TaskHistory();
        history.setUserId(userId);
        history.setTaskId(taskId);
        history.setDescription("Completed Task: " + task.getTitle());
        history.setRewardAmount(task.getRewardAmount());
        history.setType(TaskHistory.TransactionType.TASK_COMPLETED);
        
        return taskHistoryRepository.save(history);
    }
    
    public List<TaskHistory> getUserHistory(Long userId) {
        return taskHistoryRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }
}