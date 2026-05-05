package com.assetflow.mapper;

import com.assetflow.model.UserBudget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBudgetMapper {
    List<UserBudget> findByUserId(@Param("userId") Long userId);
    void upsert(UserBudget budget);
    void deleteByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
}
