package com.assetflow.controller;

import com.assetflow.model.UserSettings;
import com.assetflow.model.dto.CategorySummaryDto;
import com.assetflow.model.dto.DashboardSummaryDto;
import com.assetflow.model.dto.MonthlyTrendDto;
import com.assetflow.model.dto.StockPortfolioDto;
import com.assetflow.service.StockService;
import com.assetflow.service.TransactionService;
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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final UserSettingsService userSettingsService;
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
        model.addAttribute("periodLabel", periodLabel);

        return "dashboard";
    }
}
