package com.assetflow.mapper;

import com.assetflow.model.AiReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiReportMapper {
    void insert(AiReport report);
    AiReport findLatestByUserId(@Param("userId") Long userId);
}
