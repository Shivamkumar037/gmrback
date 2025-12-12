package com.gmr.controller;

import com.gmr.dto.TaskVerifyRequest;
import com.gmr.model.*;
import com.gmr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final UserRepository userRepository;
    private final TaskLogRepository taskLogRepository;
    private final TransactionRepository transactionRepository;

    @PostMapping("/verify")
    @Transactional // Ensures atomicity: Money is only added if log is saved
    public ResponseEntity<?> verifyTask(@RequestBody TaskVerifyRequest request, 
                                        @AuthenticationPrincipal UserDetails userDetails) {
        
        // 1. Fetch User
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if task already done TODAY
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        boolean alreadyDone = taskLogRepository.existsByUserIdAndTaskIdAndCompletedAtAfter(
                user.getId(), request.getTaskId(), startOfDay);

        if (alreadyDone) {
            return ResponseEntity.badRequest().body("Task already completed today.");
        }

        // 3. Update Wallet
        user.setWalletBalance(user.getWalletBalance().add(request.getAmount()));
        userRepository.save(user);

        // 4. Create Transaction Record
        Transaction trans = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .type(TransactionType.CREDIT)
                .description("Task Earnings: " + request.getTaskId())
                .build();
        transactionRepository.save(trans);

        // 5. Log Task Completion
        TaskLog log = TaskLog.builder()
                .user(user)
                .taskId(request.getTaskId())
                .build();
        taskLogRepository.save(log);

        return ResponseEntity.ok("Task verified. Amount credited: " + request.getAmount());
    }
}