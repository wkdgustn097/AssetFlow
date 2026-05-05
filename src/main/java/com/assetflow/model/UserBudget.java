package com.assetflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBudget {
    private Long id;
    private Long userId;
    private String category;
    private BigDecimal monthlyLimit;
}
