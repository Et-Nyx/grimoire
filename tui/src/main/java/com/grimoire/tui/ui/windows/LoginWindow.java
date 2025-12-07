package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.AuthService;

public class LoginWindow extends StandardWindow {
    
    private final AuthService authService;
    private final TextBox usernameTextBox;
    private final TextBox passwordTextBox;
    
    public LoginWindow(AuthService authService) {
        super("Grimoire - Login");
        this.authService = authService;
        
        // Create main panel with grid layout (2 columns)
        Panel mainPanel = new Panel(new GridLayout(2));
        
        // Username field
        mainPanel.addComponent(new Label("Usuário:"));
        usernameTextBox = new TextBox();
        mainPanel.addComponent(usernameTextBox);
        
        // Password field
        mainPanel.addComponent(new Label("Senha:"));
        passwordTextBox = new TextBox();
        passwordTextBox.setMask('*');
        mainPanel.addComponent(passwordTextBox);
        
        // Buttons panel
        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        
        Button loginButton = new Button("Entrar", this::onLoginClick);
        Button registerButton = new Button("Criar Conta", this::onRegisterClick);
        Button exitButton = new Button("Sair", this::onExitClick);
        
        buttonPanel.addComponent(loginButton);
        buttonPanel.addComponent(registerButton);
        buttonPanel.addComponent(exitButton);
        
        // Add button panel spanning both columns
        mainPanel.addComponent(new EmptySpace(), GridLayout.createLayoutData(
            GridLayout.Alignment.FILL, GridLayout.Alignment.FILL, true, false, 2, 1));
        mainPanel.addComponent(buttonPanel, GridLayout.createLayoutData(
            GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, false, false, 2, 1));
        
        setComponent(mainPanel);
    }
    
    private void onLoginClick() {
        String username = usernameTextBox.getText();
        String password = passwordTextBox.getText();
        
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Por favor, preencha usuário e senha.",
                MessageDialogButton.OK
            );
            return;
        }
        
        try {
            authService.login(username, password);
            
            if (authService.isLoggedIn()) {
                close();
            } else {
                MessageDialog.showMessageDialog(
                    getTextGUI(),
                    "Erro",
                    "Login falhou - resposta inválida do servidor.",
                    MessageDialogButton.OK
                );
            }
            
        } catch (GrimoireApiException e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro de Login",
                e.getErrorMessage(),
                MessageDialogButton.OK
            );
        } catch (Exception e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro Inesperado",
                "Erro ao conectar com o servidor: " + e.getMessage(),
                MessageDialogButton.OK
            );
        }
    }
    
    private void onRegisterClick() {
        RegisterWindow registerWindow = new RegisterWindow(authService);
        getTextGUI().addWindowAndWait(registerWindow);
    }
    
    private void onExitClick() {
        System.exit(0);
    }
}