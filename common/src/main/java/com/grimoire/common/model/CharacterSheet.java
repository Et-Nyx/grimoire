package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterSheet {
    private String id;
    private java.util.UUID campaignId;
    private java.util.UUID playerId;
    
    // Header (Principal)
    @Builder.Default
    private String name = "Novo Personagem";
    @Builder.Default
    private String playerName = "";
    @Builder.Default
    private String system = ""; // Derived from Session/Campaign context
    @Builder.Default
    private String race = "";
    @Builder.Default
    private String origin = "";
    @Builder.Default
    private String characterClass = "";
    @Builder.Default
    private String deity = "";
    @Builder.Default
    private int level = 1;
    @Builder.Default
    private String size = "";
    @Builder.Default
    private String speed = "";
    
    // Stats (Principal)
    @Builder.Default
    private Attributes attributes = new Attributes();
    @Builder.Default
    private Status status = new Status();
    
    // Combat (Combate)
    @Builder.Default
    private CombatStats combatStats = new CombatStats();
    @Builder.Default
    private List<Attack> attacks = new ArrayList<>();
    
    // Skills (Perícias)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();
    @Builder.Default
    private String proficiencies = "";
    
    // Magic (Magia)
    @Builder.Default
    private Magic magic = new Magic();
    
    // Inventory (Inventário)
    @Builder.Default
    private Inventory inventory = new Inventory();
    
    // Lore (Lore)
    @Builder.Default
    private Abilities abilities = new Abilities(); // Class/Race abilities
    @Builder.Default
    private String background = "";
    @Builder.Default
    private String notes = "";
}
