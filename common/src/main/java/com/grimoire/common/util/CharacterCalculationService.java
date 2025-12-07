package com.grimoire.common.util;

import com.grimoire.common.model.*;

/**
 * Serviço de cálculo de estatísticas de personagem para T20 JdA.
 * Centraliza todas as fórmulas do sistema Tormenta 20 Edição Jogo do Ano.
 * 
 * Fórmulas principais:
 * - PV = BaseClasse + Con + ((Nivel - 1) * (GanhoClasse + Con))
 * - PM = BasePM * Nível
 * - Defesa = 10 + 1/2 Nível + Des (se permitido) + Armadura + Escudo + Tamanho
 * - Ataque = 1/2 Nível + Atributo + Perícia + Bônus
 * - CD Magia = 10 + 1/2 Nível + Atributo Chave
 */
public final class CharacterCalculationService {
    
    private CharacterCalculationService() {
        // Utility class - prevent instantiation
    }
    
    // ==================== PV (Pontos de Vida) ====================
    
    /**
     * Calcula os PV máximos do personagem.
     * Fórmula T20 JdA: BaseClasse + Con + ((Nivel - 1) * (GanhoClasse + Con))
     * 
     * @param sheet Ficha do personagem
     * @param classData Dados da classe
     * @return PV máximo calculado
     */
    public static int calculateMaxHP(CharacterSheet sheet, CharacterClassData classData) {
        if (sheet == null || classData == null) return 0;
        
        int con = sheet.getAttributes() != null ? sheet.getAttributes().getConstitution() : 0;
        int level = sheet.getLevel();
        int baseHP = classData.baseHP();
        int hpPerLevel = classData.hpPerLevel();
        
        // Nível 1: BaseHP + Con
        // Níveis subsequentes: + (hpPerLevel + Con) por nível
        return baseHP + con + ((level - 1) * (hpPerLevel + con));
    }
    
    /**
     * Calcula os PV máximos usando o nome da classe da ficha.
     */
    public static int calculateMaxHP(CharacterSheet sheet) {
        if (sheet == null) return 0;
        CharacterClassData classData = CharacterClassData.getByName(sheet.getCharacterClass());
        return calculateMaxHP(sheet, classData);
    }
    
    // ==================== PM (Pontos de Mana) ====================
    
    /**
     * Calcula os PM máximos do personagem.
     * Fórmula básica: PM/Nível * Nível
     * 
     * Nota: Algumas classes/talentos adicionam atributo ao PM.
     * 
     * @param sheet Ficha do personagem
     * @param classData Dados da classe
     * @return PM máximo calculado
     */
    public static int calculateMaxMP(CharacterSheet sheet, CharacterClassData classData) {
        if (sheet == null || classData == null) return 0;
        return classData.mpPerLevel() * sheet.getLevel();
    }
    
    /**
     * Calcula os PM máximos usando o nome da classe da ficha.
     */
    public static int calculateMaxMP(CharacterSheet sheet) {
        if (sheet == null) return 0;
        CharacterClassData classData = CharacterClassData.getByName(sheet.getCharacterClass());
        return calculateMaxMP(sheet, classData);
    }
    
    // ==================== Defesa (CA) ====================
    
    /**
     * Calcula a Defesa (Classe de Armadura) do personagem.
     * Fórmula T20 JdA: 10 + 1/2 Nível + Des (se permitido) + Armadura + Escudo + Tamanho + Outros
     * 
     * @param sheet Ficha do personagem
     * @return Defesa calculada
     */
    public static int calculateDefense(CharacterSheet sheet) {
        if (sheet == null) return 10;
        
        int base = 10;
        int halfLevel = sheet.getLevel() / 2;
        int dex = sheet.getAttributes() != null ? sheet.getAttributes().getDexterity() : 0;
        
        CombatStats combat = sheet.getCombatStats();
        if (combat == null) {
            return base + halfLevel + dex;
        }
        
        // Verificar se armadura permite Destreza (armaduras pesadas não permitem)
        int dexBonus = isHeavyArmor(combat.getArmorEquipped()) ? 0 : dex;
        
        int armorBonus = combat.getArmorBonus();
        int shieldBonus = combat.getShieldBonus();
        int naturalArmor = combat.getNaturalArmorBonus();
        int sizeModifier = combat.getSizeModifier();
        
        return base + halfLevel + dexBonus + armorBonus + shieldBonus + naturalArmor + sizeModifier;
    }
    
