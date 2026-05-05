package com.assetflow.service;

import com.assetflow.mapper.StockMapper;
import com.assetflow.model.Stock;
import com.assetflow.model.dto.StockPortfolioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMapper stockMapper;
    private final YahooFinanceService yahooFinanceService;

    public List<StockPortfolioDto> getPortfolio(Long userId) {
        return stockMapper.findByUserId(userId).stream()
                .map(this::toPortfolioDto)
                .toList();
    }

    public void addOrUpdateStock(Long userId, String symbol, String companyName,
                                  BigDecimal quantity, BigDecimal avgCost) {
        Stock existing = stockMapper.findByUserIdAndSymbol(userId, symbol.toUpperCase());
        if (existing != null) {
            existing.setCompanyName(companyName);
            existing.setQuantity(quantity);
            existing.setAvgCost(avgCost);
            stockMapper.update(existing);
        } else {
            stockMapper.insert(Stock.builder()
                    .userId(userId)
                    .symbol(symbol.toUpperCase())
                    .companyName(companyName)
                    .quantity(quantity)
                    .avgCost(avgCost)
                    .currency("USD")
                    .build());
        }
    }

    public void deleteStock(Long id, Long userId) {
        stockMapper.deleteById(id, userId);
    }

    public void refreshPrices(Long userId) {
        List<String> symbols = stockMapper.findByUserId(userId).stream()
                .map(Stock::getSymbol)
                .toList();
        yahooFinanceService.clearCacheForSymbols(symbols);
    }

    public List<Stock> getRawStocks(Long userId) {
        return stockMapper.findByUserId(userId);
    }

    private StockPortfolioDto toPortfolioDto(Stock stock) {
        BigDecimal currentPrice = yahooFinanceService.getCurrentPrice(stock.getSymbol());
        boolean priceAvailable = currentPrice != null;

        if (!priceAvailable) {
            return StockPortfolioDto.builder()
                    .id(stock.getId())
                    .symbol(stock.getSymbol())
                    .companyName(stock.getCompanyName())
                    .quantity(stock.getQuantity())
                    .avgCost(stock.getAvgCost())
                    .currentPrice(BigDecimal.ZERO)
                    .marketValue(BigDecimal.ZERO)
                    .pnl(BigDecimal.ZERO)
                    .pnlPercent(BigDecimal.ZERO)
                    .currency(stock.getCurrency())
                    .priceAvailable(false)
                    .build();
        }

        BigDecimal marketValue = currentPrice.multiply(stock.getQuantity());
        BigDecimal costBasis = stock.getAvgCost().multiply(stock.getQuantity());
        BigDecimal pnl = marketValue.subtract(costBasis);
        BigDecimal pnlPercent = costBasis.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : pnl.divide(costBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return StockPortfolioDto.builder()
                .id(stock.getId())
                .symbol(stock.getSymbol())
                .companyName(stock.getCompanyName())
                .quantity(stock.getQuantity())
                .avgCost(stock.getAvgCost())
                .currentPrice(currentPrice)
                .marketValue(marketValue.setScale(2, RoundingMode.HALF_UP))
                .pnl(pnl.setScale(2, RoundingMode.HALF_UP))
                .pnlPercent(pnlPercent.setScale(2, RoundingMode.HALF_UP))
                .currency(stock.getCurrency())
                .priceAvailable(true)
                .build();
    }
}
