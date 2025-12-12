package com.gmr.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String fullName;
    private String email;
    private String mobile;
    private String password;
    private String referId; // Optional
}