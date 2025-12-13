package com.app.service;

import com.app.entity.Task;
import com.app.entity.WithdrawRequest;
import com.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private WithdrawRepository withdrawRepository;
    @Autowired private WithdrawService withdrawService;

    // Dashboard Stats
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTasks", taskRepository.count());
        stats.put("pendingWithdrawals", withdrawRepository.findByStatus(WithdrawRequest.Status.PENDING).size());
        return stats;
    }

    // Manage Tasks
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    // Manage Withdrawals
    public WithdrawRequest approveWithdrawal(Long requestId) {
        return withdrawService.processWithdrawal(requestId, true);
    }

    public WithdrawRequest rejectWithdrawal(Long requestId) {
        return withdrawService.processWithdrawal(requestId, false);
    }
    
    public List<WithdrawRequest> getPendingWithdrawals() {
        return withdrawRepository.findByStatus(WithdrawRequest.Status.PENDING);
    }
}