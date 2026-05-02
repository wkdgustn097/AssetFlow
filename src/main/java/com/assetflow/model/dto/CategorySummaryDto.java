package com.assetflow.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategorySummaryDto {
    private String category;
    private BigDecimal totalAmount;
}
