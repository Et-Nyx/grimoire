package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.service.SheetService;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.util.CharacterCalculationService;
import com.grimoire.common.util.CharacterClassData;
import com.grimoire.tui.ui.components.WrappedTextBox;
import com.grimoire.tui.ui.styles.GrimoireTheme;

public class CharacterSheetWindow extends StandardWindow {

    private final CharacterSheet sheet;
    private final SheetService sheetService;
    private final boolean isEditable;
    private final Runnable onCloseCallback;

    // UI Components for editing
    private TextBox nameBox;
    private TextBox classBox;
    private TextBox raceBox;
    private TextBox levelBox;
    private TextBox strBox, dexBox, conBox, intBox, wisBox, chaBox;
    private TextBox hpCurBox, hpMaxBox, mpCurBox, mpMaxBox;
    private TextBox defenseBox;
    private WrappedTextBox backgroundBox;

    public CharacterSheetWindow(CharacterSheet sheet, SheetService sheetService, boolean isEditable) {
        this(sheet, sheetService, isEditable, null);
    }

    public CharacterSheetWindow(CharacterSheet sheet, SheetService sheetService, boolean isEditable, Runnable onCloseCallback) {
        super("Ficha: " + sheet.getName());
        this.sheet = sheet;
        this.sheetService = sheetService;
        this.isEditable = isEditable;
        this.onCloseCallback = onCloseCallback;

        // Add Tabs
        addTab("Principal", createMainTab());
        addTab("Combate", createCombatTab());
        // Skills, Magic, Inventory are more complex to edit, keeping read-only or simple for now as per request "todos os campos... TextBox" implies flat fields mostly.
        // For lists like Skills, we'd need a dynamic list editor. For now, I'll focus on the main attributes and stats which are flat.
        addTab("Perícias", createSkillsTab()); 
        addTab("Magia", createMagicTab());
        addTab("Inventário", createInventoryTab());
        addTab("Lore", createLoreTab());
        
        // Add Save Button if editable
        if (isEditable) {
            Panel footerPanel = getRootPanel(); // Access root panel to add button at bottom
            // Actually, StandardWindow has a footer label. We can add a button panel above it or modify StandardWindow.
            // But StandardWindow layout is fixed.
            // Let's add a "Actions" tab? No, that's hidden.
            // Let's add it to the Main Tab? No, it should be global.
            // Let's use a separate panel in the content container? No.
            
            // Hack: Add it to the top tab bar? No.
            // Let's just add it to every tab? Tedious.
            
            // Best approach: Add a "Salvar" button to the footer of StandardWindow? 
            // StandardWindow doesn't support custom footer buttons easily without modification.
            
            // Alternative: Add it to the top of the window, below tabs?
            // Or just add it to the Main Tab for now, as that's where most edits happen.
            // But the user might edit Lore.
            
            // Let's modify StandardWindow to allow adding footer components? Too risky.
            
            // I'll add a "Salvar" button to the Main Tab and Lore Tab.
            // Actually, I can just add it to the top of the content container in `switchTab`? No.
            
            // Let's just add it to the Main Tab.
        }
    }
    
    @Override
    public void close() {
        // Auto-save removed
        super.close();
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }
    
