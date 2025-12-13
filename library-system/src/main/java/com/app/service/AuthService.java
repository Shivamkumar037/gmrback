package com.app.service;

import com.app.config.JwtUtils;
import com.app.config.SecurityConfig;
import com.app.dto.AuthDtos.*;
import com.app.entity.*;
import com.app.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private TaskHistoryRepository taskHistoryRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private SecurityConfig securityConfig; // To access UserDetailsService

    @Transactional
    public AuthResponse registerOrLogin(SignupRequest request) {
        
        // 1. CHECK: Does user exist?
        Optional<User> existingUser = userRepository.findByMobile(request.getMobile());
        
        if (existingUser.isPresent()) {
            // User exists -> Attempt Login
            return login(new LoginRequest() {{
                setMobile(request.getMobile());
                setPassword(request.getPassword());
            }});
        }

        // 2. NEW REGISTRATION
        User user = new User();
        user.setMobile(request.getMobile());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setReferralCode(generateUniqueReferralCode());
        user.setRole("ROLE_USER");

        // 3. REFERRAL LOGIC (With Bonus)
        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            Optional<User> referrerOpt = userRepository.findByReferralCode(request.getReferralCode());
            
            if (referrerOpt.isPresent()) {
                User referrer = referrerOpt.get();
                user.setReferredBy(referrer.getReferralCode());
                
                // Credit ₹5 to Referrer
                processReferralBonus(referrer, user);
            }
            // If referral code invalid, we just ignore it (setReferredBy remains null)
        }

        User savedUser = userRepository.save(user);

        // 4. Create Empty Wallet for new User
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        walletRepository.save(wallet);

        // 5. Generate Token
        UserDetails userDetails = securityConfig.userDetailsService().loadUserByUsername(savedUser.getMobile());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(token, "Registration Successful", mapToDto(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMobile(), request.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid Mobile or Password");
        }

        User user = userRepository.findByMobile(request.getMobile()).orElseThrow();
        UserDetails userDetails = securityConfig.userDetailsService().loadUserByUsername(user.getMobile());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(token, "Login Successful", mapToDto(user));
    }

    // Helper: Add money to referrer
    private void processReferralBonus(User referrer, User newUser) {
        // Find referrer's wallet
        Wallet referrerWallet = walletRepository.findByUser(referrer).orElse(new Wallet());
        if(referrerWallet.getUser() == null) { 
            referrerWallet.setUser(referrer); // Safety check
        }

        referrerWallet.setBalance(referrerWallet.getBalance() + 5.0);
        referrerWallet.setTotalEarned(referrerWallet.getTotalEarned() + 5.0);
        walletRepository.save(referrerWallet);

        // Add to History
        TaskHistory history = new TaskHistory();
        history.setUserId(referrer.getId());
        history.setRewardAmount(5.0);
        history.setType(TaskHistory.TransactionType.REFERRAL_BONUS);
        history.setDescription("Referral Bonus for user: " + newUser.getFullName());
        taskHistoryRepository.save(history);
    }

    private String generateUniqueReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private UserDto mapToDto(User user) {
        Wallet w = walletRepository.findByUser(user).orElse(new Wallet());
        return new UserDto(user.getId(), user.getFullName(), user.getMobile(), user.getReferralCode(), w.getBalance(), user.getRole());
    }
}