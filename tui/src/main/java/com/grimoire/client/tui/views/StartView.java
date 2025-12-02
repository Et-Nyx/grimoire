package com.grimoire.client.tui.views;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.grimoire.client.tui.components.Card;
import com.grimoire.client.tui.components.StyledButton;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.framework.View;

public class StartView implements View {
    private final Router router;

    public StartView(Router router) {
        this.router = router;
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onLeave() {
    }

    @Override
    public Component getContent() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        
        panel.addComponent(new StyledButton("Login", () -> router.navigate("login")));
        panel.addComponent(new StyledButton("Register", () -> router.navigate("register")));
        panel.addComponent(new StyledButton("Exit", () -> System.exit(0)));

        Card card = new Card("Grimoire CLI");
        card.addComponent(panel);
        
        Panel centerPanel = new Panel(new com.googlecode.lanterna.gui2.BorderLayout());
        centerPanel.addComponent(card.withBorder(), com.googlecode.lanterna.gui2.BorderLayout.Location.CENTER);
        return centerPanel;
    }

    @Override
    public String getTitle() {
        return "Start";
    }
}
