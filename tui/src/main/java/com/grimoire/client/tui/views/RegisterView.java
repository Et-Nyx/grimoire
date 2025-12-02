package com.grimoire.client.tui.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.service.ApiClient;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.components.StyledButton;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.framework.View;
import com.grimoire.common.model.User;

public class RegisterView implements View {
    private final ApiClient apiClient;
    private final Router router;
    private final TextBox usernameBox;
    private final TextBox passwordBox;
    private final Label statusLabel;

    public RegisterView(ApiClient apiClient, Router router) {
        this.apiClient = apiClient;
        this.router = router;
        this.usernameBox = new TextBox();
        this.passwordBox = new TextBox().setMask('*');
        this.statusLabel = new Label("");
    }

    @Override
    public void onEnter() {
        usernameBox.setText("");
        passwordBox.setText("");
        statusLabel.setText("");
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        Panel panel = new Panel(new GridLayout(2));

        panel.addComponent(new Label("Username:"));
        panel.addComponent(usernameBox);

        panel.addComponent(new Label("Password:"));
        panel.addComponent(passwordBox);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        panel.addComponent(new StyledButton("Register", this::performRegister));
        panel.addComponent(new StyledButton("Back", () -> router.back()));
        
        panel.addComponent(statusLabel);

        Card card = new Card("Register");
        card.addComponent(panel);
        
        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.addComponent(card.withBorder(), BorderLayout.Location.CENTER);
        
        return centerPanel;
    }

    @Override
    public String getTitle() {
        return "Register";
    }

    private void performRegister() {
        String username = usernameBox.getText();
        String password = passwordBox.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both fields.");
            return;
        }

        try {
            User user = apiClient.register(username, password);
            if (user != null) {
                statusLabel.setText("Registered! Please login.");
                // Optionally auto-login or navigate to login
                router.navigate("login");
            } else {
                statusLabel.setText("Registration failed.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
