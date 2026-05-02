package com.assetflow.controller;

import com.assetflow.model.dto.CategorySummaryDto;
import com.assetflow.model.dto.DashboardSummaryDto;
import com.assetflow.model.dto.MonthlyTrendDto;
import com.assetflow.service.TransactionService;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model,
                            @RequestParam(defaultValue = "") String yearMonth) throws JsonProcessingException {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        String displayName = SecurityUtils.getCurrentDisplayName(auth);

        if (yearMonth.isEmpty()) {
            yearMonth = transactionService.getCurrentYearMonth();
        }

        List<CategorySummaryDto> categories = transactionService.getCategorySummary(userId, yearMonth);
        List<MonthlyTrendDto> trend = transactionService.getMonthlyTrend(userId, 6);
        BigDecimal monthlyTotal = transactionService.getMonthlyTotal(userId, yearMonth);
        long count = transactionService.getTransactionCount(userId);

        String topCategory = categories.isEmpty() ? "없음" : categories.get(0).getCategory();

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
        model.addAttribute("displayName", displayName);
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("yearMonth", yearMonth);

        return "dashboard";
    }
}
