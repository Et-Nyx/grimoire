package com.grimoire.client.tui.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.service.ApiClient;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.components.StyledButton;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.framework.View;
import com.grimoire.common.model.Campaign;
import com.grimoire.common.model.User;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CampaignView implements View {
    private final ApiClient apiClient;
    private final Router router;
    private static UUID selectedCampaignId;
    
    private final Label nameLabel;
    private final Label descriptionLabel;
    private final Label idLabel;
    private final Label dmLabel;
    private final Panel playersPanel;
    private final Label statusLabel;

    public CampaignView(ApiClient apiClient, Router router) {
        this.apiClient = apiClient;
        this.router = router;
        this.nameLabel = new Label("Loading...");
        this.idLabel = new Label("");
        this.descriptionLabel = new Label("");
        this.dmLabel = new Label("");
        this.playersPanel = new Panel();
        this.statusLabel = new Label("");
    }

    public static void setSelectedCampaignId(UUID id) {
        selectedCampaignId = id;
    }

    @Override
    public void onEnter() {
        if (selectedCampaignId == null) {
            statusLabel.setText("No campaign selected.");
            return;
        }
        loadCampaignData();
    }

    private void loadCampaignData() {
        try {
            // We don't have getCampaign(ID) yet, but we can list campaigns and filter.
            // Ideally we should have getCampaign(ID).
            // For now, let's re-fetch the list and find it.
            java.util.List<Campaign> campaigns = apiClient.listMyCampaigns();
            Campaign campaign = campaigns.stream()
                    .filter(c -> c.getId().equals(selectedCampaignId))
                    .findFirst()
                    .orElse(null);

            if (campaign == null) {
                statusLabel.setText("Campaign not found.");
                return;
            }

            nameLabel.setText(campaign.getName());
            descriptionLabel.setText(campaign.getDescription());
            
            // Show ID only if current user is DM
            if (apiClient.getCurrentUser() != null && apiClient.getCurrentUser().getId().equals(campaign.getOwnerId())) {
                idLabel.setText("ID: " + campaign.getId().toString());
            } else {
                idLabel.setText("");
            }

            // Fetch DM
            if (campaign.getOwnerId() != null) {
                User dm = apiClient.getUser(campaign.getOwnerId());
                dmLabel.setText("DM: " + (dm != null ? dm.getUsername() : "Unknown"));
            } else {
                dmLabel.setText("DM: Unknown");
            }

            // Fetch Players
            playersPanel.removeAllComponents();
            if (campaign.getPlayerIds() != null) {
                for (UUID playerId : campaign.getPlayerIds()) {
                    User player = apiClient.getUser(playerId);
                    String playerName = player != null ? player.getUsername() : playerId.toString();
                    playersPanel.addComponent(new Label("- " + playerName));
                }
            }

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error loading campaign", e);
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @Override
    public void onLeave() {
        nameLabel.setText("Loading...");
        idLabel.setText("");
        descriptionLabel.setText("");
        dmLabel.setText("");
        playersPanel.removeAllComponents();
        statusLabel.setText("");
    }

    @Override
    public Component getContent() {
        Panel mainPanel = new Panel(new BorderLayout());

        // --- HEADER ---
        Panel header = new Panel(new LinearLayout(Direction.VERTICAL));
        Label title = new Label(nameLabel.getText().toUpperCase());
        title.setForegroundColor(TextColor.ANSI.CYAN);
        header.addComponent(title);
        header.addComponent(descriptionLabel);
        header.addComponent(new EmptySpace());
        header.addComponent(dmLabel);
        if (!idLabel.getText().isEmpty()) {
            header.addComponent(idLabel);
        }
        
        mainPanel.addComponent(header.withBorder(Borders.singleLine("CAMPAIGN INFO")), BorderLayout.Location.TOP);

        // --- BODY (Players List) ---
        Panel playersContainer = new Panel(new LinearLayout(Direction.VERTICAL));
        
        try {
            // We need to fetch the campaign to get the player list
            java.util.List<Campaign> campaigns = apiClient.listMyCampaigns();
            Campaign campaign = campaigns.stream().filter(c -> c.getId().equals(selectedCampaignId)).findFirst().orElse(null);
            
            if (campaign != null && campaign.getPlayerIds() != null) {
                for (UUID playerId : campaign.getPlayerIds()) {
                    User player = apiClient.getUser(playerId);
                    String playerName = player != null ? player.getUsername() : playerId.toString();
                    
                    playersContainer.addComponent(new StyledButton(playerName, () -> {
                        PlayerDetailView.setContext(selectedCampaignId, playerId, playerName);
                        router.navigate("player-detail");
                    }));
                }
            }
        } catch (Exception e) {
            playersContainer.addComponent(new Label("Error loading players: " + e.getMessage()));
        }

        mainPanel.addComponent(playersContainer.withBorder(Borders.singleLine("PARTICIPANTS")), BorderLayout.Location.CENTER);

        // --- FOOTER ---
        Panel footer = new Panel(new LinearLayout(Direction.HORIZONTAL));
        footer.addComponent(new StyledButton("BACK", router::back));
        footer.addComponent(new EmptySpace(new TerminalSize(2,0)));
        footer.addComponent(statusLabel);
        
        mainPanel.addComponent(footer, BorderLayout.Location.BOTTOM);

        return mainPanel;
    }

    @Override
    public String getTitle() {
        return "Campaign";
    }
}
