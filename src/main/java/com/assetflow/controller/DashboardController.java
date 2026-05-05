package com.assetflow.controller;

import com.assetflow.model.UserBudget;
import com.assetflow.model.UserSettings;
import com.assetflow.model.dto.BudgetUsageDto;
import com.assetflow.model.dto.CategorySummaryDto;
import com.assetflow.model.dto.DashboardSummaryDto;
import com.assetflow.model.dto.MonthlyTrendDto;
import com.assetflow.model.dto.StockPortfolioDto;
import com.assetflow.service.StockService;
import com.assetflow.service.TransactionService;
import com.assetflow.service.UserBudgetService;
import com.assetflow.service.UserSettingsService;
import com.assetflow.util.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final UserSettingsService userSettingsService;
    private final UserBudgetService userBudgetService;
    private final StockService stockService;
    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model,
                            @RequestParam(defaultValue = "") String yearMonth) throws JsonProcessingException {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        String displayName = SecurityUtils.getCurrentDisplayName(auth);

        if (yearMonth.isEmpty()) {
            yearMonth = transactionService.getCurrentYearMonth();
        }

        UserSettings settings = userSettingsService.getSettings(userId);
        Integer payday = settings.getPayday();

        List<CategorySummaryDto> categories;
        BigDecimal monthlyTotal;
        String periodLabel;
        long count;

        LocalDate startDate = null;
        LocalDate endDate = null;

        if (payday != null) {
            YearMonth targetMonth = YearMonth.parse(yearMonth);
            startDate = targetMonth.minusMonths(1).atDay(payday);
            endDate = targetMonth.atDay(Math.min(payday - 1, targetMonth.lengthOfMonth()));
            if (payday == 1) {
                startDate = targetMonth.atDay(1);
                endDate = targetMonth.atEndOfMonth();
            }

            categories = transactionService.getCategorySummaryByDateRange(userId, startDate, endDate);
            monthlyTotal = transactionService.getMonthlyTotalByDateRange(userId, startDate, endDate);
            count = transactionService.getTransactionCountByDateRange(userId, startDate, endDate);

            DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("M/d");
            periodLabel = startDate.format(labelFmt) + " ~ " + endDate.format(labelFmt);
        } else {
            categories = transactionService.getCategorySummary(userId, yearMonth);
            monthlyTotal = transactionService.getMonthlyTotal(userId, yearMonth);
            count = transactionService.getTransactionCountByYearMonth(userId, yearMonth);
            periodLabel = YearMonth.parse(yearMonth).getMonthValue() + "월";
        }

        // 전월 대비 증감 계산
        BigDecimal prevTotal;
        if (payday != null) {
            LocalDate prevStart = startDate.minusMonths(1);
            LocalDate prevEnd = endDate.minusMonths(1);
            prevTotal = transactionService.getMonthlyTotalByDateRange(userId, prevStart, prevEnd);
        } else {
            String prevYearMonth = YearMonth.parse(yearMonth).minusMonths(1).toString();
            prevTotal = transactionService.getMonthlyTotal(userId, prevYearMonth);
        }
        String deltaLabel = null;
        if (prevTotal != null && prevTotal.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal delta = monthlyTotal.subtract(prevTotal)
                    .divide(prevTotal.abs(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
            deltaLabel = (delta.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + delta + "%";
        }

        List<MonthlyTrendDto> trend = transactionService.getMonthlyTrend(userId, 6);
        List<StockPortfolioDto> portfolio = stockService.getPortfolio(userId);

        Set<String> excludedCategories = Set.of("이체", "수입", "캐시백", "수업", "기타");
        String topCategory = categories.stream()
                .filter(c -> !excludedCategories.contains(c.getCategory()))
                .map(CategorySummaryDto::getCategory)
                .findFirst()
                .orElse("없음");

        DashboardSummaryDto summary = DashboardSummaryDto.builder()
                .monthlyTotal(monthlyTotal)
                .topCategory(topCategory)
                .transactionCount(count)
                .categorySummaries(categories)
                .monthlyTrend(trend)
                .currentYearMonth(yearMonth)
                .build();

        model.addAttribute("summary", summary);
        model.addAttribute("categoryJson", objectMapper.writeValueAsString(categories));
        model.addAttribute("trendJson", objectMapper.writeValueAsString(trend));
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("displayName", displayName);
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("yearMonth", yearMonth);
        // 예산 사용률 계산
        List<UserBudget> budgets = userBudgetService.getBudgets(userId);
        Map<String, BigDecimal> spentMap = new java.util.HashMap<>();
        for (CategorySummaryDto c : categories) {
            spentMap.put(c.getCategory(), c.getTotalAmount().abs());
        }
        List<BudgetUsageDto> budgetUsages = budgets.stream().map(b -> {
            BigDecimal spent = spentMap.getOrDefault(b.getCategory(), BigDecimal.ZERO);
            int pct = b.getMonthlyLimit().compareTo(BigDecimal.ZERO) == 0 ? 0
                    : spent.divide(b.getMonthlyLimit(), 2, RoundingMode.HALF_UP)
                           .multiply(BigDecimal.valueOf(100)).intValue();
            return BudgetUsageDto.builder()
                    .category(b.getCategory())
                    .spent(spent)
                    .limit(b.getMonthlyLimit())
                    .usagePercent(Math.min(pct, 100))
                    .overBudget(pct > 100)
                    .build();
        }).toList();

        model.addAttribute("periodLabel", periodLabel);
        model.addAttribute("deltaLabel", deltaLabel);
        model.addAttribute("budgetUsages", budgetUsages);

        return "dashboard";
    }
}
