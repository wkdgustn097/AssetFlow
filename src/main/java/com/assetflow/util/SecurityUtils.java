package com.assetflow.util;

import com.assetflow.model.CustomUserDetails;
import org.springframework.security.core.Authentication;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Long getCurrentUserId(Authentication auth) {
        return ((CustomUserDetails) auth.getPrincipal()).getUserId();
    }

    public static String getCurrentDisplayName(Authentication auth) {
        return ((CustomUserDetails) auth.getPrincipal()).getDisplayName();
    }
}
