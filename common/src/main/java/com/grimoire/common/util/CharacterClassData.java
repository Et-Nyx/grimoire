package com.grimoire.common.util;

import java.util.Map;
import java.util.HashMap;

/**
 * Dados das classes de personagem do T20 JdA.
 * Contém informações sobre PV base, PV por nível, PM por nível e atributo chave.
 */
public record CharacterClassData(
    String name,
    int baseHP,
    int hpPerLevel,
    int mpPerLevel,
    String keyAttribute
) {
    private static final Map<String, CharacterClassData> CLASS_DATA = new HashMap<>();
    
    static {
        // Tabela de classes conforme Guia Tormenta 20 JdA (Seção 3.1)
        CLASS_DATA.put("Arcanista", new CharacterClassData("Arcanista", 8, 2, 6, "INT"));
        CLASS_DATA.put("Bárbaro", new CharacterClassData("Bárbaro", 24, 6, 3, "FOR"));
        CLASS_DATA.put("Bardo", new CharacterClassData("Bardo", 12, 3, 4, "CAR"));
        CLASS_DATA.put("Bucaneiro", new CharacterClassData("Bucaneiro", 16, 4, 3, "DES"));
        CLASS_DATA.put("Caçador", new CharacterClassData("Caçador", 16, 4, 4, "SAB"));
        CLASS_DATA.put("Cavaleiro", new CharacterClassData("Cavaleiro", 20, 5, 3, "FOR"));
        CLASS_DATA.put("Clérigo", new CharacterClassData("Clérigo", 16, 4, 5, "SAB"));
        CLASS_DATA.put("Druida", new CharacterClassData("Druida", 16, 4, 4, "SAB"));
        CLASS_DATA.put("Guerreiro", new CharacterClassData("Guerreiro", 20, 5, 3, "FOR"));
        CLASS_DATA.put("Inventor", new CharacterClassData("Inventor", 12, 3, 4, "INT"));
        CLASS_DATA.put("Ladino", new CharacterClassData("Ladino", 12, 3, 4, "DES"));
        CLASS_DATA.put("Lutador", new CharacterClassData("Lutador", 20, 5, 3, "FOR"));
        CLASS_DATA.put("Nobre", new CharacterClassData("Nobre", 16, 4, 4, "CAR"));
        CLASS_DATA.put("Paladino", new CharacterClassData("Paladino", 20, 5, 3, "CAR"));
    }
    
    /**
     * Obtém os dados de uma classe pelo nome.
     * @param className Nome da classe (case-sensitive)
     * @return Dados da classe ou dados padrão se não encontrada
     */
    public static CharacterClassData getByName(String className) {
        if (className == null || className.isEmpty()) {
            return new CharacterClassData("Desconhecida", 10, 3, 3, "INT");
        }
        return CLASS_DATA.getOrDefault(className, 
            new CharacterClassData("Desconhecida", 10, 3, 3, "INT"));
    }
    
    /**
     * Verifica se uma classe é conhecida no sistema.
     */
    public static boolean isKnownClass(String className) {
        return CLASS_DATA.containsKey(className);
    }
    
    /**
     * Retorna todas as classes disponíveis.
     */
    public static java.util.Collection<CharacterClassData> getAllClasses() {
        return CLASS_DATA.values();
    }
}
