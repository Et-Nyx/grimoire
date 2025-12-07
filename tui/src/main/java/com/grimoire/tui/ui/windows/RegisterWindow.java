package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.AuthService;

public class RegisterWindow extends StandardWindow {
    
    private final AuthService authService;
    private final TextBox usernameTextBox;
    private final TextBox passwordTextBox;
    private final TextBox confirmPasswordTextBox;
    
    public RegisterWindow(AuthService authService) {
        super("Grimoire - Criar Conta");
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
        
        // Confirm password field
        mainPanel.addComponent(new Label("Confirmar Senha:"));
        confirmPasswordTextBox = new TextBox();
        confirmPasswordTextBox.setMask('*');
        mainPanel.addComponent(confirmPasswordTextBox);
        
        // Buttons panel
        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        
        Button saveButton = new Button("Salvar", this::onSaveClick);
        Button cancelButton = new Button("Cancelar", this::onCancelClick);
        
        buttonPanel.addComponent(saveButton);
        buttonPanel.addComponent(cancelButton);
        
        // Add button panel spanning both columns
        mainPanel.addComponent(new EmptySpace(), GridLayout.createLayoutData(
            GridLayout.Alignment.FILL, GridLayout.Alignment.FILL, true, false, 2, 1));
        mainPanel.addComponent(buttonPanel, GridLayout.createLayoutData(
            GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, false, false, 2, 1));
        
        setComponent(mainPanel);
    }
    
    private void onSaveClick() {
        String username = usernameTextBox.getText();
        String password = passwordTextBox.getText();
        String confirmPassword = confirmPasswordTextBox.getText();
        
        // Validate fields
        if (username.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Por favor, preencha todos os campos.",
                MessageDialogButton.OK
            );
            return;
        }
        
        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "As senhas não coincidem. Por favor, verifique e tente novamente.",
                MessageDialogButton.OK
            );
            return;
        }
        
        // Attempt registration
        try {
            authService.register(username, password);
            
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Sucesso",
                "Usuário criado com sucesso!\n\nVocê já pode fazer login com as credenciais criadas.",
                MessageDialogButton.OK
            );
            
            close();
            
        } catch (GrimoireApiException e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro de Registro",
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
    
    private void onCancelClick() {
        close();
    }
}