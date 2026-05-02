package com.assetflow.service;

import com.assetflow.model.dto.StockPortfolioDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenRouterService {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${app.openrouter.api-key:}")
    private String apiKey;

    @Value("${app.openrouter.model:anthropic/claude-opus-4}")
    private String model;

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public String generateStockReport(List<StockPortfolioDto> portfolio) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("OpenRouter API 키가 설정되지 않았습니다. OPENROUTER_API_KEY 환경변수를 확인하세요.");
        }

        String prompt = buildPrompt(portfolio);

        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            ArrayNode messages = body.putArray("messages");
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);

            Request request = new Request.Builder()
                    .url(OPENROUTER_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "https://assetflow.railway.app")
                    .header("X-Title", "AssetFlow")
                    .post(RequestBody.create(objectMapper.writeValueAsString(body), JSON))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new RuntimeException("API 오류: HTTP " + response.code());
                }
                JsonNode result = objectMapper.readTree(response.body().string());
                return result.path("choices").get(0).path("message").path("content").asText();
            }
        } catch (Exception e) {
            log.error("OpenRouter API 호출 실패", e);
            throw new RuntimeException("AI 리포트 생성 실패: " + e.getMessage());
        }
    }

    private String buildPrompt(List<StockPortfolioDto> portfolio) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음은 나의 주식 포트폴리오 현황입니다:\n\n");
        sb.append(String.format("%-10s %-15s %8s %10s %10s %12s %10s%n",
                "심볼", "회사명", "수량", "평균단가", "현재가", "평가금액", "수익률"));
        sb.append("-".repeat(80)).append("\n");

        for (StockPortfolioDto dto : portfolio) {
            if (dto.isPriceAvailable()) {
                sb.append(String.format("%-10s %-15s %8.2f %10.2f %10.2f %12.2f %9.2f%%%n",
                        dto.getSymbol(),
                        dto.getCompanyName() != null ? dto.getCompanyName() : "",
                        dto.getQuantity(),
                        dto.getAvgCost(),
                        dto.getCurrentPrice(),
                        dto.getMarketValue(),
                        dto.getPnlPercent()));
            } else {
                sb.append(String.format("%-10s %-15s %8.2f %10.2f %10s %12s %10s%n",
                        dto.getSymbol(),
                        dto.getCompanyName() != null ? dto.getCompanyName() : "",
                        dto.getQuantity(),
                        dto.getAvgCost(),
                        "N/A", "N/A", "N/A"));
            }
        }

        sb.append("\n포트폴리오를 한국어로 분석해주세요:\n");
        sb.append("1. 전체 수익/손실 현황 요약\n");
        sb.append("2. 가장 수익률이 높은/낮은 종목\n");
        sb.append("3. 분산투자 관점에서의 간단한 코멘트\n");
        sb.append("4. 전반적인 포트폴리오에 대한 짧은 조언\n");
        sb.append("\n분석은 간결하고 실용적으로 작성해주세요.");

        return sb.toString();
    }
}
