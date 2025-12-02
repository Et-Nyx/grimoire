package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterSheet {
    private String id;
    private java.util.UUID campaignId;
    private java.util.UUID playerId;
    
    // Header
    private String name;
    private String playerName;
    private String system; // e.g., "Tormenta20"
    private String race;
    private String origin;
    private String characterClass;
    private String deity;
    private int level;
    private String size;
    private String speed;
    
    // Sections
    private Attributes attributes = new Attributes();
    private Status status = new Status();
    private CombatStats combatStats = new CombatStats();
    private List<Skill> skills = new ArrayList<>();
    private String proficiencies;
    
    private List<Attack> attacks = new ArrayList<>();
    private Magic magic = new Magic();
    
    private Inventory inventory = new Inventory();
    private Abilities abilities = new Abilities();
    
    private String background;
    private String notes;
}
