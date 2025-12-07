package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.CampaignService;
import com.grimoire.client.service.SessionService;
import com.grimoire.client.service.SheetService;
import com.grimoire.client.service.AuthService;
import com.grimoire.common.model.Campaign;
import com.grimoire.common.model.Session;
import com.grimoire.common.model.User;
import com.grimoire.tui.ui.components.RichTimelineEntry;
import com.grimoire.tui.ui.components.SessionTimeline;
import com.grimoire.tui.ui.styles.GrimoireTheme;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class CampaignWindow extends StandardWindow {
    
    private Campaign campaign;
    private final SessionService sessionService;
    private final SheetService sheetService;
    private final CampaignService campaignService;
    private final String currentUsername;
    private final UUID currentUserId;
    private final SessionTimeline sessionTimeline;
    private final SessionTimeline playersTimeline;
    
    // Summary Tab Components
    private Label summaryNameLabel;
    private Label summaryDescriptionLabel;
    private Label summaryOwnerLabel;
    private Label summaryStatsLabel;
    
    // Settings Tab Components
    private TextBox settingsNameBox;
    private TextBox settingsDescriptionBox;
    
    private List<Session> cachedSessions;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final Runnable onCampaignDeleted;
    
    private final AuthService authService;

    public CampaignWindow(Campaign campaign, SessionService sessionService, SheetService sheetService, CampaignService campaignService, AuthService authService, String currentUsername, UUID currentUserId, Runnable onCampaignDeleted) {
        super("Campanha: " + campaign.getName());
        this.campaign = campaign;
        this.sessionService = sessionService;
        this.sheetService = sheetService;
        this.campaignService = campaignService;
        this.authService = authService;
        this.currentUsername = currentUsername;
        this.currentUserId = currentUserId;
        this.onCampaignDeleted = onCampaignDeleted;
        
        // Initialize components needed for tabs
        sessionTimeline = new SessionTimeline();
        sessionTimeline.setPreferredSize(new TerminalSize(60, 10));
        sessionTimeline.setSelectionMode(true);
        sessionTimeline.setOnEntrySelected(payload -> {
            if (payload instanceof Session) {
                onSessionSelect((Session) payload);
            }
        });
        
        playersTimeline = new SessionTimeline();
        playersTimeline.setPreferredSize(new TerminalSize(60, 10));
        playersTimeline.setSelectionMode(true);
        playersTimeline.setOnEntrySelected(payload -> {
            if (payload instanceof User) {
                onPlayerSelect((User) payload);
            }
        });
        
        // Add Tabs
        addTab("Resumo", createSummaryTab());
        addTab("Sessões", createSessionsTab());
        addTab("Jogadores", createPlayersTab());
        addTab("Configurações", createSettingsTab());
    }
    
    // ... (createSummaryTab, createSessionsTab, createPlayersTab remain same)

    private Component createSettingsTab() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Configurações da Campanha"));
        panel.addComponent(new EmptySpace());
        
        if (campaign.getOwnerId().equals(currentUserId)) {
            panel.addComponent(new Label("Nome:"));
            settingsNameBox = new TextBox(new TerminalSize(50, 1), campaign.getName());
            panel.addComponent(settingsNameBox);
            
            panel.addComponent(new Label("Descrição:"));
            settingsDescriptionBox = new TextBox(new TerminalSize(50, 5), TextBox.Style.MULTI_LINE);
            settingsDescriptionBox.setText(campaign.getDescription() != null ? campaign.getDescription() : "");
            panel.addComponent(settingsDescriptionBox);
            
            panel.addComponent(new EmptySpace());
            
            Button saveButton = new Button("Salvar Alterações", this::onSaveSettings);
            panel.addComponent(saveButton);
            
            panel.addComponent(new EmptySpace());
            panel.addComponent(new Separator(Direction.HORIZONTAL));
            panel.addComponent(new EmptySpace());
            
            Button deleteButton = new Button("Deletar Campanha", this::onDeleteCampaignClick);
            // deleteButton.setTheme(GrimoireTheme.DANGER); // Assuming theme support or just use default
            panel.addComponent(deleteButton);
            
        } else {
            panel.addComponent(new Label("Apenas o Mestre pode configurar a campanha.").setForegroundColor(GrimoireTheme.ERROR));
        }
        
        return panel;
    }
    
    private void onDeleteCampaignClick() {
        MessageDialogButton result = MessageDialog.showMessageDialog(getTextGUI(), "Confirmar Deleção", 
            "Tem certeza que deseja deletar esta campanha?\nEsta ação não pode ser desfeita.", MessageDialogButton.Yes, MessageDialogButton.No);
            
        if (result == MessageDialogButton.Yes) {
            try {
                campaignService.deleteCampaign(campaign.getId());
                MessageDialog.showMessageDialog(getTextGUI(), "Sucesso", "Campanha deletada.", MessageDialogButton.OK);
                if (onCampaignDeleted != null) {
                    onCampaignDeleted.run();
                }
                close();
            } catch (GrimoireApiException e) {
                MessageDialog.showMessageDialog(getTextGUI(), "Erro", "Falha ao deletar: " + e.getErrorMessage(), MessageDialogButton.OK);
            }
        }
    }
    
    private Component createSummaryTab() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        
        summaryNameLabel = new Label("Campanha: " + campaign.getName());
        summaryNameLabel.addStyle(com.googlecode.lanterna.SGR.BOLD);
        panel.addComponent(summaryNameLabel);
        
        panel.addComponent(new Label("ID: " + campaign.getId()).setForegroundColor(GrimoireTheme.TIMESTAMP));
        
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("Descrição:"));
        summaryDescriptionLabel = new Label(campaign.getDescription() != null ? campaign.getDescription() : "Sem descrição.");
        panel.addComponent(summaryDescriptionLabel);
        
        panel.addComponent(new EmptySpace());
        
        summaryOwnerLabel = new Label("Dono: " + (campaign.getOwnerId().equals(currentUserId) ? "Você" : campaign.getOwnerId()));
        panel.addComponent(summaryOwnerLabel);
        
        panel.addComponent(new EmptySpace());
        
        summaryStatsLabel = new Label("Carregando estatísticas...");
        panel.addComponent(summaryStatsLabel);
        
        return panel;
    }
    
    private Component createSessionsTab() {
        Panel panel = new Panel(new BorderLayout());
        
        Panel listPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        listPanel.addComponent(new Label("Sessões da Campanha:"));
        loadSessions();
        listPanel.addComponent(sessionTimeline);
        
        panel.addComponent(listPanel, BorderLayout.Location.CENTER);
        
        if (campaign.getOwnerId().equals(currentUserId)) {
            Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            Button newSessionButton = new Button("Agendar Nova Sessão", this::onNewSessionClick);
            buttonPanel.addComponent(newSessionButton);
            panel.addComponent(buttonPanel, BorderLayout.Location.BOTTOM);
        }
        
        return panel;
    }
    
    private Component createPlayersTab() {
        Panel panel = new Panel(new BorderLayout());
        
        Panel listPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        listPanel.addComponent(new Label("Jogadores na Campanha:"));
        
        updatePlayersList();
        listPanel.addComponent(playersTimeline);
        
        panel.addComponent(listPanel, BorderLayout.Location.CENTER);
        
        return panel;
    }
    

    
    private void updatePlayersList() {
        playersTimeline.clear();
        if (campaign.getPlayerIds().isEmpty()) {
             RichTimelineEntry emptyEntry = new RichTimelineEntry(58);
             emptyEntry.addSegment("Nenhum jogador encontrado.", GrimoireTheme.TIMESTAMP);
             playersTimeline.addEntry(emptyEntry);
        } else {
            for (UUID playerId : campaign.getPlayerIds()) {
                try {
                    User player = authService.getUser(playerId);
                    RichTimelineEntry entry = new RichTimelineEntry(58);
                    entry.setPayload(player);
                    
                    entry.addSegment(player.getUsername(), GrimoireTheme.PRIMARY);
                    if (playerId.equals(campaign.getOwnerId())) {
                         entry.addSegment(" (Mestre)", GrimoireTheme.SECONDARY);
                    }
                    playersTimeline.addEntry(entry);
                } catch (GrimoireApiException e) {
                    RichTimelineEntry entry = new RichTimelineEntry(58);
                    entry.addSegment("ID: " + playerId + " (Erro ao carregar nome)", GrimoireTheme.ERROR);
                    playersTimeline.addEntry(entry);
                }
            }
        }
    }
    
    private void onPlayerSelect(User player) {
            UserProfileWindow profileWindow = new UserProfileWindow(
                authService,
                campaignService,
                sessionService,
                sheetService,
                null, // No logout callback needed for viewing other profiles
                player
            );
            navigateTo(profileWindow);
        }

    private void updateSummary() {
        summaryNameLabel.setText("Campanha: " + campaign.getName());
        summaryDescriptionLabel.setText(campaign.getDescription() != null ? campaign.getDescription() : "Sem descrição.");
        summaryOwnerLabel.setText("Dono: " + (campaign.getOwnerId().equals(currentUserId) ? "Você" : campaign.getOwnerId()));
        
        int sessionCount = cachedSessions != null ? cachedSessions.size() : 0;
        int playerCount = campaign.getPlayerIds().size();
        String lastSessionDate = "Nenhuma";
        
        if (cachedSessions != null && !cachedSessions.isEmpty()) {
            Session lastSession = cachedSessions.get(0); // Sorted descending
            if (lastSession.getDate() != null) {
                lastSessionDate = lastSession.getDate().format(DATE_FORMATTER);
            }
        }
        
        String system = campaign.getSystem() != null ? campaign.getSystem() : "Não definido";
        
        summaryStatsLabel.setText(String.format("Sistema: %s\nJogadores: %d | Sessões: %d | Última Sessão: %s", system, playerCount, sessionCount, lastSessionDate));
    }

    private void onSaveSettings() {
        String newName = settingsNameBox.getText().trim();
        String newDesc = settingsDescriptionBox.getText().trim();
        
        if (newName.isEmpty()) {
             MessageDialog.showMessageDialog(getTextGUI(), "Erro", "Nome não pode ser vazio.", MessageDialogButton.OK);
             return;
        }
        
        campaign.setName(newName);
        campaign.setDescription(newDesc);
        
        try {
            campaign = campaignService.updateCampaign(campaign);
            updateSummary();
            MessageDialog.showMessageDialog(getTextGUI(), "Sucesso", "Campanha atualizada!", MessageDialogButton.OK);
        } catch (GrimoireApiException e) {
            MessageDialog.showMessageDialog(getTextGUI(), "Erro", "Falha ao atualizar: " + e.getErrorMessage(), MessageDialogButton.OK);
        }
    }

    private void loadSessions() {
        sessionTimeline.clear();
        
        try {
            cachedSessions = sessionService.listByCampaign(campaign.getId().toString());
            cachedSessions.sort(java.util.Comparator.comparing(Session::getDate, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())));
            
            updateSummary();
            
            if (cachedSessions.isEmpty()) {
                RichTimelineEntry emptyEntry = new RichTimelineEntry(58);
                emptyEntry.addSegment("Nenhuma sessão encontrada.", GrimoireTheme.TIMESTAMP);
                sessionTimeline.addEntry(emptyEntry);
            } else {
                for (Session session : cachedSessions) {
                    RichTimelineEntry entry = new RichTimelineEntry(58);
                    entry.setPayload(session);
                    
                    String dateStr = session.getDate() != null ? 
                        session.getDate().format(DATE_FORMATTER) : "Sem data";
                    
                    entry.addSegment(session.getTitle(), GrimoireTheme.PRIMARY, java.util.EnumSet.of(com.googlecode.lanterna.SGR.BOLD));
                    entry.addSegment(" - ", GrimoireTheme.TEXT_NORMAL);
                    entry.addSegment(dateStr, GrimoireTheme.TIMESTAMP);
                    
                    if (session.getSummary() != null && !session.getSummary().isEmpty()) {
                        entry.addSegment("\n", GrimoireTheme.TEXT_NORMAL);
                        entry.addSegment(session.getSummary(), GrimoireTheme.TEXT_NORMAL);
                    }
                    
                    sessionTimeline.addEntry(entry);
                }
            }
        } catch (GrimoireApiException e) {
            RichTimelineEntry errorEntry = new RichTimelineEntry(58);
            errorEntry.addSegment("API: " + e.getErrorMessage(), GrimoireTheme.ERROR);
            if (e.getCause() != null) {
                errorEntry.addSegment(" | Causa: " + e.getCause().getMessage(), GrimoireTheme.ERROR);
            }
            sessionTimeline.addEntry(errorEntry);
        } catch (Exception e) {
            RichTimelineEntry errorEntry = new RichTimelineEntry(58);
            errorEntry.addSegment("Exceção: " + e.getClass().getSimpleName() + " - " + e.getMessage(), GrimoireTheme.ERROR);
            sessionTimeline.addEntry(errorEntry);
        }
    }
    
    private void onSessionSelect(Session session) {
            SessionDetailsWindow detailsWindow = new SessionDetailsWindow(
                session,
                sessionService,
                sheetService,
                campaignService,
                authService,
                campaign,
                currentUsername,
                currentUserId
            );
            navigateTo(detailsWindow);
        }
    
    private void onNewSessionClick() {
            SessionCreateWindow createWindow = new SessionCreateWindow(
                campaign.getId().toString(),
                sessionService,
                this::loadSessions
            );
            navigateTo(createWindow);
        }
}
