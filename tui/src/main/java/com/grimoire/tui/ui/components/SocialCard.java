package com.grimoire.tui.ui.components;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.grimoire.tui.ui.styles.GrimoireTheme;

import java.util.ArrayList;
import java.util.List;

public class SocialCard {

    private static final int MAX_LINE_WIDTH = 55;

    public static Component create(String headerText, String timestamp, String contentBody, TextColor borderColor) {
        Panel cardPanel = new Panel();
        cardPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        Panel headerPanel = new Panel();
        headerPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        
        Label headerLabel = new Label(headerText);
        headerLabel.setForegroundColor(GrimoireTheme.PRIMARY);
        
        EmptySpace spacer = new EmptySpace(new TerminalSize(1, 1));
        
        Label timestampLabel = new Label(timestamp);
        timestampLabel.setForegroundColor(GrimoireTheme.TIMESTAMP);
        
        headerPanel.addComponent(headerLabel);
        headerPanel.addComponent(spacer.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        headerPanel.addComponent(timestampLabel);
        
        cardPanel.addComponent(headerPanel);
        cardPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        
        if (contentBody != null && !contentBody.isEmpty()) {
            List<String> wrappedLines = wrapText(contentBody, MAX_LINE_WIDTH);
            
            for (String line : wrappedLines) {
                Label lineLabel = new Label(line);
                lineLabel.setForegroundColor(TextColor.ANSI.BLACK);
                cardPanel.addComponent(lineLabel);
            }
        } else {
            Label emptyLabel = new Label("(sem conteúdo)");
            emptyLabel.setForegroundColor(GrimoireTheme.TIMESTAMP);
            cardPanel.addComponent(emptyLabel);
        }
        
        return cardPanel.withBorder(Borders.singleLine());
    }
    
    private static List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return lines;
        }
        
        String[] paragraphs = text.split("\n", -1);
        
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            
            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                if (currentLine.length() == 0) {
                    currentLine.append(word);
                } else if (currentLine.length() + 1 + word.length() <= maxWidth) {
                    currentLine.append(" ").append(word);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            }
            
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }
        
        return lines;
    }
    
    private SocialCard() {
    }
}
