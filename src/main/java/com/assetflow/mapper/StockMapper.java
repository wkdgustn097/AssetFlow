package com.assetflow.mapper;

import com.assetflow.model.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockMapper {
    void insert(Stock stock);
    void update(Stock stock);
    void deleteById(@Param("id") Long id, @Param("userId") Long userId);
    List<Stock> findByUserId(@Param("userId") Long userId);
    Stock findByUserIdAndSymbol(@Param("userId") Long userId, @Param("symbol") String symbol);
}
