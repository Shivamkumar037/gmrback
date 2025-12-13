package com.app.controller;

import com.app.dto.WithdrawDtos.*;
import com.app.entity.WithdrawRequest;
import com.app.service.WithdrawService;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/withdraw")
public class WithdrawController {

    @Autowired private WithdrawService withdrawService;
    @Autowired private UserRepository userRepository;

    @PostMapping("/request")
    public ResponseEntity<WithdrawRequest> requestWithdrawal(@RequestBody WithdrawRequestDto dto, Principal principal) {
        String mobile = principal.getName();
        Long userId = userRepository.findByMobile(mobile).get().getId();
        
        return ResponseEntity.ok(withdrawService.requestWithdrawal(userId, dto.getAmount(), dto.getUpiId()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<WithdrawRequest>> getHistory(Principal principal) {
        String mobile = principal.getName();
        Long userId = userRepository.findByMobile(mobile).get().getId();
        
        return ResponseEntity.ok(withdrawService.getUserWithdrawals(userId));
    }
}