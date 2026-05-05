package com.assetflow.controller;

import com.assetflow.model.Transaction;
import com.assetflow.service.FileUploadService;
import com.assetflow.service.TransactionService;
import com.assetflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final FileUploadService fileUploadService;

    @GetMapping("/transactions")
    public String list(Authentication auth, Model model,
                       @RequestParam(defaultValue = "") String yearMonth) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        String displayName = SecurityUtils.getCurrentDisplayName(auth);

        if (yearMonth.isEmpty()) {
            yearMonth = transactionService.getCurrentYearMonth();
        }

        List<Transaction> transactions = transactionService.getTransactionsByMonth(userId, yearMonth);

        model.addAttribute("transactions", transactions);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("displayName", displayName);
        model.addAttribute("currentPage", "transactions");
        return "transactions";
    }

    @PostMapping("/transactions/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         Authentication auth,
                         RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        try {
            FileUploadService.UploadResult result = fileUploadService.processUpload(file, userId);
            StringBuilder msg = new StringBuilder(result.successCount() + "건의 거래 내역이 등록되었습니다.");
            if (result.hasFailures()) {
                msg.append(" (").append(result.failedRows().size()).append("건 실패: ")
                   .append(String.join(", ", result.failedRows())).append(")");
                redirectAttrs.addFlashAttribute("error", msg.toString());
            } else {
                redirectAttrs.addFlashAttribute("success", msg.toString());
            }
            return "redirect:/transactions?yearMonth=" + result.firstYearMonth();
        } catch (FileUploadService.FileParseException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "파일 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/transactions";
    }

    @PostMapping("/transactions/delete/{id}")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtils.getCurrentUserId(auth);
        transactionService.deleteTransaction(id, userId);
        redirectAttrs.addFlashAttribute("success", "삭제되었습니다.");
        return "redirect:/transactions";
    }
}
