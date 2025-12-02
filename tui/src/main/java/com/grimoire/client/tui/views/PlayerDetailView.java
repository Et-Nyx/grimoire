package com.grimoire.client.tui.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.service.ApiClient;
import com.grimoire.client.tui.components.StyledButton;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.framework.View;
import com.grimoire.common.model.Campaign;
import com.grimoire.common.model.CampaignNote;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerDetailView implements View {
    private final ApiClient apiClient;
    private final Router router;
    
    private static UUID campaignId;
    private static UUID targetPlayerId;
    private static String targetPlayerName;

    private final Label nameLabel = new Label("");
    private final Panel notesPanel = new Panel();
    private final Panel sheetPanel = new Panel();
    private final Label statusLabel = new Label("");

    public PlayerDetailView(ApiClient apiClient, Router router) {
        this.apiClient = apiClient;
        this.router = router;
    }

    public static void setContext(UUID cId, UUID pId, String pName) {
        campaignId = cId;
        targetPlayerId = pId;
        targetPlayerName = pName;
    }

    @Override
    public void onEnter() {
        if (campaignId == null || targetPlayerId == null) {
            statusLabel.setText("Error: No context.");
            return;
        }
        nameLabel.setText("PLAYER: " + targetPlayerName.toUpperCase());
        loadData();
    }

    private void loadData() {
        notesPanel.removeAllComponents();
        sheetPanel.removeAllComponents();
        statusLabel.setText("");

        try {
            User currentUser = apiClient.getCurrentUser();
            boolean isMe = currentUser != null && currentUser.getId().equals(targetPlayerId);
            
            // Check if I am DM
            List<Campaign> campaigns = apiClient.listMyCampaigns();
            Campaign campaign = campaigns.stream().filter(c -> c.getId().equals(campaignId)).findFirst().orElse(null);
            boolean isDM = campaign != null && currentUser != null && campaign.getOwnerId().equals(currentUser.getId());

            // --- NOTES SECTION ---
            List<CampaignNote> allNotes = apiClient.listNotes(campaignId.toString());
            List<CampaignNote> playerNotes = allNotes.stream()
                    .filter(n -> {
                        // Filter by author (we need to fetch author ID, but note usually stores username or ID. 
                        // The model has 'author' field which is String. Let's assume it stores Username for now based on previous code, 
                        // OR we need to filter by matching the author field. 
                        // Wait, CampaignNote model has 'author' as String. 
                        // Let's assume it stores the Username.
                        return n.getAuthor() != null && n.getAuthor().equals(targetPlayerName);
                    })
                    .collect(Collectors.toList());

            if (isMe) {
                notesPanel.addComponent(new StyledButton("+ NEW NOTE", () -> showNoteDialog(null)));
                notesPanel.addComponent(new EmptySpace());
            }

            for (CampaignNote note : playerNotes) {
                // Visibility Check
                if (!note.isPublic() && !isMe && !isDM) continue;

                Panel noteCard = new Panel(new LinearLayout(Direction.VERTICAL));
                String visibility = note.isPublic() ? "[PUB]" : "[PRIV]";
                noteCard.addComponent(new Label(visibility + " " + note.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE)));
                noteCard.addComponent(new Label(note.getContent()));
                
                if (isMe) {
                    Panel actions = new Panel(new LinearLayout(Direction.HORIZONTAL));
                    actions.addComponent(new Button("Edit", () -> showNoteDialog(note)));
                    actions.addComponent(new Button("Del", () -> deleteNote(note)));
                    noteCard.addComponent(actions);
                }

                notesPanel.addComponent(noteCard.withBorder(Borders.singleLine()));
            }

            if (notesPanel.getChildren().isEmpty()) {
                notesPanel.addComponent(new Label("(No visible notes)"));
            }

            // --- SHEET SECTION ---
            // Only show if Me or DM
            if (isMe || isDM) {
                List<CharacterSheet> sheets = apiClient.listSheets(campaignId);
                CharacterSheet sheet = sheets.stream()
                        .filter(s -> s.getPlayerId() != null && s.getPlayerId().equals(targetPlayerId))
                        .findFirst()
                        .orElse(null);

                if (sheet != null) {
                    sheetPanel.addComponent(new Label("Character: " + sheet.getName()));
                    sheetPanel.addComponent(new StyledButton("OPEN SHEET", () -> {
                        CharacterSheetView.setSheet(sheet, campaignId);
                        router.navigate("sheet-view");
                    }));
                } else if (isMe) {
                    sheetPanel.addComponent(new StyledButton("CREATE SHEET", () -> {
                        CharacterSheetView.setSheet(null, campaignId);
                        router.navigate("sheet-view");
                    }));
                } else {
                    sheetPanel.addComponent(new Label("(No sheet created)"));
                }
            } else {
                sheetPanel.addComponent(new Label("(Hidden)"));
            }

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error loading player details", e);
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void showNoteDialog(CampaignNote existingNote) {
        // Simple dialog to add/edit note
        // Since we don't have a modal dialog system easily accessible here without blocking,
        // we might need a separate View or a popup. 
        // For simplicity, let's use a TextInputDialog if available or just a simple form view.
        // Lanterna has TextInputDialog.
        
        String content = existingNote != null ? existingNote.getContent() : "";
        boolean isPublic = existingNote != null ? existingNote.isPublic() : false;

        // We need a custom dialog for Content + Public/Private toggle.
        // For now, let's just ask for content and default to Private (or ask in text).
        // "Content (prefix with 'PUB:' for public)"
        
        String result = new com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder()
                .setTitle(existingNote == null ? "New Note" : "Edit Note")
                .setDescription("Enter note content. Start with 'PUB:' to make public.")
                .setInitialContent(isPublic ? "PUB:" + content : content)
                .build()
                .showDialog(((com.grimoire.client.tui.components.AppShell)router.getWindow()).getTextGUI());

        if (result != null) {
            try {
                CampaignNote note = existingNote != null ? existingNote : new CampaignNote();
                if (note.getId() == null) {
                    note.setCampaignId(campaignId);
                    note.setAuthor(apiClient.getCurrentUser().getUsername());
                }
                
                if (result.startsWith("PUB:")) {
                    note.setPublic(true);
                    note.setContent(result.substring(4));
                } else {
                    note.setPublic(false);
                    note.setContent(result);
                }
                
                apiClient.saveNote(note);
                loadData(); // Refresh
            } catch (Exception e) {
                statusLabel.setText("Error saving note: " + e.getMessage());
            }
        }
    }

    private void deleteNote(CampaignNote note) {
        try {
            apiClient.deleteNote(note.getId());
            loadData();
        } catch (Exception e) {
            statusLabel.setText("Error deleting note: " + e.getMessage());
        }
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        Panel mainPanel = new Panel(new BorderLayout());

        // Header
        Label title = new Label(nameLabel.getText());
        title.setForegroundColor(TextColor.ANSI.CYAN);
        mainPanel.addComponent(title.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

        // Body (2 Columns: Notes & Sheet)
        Panel body = new Panel(new GridLayout(2));

        // Notes Col
        Panel notesContainer = new Panel(new LinearLayout(Direction.VERTICAL));
        notesContainer.addComponent(notesPanel);
        body.addComponent(notesContainer.withBorder(Borders.singleLine("NOTES")));

        // Sheet Col
        Panel sheetContainer = new Panel(new LinearLayout(Direction.VERTICAL));
        sheetContainer.addComponent(sheetPanel);
        body.addComponent(sheetContainer.withBorder(Borders.singleLine("CHARACTER SHEET")));

        mainPanel.addComponent(body, BorderLayout.Location.CENTER);

        // Footer
        Panel footer = new Panel(new LinearLayout(Direction.HORIZONTAL));
        footer.addComponent(new StyledButton("BACK", router::back));
        footer.addComponent(new EmptySpace(new TerminalSize(2,0)));
        footer.addComponent(statusLabel);
        mainPanel.addComponent(footer, BorderLayout.Location.BOTTOM);

        return mainPanel;
    }

    @Override
    public String getTitle() {
        return "Player Details";
    }
}
