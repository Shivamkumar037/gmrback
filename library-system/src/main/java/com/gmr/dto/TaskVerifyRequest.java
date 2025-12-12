package com.gmr.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TaskVerifyRequest {
    private String taskId;
    private BigDecimal amount;
}