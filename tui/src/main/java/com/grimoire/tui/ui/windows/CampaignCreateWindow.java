package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.CampaignService;
import com.grimoire.common.model.Campaign;

public class CampaignCreateWindow extends StandardWindow {
    
    private final CampaignService campaignService;
    private final Runnable onSuccess;
    
    private TextBox nameTextBox;
    private TextBox systemTextBox;
    private TextBox descriptionTextBox;
    
    public CampaignCreateWindow(CampaignService campaignService, Runnable onSuccess) {
        super("Criar Nova Campanha");
        this.campaignService = campaignService;
        this.onSuccess = onSuccess;
        
        Panel mainPanel = new Panel(new GridLayout(2));
        
        mainPanel.addComponent(new Label("Nome:"));
        nameTextBox = new TextBox(new TerminalSize(30, 1));
        mainPanel.addComponent(nameTextBox);
        
        mainPanel.addComponent(new Label("Sistema:"));
        systemTextBox = new TextBox(new TerminalSize(30, 1));
        mainPanel.addComponent(systemTextBox);
        
        mainPanel.addComponent(new Label("Descrição:"));
        descriptionTextBox = new TextBox(new TerminalSize(50, 5), TextBox.Style.MULTI_LINE);
        mainPanel.addComponent(descriptionTextBox);
        
        mainPanel.addComponent(new EmptySpace());
        
        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        
        Button createButton = new Button("Criar", this::onCreateClick);
        Button cancelButton = new Button("Cancelar", this::close);
        
        buttonPanel.addComponent(createButton);
        buttonPanel.addComponent(cancelButton);
        
        mainPanel.addComponent(buttonPanel);
        
        setComponent(mainPanel);
    }
    
    private void onCreateClick() {
        String name = nameTextBox.getText().trim();
        String system = systemTextBox.getText().trim();
        String description = descriptionTextBox.getText().trim();
        
        if (name.isEmpty()) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Nome da campanha é obrigatório.",
                MessageDialogButton.OK
            );
            return;
        }
        
        if (system.isEmpty()) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Sistema de jogo é obrigatório.",
                MessageDialogButton.OK
            );
            return;
        }
        
        try {
            Campaign campaign = campaignService.createCampaign(name, description, system);
            
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Sucesso",
                "Campanha \"" + campaign.getName() + "\" criada com sucesso!",
                MessageDialogButton.OK
            );
            
            close();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (GrimoireApiException e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Erro ao criar campanha: " + e.getErrorMessage(),
                MessageDialogButton.OK
            );
        } catch (Exception e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Erro inesperado: " + e.getMessage(),
                MessageDialogButton.OK
            );
        }
    }
}
