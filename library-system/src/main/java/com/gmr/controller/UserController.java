package com.gmr.controller;

import com.gmr.dto.UserProfileDto;
import com.gmr.model.User;
import com.gmr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map Entity to DTO to hide password and internal IDs
        UserProfileDto profile = UserProfileDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .referralCode(user.getReferralCode())
                .walletBalance(user.getWalletBalance())
                .totalWithdrawn(user.getTotalWithdrawn())
                .build();

        return ResponseEntity.ok(profile);
    }
    
    // Add /dashboard endpoint here if you have specific dashboard stats logic
}