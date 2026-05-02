package com.assetflow.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Transaction {
    private Long id;
    private Long userId;
    private LocalDate txnDate;
    private String description;
    private String category;
    private BigDecimal amount;
    private String currency;
    private String sourceFile;
    private LocalDateTime createdAt;
}
