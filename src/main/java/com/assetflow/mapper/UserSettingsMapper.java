package com.assetflow.mapper;

import com.assetflow.model.UserSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserSettingsMapper {
    UserSettings findByUserId(@Param("userId") Long userId);
    void upsert(UserSettings settings);
}
