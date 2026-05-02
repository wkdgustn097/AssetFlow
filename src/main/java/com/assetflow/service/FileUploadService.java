package com.assetflow.service;

import com.assetflow.mapper.TransactionMapper;
import com.assetflow.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final TransactionMapper transactionMapper;

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    );

    public int processUpload(MultipartFile file, Long userId) {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new FileParseException("파일 이름을 확인할 수 없습니다.");

        String lower = filename.toLowerCase();
        List<Transaction> transactions;

        try {
            if (lower.endsWith(".csv")) {
                transactions = parseCsv(file, userId, filename);
            } else if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
                transactions = parseExcel(file, userId, filename);
            } else {
                throw new FileParseException("지원하지 않는 파일 형식입니다. CSV 또는 Excel 파일을 업로드하세요.");
            }
        } catch (IOException e) {
            throw new FileParseException("파일 읽기 오류: " + e.getMessage());
        }

        if (transactions.isEmpty()) {
            throw new FileParseException("업로드할 데이터가 없습니다.");
        }

        transactionMapper.insertBatch(transactions);
        return transactions.size();
    }

    private List<Transaction> parseCsv(MultipartFile file, Long userId, String filename) throws IOException {
        List<Transaction> result = new ArrayList<>();
        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build()
                .parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            validateHeaders(parser.getHeaderNames());

            for (CSVRecord record : parser) {
                Transaction t = buildTransaction(
                        record.get("date"),
                        record.get("description"),
                        record.get("category"),
                        record.get("amount"),
                        userId,
                        filename
                );
                result.add(t);
            }
        }
        return result;
    }

    private List<Transaction> parseExcel(MultipartFile file, Long userId, String filename) throws IOException {
        List<Transaction> result = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new FileParseException("헤더 행이 없습니다.");

            Map<String, Integer> headerIndex = buildHeaderIndex(headerRow);
            validateHeaderMap(headerIndex);

            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String date = getCellValue(row, headerIndex.get("date"), formatter);
                String description = getCellValue(row, headerIndex.get("description"), formatter);
                String category = getCellValue(row, headerIndex.get("category"), formatter);
                String amount = getCellValue(row, headerIndex.get("amount"), formatter);

                if (date.isEmpty() && amount.isEmpty()) continue;

                result.add(buildTransaction(date, description, category, amount, userId, filename));
            }
        }
        return result;
    }

    private Transaction buildTransaction(String dateStr, String description, String category,
                                         String amountStr, Long userId, String filename) {
        LocalDate date = parseDate(dateStr.trim());
        BigDecimal amount = parseAmount(amountStr.trim());

        return Transaction.builder()
                .userId(userId)
                .txnDate(date)
                .description(description != null ? description.trim() : "")
                .category(category != null && !category.trim().isEmpty() ? category.trim() : "기타")
                .amount(amount)
                .currency("KRW")
                .sourceFile(filename)
                .build();
    }

    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(dateStr, fmt);
            } catch (DateTimeParseException ignored) {}
        }
        throw new FileParseException("날짜 형식을 인식할 수 없습니다: " + dateStr + " (지원: yyyy-MM-dd, yyyy/MM/dd, MM/dd/yyyy, yyyyMMdd)");
    }

    private BigDecimal parseAmount(String amountStr) {
        try {
            String cleaned = amountStr.replaceAll("[,\\s₩$]", "");
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new FileParseException("금액 형식이 올바르지 않습니다: " + amountStr);
        }
    }

    private void validateHeaders(List<String> headers) {
        List<String> required = List.of("date", "description", "category", "amount");
        List<String> lower = headers.stream().map(String::toLowerCase).toList();
        for (String req : required) {
            if (!lower.contains(req)) {
                throw new FileParseException("필수 컬럼 누락: " + req + " (필수: date, description, category, amount)");
            }
        }
    }

    private Map<String, Integer> buildHeaderIndex(Row headerRow) {
        Map<String, Integer> index = new HashMap<>();
        DataFormatter formatter = new DataFormatter();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                index.put(formatter.formatCellValue(cell).toLowerCase().trim(), i);
            }
        }
        return index;
    }

    private void validateHeaderMap(Map<String, Integer> index) {
        for (String req : List.of("date", "description", "category", "amount")) {
            if (!index.containsKey(req)) {
                throw new FileParseException("필수 컬럼 누락: " + req + " (필수: date, description, category, amount)");
            }
        }
    }

    private String getCellValue(Row row, Integer colIndex, DataFormatter formatter) {
        if (colIndex == null) return "";
        Cell cell = row.getCell(colIndex);
        return cell == null ? "" : formatter.formatCellValue(cell);
    }

    public static class FileParseException extends RuntimeException {
        public FileParseException(String message) {
            super(message);
        }
    }
}
