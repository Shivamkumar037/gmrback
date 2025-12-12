package com.gmr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String fullName;
    private BigDecimal balance;
}