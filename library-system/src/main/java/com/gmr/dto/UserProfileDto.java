package com.gmr.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserProfileDto {
    private String fullName;
    private String email;
    private String mobile;
    private String referralCode;
    private BigDecimal walletBalance;
    private BigDecimal totalWithdrawn;
}