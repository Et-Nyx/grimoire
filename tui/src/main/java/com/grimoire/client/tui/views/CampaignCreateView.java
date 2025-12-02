package com.grimoire.client.tui.views;

import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.service.ApiClient;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.components.StyledButton;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.framework.View;
import com.grimoire.common.model.Campaign;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CampaignCreateView implements View {
    private final ApiClient apiClient;
    private final Router router;
    private final TextBox nameBox;
    private final TextBox descriptionBox;
    private final Label statusLabel;

    public CampaignCreateView(ApiClient apiClient, Router router) {
        this.apiClient = apiClient;
        this.router = router;
        this.nameBox = new TextBox();
        this.descriptionBox = new TextBox();
        this.statusLabel = new Label("");
    }

    @Override
    public void onEnter() {
        nameBox.setText("");
        descriptionBox.setText("");
        statusLabel.setText("");
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        Card card = new Card("New Campaign");
        
        card.addComponent(new Label("Campaign Name:"));
        card.addComponent(nameBox);
        
        card.addComponent(new Label("Description:"));
        card.addComponent(descriptionBox);
        
        card.addComponent(new Label("")); // Spacer
        
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new StyledButton("Create", this::createCampaign));
        buttons.addComponent(new StyledButton("Cancel", router::back));
        card.addComponent(buttons);
        
        card.addComponent(new Label(""));
        card.addComponent(statusLabel);

        return card.withBorder();
    }

    private void createCampaign() {
        String name = nameBox.getText();
        String description = descriptionBox.getText();

        if (name.isEmpty()) {
            statusLabel.setText("Name is required!");
            return;
        }

        try {
            Campaign campaign = apiClient.createCampaign(name, description);
            if (campaign != null) {
                router.navigate("home");
            } else {
                statusLabel.setText("Failed to create campaign.");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error creating campaign", e);
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @Override
    public String getTitle() {
        return "New Campaign";
    }
}
