package com.app.controller;

import com.app.dto.AuthDtos.*;
import com.app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Combined Login/Signup Endpoint
    @PostMapping("/connect")
    public ResponseEntity<AuthResponse> connect(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.registerOrLogin(request));
    }
    
    // Explicit Login (Optional, if you want separate)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}