package com.app.controller;

import com.app.entity.Task;
import com.app.entity.User;
import com.app.entity.WithdrawRequest;
import com.app.helper.FileUploadService;
import com.app.repository.UserRepository;
import com.app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private AdminService adminService;
    @Autowired private FileUploadService fileUploadService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Create Task (With Image & Action URL)
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("reward") Double reward,
            @RequestParam("actionUrl") String actionUrl, // 🔥 NEW ADDED
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        String imageUrl = "https://cdn-icons-png.flaticon.com/512/732/732200.png"; // Default Icon

        if (file != null && !file.isEmpty()) {
            imageUrl = fileUploadService.uploadFile(file);
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setRewardAmount(reward);
        task.setActionUrl(actionUrl); // 🔥 SAVE LINK
        task.setImageUrl(imageUrl);
        task.setActive(true);

        return ResponseEntity.ok(adminService.createTask(task));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        adminService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/withdrawals/pending")
    public ResponseEntity<List<WithdrawRequest>> getPendingWithdrawals() {
        return ResponseEntity.ok(adminService.getPendingWithdrawals());
    }

    @PostMapping("/withdrawals/{id}/{action}")
    public ResponseEntity<WithdrawRequest> processWithdrawal(@PathVariable Long id, @PathVariable String action) {
        if ("approve".equalsIgnoreCase(action)) {
            return ResponseEntity.ok(adminService.approveWithdrawal(id));
        } else {
            return ResponseEntity.ok(adminService.rejectWithdrawal(id));
        }
    }
}