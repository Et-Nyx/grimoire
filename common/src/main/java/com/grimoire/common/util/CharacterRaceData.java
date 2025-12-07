package com.grimoire.common.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Dados das raças de personagem do T20 JdA.
 * Contém modificadores de atributos e traços raciais.
 */
public record CharacterRaceData(
    String name,
    int strengthMod,
    int dexterityMod,
    int constitutionMod,
    int intelligenceMod,
    int wisdomMod,
    int charismaMod,
    int baseSpeed,          // Deslocamento base em metros
    String size,            // Médio, Pequeno, Grande
    List<String> traits     // Traços raciais
) {
    private static final Map<String, CharacterRaceData> RACE_DATA = new HashMap<>();
    
    static {
        // Tabela de raças conforme Guia Tormenta 20 JdA (Seção 2)
        
        // Humano: +1 em três atributos à escolha (representado como 0,0,0,0,0,0 - aplicar via UI)
        RACE_DATA.put("Humano", new CharacterRaceData(
            "Humano", 0, 0, 0, 0, 0, 0, 9, "Médio",
            List.of("Versátil: +1 em três atributos à escolha", 
                    "Talentoso: 2 perícias treinadas OU 1 perícia + 1 poder geral")
        ));
        
        // Anão: CON +2, SAB +1, DES -1
        RACE_DATA.put("Anão", new CharacterRaceData(
            "Anão", 0, -1, 2, 0, 1, 0, 6, "Médio",
            List.of("Conhecimento das Rochas: +2 Percepção/Sobrevivência subterrâneo",
                    "Devagar e Sempre: Deslocamento não reduzido por armadura",
                    "Duro como Pedra: +3 PV nível 1, +1 PV/nível",
                    "Tradição de Heredrimm: Proficiência machados/martelos/picaretas")
        ));
        
        // Elfo: INT +2, DES +1, CON -1
        RACE_DATA.put("Elfo", new CharacterRaceData(
            "Elfo", 0, 1, -1, 2, 0, 0, 12, "Médio",
            List.of("Graça de Glórienn: Deslocamento 12m",
                    "Sangue Mágico: +1 PM/nível",
                    "Sentidos Élficos: Visão na penumbra, +2 Misticismo e Percepção")
        ));
        
        // Goblin: DES +2, INT +1, CAR -1
        RACE_DATA.put("Goblin", new CharacterRaceData(
            "Goblin", 0, 2, 0, 1, 0, -1, 9, "Pequeno",
            List.of("Engenhoso: Sem penalidade em Ofício sem ferramentas",
                    "Espelunqueiro: Deslocamento escalada, visão no escuro",
                    "Peste Esguia: Tamanho Pequeno (+1 Def, +1 Atk)",
                    "Rato das Ruas: +2 Fortitude, recuperação mínima = nível")
        ));
        
        // Minotauro: FOR +2, CON +1, SAB -1
        RACE_DATA.put("Minotauro", new CharacterRaceData(
            "Minotauro", 2, 0, 1, 0, -1, 0, 9, "Médio",
            List.of("Chifres: Ataque natural 1d6, ataque extra por 1 PM",
                    "Couro Rígido: +1 Defesa (armadura natural)",
                    "Faro: Detecta invisíveis em 9m",
                    "Medo de Altura: Abalado perto de quedas >3m")
        ));
        
        // Qareen: CAR +2, INT +1, SAB -1
        RACE_DATA.put("Qareen", new CharacterRaceData(
            "Qareen", 0, 0, 0, 1, -1, 2, 9, "Médio",
            List.of("Desejos: Conjura magia 1º círculo grátis para aliado",
                    "Resistência Elemental: RD 10 contra um elemento à escolha")
        ));
        
        // Lefou: +2 em três atributos (exceto CAR)
        RACE_DATA.put("Lefou", new CharacterRaceData(
            "Lefou", 0, 0, 0, 0, 0, 0, 9, "Médio",
            List.of("Cria da Tormenta: +2 em três atributos (exceto Carisma)",
                    "Poderes da Tormenta: Acesso a poderes especiais",
                    "Extroversão Limitada: Penalidade em CAR por poderes Tormenta")
        ));
        
        // Osteon (Esqueleto)
        RACE_DATA.put("Osteon", new CharacterRaceData(
            "Osteon", 0, 0, 0, 0, 0, 0, 9, "Médio",
            List.of("Morto-Vivo: Imune a veneno, doença, sono",
                    "Resistência: RD 5 corte/frio/perfuração",
                    "Memória Póstuma: Recupera poder ou perícia da vida passada",
                    "Recuperação Negativa: Só cura com energia negativa")
        ));
        
        // Dahllan
        RACE_DATA.put("Dahllan", new CharacterRaceData(
            "Dahllan", 0, 0, 0, 0, 2, 1, 9, "Médio",
            List.of("Empatia Selvagem: Comunicação com animais e plantas",
                    "Herança Feérica: +2 em Percepção e Sobrevivência")
        ));
        
        // Golem
        RACE_DATA.put("Golem", new CharacterRaceData(
            "Golem", 2, 0, 1, 0, 0, -1, 6, "Médio",
            List.of("Construto: Imune a veneno, doença, fadiga",
                    "Chassi Resistente: RD 2 contra todos os tipos",
                    "Criação Arcana: Não precisa comer, beber ou respirar")
        ));
        
        // Medusa
        RACE_DATA.put("Medusa", new CharacterRaceData(
            "Medusa", 0, 1, 0, 0, 0, 2, 9, "Médio",
            List.of("Olhar Petrificante: Poder de petrificação",
                    "Cabeleira Serpentina: Ataque natural com serpentes")
        ));
        
        // Sereia/Tritão
        RACE_DATA.put("Sereia", new CharacterRaceData(
            "Sereia", 0, 1, 0, 0, 0, 2, 9, "Médio",
            List.of("Anfíbia: Respira embaixo d'água",
                    "Cauda/Pernas: Pode alternar forma",
                    "Canto Encantador: Habilidade de fascinar")
        ));
        
        // Sílfide
        RACE_DATA.put("Sílfide", new CharacterRaceData(
            "Sílfide", 0, 2, -1, 0, 0, 1, 9, "Pequeno",
            List.of("Asas de Borboleta: Pode planar",
                    "Herança Feérica: +2 em Enganação e Intuição",
                    "Tamanho Pequeno: +1 Defesa e Ataque")
        ));
        
        // Trog
        RACE_DATA.put("Trog", new CharacterRaceData(
            "Trog", 1, 0, 2, -1, 0, 0, 9, "Médio",
            List.of("Mau Cheiro: Inimigos adjacentes ficam enjoados",
                    "Mordida: Ataque natural 1d6",
                    "Reptiliano: Resistência a frio")
        ));
    }
    
    /**
     * Obtém os dados de uma raça pelo nome.
     * @param raceName Nome da raça (case-insensitive)
     * @return Dados da raça ou dados padrão (Humano) se não encontrada
     */
    public static CharacterRaceData getByName(String raceName) {
        if (raceName == null || raceName.isEmpty()) {
            return RACE_DATA.get("Humano");
        }
        
        // Busca case-insensitive
        for (var entry : RACE_DATA.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(raceName)) {
                return entry.getValue();
            }
        }
        
        return new CharacterRaceData(raceName, 0, 0, 0, 0, 0, 0, 9, "Médio", 
            List.of("Raça personalizada"));
    }
    
    /**
     * Verifica se uma raça é conhecida no sistema.
     */
    public static boolean isKnownRace(String raceName) {
        if (raceName == null) return false;
        return RACE_DATA.keySet().stream()
            .anyMatch(k -> k.equalsIgnoreCase(raceName));
    }
    
    /**
     * Retorna todas as raças disponíveis.
     */
    public static java.util.Collection<CharacterRaceData> getAllRaces() {
        return RACE_DATA.values();
    }
    
    /**
     * Retorna os nomes de todas as raças disponíveis.
     */
    public static java.util.Set<String> getAllRaceNames() {
        return RACE_DATA.keySet();
    }
    
    /**
     * Calcula o total de modificadores de atributo da raça.
     * Útil para validação.
     */
    public int getTotalModifiers() {
        return strengthMod + dexterityMod + constitutionMod + 
               intelligenceMod + wisdomMod + charismaMod;
    }
    
    /**
     * Retorna uma descrição formatada dos modificadores.
     */
    public String getModifiersDescription() {
        StringBuilder sb = new StringBuilder();
        if (strengthMod != 0) sb.append("FOR ").append(strengthMod > 0 ? "+" : "").append(strengthMod).append(" ");
        if (dexterityMod != 0) sb.append("DES ").append(dexterityMod > 0 ? "+" : "").append(dexterityMod).append(" ");
        if (constitutionMod != 0) sb.append("CON ").append(constitutionMod > 0 ? "+" : "").append(constitutionMod).append(" ");
        if (intelligenceMod != 0) sb.append("INT ").append(intelligenceMod > 0 ? "+" : "").append(intelligenceMod).append(" ");
        if (wisdomMod != 0) sb.append("SAB ").append(wisdomMod > 0 ? "+" : "").append(wisdomMod).append(" ");
        if (charismaMod != 0) sb.append("CAR ").append(charismaMod > 0 ? "+" : "").append(charismaMod).append(" ");
        return sb.toString().trim().isEmpty() ? "Nenhum modificador fixo" : sb.toString().trim();
    }
}
