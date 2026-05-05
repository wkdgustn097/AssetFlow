package com.assetflow.mapper;

import com.assetflow.model.StockPriceCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StockPriceCacheMapper {
    StockPriceCache findBySymbol(@Param("symbol") String symbol);
    void upsert(StockPriceCache cache);
    void deleteAll();
    void deleteBySymbols(@Param("symbols") List<String> symbols);
}
