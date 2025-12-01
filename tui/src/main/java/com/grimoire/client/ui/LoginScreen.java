package com.grimoire.client.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.grimoire.client.service.ApiClient;
import com.grimoire.common.model.User;

public class LoginScreen {

    private final Screen screen;
    private final ApiClient apiClient;

    public LoginScreen(Screen screen, ApiClient apiClient) {
        this.screen = screen;
        this.apiClient = apiClient;
    }

    public boolean show() throws Exception {
        while (true) {
            screen.clear();
            TextGraphics graphics = screen.newTextGraphics();
            
            String title = "=== GRIMOIRE - LOGIN ===";
            int titleX = (screen.getTerminalSize().getColumns() - title.length()) / 2;
            graphics.setForegroundColor(TextColor.ANSI.CYAN);
            graphics.putString(titleX, 2, title);

            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(10, 5, "1. Login");
            graphics.putString(10, 6, "2. Registrar");
            graphics.putString(10, 7, "3. Sair");

            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(2, screen.getTerminalSize().getRows() - 2, "Selecione uma opção...");
            screen.refresh();

            KeyStroke key = screen.readInput();
            
            if (key.getCharacter() != null) {
                switch (key.getCharacter()) {
                    case '1':
                        if (performLogin()) return true;
                        break;
                    case '2':
                        performRegister();
                        break;
                    case '3':
                        return false;
                }
            }
        }
    }

    private boolean performLogin() throws Exception {
        String username = promptInput("Usuário:", 5);
        if (username == null) return false;
        
        String password = promptInput("Senha:", 7); // In real app, mask this
        if (password == null) return false;

        User user = apiClient.login(username, password);
        if (user != null) {
            showMessage("Login realizado com sucesso!", TextColor.ANSI.GREEN);
            return true;
        } else {
            showMessage("Falha no login. Verifique suas credenciais.", TextColor.ANSI.RED);
            return false;
        }
    }

    private void performRegister() throws Exception {
        String username = promptInput("Novo Usuário:", 5);
        if (username == null) return;
        
        String password = promptInput("Nova Senha:", 7);
        if (password == null) return;

        User user = apiClient.register(username, password);
        if (user != null) {
            showMessage("Usuário registrado! Faça login para continuar.", TextColor.ANSI.GREEN);
        } else {
            showMessage("Erro ao registrar. Usuário já existe?", TextColor.ANSI.RED);
        }
    }

    private String promptInput(String prompt, int y) throws Exception {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(2, 2, "=== AUTENTICAÇÃO ===");
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

    private void showMessage(String message, TextColor color) throws Exception {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        graphics.setForegroundColor(color);
        graphics.putString(2, 5, message);
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(2, 7, "Pressione qualquer tecla...");
        screen.refresh();
        screen.readInput();
    }
}
