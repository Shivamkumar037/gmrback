package com.app.dto;

import lombok.Data;

public class AuthDtos {

    @Data
    public static class SignupRequest {
        private String fullName;
        private String mobile;
        private String email;
        private String password;
        private String referralCode; // Optional
    }

    @Data
    public static class LoginRequest {
        private String mobile;
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String message;
        private UserDto user;

        public AuthResponse(String token, String message, UserDto user) {
            this.token = token;
            this.message = message;
            this.user = user;
        }
    }
    
    @Data
    public static class UserDto {
        private Long id;
        private String fullName;
        private String mobile;
        private String referralCode;
        private Double walletBalance;
        private String role;
        
        // Constructor for quick mapping
        public UserDto(Long id, String fullName, String mobile, String referralCode, Double walletBalance, String role) {
            this.id = id;
            this.fullName = fullName;
            this.mobile = mobile;
            this.referralCode = referralCode;
            this.walletBalance = walletBalance;
            this.role = role;
        }
    }
}