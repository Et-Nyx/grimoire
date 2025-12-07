package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionNote {
    @Builder.Default
    private String id = java.util.UUID.randomUUID().toString();
    private String sessionId;
    private String author;
    private String content;
    private LocalDateTime timestamp;
    private boolean isPublic;
}
