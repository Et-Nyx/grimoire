package com.grimoire.client.tui.views;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Label;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.framework.View;

/**
 * The settings view.
 */
public class SettingsView implements View {
    @Override
    public void onEnter() {
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        Card card = new Card("Settings");
        card.addComponent(new Label("Application Settings"));
        card.addComponent(new Label("Theme: Dark"));
        return card.withBorder();
    }

    @Override
    public String getTitle() {
        return "Settings";
    }
}
