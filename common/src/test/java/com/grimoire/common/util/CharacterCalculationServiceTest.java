package com.grimoire.common.util;

import com.grimoire.common.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CharacterCalculationService.
 * Valida todas as fórmulas conforme as regras do T20 JdA.
 */
class CharacterCalculationServiceTest {

    private CharacterSheet sheet;
    private Attributes attributes;

    @BeforeEach
    void setUp() {
        attributes = Attributes.builder()
                .strength(2)
                .dexterity(3)
                .constitution(1)
                .intelligence(0)
                .wisdom(-1)
                .charisma(2)
                .build();

        sheet = CharacterSheet.builder()
                .name("Herói Teste")
                .characterClass("Guerreiro")
                .race("Humano")
                .level(1)
                .attributes(attributes)
                .combatStats(CombatStats.builder().build())
                .skills(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("Cálculo de PV Máximo")
    class CalculateMaxHPTests {

        @Test
        @DisplayName("Guerreiro Nível 1 com CON +1 deve ter 21 PV")
        void guerreiroNivel1() {
            // Guerreiro: Base 20, +5/nível, CON +1
            // Nível 1: 20 + 1 = 21
            sheet.setCharacterClass("Guerreiro");
            sheet.setLevel(1);
            sheet.getAttributes().setConstitution(1);

            int hp = CharacterCalculationService.calculateMaxHP(sheet);

            assertEquals(21, hp, "Guerreiro N1 com CON+1: 20 + 1 = 21");
        }

        @Test
        @DisplayName("Guerreiro Nível 5 com CON +2 deve ter 50 PV")
        void guerreiroNivel5() {
            // Guerreiro: Base 20, +5/nível, CON +2
            // Nível 5: 20 + 2 + (4 * (5 + 2)) = 22 + 28 = 50
            sheet.setCharacterClass("Guerreiro");
            sheet.setLevel(5);
            sheet.getAttributes().setConstitution(2);

            int hp = CharacterCalculationService.calculateMaxHP(sheet);

            assertEquals(50, hp, "Guerreiro N5 com CON+2: 20 + 2 + (4 * 7) = 50");
        }

        @Test
        @DisplayName("Arcanista Nível 1 com CON +0 deve ter 8 PV")
        void arcanistaNivel1() {
            // Arcanista: Base 8, +2/nível
            sheet.setCharacterClass("Arcanista");
            sheet.setLevel(1);
            sheet.getAttributes().setConstitution(0);

            int hp = CharacterCalculationService.calculateMaxHP(sheet);

            assertEquals(8, hp, "Arcanista N1 com CON+0: 8 + 0 = 8");
        }

        @Test
        @DisplayName("Arcanista Nível 5 com CON +1 deve ter 21 PV")
        void arcanistaNivel5() {
            // Arcanista: Base 8, +2/nível, CON +1
            // Nível 5: 8 + 1 + (4 * (2 + 1)) = 9 + 12 = 21
            sheet.setCharacterClass("Arcanista");
            sheet.setLevel(5);
            sheet.getAttributes().setConstitution(1);

            int hp = CharacterCalculationService.calculateMaxHP(sheet);

            assertEquals(21, hp, "Arcanista N5 com CON+1: 8 + 1 + (4 * 3) = 21");
        }

        @Test
        @DisplayName("Classe desconhecida deve usar valores padrão")
        void classeDesconhecida() {
            sheet.setCharacterClass("ClasseInventada");
            sheet.setLevel(1);
            sheet.getAttributes().setConstitution(0);

            int hp = CharacterCalculationService.calculateMaxHP(sheet);

            // Padrão: Base 10, +3/nível
            assertEquals(10, hp, "Classe desconhecida usa padrão: 10 + 0 = 10");
        }
    }

    @Nested
    @DisplayName("Cálculo de PM Máximo")
    class CalculateMaxMPTests {

        @Test
        @DisplayName("Arcanista Nível 1 deve ter 6 PM")
        void arcanistaNivel1() {
            sheet.setCharacterClass("Arcanista");
            sheet.setLevel(1);

            int mp = CharacterCalculationService.calculateMaxMP(sheet);

            assertEquals(6, mp, "Arcanista N1: 6 * 1 = 6 PM");
        }

        @Test
        @DisplayName("Arcanista Nível 5 deve ter 30 PM")
        void arcanistaNivel5() {
            sheet.setCharacterClass("Arcanista");
            sheet.setLevel(5);

            int mp = CharacterCalculationService.calculateMaxMP(sheet);

            assertEquals(30, mp, "Arcanista N5: 6 * 5 = 30 PM");
        }

        @Test
        @DisplayName("Guerreiro Nível 5 deve ter 15 PM")
        void guerreiroNivel5() {
            sheet.setCharacterClass("Guerreiro");
            sheet.setLevel(5);

            int mp = CharacterCalculationService.calculateMaxMP(sheet);

            assertEquals(15, mp, "Guerreiro N5: 3 * 5 = 15 PM");
        }
    }

    @Nested
    @DisplayName("Cálculo de Defesa")
    class CalculateDefenseTests {

        @Test
        @DisplayName("Defesa base (sem armadura) Nível 1 com DES +3")
        void defesaBasicaNivel1() {
            // Defesa = 10 + 0 (1/2 de 1) + 3 (DES) = 13
            sheet.setLevel(1);
            sheet.getAttributes().setDexterity(3);

            int defense = CharacterCalculationService.calculateDefense(sheet);

            assertEquals(13, defense, "Defesa N1 sem armadura: 10 + 0 + 3 = 13");
        }

        @Test
        @DisplayName("Defesa Nível 5 com DES +2 e armadura leve (+3)")
        void defesaNivel5ComArmaduraLeve() {
            // Defesa = 10 + 2 (1/2 de 5) + 2 (DES) + 3 (Armadura) = 17
            sheet.setLevel(5);
            sheet.getAttributes().setDexterity(2);
            sheet.setCombatStats(CombatStats.builder()
                    .armorBonus(3)
                    .armorEquipped("Couro Batido")
                    .build());

            int defense = CharacterCalculationService.calculateDefense(sheet);

            assertEquals(17, defense, "Defesa N5 com armadura leve: 10 + 2 + 2 + 3 = 17");
        }

        @Test
        @DisplayName("Armadura pesada não aplica DES")
        void armaduraPesadaSemDes() {
            // Defesa = 10 + 2 (1/2 de 5) + 0 (DES ignorada) + 6 (Armadura) = 18
            sheet.setLevel(5);
            sheet.getAttributes().setDexterity(4);
            sheet.setCombatStats(CombatStats.builder()
                    .armorBonus(6)
                    .armorEquipped("Armadura Completa")
                    .build());

            int defense = CharacterCalculationService.calculateDefense(sheet);

            assertEquals(18, defense, "Armadura pesada ignora DES: 10 + 2 + 0 + 6 = 18");
        }

        @Test
        @DisplayName("Defesa com escudo e armadura natural")
        void defesaComEscudoENatural() {
            // Defesa = 10 + 0 + 2 (DES) + 2 (Armadura) + 2 (Escudo) + 1 (Natural) = 17
            sheet.setLevel(1);
            sheet.getAttributes().setDexterity(2);
            sheet.setCombatStats(CombatStats.builder()
                    .armorBonus(2)
                    .armorEquipped("Couro")
                    .shieldBonus(2)
                    .naturalArmorBonus(1)
                    .build());

            int defense = CharacterCalculationService.calculateDefense(sheet);

            assertEquals(17, defense, "Defesa com escudo e natural: 10 + 0 + 2 + 2 + 2 + 1 = 17");
        }
    }

    @Nested
    @DisplayName("Cálculo de Bônus de Ataque")
    class CalculateAttackBonusTests {

        @Test
        @DisplayName("Ataque corpo a corpo Nível 1 com FOR +2")
        void ataqueCorpoACorpoNivel1() {
            sheet.setLevel(1);
            sheet.getAttributes().setStrength(2);

            int attack = CharacterCalculationService.calculateMeleeAttackBonus(sheet);

            assertEquals(2, attack, "Ataque N1 sem treino: 0 + 2 = +2");
        }

        @Test
        @DisplayName("Ataque à distância Nível 5 com DES +3")
        void ataqueDistanciaNivel5() {
            sheet.setLevel(5);
            sheet.getAttributes().setDexterity(3);

            int attack = CharacterCalculationService.calculateRangedAttackBonus(sheet);

            assertEquals(5, attack, "Ataque N5: 2 + 3 = +5");
        }

        @Test
        @DisplayName("Ataque com perícia Luta treinada")
        void ataqueComLutaTreinada() {
            sheet.setLevel(1);
            sheet.getAttributes().setStrength(2);
            
            List<Skill> skills = new ArrayList<>();
            skills.add(Skill.builder()
                    .type(SkillType.LUTA)
                    .isTrained(true)
                    .build());
            sheet.setSkills(skills);

            int attack = CharacterCalculationService.calculateMeleeAttackBonus(sheet);

            // 0 (1/2 N1) + 2 (FOR) + 2 (Treino N1-6) = 4
            assertEquals(4, attack, "Ataque N1 com Luta treinada: 0 + 2 + 2 = +4");
        }
    }

    @Nested
    @DisplayName("Cálculo de CD de Magias")
    class CalculateSpellDCTests {

        @Test
        @DisplayName("Arcanista Nível 1 com INT +3 tem CD 13")
        void cdArcanistaNivel1() {
            sheet.setCharacterClass("Arcanista");
            sheet.setLevel(1);
            sheet.getAttributes().setIntelligence(3);

            int dc = CharacterCalculationService.calculateSpellDC(sheet);

            assertEquals(13, dc, "CD N1: 10 + 0 + 3 = 13");
        }

        @Test
        @DisplayName("Clérigo Nível 7 com SAB +4 tem CD 17")
        void cdClerigoNivel7() {
            sheet.setCharacterClass("Clérigo");
            sheet.setLevel(7);
            sheet.getAttributes().setWisdom(4);

            int dc = CharacterCalculationService.calculateSpellDC(sheet);

            assertEquals(17, dc, "CD N7: 10 + 3 + 4 = 17");
        }
    }

    @Nested
    @DisplayName("Validação Point Buy")
    class ValidatePointBuyTests {

        @Test
        @DisplayName("Distribuição válida com 10 pontos")
        void distribuicaoValida() {
            // FOR +2 (2), DES +2 (2), CON +1 (1), INT +1 (1), SAB +1 (1), CAR +3 (4)
            // Total: 2 + 2 + 1 + 1 + 1 + 4 = 11 - espera, isso excede
            // Vamos usar: FOR +2 (2), DES +2 (2), CON +1 (1), INT +1 (1), SAB +0 (0), CAR +3 (4)
            // Total: 2 + 2 + 1 + 1 + 0 + 4 = 10
            Attributes attrs = Attributes.builder()
                    .strength(2)
                    .dexterity(2)
                    .constitution(1)
                    .intelligence(1)
                    .wisdom(0)
                    .charisma(3)
                    .build();

            ValidationResult result = CharacterCalculationService.validatePointBuy(attrs);

            assertTrue(result.isValid(), "Distribuição com 10 pontos deve ser válida");
            assertTrue(result.message().contains("restantes: 0"), "Mensagem deve indicar 0 pontos restantes");
        }

        @Test
        @DisplayName("Distribuição inválida - excede 10 pontos")
        void distribuicaoExcede() {
            // FOR +4 (7), DES +3 (4), CON +0 (0), INT +0 (0), SAB +0 (0), CAR +0 (0)
            // Total: 7 + 4 = 11
            Attributes attrs = Attributes.builder()
                    .strength(4)
                    .dexterity(3)
                    .constitution(0)
                    .intelligence(0)
                    .wisdom(0)
                    .charisma(0)
                    .build();

            ValidationResult result = CharacterCalculationService.validatePointBuy(attrs);

            assertFalse(result.isValid(), "Distribuição que excede 10 pontos deve ser inválida");
        }

        @Test
        @DisplayName("Atributo +5 ou maior é inválido")
        void atributo5OuMaior() {
            Attributes attrs = Attributes.builder()
                    .strength(5)
                    .dexterity(0)
                    .constitution(0)
                    .intelligence(0)
                    .wisdom(0)
                    .charisma(0)
                    .build();

            ValidationResult result = CharacterCalculationService.validatePointBuy(attrs);

            assertFalse(result.isValid(), "+5 não é permitido em point buy");
            assertTrue(result.message().contains("+5"), "Mensagem deve mencionar +5");
        }

        @Test
        @DisplayName("Apenas um atributo pode ser -1")
        void apenasUmMenosUm() {
            Attributes attrs = Attributes.builder()
                    .strength(-1)
                    .dexterity(-1)
                    .constitution(0)
                    .intelligence(0)
                    .wisdom(0)
                    .charisma(0)
                    .build();

            ValidationResult result = CharacterCalculationService.validatePointBuy(attrs);

            assertFalse(result.isValid(), "Dois atributos -1 não são permitidos");
        }

        @Test
        @DisplayName("Usar -1 para ganhar ponto extra")
        void usarMenosUmParaGanharPonto() {
            // FOR +4 (7), DES +0 (0), CON +0 (0), INT +0 (0), SAB -1 (-1), CAR +3 (4)
            // Total: 7 + 0 + 0 + 0 - 1 + 4 = 10
            Attributes attrs = Attributes.builder()
                    .strength(4)
                    .dexterity(0)
                    .constitution(0)
                    .intelligence(0)
                    .wisdom(-1)
                    .charisma(3)
                    .build();

            ValidationResult result = CharacterCalculationService.validatePointBuy(attrs);

            assertTrue(result.isValid(), "Usar -1 para compensar deve funcionar");
        }
    }

    @Nested
    @DisplayName("Bônus de Treinamento por Patamar")
    class TrainingBonusTests {

        @Test
        @DisplayName("Níveis 1-6 dão +2")
        void patamar1() {
            assertEquals(2, CharacterCalculationService.getTrainingBonus(1));
            assertEquals(2, CharacterCalculationService.getTrainingBonus(6));
        }

        @Test
        @DisplayName("Níveis 7-14 dão +4")
        void patamar2() {
            assertEquals(4, CharacterCalculationService.getTrainingBonus(7));
            assertEquals(4, CharacterCalculationService.getTrainingBonus(14));
        }

        @Test
        @DisplayName("Níveis 15+ dão +6")
        void patamar3() {
            assertEquals(6, CharacterCalculationService.getTrainingBonus(15));
            assertEquals(6, CharacterCalculationService.getTrainingBonus(20));
        }
    }

    @Nested
    @DisplayName("Verificação de Armadura Pesada")
    class HeavyArmorTests {

        @Test
        @DisplayName("Armadura Completa é pesada")
        void armaduraCompletaEPesada() {
            assertTrue(CharacterCalculationService.isHeavyArmor("Armadura Completa"));
        }

        @Test
        @DisplayName("Brunea é pesada")
        void bruneaEPesada() {
            assertTrue(CharacterCalculationService.isHeavyArmor("Brunea"));
        }

        @Test
        @DisplayName("Cota de Malha é pesada")
        void cotaDeMalhaEPesada() {
            assertTrue(CharacterCalculationService.isHeavyArmor("Cota de Malha"));
        }

        @Test
        @DisplayName("Couro não é pesada")
        void couroNaoEPesada() {
            assertFalse(CharacterCalculationService.isHeavyArmor("Couro"));
        }

        @Test
        @DisplayName("Couro Batido não é pesada")
        void couroBatidoNaoEPesado() {
            assertFalse(CharacterCalculationService.isHeavyArmor("Couro Batido"));
        }

        @Test
        @DisplayName("Null ou vazio não é pesada")
        void nullOuVazioNaoEPesada() {
            assertFalse(CharacterCalculationService.isHeavyArmor(null));
            assertFalse(CharacterCalculationService.isHeavyArmor(""));
        }
    }
}
