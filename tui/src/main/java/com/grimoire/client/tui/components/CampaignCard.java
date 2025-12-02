package com.grimoire.client.tui.components;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.common.model.Campaign;

public class CampaignCard {

    public static Component create(Campaign campaign, Runnable onAccess) {
        Panel card = new Panel(new LinearLayout(Direction.VERTICAL));

        // Title
        Label title = new Label(campaign.getName().toUpperCase());
        title.setForegroundColor(TextColor.ANSI.CYAN);
        card.addComponent(title);

        // Metadata
        // We don't have ownerName in Campaign model yet, using ID for now or "Unknown"
        // Ideally we should fetch it or update the model. For now let's just show "DM: <OwnerID>"
        String ownerDisplay = campaign.getOwnerId() != null ? campaign.getOwnerId().toString().substring(0, 8) : "Unknown";
        card.addComponent(new Label("DM: " + ownerDisplay));
        
        int playerCount = campaign.getPlayerIds() != null ? campaign.getPlayerIds().size() : 0;
        card.addComponent(new Label("Players: " + playerCount));

        card.addComponent(new EmptySpace());

        // Action Button
        Button btn = new Button("ACCESS", onAccess);
        // Fill width not directly supported on Button without LayoutData, 
        // but LinearLayout aligns to start by default. 
        // To fill, we might need a different layout or just let it be.
        // Let's try to set LayoutData if parent was not LinearLayout, but here it is.
        // Actually, LinearLayout.createLayoutData(LinearLayout.Alignment.Fill) works if added to the component.
        btn.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        card.addComponent(btn);

        return card.withBorder(Borders.singleLine("ID: " + campaign.getId().toString().substring(0, 4)));
    }
}
