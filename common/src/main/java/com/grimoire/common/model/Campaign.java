package com.grimoire.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Campaign {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private List<UUID> playerIds;

    public Campaign() {
        this.id = UUID.randomUUID();
        this.playerIds = new ArrayList<>();
    }

    public Campaign(String name, String description, UUID ownerId) {
        this();
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public List<UUID> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<UUID> playerIds) {
        this.playerIds = playerIds;
    }
    
    public void addPlayer(UUID playerId) {
        if (!this.playerIds.contains(playerId)) {
            this.playerIds.add(playerId);
        }
    }
}
