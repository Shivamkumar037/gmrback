package com.app.service;

import com.app.entity.*;
import com.app.exception.BadRequestException;
import com.app.exception.ResourceNotFound;
import com.app.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawService {

    @Autowired private WithdrawRepository withdrawRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskHistoryRepository historyRepository;

    // USER: Request Withdrawal
    public WithdrawRequest requestWithdrawal(Long userId, Double amount, String upiId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFound("User not found"));
        Wallet wallet = walletRepository.findByUser(user).orElseThrow(() -> new ResourceNotFound("Wallet not found"));

        if (amount < 30) {
            throw new BadRequestException("Minimum withdrawal amount is ₹30");
        }
        
        if (wallet.getBalance() < amount) {
            throw new BadRequestException("Insufficient wallet balance!");
        }

        // Create Request
        WithdrawRequest request = new WithdrawRequest();
        request.setUser(user);
        request.setAmount(amount);
        request.setUpiId(upiId);
        
        return withdrawRepository.save(request);
    }

    // USER: Get own history
    public List<WithdrawRequest> getUserWithdrawals(Long userId) {
        return withdrawRepository.findByUserIdOrderByRequestedAtDesc(userId);
    }

    // ADMIN: Approve/Reject
    @Transactional
    public WithdrawRequest processWithdrawal(Long requestId, boolean approved) {
        WithdrawRequest request = withdrawRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFound("Request not found"));

        if (request.getStatus() != WithdrawRequest.Status.PENDING) {
            throw new BadRequestException("Request is already processed");
        }

        if (approved) {
            // Deduct Balance
            Wallet wallet = walletRepository.findByUser(request.getUser()).orElseThrow();
            
            if (wallet.getBalance() < request.getAmount()) {
                throw new BadRequestException("User no longer has sufficient balance!");
            }
            
            wallet.setBalance(wallet.getBalance() - request.getAmount());
            wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + request.getAmount());
            walletRepository.save(wallet);

            request.setStatus(WithdrawRequest.Status.APPROVED);
            
            // Add to History
            TaskHistory history = new TaskHistory();
            history.setUserId(request.getUser().getId());
            history.setRewardAmount(-request.getAmount()); // Negative for withdrawal
            history.setType(TaskHistory.TransactionType.WITHDRAWAL);
            history.setDescription("Withdrawal to UPI: " + request.getUpiId());
            historyRepository.save(history);

        } else {
            request.setStatus(WithdrawRequest.Status.REJECTED);
        }

        request.setProcessedAt(LocalDateTime.now());
        return withdrawRepository.save(request);
    }
}