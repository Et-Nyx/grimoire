package com.grimoire.client.tui.views;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Label;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.framework.View;

/**
 * The user profile view.
 */
public class ProfileView implements View {
    private final com.grimoire.client.service.ApiClient apiClient;

    public ProfileView(com.grimoire.client.service.ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        String username = "Unknown";
        String id = "Unknown";

        if (apiClient.getCurrentUser() != null) {
            username = apiClient.getCurrentUser().getUsername();
            id = apiClient.getCurrentUser().getId().toString();
        }

        Card card = new Card("Profile");
        card.addComponent(new Label("User Details"));
        card.addComponent(new Label(""));
        card.addComponent(new Label("ID: " + id));
        card.addComponent(new Label("Username: " + username));
        
        return card.withBorder();
    }

    @Override
    public String getTitle() {
        return "Profile";
    }
}
