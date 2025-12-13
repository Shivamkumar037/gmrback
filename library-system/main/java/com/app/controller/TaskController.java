package com.app.controller;

import com.app.entity.Task;
import com.app.entity.TaskHistory;
import com.app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired private TaskService taskService;
    @Autowired private com.app.repository.UserRepository userRepository; // Direct repo for quick ID lookup

    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        return ResponseEntity.ok(taskService.getAllActiveTasks());
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<TaskHistory> completeTask(@PathVariable Long taskId, Principal principal) {
        // Principal.getName() returns the mobile number (username)
        String mobile = principal.getName();
        Long userId = userRepository.findByMobile(mobile).get().getId();
        
        return ResponseEntity.ok(taskService.completeTask(userId, taskId));
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<TaskHistory>> getHistory(Principal principal) {
        String mobile = principal.getName();
        Long userId = userRepository.findByMobile(mobile).get().getId();
        return ResponseEntity.ok(taskService.getUserHistory(userId));
    }
}