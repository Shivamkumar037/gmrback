package com.app.dto;

import lombok.Data;

public class WithdrawDtos {
    
    @Data
    public static class WithdrawRequestDto {
        private Double amount;
        private String upiId;
    }
}