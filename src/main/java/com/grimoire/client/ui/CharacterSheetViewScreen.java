package com.grimoire.client.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.grimoire.client.service.ApiClient;
import com.grimoire.common.model.CharacterSheet;

public class CharacterSheetViewScreen {
    
    private final Screen screen;
    private final ApiClient apiClient;
    
    public CharacterSheetViewScreen(Screen screen, ApiClient apiClient) {
        this.screen = screen;
        this.apiClient = apiClient;
    }
    
    public void show() throws Exception {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        
        // Prompt for sheet ID
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(2, 2, "Digite o ID da ficha:");
        screen.refresh();
        
        String sheetId = readInput();
        
        if (sheetId == null || sheetId.isEmpty()) {
            return;
        }
        
        CharacterSheet sheet = apiClient.getSheet(sheetId);
        
        if (sheet == null) {
            screen.clear();
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(2, 2, "Ficha não encontrada!");
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(2, 4, "Pressione qualquer tecla para voltar...");
            screen.refresh();
            screen.readInput();
            return;
        }
        
        displaySheet(sheet);
    }
    
    private void displaySheet(CharacterSheet sheet) throws Exception {
        boolean running = true;
        
        while (running) {
            screen.clear();
            TextGraphics graphics = screen.newTextGraphics();
            
            int y = 1;
            
            // Header
            graphics.setForegroundColor(TextColor.ANSI.CYAN);
            graphics.putString(2, y++, "═══════════════════════════════════════");
            graphics.putString(2, y++, "  FICHA DE PERSONAGEM - " + sheet.getSystem());
            graphics.putString(2, y++, "═══════════════════════════════════════");
            y++;
            
            // Basic Info
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(2, y++, "Nome: " + sheet.getName());
            graphics.putString(2, y++, "Jogador: " + (sheet.getPlayerName() != null ? sheet.getPlayerName() : "N/A"));
            graphics.putString(2, y++, "Raça: " + (sheet.getRace() != null ? sheet.getRace() : "N/A"));
            graphics.putString(2, y++, "Origem: " + (sheet.getOrigin() != null ? sheet.getOrigin() : "N/A"));
            graphics.putString(2, y++, "Classe: " + (sheet.getCharacterClass() != null ? sheet.getCharacterClass() : "N/A"));
            graphics.putString(2, y++, "Nível: " + sheet.getLevel());
            y++;
            
            // Attributes
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
            graphics.putString(2, y++, "--- ATRIBUTOS ---");
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(2, y++, String.format("FOR: %d  DES: %d  CON: %d", 
                sheet.getAttributes().getStrength(),
                sheet.getAttributes().getDexterity(),
                sheet.getAttributes().getConstitution()));
            graphics.putString(2, y++, String.format("INT: %d  SAB: %d  CAR: %d",
                sheet.getAttributes().getIntelligence(),
                sheet.getAttributes().getWisdom(),
                sheet.getAttributes().getCharisma()));
            y++;
            
            // Status
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(2, y++, "--- STATUS ---");
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(2, y++, String.format("PV: %d/%d  PM: %d/%d",
                sheet.getStatus().getHpCurrent(),
                sheet.getStatus().getHpMax(),
                sheet.getStatus().getMpCurrent(),
                sheet.getStatus().getMpMax()));
            graphics.putString(2, y++, "XP: " + sheet.getStatus().getXp());
            y++;
            
            // Combat
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(2, y++, "--- COMBATE ---");
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(2, y++, "Defesa: " + sheet.getCombatStats().getDefenseTotal());
            graphics.putString(2, y++, "Armadura: " + (sheet.getCombatStats().getArmorEquipped() != null ? 
                sheet.getCombatStats().getArmorEquipped() : "Nenhuma"));
            
            // Footer
            y = screen.getTerminalSize().getRows() - 2;
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(2, y, "Pressione ESC para voltar");
            
            screen.refresh();
            
            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.Escape) {
                running = false;
            }
        }
    }
    
    private String readInput() throws Exception {
        StringBuilder input = new StringBuilder();
        int cursorY = 3;
        
        while (true) {
            TextGraphics graphics = screen.newTextGraphics();
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(2, cursorY, input.toString() + "_");
            screen.refresh();
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke.getKeyType() == KeyType.Enter) {
                return input.toString();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                return null;
            } else if (keyStroke.getKeyType() == KeyType.Backspace && input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
                graphics.putString(2, cursorY, " ".repeat(50)); // Clear line
            } else if (keyStroke.getCharacter() != null) {
                input.append(keyStroke.getCharacter());
            }
        }
    }
}
