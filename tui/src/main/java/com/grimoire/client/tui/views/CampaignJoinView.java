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

public class CampaignJoinView implements View {
    private final ApiClient apiClient;
    private final Router router;
    private final TextBox campaignIdBox;
    private final Label statusLabel;

    public CampaignJoinView(ApiClient apiClient, Router router) {
        this.apiClient = apiClient;
        this.router = router;
        this.campaignIdBox = new TextBox();
        this.statusLabel = new Label("");
    }

    @Override
    public void onEnter() {
        campaignIdBox.setText("");
        statusLabel.setText("");
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        Card card = new Card("Join Campaign");
        
        card.addComponent(new Label("Enter Campaign ID:"));
        card.addComponent(campaignIdBox);
        
        card.addComponent(new Label("")); // Spacer
        
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new StyledButton("Join", this::joinCampaign));
        buttons.addComponent(new StyledButton("Cancel", router::back));
        card.addComponent(buttons);
        
        card.addComponent(new Label(""));
        card.addComponent(statusLabel);

        return card.withBorder();
    }

    private void joinCampaign() {
        String campaignId = campaignIdBox.getText();

        if (campaignId.isEmpty()) {
            statusLabel.setText("Campaign ID is required!");
            return;
        }

        try {
            Campaign campaign = apiClient.joinCampaign(campaignId);
            if (campaign != null) {
                router.navigate("home");
            } else {
                statusLabel.setText("Failed to join campaign. Check ID.");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error joining campaign", e);
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @Override
    public String getTitle() {
        return "Join Campaign";
    }
}
