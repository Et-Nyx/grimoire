package com.grimoire.client.tui.views;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.framework.View;

/**
 * The home dashboard view.
 */
public class HomeView implements View {
    private final com.grimoire.client.service.ApiClient apiClient;
    private final com.grimoire.client.tui.framework.Router router;

    public HomeView(com.grimoire.client.service.ApiClient apiClient, com.grimoire.client.tui.framework.Router router) {
        this.apiClient = apiClient;
        this.router = router;
    }

    @Override
    public void onEnter() {
        // Load data if needed
    }

    @Override
    public void onLeave() {
        // Cleanup
    }

    @Override
    public Component getContent() {
        String username = "Traveler";
        if (apiClient.getCurrentUser() != null) {
            username = apiClient.getCurrentUser().getUsername();
        }

        Card card = new Card("Dashboard");
        card.addComponent(new Label("Welcome back, " + username + "!"));
        card.addComponent(new Label("")); // Spacer
        
        // Actions
        Panel actionsPanel = new Panel(new com.googlecode.lanterna.gui2.LinearLayout(com.googlecode.lanterna.gui2.Direction.HORIZONTAL));
        actionsPanel.addComponent(new com.grimoire.client.tui.components.StyledButton("New Campaign", () -> {
            router.navigate("campaign-create");
        }));
        actionsPanel.addComponent(new com.grimoire.client.tui.components.StyledButton("Join Campaign", () -> {
            router.navigate("campaign-join");
        }));
        card.addComponent(actionsPanel);
        
        card.addComponent(new Label("")); // Spacer
        card.addComponent(new Label("Your Campaigns:"));

        try {
            java.util.List<com.grimoire.common.model.Campaign> campaigns = apiClient.listMyCampaigns();
            if (campaigns.isEmpty()) {
                card.addComponent(new Label("(No campaigns found)"));
            } else {
                Panel grid = new Panel(new com.googlecode.lanterna.gui2.GridLayout(2));
                for (com.grimoire.common.model.Campaign c : campaigns) {
                    grid.addComponent(com.grimoire.client.tui.components.CampaignCard.create(c, () -> {
                        com.grimoire.client.tui.views.CampaignView.setSelectedCampaignId(c.getId());
                        router.navigate("campaign-view");
                    }));
                }
                card.addComponent(grid);
            }
        } catch (Exception e) {
            card.addComponent(new Label("Error loading campaigns: " + e.getMessage()));
        }
        
        return card.withBorder();
    }

    @Override
    public String getTitle() {
        return "Home";
    }
}
