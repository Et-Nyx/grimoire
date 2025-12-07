package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatStats {
    private int defenseTotal;
    private int armorBonus;
    private int shieldBonus;
    private int armorPenalty;
    private String armorEquipped;
    private String shieldEquipped;
    
    // Campos adicionais para cálculos T20 JdA
    private int naturalArmorBonus;  // Armadura natural (ex: Minotauro +1)
    private int sizeModifier;       // Modificador de tamanho (Pequeno +1, Grande -1)
    private int otherDefenseBonus;  // Outros bônus de defesa (talentos, magias, etc.)
}
