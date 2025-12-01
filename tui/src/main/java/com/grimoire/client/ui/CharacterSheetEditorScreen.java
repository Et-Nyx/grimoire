package com.grimoire.client.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.grimoire.client.service.ApiClient;
import com.grimoire.common.model.Attributes;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.model.Status;

import java.util.UUID;

public class CharacterSheetEditorScreen {
    
    private final Screen screen;
    private final ApiClient apiClient;
    private CharacterSheet sheet;
    
    public CharacterSheetEditorScreen(Screen screen, ApiClient apiClient, CharacterSheet existingSheet) {
        this.screen = screen;
        this.apiClient = apiClient;
        this.sheet = existingSheet != null ? existingSheet : new CharacterSheet();
        
        if (existingSheet == null) {
            sheet.setId(UUID.randomUUID().toString());
            sheet.setAttributes(new Attributes());
            sheet.setStatus(new Status());
        }
    }
    
    public void show() throws Exception {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        
        // Collect basic info
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(2, 2, "=== CRIAR NOVA FICHA ===");
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        
        sheet.setName(promptInput("Nome do Personagem:", 4));
        if (sheet.getName() == null) return;
        
        sheet.setPlayerName(promptInput("Nome do Jogador:", 5));
        if (sheet.getPlayerName() == null) return;
        
        sheet.setSystem(promptInput("Sistema (ex: Tormenta20):", 6));
        if (sheet.getSystem() == null) return;
        
        sheet.setRace(promptInput("Raça:", 7));
        if (sheet.getRace() == null) return;
        
        sheet.setCharacterClass(promptInput("Classe:", 8));
        if (sheet.getCharacterClass() == null) return;
        
        // Save
        CharacterSheet saved = apiClient.saveSheet(sheet);
        
        screen.clear();
        graphics = screen.newTextGraphics();
        if (saved != null) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
            graphics.putString(2, 2, "Ficha criada com sucesso!");
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(2, 3, "ID: " + saved.getId());
        } else {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(2, 2, "Erro ao criar ficha!");
        }
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(2, 5, "Pressione qualquer tecla para voltar...");
        screen.refresh();
        screen.readInput();
    }
    
    private String promptInput(String prompt, int y) throws Exception {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(2, 2, "=== CRIAR NOVA FICHA ===");
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(2, y, prompt);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(2, y + 2, "(ESC para cancelar)");
        screen.refresh();
        
        StringBuilder input = new StringBuilder();
        int inputY = y + 1;
        
        while (true) {
            graphics = screen.newTextGraphics();
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(2, inputY, " ".repeat(50)); // Clear
            graphics.putString(2, inputY, input.toString() + "_");
            screen.refresh();
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke.getKeyType() == KeyType.Enter) {
                return input.toString();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                return null;
            } else if (keyStroke.getKeyType() == KeyType.Backspace && input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
            } else if (keyStroke.getCharacter() != null) {
                input.append(keyStroke.getCharacter());
            }
        }
    }
}
