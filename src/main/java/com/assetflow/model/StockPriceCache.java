package com.assetflow.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StockPriceCache {
    private String symbol;
    private BigDecimal currentPrice;
    private String currency;
    private LocalDateTime fetchedAt;
}
