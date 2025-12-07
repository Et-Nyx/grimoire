package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.CampaignService;
import com.grimoire.common.model.Campaign;

public class CampaignJoinWindow extends StandardWindow {
    
    private final CampaignService campaignService;
    private final Runnable onSuccess;
    
    private TextBox campaignIdTextBox;
    
    private final com.grimoire.client.service.SheetService sheetService;
    private final com.grimoire.client.service.AuthService authService;
    private final com.grimoire.client.service.SessionService sessionService;
    
    public CampaignJoinWindow(CampaignService campaignService, 
                              com.grimoire.client.service.SheetService sheetService,
                              com.grimoire.client.service.AuthService authService,
                              com.grimoire.client.service.SessionService sessionService,
                              Runnable onSuccess) {
        super("Entrar em Campanha");
        this.campaignService = campaignService;
        this.sheetService = sheetService;
        this.authService = authService;
        this.sessionService = sessionService;
        this.onSuccess = onSuccess;
        
        setHints(java.util.Arrays.asList(Hint.CENTERED, Hint.MODAL));
        
        Panel mainPanel = new Panel(new GridLayout(2));
        
        mainPanel.addComponent(new Label("ID da Campanha:"));
        campaignIdTextBox = new TextBox(new TerminalSize(36, 1));
        mainPanel.addComponent(campaignIdTextBox);
        
        mainPanel.addComponent(new EmptySpace());
        
        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        
        Button joinButton = new Button("Entrar", this::onJoinClick);
        Button cancelButton = new Button("Cancelar", this::close);
        
        buttonPanel.addComponent(joinButton);
        buttonPanel.addComponent(cancelButton);
        
        mainPanel.addComponent(buttonPanel);
        
        setComponent(mainPanel);
    }
    
    private void onJoinClick() {
        String campaignId = campaignIdTextBox.getText().trim();
        
        if (campaignId.isEmpty()) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "ID da campanha é obrigatório.",
                MessageDialogButton.OK
            );
            return;
        }
        
        try {
            Campaign campaign = campaignService.joinCampaign(campaignId);
            
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Sucesso",
                "Você entrou na campanha \"" + campaign.getName() + "\"!",
                MessageDialogButton.OK
            );
            
            // Fetch the new sheet
            java.util.UUID userId = authService.getCurrentUser().getId();
            java.util.List<com.grimoire.common.model.CharacterSheet> sheets = sheetService.getSheetsByPlayer(userId);
            com.grimoire.common.model.CharacterSheet newSheet = sheets.stream()
                .filter(s -> s.getCampaignId().equals(campaign.getId()))
                .findFirst()
                .orElse(null);
            
            // Fetch Session Zero
            com.grimoire.common.model.Session sessionZero = null;
            try {
                java.util.List<com.grimoire.common.model.Session> sessions = sessionService.listByCampaign(campaign.getId().toString());
                sessionZero = sessions.stream()
                    .filter(s -> "Sessão Zero".equalsIgnoreCase(s.getTitle()))
                    .findFirst()
                    .orElse(sessions.isEmpty() ? null : sessions.get(0));
            } catch (Exception e) {
                // Ignore error fetching sessions
            }
            
            // Capture GUI before closing
            WindowBasedTextGUI gui = (WindowBasedTextGUI) getTextGUI();
            close(); // Close Join Window
            
            // Define the chain of windows
            
            // 3. Campaign Window (Base)
            CampaignWindow campaignWindow = new CampaignWindow(
                campaign,
                sessionService,
                sheetService,
                campaignService,
                authService,
                authService.getCurrentUser().getUsername(),
                userId,
                onSuccess
            );
            
            // 2. Session Zero Window (Middle)
            SessionDetailsWindow sessionWindow = null;
            if (sessionZero != null) {
                sessionWindow = new SessionDetailsWindow(
                    sessionZero,
                    sessionService,
                    sheetService,
                    campaignService,
                    authService,
                    campaign,
                    authService.getCurrentUser().getUsername(),
                    userId
                );
            }
            
            // 1. Sheet Window (Top)
            CharacterSheetWindow sheetWindow = null;
            if (newSheet != null) {
                sheetWindow = new CharacterSheetWindow(
                    newSheet,
                    sheetService,
                    true,
                    null // No callback needed as we stack them
                );
            }
            
            // Add windows to GUI in reverse order (Base first)
            if (gui != null) {
                gui.addWindow(campaignWindow);
                if (sessionWindow != null) gui.addWindow(sessionWindow);
                if (sheetWindow != null) gui.addWindowAndWait(sheetWindow); // Block on the top one
                else if (sessionWindow != null) gui.setActiveWindow(sessionWindow); // If no sheet, focus session
                else gui.setActiveWindow(campaignWindow); // If neither, focus campaign
            }
            
            if (onSuccess != null) {
                onSuccess.run(); // Refresh main menu list
            }
            
        } catch (GrimoireApiException e) {
            com.grimoire.client.util.ClientLogger.logError("API Error joining campaign", e);
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Erro ao entrar na campanha: " + e.getErrorMessage(),
                MessageDialogButton.OK
            );
        } catch (Exception e) {
            com.grimoire.client.util.ClientLogger.logError("Unexpected Error joining campaign", e);
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Erro inesperado: " + e.getMessage(),
                MessageDialogButton.OK
            );
        }
    }
}
