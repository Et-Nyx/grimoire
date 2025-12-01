package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    private SkillType type;
    private String customName; // For "Ofício" skills - e.g., "Ofício(Ferreiro)"
    private int trainingRanks; // Number of training ranks (each adds +2)
    private int others; // Bonus from equipment, effects, etc.
    private boolean isTrained;
    
    public Skill(SkillType type) {
        this.type = type;
        this.customName = null;
        this.trainingRanks = 0;
        this.others = 0;
        this.isTrained = false;
    }
    
    public String getDisplayName() {
        if (type == SkillType.OFICIO && customName != null && !customName.isEmpty()) {
            return "Ofício(" + customName + ")";
        }
        return type.getDisplayName();
    }
    
    /**
     * Calculates the total skill value
     * Total = 1/2 Level (rounded down) + Attribute Mod + Training + Others
     */
    public int calculateTotal(int characterLevel, Attributes attributes) {
        int halfLevel = characterLevel / 2;
        int attrMod = getAttributeModifier(attributes);
        int training = isTrained ? (trainingRanks * 2) : 0;
        
        return halfLevel + attrMod + training + others;
    }
    
    /**
     * Gets the attribute modifier based on the skill's associated attribute
     */
    private int getAttributeModifier(Attributes attributes) {
        int attributeValue = switch (type.getAttribute()) {
            case "FOR" -> attributes.getStrength();
            case "DES" -> attributes.getDexterity();
            case "CON" -> attributes.getConstitution();
            case "INT" -> attributes.getIntelligence();
            case "SAB" -> attributes.getWisdom();
            case "CAR" -> attributes.getCharisma();
            default -> 0;
        };
        
        // D&D/T20 attribute modifier formula: (value - 10) / 2
        return (attributeValue - 10) / 2;
    }
    
    public int getHalfLevel(int characterLevel) {
        return characterLevel / 2;
    }
    
    public int getAttrMod(Attributes attributes) {
        return getAttributeModifier(attributes);
    }
    
    public int getTraining() {
        return isTrained ? (trainingRanks * 2) : 0;
    }
}
