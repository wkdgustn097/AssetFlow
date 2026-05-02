package com.assetflow.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardSummaryDto {
    private BigDecimal monthlyTotal;
    private String topCategory;
    private long transactionCount;
    private List<CategorySummaryDto> categorySummaries;
    private List<MonthlyTrendDto> monthlyTrend;
    private String currentYearMonth;
}