    // ==================== Ataque ====================
    
    /**
     * Calcula o bônus de ataque corpo a corpo.
     * Fórmula T20 JdA: 1/2 Nível + FOR + Perícia(Luta) + Bônus
     * 
     * @param sheet Ficha do personagem
     * @return Bônus de ataque corpo a corpo
     */
    public static int calculateMeleeAttackBonus(CharacterSheet sheet) {
        return calculateAttackBonus(sheet, true);
    }
    
    /**
     * Calcula o bônus de ataque à distância.
     * Fórmula T20 JdA: 1/2 Nível + DES + Perícia(Pontaria) + Bônus
     * 
     * @param sheet Ficha do personagem
     * @return Bônus de ataque à distância
     */
    public static int calculateRangedAttackBonus(CharacterSheet sheet) {
        return calculateAttackBonus(sheet, false);
    }
    
    /**
     * Calcula o bônus de ataque genérico.
     * 
     * @param sheet Ficha do personagem
     * @param isMelee true para corpo a corpo (FOR + Luta), false para distância (DES + Pontaria)
     * @return Bônus de ataque calculado
     */
    public static int calculateAttackBonus(CharacterSheet sheet, boolean isMelee) {
        if (sheet == null) return 0;
        
        int halfLevel = sheet.getLevel() / 2;
        Attributes attrs = sheet.getAttributes();
        if (attrs == null) return halfLevel;
        
        int attrMod = isMelee ? attrs.getStrength() : attrs.getDexterity();
        
        // Adicionar bônus de perícia (Luta ou Pontaria) se treinado
        SkillType skillType = isMelee ? SkillType.LUTA : SkillType.PONTARIA;
        int skillBonus = getTrainedSkillBonus(sheet, skillType);
        
        return halfLevel + attrMod + skillBonus;
    }
    
    // ==================== CD de Magias ====================
    
    /**
     * Calcula a Classe de Dificuldade das magias.
     * Fórmula T20 JdA: 10 + 1/2 Nível + Atributo Chave
     * 
     * @param sheet Ficha do personagem
     * @param keyAttributeValue Valor do atributo chave de conjuração
     * @return CD das magias
     */
    public static int calculateSpellDC(CharacterSheet sheet, int keyAttributeValue) {
        if (sheet == null) return 10;
        return 10 + (sheet.getLevel() / 2) + keyAttributeValue;
    }
    
    /**
     * Calcula a CD usando o atributo chave da classe.
     */
    public static int calculateSpellDC(CharacterSheet sheet) {
        if (sheet == null) return 10;
        
        CharacterClassData classData = CharacterClassData.getByName(sheet.getCharacterClass());
        int keyAttr = getKeyAttributeValue(sheet, classData.keyAttribute());
        return calculateSpellDC(sheet, keyAttr);
    }
    
    // ==================== Validação de Atributos ====================
    
    /**
     * Valida se a distribuição de pontos de atributo está correta (Point Buy).
     * Sistema T20 JdA: 10 pontos, custos variáveis conforme tabela.
     * 
     * Custos:
     * -1 = ganha 1 ponto
     *  0 = 0 pontos
     * +1 = 1 ponto
     * +2 = 2 pontos
     * +3 = 4 pontos
     * +4 = 7 pontos
     * +5+ = inválido (apenas via raça/progressão)
     * 
     * @param attrs Atributos a validar
     * @return Resultado da validação com mensagem
     */
    public static ValidationResult validatePointBuy(Attributes attrs) {
        if (attrs == null) {
            return new ValidationResult(false, "Atributos não definidos");
        }
        
        int[] values = {
            attrs.getStrength(), 
            attrs.getDexterity(), 
            attrs.getConstitution(),
            attrs.getIntelligence(), 
            attrs.getWisdom(), 
            attrs.getCharisma()
        };
        
        int totalCost = 0;
        int negativeCount = 0;
        
        for (int attr : values) {
            if (attr >= 5) {
                return new ValidationResult(false, 
                    "Atributo +5 ou maior não é permitido na compra de pontos (use raça/progressão)");
            }
            if (attr < -2) {
                return new ValidationResult(false, 
                    "Atributo menor que -2 não é permitido");
            }
            if (attr == -1) {
                negativeCount++;
            }
            totalCost += getPointCost(attr);
        }
        
        // Regra: apenas um atributo pode ser -1 para ganhar pontos
        if (negativeCount > 1) {
            return new ValidationResult(false, 
                "Apenas um atributo pode ser reduzido para -1 na compra de pontos");
        }
        
        if (totalCost > 10) {
            return new ValidationResult(false, 
                "Pontos gastos (" + totalCost + ") excedem o limite de 10");
        }
        
        int remaining = 10 - totalCost;
        return new ValidationResult(true, 
            "Atributos válidos. Pontos gastos: " + totalCost + ", restantes: " + remaining);
    }
    
