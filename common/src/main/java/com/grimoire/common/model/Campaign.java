package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private String system;
    private UUID ownerId;
    @Builder.Default
    private List<UUID> playerIds = new ArrayList<>();
    
    public void addPlayer(UUID playerId) {
        if (!this.playerIds.contains(playerId)) {
            this.playerIds.add(playerId);
        }
    }
}
