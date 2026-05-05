package com.assetflow.controller;

import com.assetflow.service.UserSettingsService;
import com.assetflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping("/settings")
    public String settings(Authentication auth, Model model) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        String displayName = SecurityUtils.getCurrentDisplayName(auth);
        model.addAttribute("settings", userSettingsService.getSettings(userId));
        model.addAttribute("displayName", displayName);
        model.addAttribute("currentPage", "settings");
        return "settings";
    }

    @PostMapping("/settings")
    public String saveSettings(Authentication auth,
                               @RequestParam(required = false) Integer payday,
                               RedirectAttributes ra) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        Integer value = (payday != null && payday >= 1 && payday <= 31) ? payday : null;
        userSettingsService.savePayday(userId, value);
        ra.addFlashAttribute("success", "설정이 저장되었습니다.");
        return "redirect:/settings";
    }
}
