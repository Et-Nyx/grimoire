package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignNote {
    private String id;
    private String author;
    private String content;
    private LocalDateTime timestamp;
    private boolean isPublic;
}
