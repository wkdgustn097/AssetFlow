package com.assetflow.service;

import com.assetflow.mapper.UserBudgetMapper;
import com.assetflow.model.UserBudget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBudgetService {

    private final UserBudgetMapper userBudgetMapper;

    public List<UserBudget> getBudgets(Long userId) {
        return userBudgetMapper.findByUserId(userId);
    }

    public void saveBudget(Long userId, String category, BigDecimal monthlyLimit) {
        userBudgetMapper.upsert(UserBudget.builder()
                .userId(userId)
                .category(category)
                .monthlyLimit(monthlyLimit)
                .build());
    }

    public void deleteBudget(Long userId, String category) {
        userBudgetMapper.deleteByUserIdAndCategory(userId, category);
    }
}
