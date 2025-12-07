package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.AuthService;
import com.grimoire.client.service.CampaignService;
import com.grimoire.client.service.SessionService;
import com.grimoire.client.service.SheetService;
import com.grimoire.common.model.Campaign;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.model.Session;
import com.grimoire.common.model.User;
import com.grimoire.tui.ui.components.RichTimelineEntry;
import com.grimoire.tui.ui.components.SessionTimeline;
import com.grimoire.tui.ui.styles.GrimoireTheme;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class UserProfileWindow extends StandardWindow {

    private final AuthService authService;
    private final CampaignService campaignService;
    private final SessionService sessionService;
    private final SheetService sheetService;
    private final User user;
    private final Runnable onLogout;

    private final SessionTimeline campaignTimeline;
    private final SessionTimeline sheetTimeline;

    private Label summaryCampaignsLabel;
    private Label summarySessionsLabel;
    private Label summarySheetsLabel;
    
    private TextBox settingsUsernameBox;
    private TextBox settingsPasswordBox;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final User targetUser;
    private final boolean isCurrentUser;

    public UserProfileWindow(AuthService authService, CampaignService campaignService, SessionService sessionService, SheetService sheetService, Runnable onLogout, User targetUser) {
        super("Perfil: " + targetUser.getUsername());
        this.authService = authService;
        this.campaignService = campaignService;
        this.sessionService = sessionService;
        this.sheetService = sheetService;
        this.user = authService.getCurrentUser();
        this.targetUser = targetUser;
        this.onLogout = onLogout;
        this.isCurrentUser = user.getId().equals(targetUser.getId());

        // Initialize Timelines
        campaignTimeline = new SessionTimeline();
        campaignTimeline.setPreferredSize(new TerminalSize(60, 10));
        campaignTimeline.setSelectionMode(true);
        campaignTimeline.setOnEntrySelected(payload -> {
            if (payload instanceof Campaign) {
                onCampaignSelect((Campaign) payload);
            }
        });
        
        sheetTimeline = new SessionTimeline();
        sheetTimeline.setPreferredSize(new TerminalSize(60, 10));
        sheetTimeline.setSelectionMode(true);
        sheetTimeline.setOnEntrySelected(payload -> {
            if (payload instanceof CharacterSheet) {
                onSheetSelect((CharacterSheet) payload);
            }
        });

        // Add Tabs
        addTab("Resumo", createSummaryTab());
        addTab("Campanhas", createCampaignsTab());
        addTab("Fichas", createSheetsTab());
        
        if (isCurrentUser) {
            addTab("Configurações", createSettingsTab());
        }
        
        // Load initial data
        loadData();
    }
    
    // Constructor for current user (convenience)
    public UserProfileWindow(AuthService authService, CampaignService campaignService, SessionService sessionService, SheetService sheetService, Runnable onLogout) {
        this(authService, campaignService, sessionService, sheetService, onLogout, authService.getCurrentUser());
    }

    private Component createSummaryTab() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        
        panel.addComponent(new Label("Usuário: " + targetUser.getUsername()).addStyle(com.googlecode.lanterna.SGR.BOLD));
        panel.addComponent(new Label("ID: " + targetUser.getId()));
        panel.addComponent(new EmptySpace());
        
        summaryCampaignsLabel = new Label("Campanhas: Carregando...");
        panel.addComponent(summaryCampaignsLabel);
        
        summarySheetsLabel = new Label("Fichas: Carregando...");
        panel.addComponent(summarySheetsLabel);
        
        return panel;
    }

    private Component createCampaignsTab() {
        Panel panel = new Panel(new BorderLayout());
        panel.addComponent(new Label("Campanhas:"), BorderLayout.Location.TOP);
        panel.addComponent(campaignTimeline, BorderLayout.Location.CENTER);
        return panel;
    }

    private Component createSheetsTab() {
        Panel panel = new Panel(new BorderLayout());
        panel.addComponent(new Label("Fichas:"), BorderLayout.Location.TOP);
        panel.addComponent(sheetTimeline, BorderLayout.Location.CENTER);
        return panel;
    }

    private Component createSettingsTab() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        
        panel.addComponent(new Label("Editar Perfil"));
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("Novo Nome de Usuário:"));
        settingsUsernameBox = new TextBox(user.getUsername());
        panel.addComponent(settingsUsernameBox);
        
        panel.addComponent(new Label("Nova Senha (deixe em branco para manter):"));
        settingsPasswordBox = new TextBox("").setMask('*');
        panel.addComponent(settingsPasswordBox);
        
        panel.addComponent(new EmptySpace());
        
        Button saveButton = new Button("Salvar Alterações", this::onSaveSettings);
        panel.addComponent(saveButton);
        
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Separator(Direction.HORIZONTAL));
        panel.addComponent(new EmptySpace());
        
        Button deleteButton = new Button("Deletar Conta", this::onDeleteAccount);
        // deleteButton.setTheme(GrimoireTheme.DANGER);
        panel.addComponent(deleteButton);
        
        return panel;
    }

    private void loadData() {
        loadCampaigns();
        loadSheets();
    }

    private void loadCampaigns() {
        campaignTimeline.clear();
        try {
            List<Campaign> campaigns = campaignService.getCampaignsByPlayer(targetUser.getId());
            java.util.Collections.reverse(campaigns);
            
            summaryCampaignsLabel.setText("Campanhas: " + campaigns.size());
            
            if (campaigns.isEmpty()) {
                campaignTimeline.addEntry(createEmptyEntry("Nenhuma campanha encontrada."));
            } else {
                for (Campaign c : campaigns) {
                    RichTimelineEntry entry = new RichTimelineEntry(58);
                    entry.setPayload(c);
                    entry.addSegment(c.getName(), GrimoireTheme.PRIMARY, java.util.EnumSet.of(com.googlecode.lanterna.SGR.BOLD));
                    
                    String role = c.getOwnerId().equals(targetUser.getId()) ? " (Mestre)" : " (Jogador)";
                    entry.addSegment(role, GrimoireTheme.SECONDARY);
                    
                    if (c.getDescription() != null && !c.getDescription().isEmpty()) {
                        entry.addSegment("\n" + c.getDescription(), GrimoireTheme.TEXT_NORMAL);
                    }
                    campaignTimeline.addEntry(entry);
                }
            }
        } catch (GrimoireApiException e) {
            campaignTimeline.addEntry(createErrorEntry(e));
        }
    }

    private void loadSheets() {
        sheetTimeline.clear();
        try {
            List<CharacterSheet> sheets = sheetService.getSheetsByPlayer(targetUser.getId());
            
            summarySheetsLabel.setText("Fichas: " + sheets.size());
            
            if (sheets.isEmpty()) {
                sheetTimeline.addEntry(createEmptyEntry("Nenhuma ficha encontrada."));
            } else {
                for (CharacterSheet s : sheets) {
                    RichTimelineEntry entry = new RichTimelineEntry(58);
                    entry.setPayload(s);
                    entry.addSegment(s.getName(), GrimoireTheme.PRIMARY);
                    entry.addSegment(" (" + s.getSystem() + " Nvl " + s.getLevel() + ")", GrimoireTheme.TEXT_NORMAL);
                    sheetTimeline.addEntry(entry);
                }
            }
        } catch (GrimoireApiException e) {
            sheetTimeline.addEntry(createErrorEntry(e));
        }
    }
    
    private void onCampaignSelect(Campaign campaign) {
            CampaignWindow campaignWindow = new CampaignWindow(
                campaign,
                sessionService,
                sheetService,
                campaignService,
                authService, // Pass authService
                user.getUsername(),
                user.getId(),
                this::loadCampaigns
            );
            navigateTo(campaignWindow);
        }
    
    private void onSheetSelect(CharacterSheet sheet) {
        CharacterSheetWindow sheetWindow = new CharacterSheetWindow(
            sheet,
            sheetService,
            isCurrentUser // Only editable if it's the current user's profile
        );
        navigateTo(sheetWindow);
    }

    private RichTimelineEntry createEmptyEntry(String text) {
        RichTimelineEntry entry = new RichTimelineEntry(58);
        entry.addSegment(text, GrimoireTheme.TIMESTAMP);
        return entry;
    }

    private RichTimelineEntry createErrorEntry(Exception e) {
        RichTimelineEntry entry = new RichTimelineEntry(58);
        entry.addSegment("Erro: " + e.getMessage(), GrimoireTheme.ERROR);
        return entry;
    }

    private void onSessionSelect(Session session) {
        // Removed
    }

    private void onSaveSettings() {
        String newUsername = settingsUsernameBox.getText().trim();
        String newPassword = settingsPasswordBox.getText().trim();
        
        if (newUsername.isEmpty()) {
            MessageDialog.showMessageDialog(getTextGUI(), "Erro", "Nome de usuário não pode ser vazio.", MessageDialogButton.OK);
            return;
        }
        
        user.setUsername(newUsername);
        if (!newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        
        try {
            authService.updateUser(user);
            MessageDialog.showMessageDialog(getTextGUI(), "Sucesso", "Perfil atualizado!", MessageDialogButton.OK);
        } catch (GrimoireApiException e) {
            MessageDialog.showMessageDialog(getTextGUI(), "Erro", "Falha ao atualizar: " + e.getErrorMessage(), MessageDialogButton.OK);
        }
    }

    private void onDeleteAccount() {
        MessageDialogButton result = MessageDialog.showMessageDialog(getTextGUI(), "Confirmar Deleção", 
            "Tem certeza que deseja deletar sua conta?\nEsta ação é irreversível.\nSeus dados (campanhas, fichas) permanecerão no servidor.", MessageDialogButton.Yes, MessageDialogButton.No);
            
        if (result == MessageDialogButton.Yes) {
            try {
                authService.deleteUser(user.getId());
                MessageDialog.showMessageDialog(getTextGUI(), "Adeus", "Conta deletada com sucesso.", MessageDialogButton.OK);
                if (onLogout != null) {
                    onLogout.run();
                }
                close();
            } catch (GrimoireApiException e) {
                MessageDialog.showMessageDialog(getTextGUI(), "Erro", "Falha ao deletar conta: " + e.getErrorMessage(), MessageDialogButton.OK);
            }
        }
    }
}
