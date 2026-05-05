package com.assetflow.service;

import com.assetflow.mapper.TransactionMapper;
import com.assetflow.model.Transaction;
import com.assetflow.model.dto.CategorySummaryDto;
import com.assetflow.model.dto.MonthlyTrendDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;

    public List<Transaction> getTransactions(Long userId) {
        return transactionMapper.findByUserId(userId);
    }

    public List<Transaction> getTransactionsByMonth(Long userId, String yearMonth) {
        return transactionMapper.findByUserIdAndYearMonth(userId, yearMonth);
    }

    public List<CategorySummaryDto> getCategorySummary(Long userId, String yearMonth) {
        return transactionMapper.findCategorySummary(userId, yearMonth);
    }

    public List<MonthlyTrendDto> getMonthlyTrend(Long userId, int months) {
        return transactionMapper.findMonthlyTrend(userId, months);
    }

    public BigDecimal getMonthlyTotal(Long userId, String yearMonth) {
        return transactionMapper.findMonthlyTotal(userId, yearMonth);
    }

    public long getTransactionCount(Long userId) {
        return transactionMapper.countByUserId(userId);
    }

    public String getCurrentYearMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    public void deleteTransaction(Long id, Long userId) {
        transactionMapper.deleteById(id, userId);
    }

    public List<CategorySummaryDto> getCategorySummaryByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionMapper.findCategorySummaryByDateRange(userId, startDate, endDate);
    }

    public BigDecimal getMonthlyTotalByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionMapper.findMonthlyTotalByDateRange(userId, startDate, endDate);
    }
}
