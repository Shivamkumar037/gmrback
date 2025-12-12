package com.gmr.controller;

import com.gmr.dto.WithdrawalRequest;
import com.gmr.model.*;
import com.gmr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WalletController {

    private final UserRepository userRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TransactionRepository transactionRepository;

    @PostMapping("/withdraw")
    @Transactional // Critical for financial accuracy
    public ResponseEntity<?> requestWithdrawal(@RequestBody WithdrawalRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        
        // 1. Fetch User
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check Balance
        if (user.getWalletBalance().compareTo(request.getAmount()) < 0) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        }

        // 3. Deduct Balance immediately
        user.setWalletBalance(user.getWalletBalance().subtract(request.getAmount()));
        user.setTotalWithdrawn(user.getTotalWithdrawn().add(request.getAmount()));
        userRepository.save(user);

        // 4. Create Withdrawal Request
        Withdrawal withdrawal = Withdrawal.builder()
                .user(user)
                .amount(request.getAmount())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .accountHolderName(request.getAccountHolderName())
                .status(WithdrawalStatus.PENDING)
                .build();
        withdrawalRepository.save(withdrawal);

        // 5. Create Transaction Record (DEBIT)
        Transaction trans = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .type(TransactionType.DEBIT)
                .description("Withdrawal Request #" + withdrawal.getId())
                .build();
        transactionRepository.save(trans);

        return ResponseEntity.ok("Withdrawal request submitted successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(transactionRepository.findByUserIdOrderByTimestampDesc(user.getId()));
    }

}
