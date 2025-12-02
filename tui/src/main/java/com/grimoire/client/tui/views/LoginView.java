package com.grimoire.client.tui.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.service.ApiClient;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.components.StyledButton;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.framework.View;
import com.grimoire.common.model.User;

import java.util.Arrays;

public class LoginView implements View {
    private final ApiClient apiClient;
    private final Router router;
    private final TextBox usernameBox;
    private final TextBox passwordBox;
    private final Label statusLabel;

    private final Runnable onSuccess;

    public LoginView(ApiClient apiClient, Router router, Runnable onSuccess) {
        this.apiClient = apiClient;
        this.router = router;
        this.onSuccess = onSuccess;
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

        panel.addComponent(new StyledButton("Login", this::performLogin));
        panel.addComponent(new StyledButton("Back", () -> router.back()));
        panel.addComponent(statusLabel);

        Card card = new Card("Login");
        card.addComponent(panel);
        
        // Center the card
        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.addComponent(card.withBorder(), BorderLayout.Location.CENTER);
        
        return centerPanel;
    }

    @Override
    public String getTitle() {
        return "Login";
    }

    private void performLogin() {
        String username = usernameBox.getText();
        String password = passwordBox.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both fields.");
            return;
        }

        try {
            User user = apiClient.login(username, password);
            if (user != null) {
                statusLabel.setText("Success!");
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                statusLabel.setText("Invalid credentials.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
