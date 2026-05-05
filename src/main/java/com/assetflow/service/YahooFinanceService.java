package com.assetflow.service;

import com.assetflow.mapper.StockPriceCacheMapper;
import com.assetflow.model.StockPriceCache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YahooFinanceService {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final StockPriceCacheMapper cacheMapper;

    @Value("${app.stock-cache.ttl-minutes:30}")
    private int cacheTtlMinutes;

    public BigDecimal getCurrentPrice(String symbol) {
        StockPriceCache cached = cacheMapper.findBySymbol(symbol);
        if (cached != null && cached.getFetchedAt().isAfter(LocalDateTime.now().minusMinutes(cacheTtlMinutes))) {
            return cached.getCurrentPrice();
        }

        try {
            BigDecimal price = fetchFromYahoo(symbol);
            cacheMapper.upsert(StockPriceCache.builder()
                    .symbol(symbol)
                    .currentPrice(price)
                    .currency("USD")
                    .fetchedAt(LocalDateTime.now())
                    .build());
            return price;
        } catch (Exception e) {
            log.warn("Yahoo Finance 가격 조회 실패 [{}]: {}", symbol, e.getMessage());
            if (cached != null) return cached.getCurrentPrice();
            return null;
        }
    }

    private BigDecimal fetchFromYahoo(String symbol) throws Exception {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?interval=1d&range=1d";
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("HTTP " + response.code());
            }
            String body = response.body().string();
            JsonNode root = objectMapper.readTree(body);
            JsonNode price = root.path("chart").path("result").get(0).path("meta").path("regularMarketPrice");
            if (price.isMissingNode()) throw new RuntimeException("가격 데이터 없음");
            return price.decimalValue();
        }
    }

    public void clearCache() {
        cacheMapper.deleteAll();
    }

    public void clearCacheForSymbols(List<String> symbols) {
        if (!symbols.isEmpty()) cacheMapper.deleteBySymbols(symbols);
    }
}
