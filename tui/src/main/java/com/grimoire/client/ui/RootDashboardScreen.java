package com.grimoire.client.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.grimoire.client.service.ApiClient;
import com.grimoire.common.model.Campaign;

import java.util.List;

public class RootDashboardScreen {

    private final Screen screen;
    private final ApiClient apiClient;
    private int selectedOption = 0;
    private List<Campaign> myCampaigns;

    public RootDashboardScreen(Screen screen, ApiClient apiClient) {
        this.screen = screen;
        this.apiClient = apiClient;
    }

    public void show() throws Exception {
        boolean running = true;
        
        while (running) {
            // Refresh campaigns list
            myCampaigns = apiClient.listMyCampaigns();
            
            screen.clear();
            draw();
            screen.refresh();
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedOption = Math.max(0, selectedOption - 1);
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                int maxOptions = myCampaigns.size() + 3; // Campaigns + Create + Join + Logout
                selectedOption = Math.min(maxOptions - 1, selectedOption + 1);
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                running = handleSelection();
            }
        }
    }

    private void draw() {
        TextGraphics graphics = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();
        
        String title = "=== PAINEL PRINCIPAL: " + apiClient.getCurrentUser().getUsername() + " ===";
        int titleX = (size.getColumns() - title.length()) / 2;
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(titleX, 2, title);
        
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(2, 4, "Suas Campanhas:");
        
        int y = 5;
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        
        // List Campaigns
        for (int i = 0; i < myCampaigns.size(); i++) {
            String prefix = (i == selectedOption) ? "> " : "  ";
            graphics.putString(4, y + i, prefix + myCampaigns.get(i).getName());
            if (i == selectedOption) graphics.setForegroundColor(TextColor.ANSI.WHITE);
        }
        
        y += Math.max(1, myCampaigns.size()) + 1;
        
        // Menu Options
        String[] options = {"Criar Nova Campanha", "Entrar em Campanha", "Sair (Logout)"};
        for (int i = 0; i < options.length; i++) {
            int optionIndex = myCampaigns.size() + i;
            String prefix = (optionIndex == selectedOption) ? "> " : "  ";
            graphics.putString(4, y + i, prefix + options[i]);
        }
    }

    private boolean handleSelection() throws Exception {
        if (selectedOption < myCampaigns.size()) {
            // Open Campaign Dashboard
            Campaign selected = myCampaigns.get(selectedOption);
            new CampaignDashboardScreen(screen, apiClient, selected).show();
            return true;
        }
        
        int menuIndex = selectedOption - myCampaigns.size();
        switch (menuIndex) {
            case 0: // Create
                createCampaign();
                return true;
            case 1: // Join
                joinCampaign();
                return true;
            case 2: // Logout
                return false;
        }
        return true;
    }

    private void createCampaign() throws Exception {
        String name = promptInput("Nome da Campanha:", 10);
        if (name == null) return;
        
        String desc = promptInput("Descrição:", 12);
        
        Campaign c = apiClient.createCampaign(name, desc);
        if (c != null) {
            showMessage("Campanha criada!", TextColor.ANSI.GREEN);
        } else {
            showMessage("Erro ao criar campanha.", TextColor.ANSI.RED);
        }
    }

    private void joinCampaign() throws Exception {
        String id = promptInput("ID da Campanha:", 10);
        if (id == null) return;
        
        Campaign c = apiClient.joinCampaign(id);
        if (c != null) {
            showMessage("Você entrou na campanha: " + c.getName(), TextColor.ANSI.GREEN);
        } else {
            showMessage("Erro ao entrar. ID inválido?", TextColor.ANSI.RED);
        }
    }

    // Helper methods (duplicated from LoginScreen, could be refactored to a base class or utility)
    private String promptInput(String prompt, int y) throws Exception {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        graphics.putString(2, y, prompt);
        screen.refresh();
        
        StringBuilder input = new StringBuilder();
        int inputY = y + 1;
        
        while (true) {
            graphics = screen.newTextGraphics();
            graphics.putString(2, inputY, " ".repeat(50));
            graphics.putString(2, inputY, input.toString() + "_");
            screen.refresh();
            
            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.Enter) return input.toString();
            if (keyStroke.getKeyType() == KeyType.Escape) return null;
            if (keyStroke.getKeyType() == KeyType.Backspace && input.length() > 0) input.deleteCharAt(input.length() - 1);
            else if (keyStroke.getCharacter() != null) input.append(keyStroke.getCharacter());
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
