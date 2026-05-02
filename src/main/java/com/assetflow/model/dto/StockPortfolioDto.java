package com.assetflow.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StockPortfolioDto {
    private Long id;
    private String symbol;
    private String companyName;
    private BigDecimal quantity;
    private BigDecimal avgCost;
    private BigDecimal currentPrice;
    private BigDecimal marketValue;
    private BigDecimal pnl;
    private BigDecimal pnlPercent;
    private String currency;
    private boolean priceAvailable;
}
