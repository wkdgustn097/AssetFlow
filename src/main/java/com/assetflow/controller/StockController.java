package com.assetflow.controller;

import com.assetflow.model.dto.StockPortfolioDto;
import com.assetflow.service.StockService;
import com.assetflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks")
    public String list(Authentication auth, Model model) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        String displayName = SecurityUtils.getCurrentDisplayName(auth);

        List<StockPortfolioDto> portfolio = stockService.getPortfolio(userId);

        BigDecimal totalMarketValue = portfolio.stream()
                .map(StockPortfolioDto::getMarketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPnl = portfolio.stream()
                .map(StockPortfolioDto::getPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("portfolio", portfolio);
        model.addAttribute("totalMarketValue", totalMarketValue);
        model.addAttribute("totalPnl", totalPnl);
        model.addAttribute("displayName", displayName);
        model.addAttribute("currentPage", "stocks");
        return "stocks";
    }

    @PostMapping("/stocks")
    public String addStock(@RequestParam String symbol,
                           @RequestParam(defaultValue = "") String companyName,
                           @RequestParam BigDecimal quantity,
                           @RequestParam BigDecimal avgCost,
                           Authentication auth,
                           RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        try {
            stockService.addOrUpdateStock(userId, symbol, companyName, quantity, avgCost);
            redirectAttrs.addFlashAttribute("success", symbol.toUpperCase() + " 종목이 추가/업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "종목 추가 실패: " + e.getMessage());
        }
        return "redirect:/stocks";
    }

    @PostMapping("/stocks/delete/{id}")
    public String deleteStock(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        stockService.deleteStock(id, userId);
        redirectAttrs.addFlashAttribute("success", "종목이 삭제되었습니다.");
        return "redirect:/stocks";
    }

    @PostMapping("/stocks/refresh")
    public String refreshPrices(Authentication auth, RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        stockService.refreshPrices(userId);
        redirectAttrs.addFlashAttribute("success", "가격 캐시가 초기화되었습니다. 새 가격을 조회합니다.");
        return "redirect:/stocks";
    }
}
