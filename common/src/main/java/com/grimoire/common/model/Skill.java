package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {
    private SkillType type;
    private String customName; // For "Ofício" skills - e.g., "Ofício(Ferreiro)"
    private int others; // Bonus from equipment, effects, etc.
    private boolean isTrained;
    
    
    public String getDisplayName() {
        if (type == SkillType.OFICIO && customName != null && !customName.isEmpty()) {
            return "Ofício(" + customName + ")";
        }
        return type.getDisplayName();
    }
    
    /**
     * Calcula o valor total da perícia conforme T20 JdA.
     * Fórmula: 1/2 Nível + Modificador de Atributo + Bônus de Treinamento + Outros - Penalidade de Armadura
     * 
     * @param characterLevel Nível do personagem
     * @param attributes Atributos do personagem (valores JÁ SÃO os modificadores)
     * @param armorPenalty Penalidade de armadura (aplicada apenas se a perícia sofre penalidade)
     * @return Valor total da perícia
     */
    public int calculateTotal(int characterLevel, Attributes attributes, int armorPenalty) {
        int halfLevel = characterLevel / 2;
        int attrMod = getAttributeModifier(attributes);
        int training = getTrainingBonus(characterLevel);
        int acp = type.hasArmorPenalty() ? armorPenalty : 0;
        
        return halfLevel + attrMod + training + others - acp;
    }
    
    /**
     * Calcula o valor total da perícia sem penalidade de armadura (compatibilidade).
     */
    public int calculateTotal(int characterLevel, Attributes attributes) {
        return calculateTotal(characterLevel, attributes, 0);
    }
    
    /**
     * Obtém o modificador de atributo da perícia.
     * T20 JdA: O valor do atributo JÁ É o modificador (armazenado diretamente como -2 a +5).
     * Não há conversão como em D&D!
     */
    private int getAttributeModifier(Attributes attributes) {
        if (attributes == null || type == null) return 0;
        
        // T20 JdA: O atributo JÁ É o modificador (armazenado diretamente)
        return switch (type.getAttribute()) {
            case "FOR" -> attributes.getStrength();
            case "DES" -> attributes.getDexterity();
            case "CON" -> attributes.getConstitution();
            case "INT" -> attributes.getIntelligence();
            case "SAB" -> attributes.getWisdom();
            case "CAR" -> attributes.getCharisma();
            default -> 0;
        };
    }
    
    /**
     * Calcula o bônus de treinamento baseado no patamar do personagem.
     * T20 JdA:
     * - Níveis 1-6: +2
     * - Níveis 7-14: +4
     * - Níveis 15+: +6
     */
    public int getTrainingBonus(int level) {
        if (!isTrained) return 0;
        if (level >= 15) return 6;
        if (level >= 7) return 4;
        return 2;
    }
    
    public int getHalfLevel(int characterLevel) {
        return characterLevel / 2;
    }
    
    public int getAttrMod(Attributes attributes) {
        return getAttributeModifier(attributes);
    }
    
    /**
     * @deprecated Use {@link #getTrainingBonus(int)} com o nível do personagem
     */
    @Deprecated
    public int getTraining() {
        // Mantido para compatibilidade, assume nível 1
        return isTrained ? 2 : 0;
    }
}

