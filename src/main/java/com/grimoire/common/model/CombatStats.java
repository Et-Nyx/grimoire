package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatStats {
    private int defenseTotal;
    private int armorBonus;
    private int shieldBonus;
    private int armorPenalty;
    private String armorEquipped;
    private String shieldEquipped;
}
