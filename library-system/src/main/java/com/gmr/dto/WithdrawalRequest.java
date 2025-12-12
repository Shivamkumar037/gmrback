package com.gmr.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
    private BigDecimal amount;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
}