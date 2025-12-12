package com.gmr.controller;

import com.gmr.dto.*;
import com.gmr.model.User;
import com.gmr.repository.UserRepository;
import com.gmr.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate unique referral code (First 8 chars of UUID)
        user.setReferralCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Validate Referral
        if (request.getReferId() != null && !request.getReferId().isEmpty()) {
            if (userRepository.findByReferralCode(request.getReferId()).isPresent()) {
                user.setReferredBy(request.getReferId());
            } else {
                return ResponseEntity.badRequest().body("Invalid Referral Code");
            }
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Authenticate the user (Will throw exception if fails)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Get the valid UserDetails object (Fixed Type Error)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. We still need the User Entity to get name/balance for the response
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        // 4. Generate Token using the UserDetails
        var jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwtToken, user.getFullName(), user.getWalletBalance()));
    }
}