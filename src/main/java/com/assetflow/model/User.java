package com.assetflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private boolean enabled;
    private LocalDateTime createdAt;
}
