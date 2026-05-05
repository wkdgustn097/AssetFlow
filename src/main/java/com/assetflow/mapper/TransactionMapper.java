package com.assetflow.mapper;

import com.assetflow.model.Transaction;
import com.assetflow.model.dto.CategorySummaryDto;
import com.assetflow.model.dto.MonthlyTrendDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TransactionMapper {
    void insert(Transaction transaction);
    void insertBatch(@Param("list") List<Transaction> transactions);
    List<Transaction> findByUserIdAndYearMonth(@Param("userId") Long userId, @Param("yearMonth") String yearMonth);
    List<Transaction> findByUserId(@Param("userId") Long userId);
    List<CategorySummaryDto> findCategorySummary(@Param("userId") Long userId, @Param("yearMonth") String yearMonth);
    List<MonthlyTrendDto> findMonthlyTrend(@Param("userId") Long userId, @Param("months") int months);
    BigDecimal findMonthlyTotal(@Param("userId") Long userId, @Param("yearMonth") String yearMonth);
    long countByUserId(@Param("userId") Long userId);
    long countByUserIdAndYearMonth(@Param("userId") Long userId, @Param("yearMonth") String yearMonth);
    long countByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    void deleteById(@Param("id") Long id, @Param("userId") Long userId);
    List<CategorySummaryDto> findCategorySummaryByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    BigDecimal findMonthlyTotalByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
