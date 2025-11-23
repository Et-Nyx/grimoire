package com.grimoire;

import com.grimoire.common.model.Attributes;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.model.CombatStats;
import com.grimoire.common.model.Status;
import com.grimoire.server.service.JsonPersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersistenceTests {

    @Autowired
    private JsonPersistenceService persistenceService;

    @Test
    void testSaveAndLoadCharacterSheet() throws IOException {
        CharacterSheet sheet = new CharacterSheet();
        sheet.setId("test-char-1");
        sheet.setName("Gideon");
        sheet.setPlayerName("Eduardo");
        sheet.setSystem("Tormenta20");
        sheet.setRace("Humano");
        sheet.setCharacterClass("Guerreiro");
        sheet.setLevel(1);
        sheet.setAttributes(new Attributes(10, 15, 12, 14, 13, 8));
        sheet.setStatus(new Status(20, 20, 5, 5, 0, 1000));
        sheet.setCombatStats(new CombatStats(15, 2, 0, 0, "Couro", "Nenhum"));
        
        persistenceService.saveCharacterSheet(sheet);

        CharacterSheet loaded = persistenceService.loadCharacterSheet("test-char-1").orElseThrow();
        assertEquals("Gideon", loaded.getName());
        assertEquals(15, loaded.getAttributes().getDexterity());
    }
}
