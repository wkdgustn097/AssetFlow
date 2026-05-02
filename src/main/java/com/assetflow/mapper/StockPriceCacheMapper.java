package com.assetflow.mapper;

import com.assetflow.model.StockPriceCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockPriceCacheMapper {
    StockPriceCache findBySymbol(@Param("symbol") String symbol);
    void upsert(StockPriceCache cache);
    void deleteAll();
}