    /**
     * Calcula o custo de um valor de atributo no sistema Point Buy.
     */
    public static int getPointCost(int attributeValue) {
        return switch (attributeValue) {
            case -2 -> 0;   // Penalidade racial, não ganha/gasta pontos
            case -1 -> -1;  // Ganha 1 ponto
            case 0 -> 0;    // Estado inicial
            case 1 -> 1;    // Custo linear
            case 2 -> 2;    // Custo linear
            case 3 -> 4;    // Salto de custo
            case 4 -> 7;    // Investimento máximo
            default -> attributeValue >= 5 ? 999 : 0; // Inválido
        };
    }
    
    // ==================== Bônus de Treinamento ====================
    
    /**
     * Calcula o bônus de treinamento baseado no patamar do personagem.
     * T20 JdA:
     * - Níveis 1-6: +2
     * - Níveis 7-14: +4
     * - Níveis 15+: +6
     * 
     * @param level Nível do personagem
     * @return Bônus de treinamento
     */
    public static int getTrainingBonus(int level) {
        if (level >= 15) return 6;
        if (level >= 7) return 4;
        return 2;
    }
    
    // ==================== Métodos Auxiliares ====================
    
    /**
     * Verifica se uma armadura é pesada (não permite bônus de Destreza).
     */
    public static boolean isHeavyArmor(String armorName) {
        if (armorName == null || armorName.isEmpty()) return false;
        
        String lower = armorName.toLowerCase();
        return lower.contains("brunea") ||
               lower.contains("cota de malha") ||
               lower.contains("meia-armadura") ||
               lower.contains("armadura completa") ||
               lower.contains("pesada");
    }
    
    /**
     * Obtém o bônus de perícia treinada.
     */
    private static int getTrainedSkillBonus(CharacterSheet sheet, SkillType skillType) {
        if (sheet.getSkills() == null) return 0;
        
        for (Skill skill : sheet.getSkills()) {
            if (skill.getType() == skillType && skill.isTrained()) {
                return getTrainingBonus(sheet.getLevel());
            }
        }
        return 0;
    }
    
    /**
     * Obtém o valor do atributo chave baseado no código.
     */
    private static int getKeyAttributeValue(CharacterSheet sheet, String attrCode) {
        if (sheet.getAttributes() == null) return 0;
        
        Attributes attrs = sheet.getAttributes();
        return switch (attrCode) {
            case "FOR" -> attrs.getStrength();
            case "DES" -> attrs.getDexterity();
            case "CON" -> attrs.getConstitution();
            case "INT" -> attrs.getIntelligence();
            case "SAB" -> attrs.getWisdom();
            case "CAR" -> attrs.getCharisma();
            default -> 0;
        };
    }
    
    // ==================== Capacidade de Carga ====================
    
    /**
     * Calcula a capacidade de carga sem penalidade.
     * Fórmula T20 JdA: 3 * FOR (em kg)
     */
    public static double calculateLightLoad(int strength) {
        return 3.0 * strength;
    }
    
    /**
     * Calcula a capacidade de carga máxima (com penalidade).
     * Fórmula T20 JdA: 10 * FOR (em kg)
     */
    public static double calculateHeavyLoad(int strength) {
        return 10.0 * strength;
    }
    
    /**
     * Verifica se o personagem está sobrecarregado.
     */
    public static boolean isOverloaded(CharacterSheet sheet) {
        if (sheet == null || sheet.getInventory() == null || sheet.getAttributes() == null) {
            return false;
        }
        
        double lightLoad = calculateLightLoad(sheet.getAttributes().getStrength());
        return sheet.getInventory().getLoadCurrent() > lightLoad;
    }
}
