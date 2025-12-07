package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private UUID campaignId;
    private LocalDateTime date;
    private String title;
    @Builder.Default
    private List<SessionNote> notes = new ArrayList<>();
    private String summary;
}
