package com.assetflow.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Stock {
    private Long id;
    private Long userId;
    private String symbol;
    private String companyName;
    private BigDecimal quantity;
    private BigDecimal avgCost;
    private String currency;
    private LocalDateTime createdAt;
}
