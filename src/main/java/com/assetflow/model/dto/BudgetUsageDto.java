package com.assetflow.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BudgetUsageDto {
    private String category;
    private BigDecimal spent;
    private BigDecimal limit;
    private int usagePercent;
    private boolean overBudget;
}
