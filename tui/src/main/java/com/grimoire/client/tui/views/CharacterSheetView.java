package com.grimoire.client.tui.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.service.ApiClient;
import com.grimoire.client.tui.components.StyledButton;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.framework.View;
import com.grimoire.common.model.CharacterSheet;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CharacterSheetView implements View {
    private final ApiClient apiClient;
    private final Router router;
    private static CharacterSheet currentSheet;
    private static UUID currentCampaignId;

    // Header Fields
    private final TextBox nameBox = new TextBox();
    private final TextBox classBox = new TextBox();
    private final TextBox raceBox = new TextBox();
    private final TextBox levelBox = new TextBox();
    private final TextBox originBox = new TextBox();
    private final TextBox deityBox = new TextBox();

    // Attributes
    private final TextBox strBox = new TextBox();
    private final TextBox dexBox = new TextBox();
    private final TextBox conBox = new TextBox();
    private final TextBox intBox = new TextBox();
    private final TextBox wisBox = new TextBox();
    private final TextBox chaBox = new TextBox();

    // Vitals
    private final TextBox hpCurBox = new TextBox();
    private final TextBox hpMaxBox = new TextBox();
    private final TextBox mpCurBox = new TextBox();
    private final TextBox mpMaxBox = new TextBox();
    private final TextBox xpBox = new TextBox();

    // Combat
    private final TextBox defenseBox = new TextBox();
    
    // Inventory
    private final TextBox currencyBox = new TextBox();
    private final Label loadLabel = new Label("Load: 0/0");

    private final Label statusLabel = new Label("");

    public CharacterSheetView(ApiClient apiClient, Router router) {
        this.apiClient = apiClient;
        this.router = router;
    }

    public static void setSheet(CharacterSheet sheet, UUID campaignId) {
        currentSheet = sheet;
        currentCampaignId = campaignId;
    }

    @Override
    public void onEnter() {
        if (currentSheet != null) {
            // Header
            nameBox.setText(currentSheet.getName() != null ? currentSheet.getName() : "");
            classBox.setText(currentSheet.getCharacterClass() != null ? currentSheet.getCharacterClass() : "");
            raceBox.setText(currentSheet.getRace() != null ? currentSheet.getRace() : "");
            levelBox.setText(String.valueOf(currentSheet.getLevel()));
            originBox.setText(currentSheet.getOrigin() != null ? currentSheet.getOrigin() : "");
            deityBox.setText(currentSheet.getDeity() != null ? currentSheet.getDeity() : "");

            // Attributes
            strBox.setText(String.valueOf(currentSheet.getAttributes().getStrength()));
            dexBox.setText(String.valueOf(currentSheet.getAttributes().getDexterity()));
            conBox.setText(String.valueOf(currentSheet.getAttributes().getConstitution()));
            intBox.setText(String.valueOf(currentSheet.getAttributes().getIntelligence()));
            wisBox.setText(String.valueOf(currentSheet.getAttributes().getWisdom()));
            chaBox.setText(String.valueOf(currentSheet.getAttributes().getCharisma()));

            // Vitals
            hpCurBox.setText(String.valueOf(currentSheet.getStatus().getHpCurrent()));
            hpMaxBox.setText(String.valueOf(currentSheet.getStatus().getHpMax()));
            mpCurBox.setText(String.valueOf(currentSheet.getStatus().getMpCurrent()));
            mpMaxBox.setText(String.valueOf(currentSheet.getStatus().getMpMax()));
            xpBox.setText(String.valueOf(currentSheet.getStatus().getXp()));

            // Combat
            defenseBox.setText(String.valueOf(currentSheet.getCombatStats().getDefenseTotal()));

            // Inventory
            currencyBox.setText(String.valueOf(currentSheet.getInventory().getCurrency()));
            
        } else {
            // Defaults for new sheet
            nameBox.setText("");
            classBox.setText("");
            raceBox.setText("");
            levelBox.setText("1");
            originBox.setText("");
            deityBox.setText("");
            
            strBox.setText("10"); dexBox.setText("10"); conBox.setText("10");
            intBox.setText("10"); wisBox.setText("10"); chaBox.setText("10");
            
            hpCurBox.setText("10"); hpMaxBox.setText("10");
            mpCurBox.setText("5"); mpMaxBox.setText("5");
            xpBox.setText("0");
            
            defenseBox.setText("10");
            currencyBox.setText("0");
        }
        statusLabel.setText("");
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        Panel mainPanel = new Panel(new BorderLayout());

        // --- HEADER ---
        Panel header = new Panel(new GridLayout(4));
        header.addComponent(new Label("Name:")); header.addComponent(nameBox);
        header.addComponent(new Label("Class:")); header.addComponent(classBox);
        header.addComponent(new Label("Race:")); header.addComponent(raceBox);
        header.addComponent(new Label("Level:")); header.addComponent(levelBox);
        header.addComponent(new Label("Origin:")); header.addComponent(originBox);
        header.addComponent(new Label("Deity:")); header.addComponent(deityBox);
        
        mainPanel.addComponent(header.withBorder(Borders.singleLine("CHARACTER INFO")), BorderLayout.Location.TOP);

        // --- BODY (3 Columns) ---
        Panel body = new Panel(new GridLayout(3));

        // Col 1: Attributes & Combat
        Panel col1 = new Panel(new LinearLayout(Direction.VERTICAL));
        
        Panel attrPanel = new Panel(new GridLayout(2));
        attrPanel.addComponent(new Label("STR:")); attrPanel.addComponent(strBox);
        attrPanel.addComponent(new Label("DEX:")); attrPanel.addComponent(dexBox);
        attrPanel.addComponent(new Label("CON:")); attrPanel.addComponent(conBox);
        attrPanel.addComponent(new Label("INT:")); attrPanel.addComponent(intBox);
        attrPanel.addComponent(new Label("WIS:")); attrPanel.addComponent(wisBox);
        attrPanel.addComponent(new Label("CHA:")); attrPanel.addComponent(chaBox);
        col1.addComponent(attrPanel.withBorder(Borders.singleLine("ATTRIBUTES")));

        Panel combatPanel = new Panel(new GridLayout(2));
        combatPanel.addComponent(new Label("Defense:")); combatPanel.addComponent(defenseBox);
        col1.addComponent(combatPanel.withBorder(Borders.singleLine("COMBAT")));
        
        body.addComponent(col1);

        // Col 2: Vitals & Skills
        Panel col2 = new Panel(new LinearLayout(Direction.VERTICAL));
        
        Panel vitalsPanel = new Panel(new GridLayout(4));
        vitalsPanel.addComponent(new Label("HP:")); vitalsPanel.addComponent(hpCurBox);
        vitalsPanel.addComponent(new Label("/")); vitalsPanel.addComponent(hpMaxBox);
        
        vitalsPanel.addComponent(new Label("MP:")); vitalsPanel.addComponent(mpCurBox);
        vitalsPanel.addComponent(new Label("/")); vitalsPanel.addComponent(mpMaxBox);
        
        vitalsPanel.addComponent(new Label("XP:")); vitalsPanel.addComponent(xpBox);
        col2.addComponent(vitalsPanel.withBorder(Borders.singleLine("VITALS")));
        
        // Skills Placeholder
        Panel skillsPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        skillsPanel.addComponent(new Label("(Skills list here)"));
        col2.addComponent(skillsPanel.withBorder(Borders.singleLine("SKILLS")));
        
        body.addComponent(col2);

        // Col 3: Inventory
        Panel col3 = new Panel(new LinearLayout(Direction.VERTICAL));
        
        Panel invPanel = new Panel(new GridLayout(2));
        invPanel.addComponent(new Label("T$:")); invPanel.addComponent(currencyBox);
        col3.addComponent(invPanel.withBorder(Borders.singleLine("INVENTORY")));
        
        Panel backpack = new Panel(new LinearLayout(Direction.VERTICAL));
        backpack.addComponent(new Label("> Item 1"));
        backpack.addComponent(new Label("> Item 2"));
        col3.addComponent(backpack.withBorder(Borders.singleLine("BACKPACK")));
        
        body.addComponent(col3);

        mainPanel.addComponent(body, BorderLayout.Location.CENTER);

        // --- FOOTER (Actions) ---
        Panel footer = new Panel(new LinearLayout(Direction.HORIZONTAL));
        footer.addComponent(new StyledButton("SAVE CHARACTER", this::saveSheet));
        footer.addComponent(new EmptySpace(new TerminalSize(2,0)));
        footer.addComponent(new StyledButton("CANCEL", router::back));
        footer.addComponent(new EmptySpace(new TerminalSize(2,0)));
        footer.addComponent(statusLabel);
        
        mainPanel.addComponent(footer.withBorder(Borders.singleLine()), BorderLayout.Location.BOTTOM);

        return mainPanel;
    }

    private void saveSheet() {
        try {
            if (currentSheet == null) {
                currentSheet = new CharacterSheet();
                currentSheet.setId(UUID.randomUUID().toString());
                currentSheet.setCampaignId(currentCampaignId);
                if (apiClient.getCurrentUser() != null) {
                    currentSheet.setPlayerId(apiClient.getCurrentUser().getId());
                    currentSheet.setPlayerName(apiClient.getCurrentUser().getUsername());
                }
            }

            // Header
            currentSheet.setName(nameBox.getText());
            currentSheet.setCharacterClass(classBox.getText());
            currentSheet.setRace(raceBox.getText());
            currentSheet.setOrigin(originBox.getText());
            currentSheet.setDeity(deityBox.getText());
            try {
                currentSheet.setLevel(Integer.parseInt(levelBox.getText()));
            } catch (NumberFormatException e) {
                statusLabel.setText("Level must be a number."); return;
            }

            // Attributes
            try {
                currentSheet.getAttributes().setStrength(Integer.parseInt(strBox.getText()));
                currentSheet.getAttributes().setDexterity(Integer.parseInt(dexBox.getText()));
                currentSheet.getAttributes().setConstitution(Integer.parseInt(conBox.getText()));
                currentSheet.getAttributes().setIntelligence(Integer.parseInt(intBox.getText()));
                currentSheet.getAttributes().setWisdom(Integer.parseInt(wisBox.getText()));
                currentSheet.getAttributes().setCharisma(Integer.parseInt(chaBox.getText()));
            } catch (NumberFormatException e) {
                statusLabel.setText("Attributes must be numbers."); return;
            }

            // Vitals
            try {
                currentSheet.getStatus().setHpCurrent(Integer.parseInt(hpCurBox.getText()));
                currentSheet.getStatus().setHpMax(Integer.parseInt(hpMaxBox.getText()));
                currentSheet.getStatus().setMpCurrent(Integer.parseInt(mpCurBox.getText()));
                currentSheet.getStatus().setMpMax(Integer.parseInt(mpMaxBox.getText()));
                currentSheet.getStatus().setXp(Integer.parseInt(xpBox.getText()));
            } catch (NumberFormatException e) {
                statusLabel.setText("Vitals must be numbers."); return;
            }

            // Combat
            try {
                currentSheet.getCombatStats().setDefenseTotal(Integer.parseInt(defenseBox.getText()));
            } catch (NumberFormatException e) {
                statusLabel.setText("Defense must be a number."); return;
            }
            
            // Inventory
            try {
                currentSheet.getInventory().setCurrency(Integer.parseInt(currencyBox.getText()));
            } catch (NumberFormatException e) {
                statusLabel.setText("Currency must be a number."); return;
            }

            apiClient.saveSheet(currentSheet);
            router.back();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error saving sheet", e);
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @Override
    public String getTitle() {
        return "Character Sheet";
    }
}
