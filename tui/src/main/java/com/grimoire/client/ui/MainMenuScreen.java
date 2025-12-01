package com.grimoire.client.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.grimoire.client.service.ApiClient;

public class MainMenuScreen {
    
    private final Screen screen;
    private final ApiClient apiClient;
    private int selectedOption = 0;
    private final String[] menuOptions = {
        "1. Visualizar Ficha",
        "2. Criar Nova Ficha",
        "3. Sair"
    };
    
    public MainMenuScreen(Screen screen, ApiClient apiClient) {
        this.screen = screen;
        this.apiClient = apiClient;
    }
    
    public void show() throws Exception {
        boolean running = true;
        
        while (running) {
            screen.clear();
            draw();
            screen.refresh();
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedOption = Math.max(0, selectedOption - 1);
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                selectedOption = Math.min(menuOptions.length - 1, selectedOption + 1);
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                running = handleSelection();
            }
        }
    }
    
    private void draw() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();
        
        // Title
        String title = "=== GRIMOIRE - RPG MANAGER ===";
        int titleX = (size.getColumns() - title.length()) / 2;
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(titleX, 2, title);
        
        // Menu options
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN);
                graphics.putString(10, 5 + i, "> " + menuOptions[i]);
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
            } else {
                graphics.putString(10, 5 + i, "  " + menuOptions[i]);
            }
        }
        
        // Instructions
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(2, size.getRows() - 2, "Use ↑/↓ para navegar, Enter para selecionar");
    }
    
    private boolean handleSelection() throws Exception {
        switch (selectedOption) {
            case 0: // Visualizar Ficha
                new CharacterSheetViewScreen(screen, apiClient).show();
                return true;
            case 1: // Criar Nova Ficha
                new CharacterSheetEditorScreen(screen, apiClient, null).show();
                return true;
            case 2: // Sair
                return false;
            default:
                return true;
        }
    }
}
