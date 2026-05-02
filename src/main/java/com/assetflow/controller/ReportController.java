package com.assetflow.controller;

import com.assetflow.mapper.AiReportMapper;
import com.assetflow.model.AiReport;
import com.assetflow.model.dto.StockPortfolioDto;
import com.assetflow.service.OpenRouterService;
import com.assetflow.service.StockService;
import com.assetflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final StockService stockService;
    private final OpenRouterService openRouterService;
    private final AiReportMapper aiReportMapper;

    @GetMapping("/report")
    public String reportPage(Authentication auth, Model model) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        String displayName = SecurityUtils.getCurrentDisplayName(auth);

        AiReport latestReport = aiReportMapper.findLatestByUserId(userId);

        model.addAttribute("report", latestReport);
        model.addAttribute("displayName", displayName);
        model.addAttribute("currentPage", "report");
        return "report";
    }

    @PostMapping("/report/generate")
    public String generateReport(Authentication auth, RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        try {
            List<StockPortfolioDto> portfolio = stockService.getPortfolio(userId);
            if (portfolio.isEmpty()) {
                redirectAttrs.addFlashAttribute("error", "포트폴리오에 종목이 없습니다. 주식을 먼저 추가하세요.");
                return "redirect:/report";
            }
            String reportText = openRouterService.generateStockReport(portfolio);
            aiReportMapper.insert(AiReport.builder()
                    .userId(userId)
                    .reportText(reportText)
                    .build());
            redirectAttrs.addFlashAttribute("success", "AI 리포트가 생성되었습니다.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/report";
    }
}
