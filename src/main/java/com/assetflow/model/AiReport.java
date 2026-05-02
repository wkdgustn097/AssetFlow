package com.assetflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AiReport {
    private Long id;
    private Long userId;
    private String reportText;
    private LocalDateTime createdAt;
}
