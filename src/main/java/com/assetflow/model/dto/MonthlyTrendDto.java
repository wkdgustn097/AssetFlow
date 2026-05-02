package com.assetflow.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyTrendDto {
    private String month;
    private BigDecimal totalAmount;
}
