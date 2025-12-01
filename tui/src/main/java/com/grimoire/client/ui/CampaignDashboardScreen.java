package com.grimoire.client.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.grimoire.client.service.ApiClient;
import com.grimoire.common.model.Campaign;

public class CampaignDashboardScreen {

    private final Screen screen;
    private final ApiClient apiClient;
    private final Campaign campaign;
    private final boolean isDM;
    private int selectedOption = 0;
    private String[] menuOptions;

    public CampaignDashboardScreen(Screen screen, ApiClient apiClient, Campaign campaign) {
        this.screen = screen;
        this.apiClient = apiClient;
        this.campaign = campaign;
        this.isDM = campaign.getOwnerId().equals(apiClient.getCurrentUser().getId());
        
        if (isDM) {
            menuOptions = new String[]{
                "1. Gerenciar Jogadores (TODO)",
                "2. Ver Todas as Fichas (TODO)",
                "3. Notas da Campanha (TODO)",
                "4. Voltar"
            };
        } else {
            menuOptions = new String[]{
                "1. Minha Ficha",
                "2. Notas da Campanha (TODO)",
                "3. Voltar"
            };
        }
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
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                running = false;
            }
        }
    }

    private void draw() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();
        
        String title = "=== CAMPANHA: " + campaign.getName() + " ===";
        String role = isDM ? "[MESTRE]" : "[JOGADOR]";
        
        int titleX = (size.getColumns() - title.length()) / 2;
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(titleX, 2, title);
        graphics.putString(titleX + title.length() + 2, 2, role);
        
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(2, 4, "ID: " + campaign.getId());
        graphics.putString(2, 5, "Descrição: " + (campaign.getDescription() != null ? campaign.getDescription() : "N/A"));
        
        // Menu
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                graphics.setForegroundColor(TextColor.ANSI.GREEN);
                graphics.putString(10, 8 + i, "> " + menuOptions[i]);
            } else {
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
                graphics.putString(10, 8 + i, "  " + menuOptions[i]);
            }
        }
    }

    private boolean handleSelection() throws Exception {
        if (isDM) {
            switch (selectedOption) {
                case 3: return false; // Voltar
                default: return true;
            }
        } else {
            switch (selectedOption) {
                case 0: // Minha Ficha
                    // TODO: Load specific sheet for this campaign
                    // For now, just open the view screen which loads by ID (we need to link sheet to campaign/user)
                    // Since we don't have that link yet, I'll just open the MainMenuScreen logic or similar.
                    // Actually, let's just open the existing CharacterSheetViewScreen but we need a sheet ID.
                    // We haven't implemented "User has Character in Campaign" link yet.
                    // So I'll just show a placeholder message.
                    showMessage("Funcionalidade em desenvolvimento: Link Personagem-Campanha", TextColor.ANSI.YELLOW);
                    return true;
                case 2: return false; // Voltar
                default: return true;
            }
        }
    }

    private void showMessage(String message, TextColor color) throws Exception {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        graphics.setForegroundColor(color);
        graphics.putString(2, 5, message);
        screen.refresh();
        screen.readInput();
    }
}
