package com.assetflow.service;

import com.assetflow.mapper.UserSettingsMapper;
import com.assetflow.model.UserSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsMapper userSettingsMapper;

    public UserSettings getSettings(Long userId) {
        UserSettings settings = userSettingsMapper.findByUserId(userId);
        return settings != null ? settings : new UserSettings(userId, null);
    }

    public void savePayday(Long userId, Integer payday) {
        userSettingsMapper.upsert(new UserSettings(userId, payday));
    }
}