    private void onSaveClick() {
        saveChanges();
        com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(getTextGUI(), "Sucesso", "Ficha salva com sucesso!", com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
    }
    
    private void saveChanges() {
        try {
            // Update sheet object from UI components
            if (nameBox != null) sheet.setName(nameBox.getText());
            if (classBox != null) sheet.setCharacterClass(classBox.getText());
            if (raceBox != null) sheet.setRace(raceBox.getText());
            try {
                if (levelBox != null) sheet.setLevel(Integer.parseInt(levelBox.getText()));
            } catch (NumberFormatException e) { /* Ignore invalid number */ }
            
            // Attributes
            if (sheet.getAttributes() == null) sheet.setAttributes(new com.grimoire.common.model.Attributes());
            try { if (strBox != null) sheet.getAttributes().setStrength(Integer.parseInt(strBox.getText())); } catch (Exception e) {}
            try { if (dexBox != null) sheet.getAttributes().setDexterity(Integer.parseInt(dexBox.getText())); } catch (Exception e) {}
            try { if (conBox != null) sheet.getAttributes().setConstitution(Integer.parseInt(conBox.getText())); } catch (Exception e) {}
            try { if (intBox != null) sheet.getAttributes().setIntelligence(Integer.parseInt(intBox.getText())); } catch (Exception e) {}
            try { if (wisBox != null) sheet.getAttributes().setWisdom(Integer.parseInt(wisBox.getText())); } catch (Exception e) {}
            try { if (chaBox != null) sheet.getAttributes().setCharisma(Integer.parseInt(chaBox.getText())); } catch (Exception e) {}
            
            // Status
            if (sheet.getStatus() == null) sheet.setStatus(new com.grimoire.common.model.Status());
            try { if (hpCurBox != null) sheet.getStatus().setHpCurrent(Integer.parseInt(hpCurBox.getText())); } catch (Exception e) {}
            try { if (hpMaxBox != null) sheet.getStatus().setHpMax(Integer.parseInt(hpMaxBox.getText())); } catch (Exception e) {}
            try { if (mpCurBox != null) sheet.getStatus().setMpCurrent(Integer.parseInt(mpCurBox.getText())); } catch (Exception e) {}
            try { if (mpMaxBox != null) sheet.getStatus().setMpMax(Integer.parseInt(mpMaxBox.getText())); } catch (Exception e) {}
            
            // Lore
            if (backgroundBox != null) sheet.setBackground(backgroundBox.getUnwrappedText());
            
            // Save to server
            sheetService.updateSheet(sheet);
            
        } catch (Exception e) {
             com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(getTextGUI(), "Erro", "Erro ao salvar ficha: " + e.getMessage(), com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
        }
    }

    private Component createMainTab() {
        Panel panel = new Panel(new GridLayout(3)); // 3 columns for selector buttons

        panel.addComponent(new Label("Nome:"));
        nameBox = new TextBox(sheet.getName());
        nameBox.setReadOnly(!isEditable);
        panel.addComponent(nameBox);
        panel.addComponent(new EmptySpace());

        // Classe com seletor
        panel.addComponent(new Label("Classe:"));
        classBox = new TextBox(sheet.getCharacterClass());
        classBox.setReadOnly(!isEditable);
        panel.addComponent(classBox);
        if (isEditable) {
            panel.addComponent(new Button("▼", () -> {
                String[] classNames = CharacterClassData.getAllClasses().stream()
                    .map(CharacterClassData::name)
                    .sorted()
                    .toArray(String[]::new);
                String selected = com.googlecode.lanterna.gui2.dialogs.ListSelectDialog.showDialog(
                    getTextGUI(), "Selecionar Classe", "Escolha uma classe:", classNames);
                if (selected != null) {
                    classBox.setText(selected);
                }
            }));
        } else {
            panel.addComponent(new EmptySpace());
        }

        // Raça com seletor
        panel.addComponent(new Label("Raça:"));
        raceBox = new TextBox(sheet.getRace());
        raceBox.setReadOnly(!isEditable);
        panel.addComponent(raceBox);
        if (isEditable) {
            panel.addComponent(new Button("▼", () -> {
                String[] raceNames = com.grimoire.common.util.CharacterRaceData.getAllRaceNames().stream()
                    .sorted()
                    .toArray(String[]::new);
                String selected = com.googlecode.lanterna.gui2.dialogs.ListSelectDialog.showDialog(
                    getTextGUI(), "Selecionar Raça", "Escolha uma raça:", raceNames);
                if (selected != null) {
                    raceBox.setText(selected);
                }
            }));
        } else {
            panel.addComponent(new EmptySpace());
        }

        panel.addComponent(new Label("Nível:"));
        levelBox = new TextBox(String.valueOf(sheet.getLevel()));
        levelBox.setReadOnly(!isEditable);
        panel.addComponent(levelBox);
        panel.addComponent(new EmptySpace());
        
        // Informação da raça
        com.grimoire.common.util.CharacterRaceData raceData = 
            com.grimoire.common.util.CharacterRaceData.getByName(sheet.getRace());
        if (com.grimoire.common.util.CharacterRaceData.isKnownRace(sheet.getRace())) {
            panel.addComponent(new Label("Mod. Raciais:"));
            panel.addComponent(new Label(raceData.getModifiersDescription()));
            panel.addComponent(new EmptySpace());
            
            panel.addComponent(new Label("Deslocamento:"));
            panel.addComponent(new Label(raceData.baseSpeed() + "m (" + raceData.size() + ")"));
            panel.addComponent(new EmptySpace());
        }
        
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());

        // Attributes
        panel.addComponent(new Label("Atributos").addStyle(com.googlecode.lanterna.SGR.BOLD));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());

        com.grimoire.common.model.Attributes attrs = sheet.getAttributes();
        if (attrs == null) attrs = new com.grimoire.common.model.Attributes();

        panel.addComponent(new Label("FOR:"));
        strBox = new TextBox(String.valueOf(attrs.getStrength()));
        strBox.setReadOnly(!isEditable);
        panel.addComponent(strBox);
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("DES:"));
        dexBox = new TextBox(String.valueOf(attrs.getDexterity()));
        dexBox.setReadOnly(!isEditable);
        panel.addComponent(dexBox);
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("CON:"));
        conBox = new TextBox(String.valueOf(attrs.getConstitution()));
        conBox.setReadOnly(!isEditable);
        panel.addComponent(conBox);
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("INT:"));
        intBox = new TextBox(String.valueOf(attrs.getIntelligence()));
        intBox.setReadOnly(!isEditable);
        panel.addComponent(intBox);
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("SAB:"));
        wisBox = new TextBox(String.valueOf(attrs.getWisdom()));
        wisBox.setReadOnly(!isEditable);
        panel.addComponent(wisBox);
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("CAR:"));
        chaBox = new TextBox(String.valueOf(attrs.getCharisma()));
        chaBox.setReadOnly(!isEditable);
        panel.addComponent(chaBox);
        panel.addComponent(new EmptySpace());
        
        // Validação Point Buy
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        com.grimoire.common.util.ValidationResult validation = 
            CharacterCalculationService.validatePointBuy(attrs);
        if (validation.isValid()) {
            panel.addComponent(new Label("Point Buy:"));
            panel.addComponent(new Label(validation.message()));
        } else {
            panel.addComponent(new Label("Point Buy:").addStyle(com.googlecode.lanterna.SGR.BOLD));
            panel.addComponent(new Label("⚠ " + validation.message()));
        }
        panel.addComponent(new EmptySpace());

        if (isEditable) {
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            
            // Primeira linha de botões
            Panel buttonPanel1 = new Panel(new LinearLayout(Direction.HORIZONTAL));
            buttonPanel1.addComponent(new Button("Salvar Mudanças", this::onSaveClick));
            buttonPanel1.addComponent(new Button("Validar Atributos", () -> {
                try {
                    com.grimoire.common.model.Attributes currentAttrs = com.grimoire.common.model.Attributes.builder()
                        .strength(Integer.parseInt(strBox.getText()))
                        .dexterity(Integer.parseInt(dexBox.getText()))
                        .constitution(Integer.parseInt(conBox.getText()))
                        .intelligence(Integer.parseInt(intBox.getText()))
                        .wisdom(Integer.parseInt(wisBox.getText()))
                        .charisma(Integer.parseInt(chaBox.getText()))
                        .build();
                    
                    com.grimoire.common.util.ValidationResult result = 
                        CharacterCalculationService.validatePointBuy(currentAttrs);
                    
                    com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                        getTextGUI(),
                        result.isValid() ? "Válido" : "Inválido",
                        result.message(),
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                    );
                } catch (NumberFormatException e) {
                    com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                        getTextGUI(), "Erro", "Digite apenas números nos atributos.",
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                    );
                }
            }));
            panel.addComponent(buttonPanel1);
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            
            // Segunda linha de botões - Aplicar Raça
            Panel buttonPanel2 = new Panel(new LinearLayout(Direction.HORIZONTAL));
            buttonPanel2.addComponent(new Button("Aplicar Mod. Raciais", () -> {
                String currentRace = raceBox.getText();
                if (!com.grimoire.common.util.CharacterRaceData.isKnownRace(currentRace)) {
                    com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                        getTextGUI(), "Raça Desconhecida", 
                        "A raça '" + currentRace + "' não está cadastrada.\nSelecione uma raça conhecida.",
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                    );
                    return;
                }
                
                com.grimoire.common.util.CharacterRaceData race = 
                    com.grimoire.common.util.CharacterRaceData.getByName(currentRace);
                
                try {
                    int curStr = Integer.parseInt(strBox.getText());
                    int curDex = Integer.parseInt(dexBox.getText());
                    int curCon = Integer.parseInt(conBox.getText());
                    int curInt = Integer.parseInt(intBox.getText());
                    int curWis = Integer.parseInt(wisBox.getText());
                    int curCha = Integer.parseInt(chaBox.getText());
                    
                    int newStr = curStr + race.strengthMod();
                    int newDex = curDex + race.dexterityMod();
                    int newCon = curCon + race.constitutionMod();
                    int newInt = curInt + race.intelligenceMod();
                    int newWis = curWis + race.wisdomMod();
                    int newCha = curCha + race.charismaMod();
                    
                    StringBuilder preview = new StringBuilder();
                    preview.append("Aplicar modificadores de ").append(race.name()).append("?\n\n");
                    if (race.strengthMod() != 0) preview.append("FOR: ").append(curStr).append(" → ").append(newStr).append("\n");
                    if (race.dexterityMod() != 0) preview.append("DES: ").append(curDex).append(" → ").append(newDex).append("\n");
                    if (race.constitutionMod() != 0) preview.append("CON: ").append(curCon).append(" → ").append(newCon).append("\n");
                    if (race.intelligenceMod() != 0) preview.append("INT: ").append(curInt).append(" → ").append(newInt).append("\n");
                    if (race.wisdomMod() != 0) preview.append("SAB: ").append(curWis).append(" → ").append(newWis).append("\n");
                    if (race.charismaMod() != 0) preview.append("CAR: ").append(curCha).append(" → ").append(newCha).append("\n");
                    
                    if (race.getTotalModifiers() == 0) {
                        preview.append("Esta raça não possui modificadores fixos.\n(Escolha manual de bônus necessária)");
                    }
                    
                    var result = com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                        getTextGUI(), "Aplicar Raça", preview.toString(),
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes,
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.No
                    );
                    
                    if (result == com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes) {
                        strBox.setText(String.valueOf(newStr));
                        dexBox.setText(String.valueOf(newDex));
                        conBox.setText(String.valueOf(newCon));
                        intBox.setText(String.valueOf(newInt));
                        wisBox.setText(String.valueOf(newWis));
                        chaBox.setText(String.valueOf(newCha));
                    }
                } catch (NumberFormatException e) {
                    com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                        getTextGUI(), "Erro", "Digite apenas números nos atributos.",
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                    );
                }
            }));
            panel.addComponent(buttonPanel2);
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
        }

        return panel;
    }

    private Component createCombatTab() {
        Panel panel = new Panel(new GridLayout(4)); // 4 columns for compact layout
        panel.addComponent(new Label("Estatísticas de Combate").addStyle(com.googlecode.lanterna.SGR.BOLD));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        com.grimoire.common.model.Status status = sheet.getStatus();
        if (status == null) status = new com.grimoire.common.model.Status();
        
        // PV Atual e Máximo
        panel.addComponent(new Label("PV Atual:"));
        hpCurBox = new TextBox(String.valueOf(status.getHpCurrent()));
        hpCurBox.setReadOnly(!isEditable);
        panel.addComponent(hpCurBox);
        
        panel.addComponent(new Label("PV Máx:"));
        hpMaxBox = new TextBox(String.valueOf(status.getHpMax()));
        hpMaxBox.setReadOnly(!isEditable);
        panel.addComponent(hpMaxBox);
        
        // PM Atual e Máximo
        panel.addComponent(new Label("PM Atual:"));
        mpCurBox = new TextBox(String.valueOf(status.getMpCurrent()));
        mpCurBox.setReadOnly(!isEditable);
        panel.addComponent(mpCurBox);
        
        panel.addComponent(new Label("PM Máx:"));
        mpMaxBox = new TextBox(String.valueOf(status.getMpMax()));
        mpMaxBox.setReadOnly(!isEditable);
        panel.addComponent(mpMaxBox);
        
        // Separador visual
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        // === Valores Calculados (T20 JdA) ===
        panel.addComponent(new Label("Valores Calculados").addStyle(com.googlecode.lanterna.SGR.BOLD));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        // PV Calculado
        int calculatedHP = CharacterCalculationService.calculateMaxHP(sheet);
        panel.addComponent(new Label("PV Calculado:"));
        panel.addComponent(new Label(String.valueOf(calculatedHP)));
        
        // PM Calculado
        int calculatedMP = CharacterCalculationService.calculateMaxMP(sheet);
        panel.addComponent(new Label("PM Calculado:"));
        panel.addComponent(new Label(String.valueOf(calculatedMP)));
        
        // Defesa Calculada
        int defense = CharacterCalculationService.calculateDefense(sheet);
        panel.addComponent(new Label("Defesa:"));
        panel.addComponent(new Label(String.valueOf(defense)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        // Bônus de Ataque
        int meleeAtk = CharacterCalculationService.calculateMeleeAttackBonus(sheet);
        int rangedAtk = CharacterCalculationService.calculateRangedAttackBonus(sheet);
        panel.addComponent(new Label("Ataque (Corpo):"));
        panel.addComponent(new Label((meleeAtk >= 0 ? "+" : "") + meleeAtk));
        panel.addComponent(new Label("Ataque (Dist.):"));
        panel.addComponent(new Label((rangedAtk >= 0 ? "+" : "") + rangedAtk));
        
        // CD de Magias (se aplicável)
        int spellDC = CharacterCalculationService.calculateSpellDC(sheet);
        panel.addComponent(new Label("CD Magias:"));
        panel.addComponent(new Label(String.valueOf(spellDC)));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        // Informação da classe
        CharacterClassData classData = CharacterClassData.getByName(sheet.getCharacterClass());
        if (CharacterClassData.isKnownClass(sheet.getCharacterClass())) {
            panel.addComponent(new Label("Classe:"));
            panel.addComponent(new Label(classData.name() + " (PV:" + classData.baseHP() + "+" + classData.hpPerLevel() + "/nv)"));
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
        }
        
        // Botão Aplicar Calculados (se editável)
        if (isEditable) {
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            
            final int finalCalculatedHP = calculatedHP;
            final int finalCalculatedMP = calculatedMP;
            
            panel.addComponent(new Button("Aplicar Calculados", () -> {
                var result = com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                    getTextGUI(), 
                    "Confirmar", 
                    "Aplicar valores calculados?\nPV Máx: " + finalCalculatedHP + "\nPM Máx: " + finalCalculatedMP,
                    com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes,
                    com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.No
                );
                if (result == com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes) {
                    hpMaxBox.setText(String.valueOf(finalCalculatedHP));
                    mpMaxBox.setText(String.valueOf(finalCalculatedMP));
                    if ("0".equals(hpCurBox.getText())) {
                        hpCurBox.setText(String.valueOf(finalCalculatedHP));
                    }
                    if ("0".equals(mpCurBox.getText())) {
                        mpCurBox.setText(String.valueOf(finalCalculatedMP));
                    }
                }
            }));
            panel.addComponent(new EmptySpace());
            panel.addComponent(new Button("Salvar Combate", this::onSaveClick));
            panel.addComponent(new EmptySpace());
        }
        
        // === Seção de Ataques ===
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("Ataques").addStyle(com.googlecode.lanterna.SGR.BOLD));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());
        
        if (sheet.getAttacks() != null && !sheet.getAttacks().isEmpty()) {
            panel.addComponent(new Label("Nome").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            panel.addComponent(new Label("Ataque").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            panel.addComponent(new Label("Dano").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            panel.addComponent(new Label("Tipo").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            
            for (var attack : sheet.getAttacks()) {
                panel.addComponent(new Label(attack.getName() != null ? attack.getName() : "-"));
                panel.addComponent(new Label(attack.getAttackBonus() != null ? attack.getAttackBonus() : "-"));
                panel.addComponent(new Label(attack.getDamage() != null ? attack.getDamage() : "-"));
                
                if (isEditable) {
                    final var currentAttack = attack;
                    panel.addComponent(new Button("Remover", () -> {
                        sheet.getAttacks().remove(currentAttack);
                        com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                            getTextGUI(), "Ataque Removido", 
                            currentAttack.getName() + " removido. Salve para persistir.",
                            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                        );
                    }));
                } else {
                    panel.addComponent(new Label(attack.getType() != null ? attack.getType() : "-"));
                }
            }
        } else {
            panel.addComponent(new Label("Nenhum ataque cadastrado."));
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
        }
        
        // Botão adicionar ataque
        if (isEditable) {
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            
            panel.addComponent(new Button("Adicionar Ataque", () -> {
                // Diálogo para nome
                String name = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Nome do Ataque", "Digite o nome da arma/ataque:", "");
                if (name == null || name.trim().isEmpty()) return;
                
                // Diálogo para tipo (Corpo a corpo ou Distância)
                String[] types = {"Corpo a Corpo (FOR)", "Distância (DES)", "Natural (FOR)"};
                String type = com.googlecode.lanterna.gui2.dialogs.ListSelectDialog.showDialog(
                    getTextGUI(), "Tipo de Ataque", "Selecione o tipo:", types);
                if (type == null) return;
                
                // Diálogo para dano
                String damage = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Dano", "Digite a fórmula de dano:", "1d8");
                if (damage == null) damage = "1d8";
                
                // Calcular bônus de ataque automaticamente
                int attrMod = 0;
                String damageType = "Corte";
                if (type.contains("DES")) {
                    attrMod = sheet.getAttributes().getDexterity();
                    damageType = "Perfuração";
                } else {
                    attrMod = sheet.getAttributes().getStrength();
                }
                
                int halfLevel = sheet.getLevel() / 2;
                int totalAtk = halfLevel + attrMod;
                String attackBonus = (totalAtk >= 0 ? "+" : "") + totalAtk;
                
                // Criar o ataque
                com.grimoire.common.model.Attack newAttack = com.grimoire.common.model.Attack.builder()
                    .name(name.trim())
                    .attackBonus(attackBonus)
                    .damage(damage)
                    .critical("20/x2")
                    .type(damageType)
                    .range(type.contains("Distância") ? "Curto" : "Corpo a corpo")
                    .build();
                
                if (sheet.getAttacks() == null) {
                    sheet.setAttacks(new java.util.ArrayList<>());
                }
                sheet.getAttacks().add(newAttack);
                
                com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                    getTextGUI(), "Ataque Adicionado", 
                    name + " adicionado com bônus " + attackBonus + ".\nSalve para persistir.",
                    com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                );
            }));
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
            panel.addComponent(new EmptySpace());
        }
        
        return panel;
    }

    private Component createSkillsTab() {
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(new Label("Perícias (T20 JdA)").addStyle(com.googlecode.lanterna.SGR.BOLD));
        mainPanel.addComponent(new EmptySpace());
        
        // Obter penalidade de armadura
        int armorPenalty = 0;
        if (sheet.getCombatStats() != null) {
            armorPenalty = sheet.getCombatStats().getArmorPenalty();
        }
        
        // Mostrar bônus de treinamento do patamar
        int trainingBonus = CharacterCalculationService.getTrainingBonus(sheet.getLevel());
        mainPanel.addComponent(new Label("Nível " + sheet.getLevel() + " (Bônus Treinamento: +" + trainingBonus + ")"));
        if (armorPenalty > 0) {
            mainPanel.addComponent(new Label("Penalidade de Armadura: -" + armorPenalty));
        }
        mainPanel.addComponent(new EmptySpace());
        
        // Lista de perícias existentes
        final int finalArmorPenalty = armorPenalty;
        if (sheet.getSkills() != null && !sheet.getSkills().isEmpty()) {
            Panel skillsGrid = new Panel(new GridLayout(3));
            skillsGrid.addComponent(new Label("Perícia").addStyle(com.googlecode.lanterna.SGR.BOLD));
            skillsGrid.addComponent(new Label("Total").addStyle(com.googlecode.lanterna.SGR.BOLD));
            skillsGrid.addComponent(new Label("Ação").addStyle(com.googlecode.lanterna.SGR.BOLD));
            
            for (var skill : sheet.getSkills()) {
                int total = skill.calculateTotal(sheet.getLevel(), sheet.getAttributes(), finalArmorPenalty);
                String trainedMark = skill.isTrained() ? " [T]" : "";
                String penaltyMark = skill.getType().hasArmorPenalty() && finalArmorPenalty > 0 ? "*" : "";
                String sign = total >= 0 ? "+" : "";
                
                skillsGrid.addComponent(new Label(skill.getDisplayName() + penaltyMark));
                skillsGrid.addComponent(new Label(sign + total + trainedMark));
                
                if (isEditable) {
                    final com.grimoire.common.model.Skill currentSkill = skill;
                    skillsGrid.addComponent(new Button(skill.isTrained() ? "Destreinar" : "Treinar", () -> {
                        currentSkill.setTrained(!currentSkill.isTrained());
                        // Refresh tab - reconstruct and switch to it
                        com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                            getTextGUI(), 
                            "Perícia Atualizada", 
                            currentSkill.getDisplayName() + " agora está " + (currentSkill.isTrained() ? "treinada" : "destreinada") + ".\nSalve para persistir.",
                            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                        );
                    }));
                } else {
                    skillsGrid.addComponent(new EmptySpace());
                }
            }
            mainPanel.addComponent(skillsGrid);
        } else {
            mainPanel.addComponent(new Label("Nenhuma perícia listada."));
        }
        
        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Label("[T] = Treinado, * = Sofre penalidade de armadura"));
        
        // Botões de ação
        if (isEditable) {
            mainPanel.addComponent(new EmptySpace());
            Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            
            buttonPanel.addComponent(new Button("Adicionar Perícia", () -> {
                // Criar lista de perícias disponíveis
                String[] skillNames = java.util.Arrays.stream(com.grimoire.common.model.SkillType.values())
                    .map(st -> st.getDisplayName() + " (" + st.getAttribute() + ")")
                    .toArray(String[]::new);
                
                String selected = com.googlecode.lanterna.gui2.dialogs.ListSelectDialog.showDialog(
                    getTextGUI(),
                    "Adicionar Perícia",
                    "Selecione uma perícia para adicionar:",
                    skillNames
                );
                
                if (selected != null) {
                    // Extrair o tipo da perícia selecionada
                    String skillDisplayName = selected.split(" \\(")[0];
                    com.grimoire.common.model.SkillType selectedType = null;
                    for (var st : com.grimoire.common.model.SkillType.values()) {
                        if (st.getDisplayName().equals(skillDisplayName)) {
                            selectedType = st;
                            break;
                        }
                    }
                    
                    if (selectedType != null) {
                        // Verificar se já existe
                        if (sheet.getSkills() == null) {
                            sheet.setSkills(new java.util.ArrayList<>());
                        }
                        
                        final com.grimoire.common.model.SkillType finalType = selectedType;
                        boolean exists = sheet.getSkills().stream()
                            .anyMatch(s -> s.getType() == finalType);
                        
                        if (exists) {
                            com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                                getTextGUI(), "Aviso", "Esta perícia já existe na ficha.",
                                com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                            );
                        } else {
                            com.grimoire.common.model.Skill newSkill = com.grimoire.common.model.Skill.builder()
                                .type(finalType)
                                .isTrained(false)
                                .others(0)
                                .build();
                            sheet.getSkills().add(newSkill);
                            com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                                getTextGUI(), "Sucesso", 
                                finalType.getDisplayName() + " adicionada. Salve para persistir.",
                                com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                            );
                        }
                    }
                }
            }));
            
            buttonPanel.addComponent(new Button("Adicionar Perícias Padrão", () -> {
                // Adicionar perícias de defesa básicas
                if (sheet.getSkills() == null) {
                    sheet.setSkills(new java.util.ArrayList<>());
                }
                
                com.grimoire.common.model.SkillType[] defaultSkills = {
                    com.grimoire.common.model.SkillType.FORTITUDE,
                    com.grimoire.common.model.SkillType.REFLEXOS,
                    com.grimoire.common.model.SkillType.VONTADE,
                    com.grimoire.common.model.SkillType.PERCEPCAO,
                    com.grimoire.common.model.SkillType.INICIATIVA
                };
                
                int added = 0;
                for (var skillType : defaultSkills) {
                    final var ft = skillType;
                    boolean exists = sheet.getSkills().stream().anyMatch(s -> s.getType() == ft);
                    if (!exists) {
                        sheet.getSkills().add(com.grimoire.common.model.Skill.builder()
                            .type(skillType)
                            .isTrained(false)
                            .others(0)
                            .build());
                        added++;
                    }
                }
                
                com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                    getTextGUI(), "Perícias Adicionadas", 
                    added + " perícias padrão adicionadas. Salve para persistir.",
                    com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                );
            }));
            
            buttonPanel.addComponent(new Button("Salvar Perícias", this::onSaveClick));
            
            mainPanel.addComponent(buttonPanel);
        }
        
        return mainPanel;
    }

    private Component createMagicTab() {
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(new Label("Magias (T20 JdA)").addStyle(com.googlecode.lanterna.SGR.BOLD));
        mainPanel.addComponent(new EmptySpace());
        
        // CD de Magias
        int spellDC = CharacterCalculationService.calculateSpellDC(sheet);
        CharacterClassData classData = CharacterClassData.getByName(sheet.getCharacterClass());
        mainPanel.addComponent(new Label("CD de Magias: " + spellDC + " (10 + " + (sheet.getLevel()/2) + " + " + classData.keyAttribute() + ")"));
        mainPanel.addComponent(new EmptySpace());
        
        // PM
        int maxMP = CharacterCalculationService.calculateMaxMP(sheet);
        mainPanel.addComponent(new Label("PM Máximo Calculado: " + maxMP));
        mainPanel.addComponent(new EmptySpace());
        
        // Lista de magias organizadas por círculo
        com.grimoire.common.model.Magic magic = sheet.getMagic();
        if (magic != null && magic.getSpells() != null && !magic.getSpells().isEmpty()) {
            // Agrupar por círculo
            java.util.Map<Integer, java.util.List<com.grimoire.common.model.Spell>> byCircle = 
                magic.getSpells().stream()
                    .collect(java.util.stream.Collectors.groupingBy(com.grimoire.common.model.Spell::getCircle));
            
            for (int circle = 1; circle <= 5; circle++) {
                if (byCircle.containsKey(circle)) {
                    mainPanel.addComponent(new Label(circle + "º Círculo").addStyle(com.googlecode.lanterna.SGR.BOLD));
                    
                    Panel spellGrid = new Panel(new GridLayout(isEditable ? 4 : 3));
                    spellGrid.addComponent(new Label("Nome").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
                    spellGrid.addComponent(new Label("Escola").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
                    spellGrid.addComponent(new Label("Custo").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
                    if (isEditable) {
                        spellGrid.addComponent(new Label("Ação").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
                    }
                    
                    for (var spell : byCircle.get(circle)) {
                        spellGrid.addComponent(new Label(spell.getName() != null ? spell.getName() : "-"));
                        spellGrid.addComponent(new Label(spell.getSchool() != null ? spell.getSchool() : "-"));
                        spellGrid.addComponent(new Label(spell.getCost() != null ? spell.getCost() : "-"));
                        
                        if (isEditable) {
                            final var currentSpell = spell;
                            spellGrid.addComponent(new Button("Remover", () -> {
                                magic.getSpells().remove(currentSpell);
                                com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                                    getTextGUI(), "Magia Removida", 
                                    currentSpell.getName() + " removida. Salve para persistir.",
                                    com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                                );
                            }));
                        }
                    }
                    mainPanel.addComponent(spellGrid);
                    mainPanel.addComponent(new EmptySpace());
                }
            }
        } else {
            mainPanel.addComponent(new Label("Nenhuma magia conhecida."));
            mainPanel.addComponent(new EmptySpace());
        }
        
        // Botões de ação
        if (isEditable) {
            mainPanel.addComponent(new EmptySpace());
            Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            
            buttonPanel.addComponent(new Button("Adicionar Magia", () -> {
                // Diálogo para nome
                String name = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Nome da Magia", "Digite o nome da magia:", "");
                if (name == null || name.trim().isEmpty()) return;
                
                // Diálogo para círculo
                String[] circles = {"1º Círculo", "2º Círculo", "3º Círculo", "4º Círculo", "5º Círculo"};
                String circleStr = com.googlecode.lanterna.gui2.dialogs.ListSelectDialog.showDialog(
                    getTextGUI(), "Círculo", "Selecione o círculo:", circles);
                if (circleStr == null) return;
                int circle = Integer.parseInt(circleStr.substring(0, 1));
                
                // Diálogo para escola
                String[] schools = {"Abjuração", "Adivinhação", "Convocação", "Encantamento", 
                                   "Evocação", "Ilusão", "Necromancia", "Transmutação"};
                String school = com.googlecode.lanterna.gui2.dialogs.ListSelectDialog.showDialog(
                    getTextGUI(), "Escola", "Selecione a escola:", schools);
                if (school == null) school = "Universal";
                
                // Diálogo para custo
                String cost = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Custo", "Digite o custo em PM:", circle + " PM");
                if (cost == null) cost = circle + " PM";
                
                // Diálogo para efeito (opcional)
                String effect = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Efeito", "Digite uma descrição breve (opcional):", "");
                
                // Criar a magia
                com.grimoire.common.model.Spell newSpell = com.grimoire.common.model.Spell.builder()
                    .name(name.trim())
                    .circle(circle)
                    .school(school)
                    .cost(cost)
                    .execution("Padrão")
                    .range("Curto")
                    .duration("Instantânea")
                    .effect(effect != null ? effect : "")
                    .build();
                
                if (sheet.getMagic() == null) {
                    sheet.setMagic(new com.grimoire.common.model.Magic());
                }
                if (sheet.getMagic().getSpells() == null) {
                    sheet.getMagic().setSpells(new java.util.ArrayList<>());
                }
                sheet.getMagic().getSpells().add(newSpell);
                
                com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                    getTextGUI(), "Magia Adicionada", 
                    name + " (" + circle + "º círculo) adicionada.\nSalve para persistir.",
                    com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK
                );
            }));
            
            buttonPanel.addComponent(new Button("Salvar Magias", this::onSaveClick));
            
            mainPanel.addComponent(buttonPanel);
        }
        
        return mainPanel;
    }

    private Component createInventoryTab() {
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(new Label("Inventário (T20 JdA)").addStyle(com.googlecode.lanterna.SGR.BOLD));
        mainPanel.addComponent(new EmptySpace());
        
        com.grimoire.common.model.Inventory inventory = sheet.getInventory();
        if (inventory == null) {
            inventory = new com.grimoire.common.model.Inventory();
            sheet.setInventory(inventory);
        }
        final com.grimoire.common.model.Inventory inv = inventory;
        
        // === Calcular totais ===
        int strength = sheet.getAttributes() != null ? sheet.getAttributes().getStrength() : 0;
        double lightLoad = CharacterCalculationService.calculateLightLoad(strength);
        double heavyLoad = CharacterCalculationService.calculateHeavyLoad(strength);
        
        double totalWeight = 0;
        double totalValue = 0;
        if (inv.getItems() != null) {
            for (var item : inv.getItems()) {
                totalWeight += item.getWeight() * item.getQuantity();
                totalValue += item.getCost() * item.getQuantity();
            }
        }
        if (inv.getEquippedItems() != null) {
            for (var item : inv.getEquippedItems()) {
                totalWeight += item.getWeight() * item.getQuantity();
                totalValue += item.getCost() * item.getQuantity();
            }
        }
        
        // === Dinheiro e Resumo ===
        Panel infoPanel = new Panel(new GridLayout(4));
        
        infoPanel.addComponent(new Label("Dinheiro (T$):"));
        TextBox currencyBox = new TextBox(String.valueOf(inv.getCurrency()));
        currencyBox.setReadOnly(!isEditable);
        infoPanel.addComponent(currencyBox);
        
        infoPanel.addComponent(new Label("Valor Total Itens:"));
        infoPanel.addComponent(new Label(String.format("T$ %.0f", totalValue)));
        
        // Carga
        infoPanel.addComponent(new Label("Carga Atual:"));
        String loadStatus = "";
        if (totalWeight > heavyLoad) {
            loadStatus = " (SOBRECARREGADO!)";
        } else if (totalWeight > lightLoad) {
            loadStatus = " (Pesada)";
        }
        infoPanel.addComponent(new Label(String.format("%.1f kg%s", totalWeight, loadStatus)));
        
        infoPanel.addComponent(new Label("Capacidade:"));
        infoPanel.addComponent(new Label(String.format("Leve: %.1f / Pesada: %.1f kg", lightLoad, heavyLoad)));
        
        mainPanel.addComponent(infoPanel);
        mainPanel.addComponent(new EmptySpace());
        
        // === Itens Equipados ===
        mainPanel.addComponent(new Label("Itens Equipados").addStyle(com.googlecode.lanterna.SGR.BOLD));
        
        if (inv.getEquippedItems() != null && !inv.getEquippedItems().isEmpty()) {
            Panel equippedGrid = new Panel(new GridLayout(isEditable ? 5 : 4));
            equippedGrid.addComponent(new Label("Nome").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            equippedGrid.addComponent(new Label("Tipo").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            equippedGrid.addComponent(new Label("Peso").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            equippedGrid.addComponent(new Label("Qtd").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            if (isEditable) {
                equippedGrid.addComponent(new Label("Ação").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            }
            
            for (var item : inv.getEquippedItems()) {
                equippedGrid.addComponent(new Label(item.getName() != null ? item.getName() : "-"));
                equippedGrid.addComponent(new Label(item.getType() != null ? item.getType() : "Geral"));
                equippedGrid.addComponent(new Label(String.format("%.1f", item.getWeight())));
                equippedGrid.addComponent(new Label(String.valueOf(item.getQuantity())));
                
                if (isEditable) {
                    final var currentItem = item;
                    Panel actionPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
                    actionPanel.addComponent(new Button("↓", () -> {
                        inv.getEquippedItems().remove(currentItem);
                        if (inv.getItems() == null) inv.setItems(new java.util.ArrayList<>());
                        inv.getItems().add(currentItem);
                        com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                            getTextGUI(), "Desequipado", currentItem.getName() + " → mochila.",
                            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
                    }));
                    actionPanel.addComponent(new Button("?", () -> showItemDetails(currentItem)));
                    equippedGrid.addComponent(actionPanel);
                }
            }
            mainPanel.addComponent(equippedGrid);
        } else {
            mainPanel.addComponent(new Label("Nenhum item equipado."));
        }
        
        mainPanel.addComponent(new EmptySpace());
        
        // === Mochila ===
        mainPanel.addComponent(new Label("Mochila").addStyle(com.googlecode.lanterna.SGR.BOLD));
        
        if (inv.getItems() != null && !inv.getItems().isEmpty()) {
            Panel itemsGrid = new Panel(new GridLayout(isEditable ? 6 : 5));
            itemsGrid.addComponent(new Label("Nome").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            itemsGrid.addComponent(new Label("Tipo").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            itemsGrid.addComponent(new Label("Peso").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            itemsGrid.addComponent(new Label("Qtd").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            itemsGrid.addComponent(new Label("Custo").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            if (isEditable) {
                itemsGrid.addComponent(new Label("Ação").addStyle(com.googlecode.lanterna.SGR.UNDERLINE));
            }
            
            for (var item : inv.getItems()) {
                itemsGrid.addComponent(new Label(item.getName() != null ? item.getName() : "-"));
                itemsGrid.addComponent(new Label(item.getType() != null ? item.getType() : "Geral"));
                itemsGrid.addComponent(new Label(String.format("%.1f", item.getWeight())));
                itemsGrid.addComponent(new Label(String.valueOf(item.getQuantity())));
                itemsGrid.addComponent(new Label(String.format("T$ %.0f", item.getCost())));
                
                if (isEditable) {
                    final var currentItem = item;
                    Panel actionPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
                    actionPanel.addComponent(new Button("↑", () -> {
                        inv.getItems().remove(currentItem);
                        if (inv.getEquippedItems() == null) inv.setEquippedItems(new java.util.ArrayList<>());
                        inv.getEquippedItems().add(currentItem);
                        com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                            getTextGUI(), "Equipado", currentItem.getName() + " equipado.",
                            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
                    }));
                    actionPanel.addComponent(new Button("✎", () -> editItem(currentItem)));
                    actionPanel.addComponent(new Button("?", () -> showItemDetails(currentItem)));
                    actionPanel.addComponent(new Button("X", () -> {
                        var result = com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                            getTextGUI(), "Remover", "Remover " + currentItem.getName() + "?",
                            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes,
                            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.No);
                        if (result == com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes) {
                            inv.getItems().remove(currentItem);
                        }
                    }));
                    itemsGrid.addComponent(actionPanel);
                }
            }
            mainPanel.addComponent(itemsGrid);
        } else {
            mainPanel.addComponent(new Label("Mochila vazia."));
        }
        
        mainPanel.addComponent(new EmptySpace());
        
        // === Totais ===
        Panel totalsPanel = new Panel(new GridLayout(4));
        totalsPanel.addComponent(new Label("Peso Total:").addStyle(com.googlecode.lanterna.SGR.BOLD));
        totalsPanel.addComponent(new Label(String.format("%.1f kg", totalWeight)));
        totalsPanel.addComponent(new Label("Valor Total:").addStyle(com.googlecode.lanterna.SGR.BOLD));
        totalsPanel.addComponent(new Label(String.format("T$ %.0f", totalValue)));
        mainPanel.addComponent(totalsPanel);
        
        mainPanel.addComponent(new EmptySpace());
        
        // === Botões de Ação ===
        if (isEditable) {
            Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            
            buttonPanel.addComponent(new Button("Adicionar Item", () -> {
                String name = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Nome do Item", "Nome:", "");
                if (name == null || name.trim().isEmpty()) return;
                
                String[] types = {"Arma", "Armadura", "Consumível", "Ferramenta", "Geral"};
                String type = com.googlecode.lanterna.gui2.dialogs.ListSelectDialog.showDialog(
                    getTextGUI(), "Tipo", "Selecione o tipo:", types);
                if (type == null) type = "Geral";
                
                String qtyStr = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Quantidade", "Quantidade:", "1");
                int quantity = 1;
                try { quantity = Integer.parseInt(qtyStr); } catch (Exception e) {}
                
                String weightStr = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Peso (kg)", "Peso por unidade:", "0.5");
                double weight = 0.5;
                try { weight = Double.parseDouble(weightStr.replace(",", ".")); } catch (Exception e) {}
                
                String costStr = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Custo (T$)", "Custo por unidade:", "0");
                double cost = 0;
                try { cost = Double.parseDouble(costStr.replace(",", ".")); } catch (Exception e) {}
                
                String description = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Descrição", "Descrição (opcional):", "");
                
                com.grimoire.common.model.Item newItem = com.grimoire.common.model.Item.builder()
                    .name(name.trim())
                    .type(type)
                    .quantity(quantity)
                    .weight(weight)
                    .cost(cost)
                    .description(description != null ? description : "")
                    .build();
                
                if (inv.getItems() == null) inv.setItems(new java.util.ArrayList<>());
                inv.getItems().add(newItem);
                
                com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                    getTextGUI(), "Adicionado", name + " (x" + quantity + ") adicionado.",
                    com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
            }));
            
            buttonPanel.addComponent(new Button("+ T$", () -> {
                String amountStr = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
                    getTextGUI(), "Adicionar T$", "Valor (negativo para remover):", "0");
                try {
                    int amount = Integer.parseInt(amountStr);
                    inv.setCurrency(inv.getCurrency() + amount);
                    currencyBox.setText(String.valueOf(inv.getCurrency()));
                    com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                        getTextGUI(), "Atualizado", "Saldo: T$ " + inv.getCurrency(),
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
                } catch (NumberFormatException e) {
                    com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
                        getTextGUI(), "Erro", "Número inválido.",
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
                }
            }));
            
            buttonPanel.addComponent(new Button("Salvar", () -> {
                try { inv.setCurrency(Integer.parseInt(currencyBox.getText())); } catch (NumberFormatException e) {}
                onSaveClick();
            }));
            
            mainPanel.addComponent(buttonPanel);
        }
        
        return mainPanel;
    }
    
    private void showItemDetails(com.grimoire.common.model.Item item) {
        StringBuilder details = new StringBuilder();
        details.append("Nome: ").append(item.getName()).append("\n");
        details.append("Tipo: ").append(item.getType() != null ? item.getType() : "Geral").append("\n");
        details.append("Quantidade: ").append(item.getQuantity()).append("\n");
        details.append("Peso: ").append(String.format("%.1f kg", item.getWeight())).append("\n");
        details.append("Custo: ").append(String.format("T$ %.0f", item.getCost())).append("\n");
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            details.append("\nDescrição:\n").append(item.getDescription());
        }
        com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
            getTextGUI(), item.getName(), details.toString(),
            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
    }
    
    private void editItem(com.grimoire.common.model.Item item) {
        // Editar quantidade
        String qtyStr = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
            getTextGUI(), "Editar Quantidade", "Nova quantidade:", String.valueOf(item.getQuantity()));
        if (qtyStr != null) {
            try { item.setQuantity(Integer.parseInt(qtyStr)); } catch (Exception e) {}
        }
        
        // Editar peso
        String weightStr = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
            getTextGUI(), "Editar Peso", "Novo peso (kg):", String.valueOf(item.getWeight()));
        if (weightStr != null) {
            try { item.setWeight(Double.parseDouble(weightStr.replace(",", "."))); } catch (Exception e) {}
        }
        
        // Editar custo
        String costStr = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
            getTextGUI(), "Editar Custo", "Novo custo (T$):", String.valueOf(item.getCost()));
        if (costStr != null) {
            try { item.setCost(Double.parseDouble(costStr.replace(",", "."))); } catch (Exception e) {}
        }
        
        // Editar descrição
        String desc = com.googlecode.lanterna.gui2.dialogs.TextInputDialog.showDialog(
            getTextGUI(), "Editar Descrição", "Nova descrição:", item.getDescription() != null ? item.getDescription() : "");
        if (desc != null) {
            item.setDescription(desc);
        }
        
        com.googlecode.lanterna.gui2.dialogs.MessageDialog.showMessageDialog(
            getTextGUI(), "Atualizado", item.getName() + " atualizado. Salve para persistir.",
            com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.OK);
    }

    private Component createLoreTab() {
        Panel panel = new Panel(new BorderLayout());
        
        Panel headerPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        headerPanel.addComponent(new Label("Histórico do Personagem").addStyle(com.googlecode.lanterna.SGR.BOLD));
        headerPanel.addComponent(new Label("(Máximo: 2000 caracteres)").setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.BLUE));
        panel.addComponent(headerPanel, BorderLayout.Location.TOP);
        
        backgroundBox = new WrappedTextBox(new TerminalSize(70, 15), sheet.getBackground());
        backgroundBox.setReadOnly(!isEditable);
        
        panel.addComponent(backgroundBox, BorderLayout.Location.CENTER);
        
        if (isEditable) {
            Panel footerPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            footerPanel.addComponent(new Button("Salvar Histórico", this::onSaveClick));
            panel.addComponent(footerPanel, BorderLayout.Location.BOTTOM);
        }
        
        return panel;
    }
}
