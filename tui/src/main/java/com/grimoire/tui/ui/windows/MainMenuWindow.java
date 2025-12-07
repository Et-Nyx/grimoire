package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.TerminalSize;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.AuthService;
import com.grimoire.client.service.CampaignService;
import com.grimoire.client.service.SessionService;
import com.grimoire.client.service.SheetService;
import com.grimoire.common.model.Campaign;
import com.grimoire.tui.ui.components.RichTimelineEntry;
import com.grimoire.tui.ui.components.SessionTimeline;
import com.grimoire.tui.ui.styles.GrimoireTheme;

import java.util.List;

public class MainMenuWindow extends StandardWindow {
    
    private final AuthService authService;
    private final CampaignService campaignService;
    private final SessionService sessionService;
    private final SheetService sheetService;
    private final SessionTimeline campaignTimeline;
    
    public MainMenuWindow(AuthService authService, CampaignService campaignService, 
                         SessionService sessionService, SheetService sheetService) {
        super("Grimoire RPG - Menu Principal");
        this.authService = authService;
        this.campaignService = campaignService;
        this.sessionService = sessionService;
        this.sheetService = sheetService;
        
        // Initialize timeline
        campaignTimeline = new SessionTimeline();
        campaignTimeline.setPreferredSize(new TerminalSize(50, 8));
        campaignTimeline.setSelectionMode(true);
        campaignTimeline.setOnEntrySelected(payload -> {
            if (payload instanceof Campaign) {
                onCampaignSelect((Campaign) payload);
            }
        });
        
        // Add Tabs
        addTab("Início", createHomeTab());
        addTab("Perfil", createProfileTab());
    }
    
    private Component createHomeTab() {
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        
        // Welcome message
        String username = authService.getCurrentUser() != null ? 
            authService.getCurrentUser().getUsername() : "Usuário";
        Label welcomeLabel = new Label("Olá, " + username + "!");
        mainPanel.addComponent(welcomeLabel);
        
        // Add separator
        mainPanel.addComponent(new EmptySpace());
        
        // Campaigns section
        Label campaignsLabel = new Label("Suas Campanhas:");
        mainPanel.addComponent(campaignsLabel);
        
        // Campaign list
        loadCampaigns();
        mainPanel.addComponent(campaignTimeline);
        
        // Add separator
        mainPanel.addComponent(new EmptySpace());
        
        // Buttons panel
        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        
        Button newCampaignButton = new Button("Nova Campanha", this::onNewCampaignClick);
        Button joinCampaignButton = new Button("Entrar em Campanha", this::onJoinCampaignClick);
        Button logoutButton = new Button("Logout", this::onLogoutClick);
        
        buttonPanel.addComponent(newCampaignButton);
        buttonPanel.addComponent(joinCampaignButton);
        buttonPanel.addComponent(logoutButton);
        
        mainPanel.addComponent(buttonPanel);
        
        return mainPanel;
    }
    
    private Component createProfileTab() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        
        String username = authService.getCurrentUser() != null ? 
            authService.getCurrentUser().getUsername() : "Usuário";
        
        panel.addComponent(new Label("Perfil de Usuário").addStyle(com.googlecode.lanterna.SGR.BOLD));
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("Nome: " + username));
        panel.addComponent(new Label("ID: " + (authService.getCurrentUser() != null ? authService.getCurrentUser().getId() : "N/A")));
        
        panel.addComponent(new EmptySpace());
        
        Button moreInfoButton = new Button("Mais Info", this::onMoreInfoClick);
        panel.addComponent(moreInfoButton);
        
        return panel;
    }
    
    private void onMoreInfoClick() {
            UserProfileWindow profileWindow = new UserProfileWindow(
                authService,
                campaignService,
                sessionService,
                sheetService,
                this::onLogoutClick
            );
            navigateTo(profileWindow);
        }
    
    private void loadCampaigns() {
        campaignTimeline.clear();
        
        try {
            List<Campaign> campaigns = campaignService.getMyCampaigns();
            java.util.Collections.reverse(campaigns);
            
            if (campaigns.isEmpty()) {
                RichTimelineEntry emptyEntry = new RichTimelineEntry(48);
                emptyEntry.addSegment("Nenhuma campanha encontrada.", GrimoireTheme.TIMESTAMP);
                campaignTimeline.addEntry(emptyEntry);
            } else {
                for (Campaign campaign : campaigns) {
                    RichTimelineEntry entry = new RichTimelineEntry(48);
                    entry.setPayload(campaign);
                    
                    entry.addSegment(campaign.getName(), GrimoireTheme.PRIMARY, java.util.EnumSet.of(com.googlecode.lanterna.SGR.BOLD));
                    entry.addSegment(" (ID: " + campaign.getId() + ")", GrimoireTheme.TIMESTAMP);
                    
                    if (campaign.getDescription() != null && !campaign.getDescription().isEmpty()) {
                        entry.addSegment("\n", GrimoireTheme.TEXT_NORMAL);
                        entry.addSegment(campaign.getDescription(), GrimoireTheme.TEXT_NORMAL);
                    }
                    
                    campaignTimeline.addEntry(entry);
                }
            }
        } catch (GrimoireApiException e) {
            RichTimelineEntry errorEntry = new RichTimelineEntry(48);
            errorEntry.addSegment("API: " + e.getErrorMessage(), GrimoireTheme.ERROR);
            if (e.getCause() != null) {
                errorEntry.addSegment(" | Causa: " + e.getCause().getMessage(), GrimoireTheme.ERROR);
            }
            campaignTimeline.addEntry(errorEntry);
        } catch (Exception e) {
            RichTimelineEntry errorEntry = new RichTimelineEntry(48);
            errorEntry.addSegment("Exceção: " + e.getClass().getSimpleName() + " - " + e.getMessage(), GrimoireTheme.ERROR);
            if (e.getCause() != null) {
                errorEntry.addSegment(" | Causa: " + e.getCause().getMessage(), GrimoireTheme.ERROR);
            }
            campaignTimeline.addEntry(errorEntry);
        }
    }
    
    private void onCampaignSelect(Campaign campaign) {
        String username = authService.getCurrentUser() != null ? 
            authService.getCurrentUser().getUsername() : "Anônimo";
        
        java.util.UUID userId = authService.getCurrentUser() != null ?
            authService.getCurrentUser().getId() : null;
            
        CampaignWindow campaignWindow = new CampaignWindow(
            campaign,
            sessionService,
            sheetService,
            campaignService,
            authService,
            username,
            userId,
            this::loadCampaigns
        );
        navigateTo(campaignWindow);
    }
    
    private void onNewCampaignClick() {
            CampaignCreateWindow createWindow = new CampaignCreateWindow(
                campaignService,
                this::loadCampaigns
            );
            navigateTo(createWindow);
        }
    
    private void onJoinCampaignClick() {
            CampaignJoinWindow joinWindow = new CampaignJoinWindow(
                campaignService,
                sheetService,
                authService,
                sessionService,
                this::loadCampaigns
            );
            navigateTo(joinWindow);
        }
    
    private void onLogoutClick() {
        try {
            authService.logout();
        } catch (Exception e) {
            // Log error but continue with logout
        }
        close();
    }
}