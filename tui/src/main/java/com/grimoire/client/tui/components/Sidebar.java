package com.grimoire.client.tui.components;

import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.grimoire.client.tui.framework.Router;

/**
 * Navigation sidebar.
 */
public class Sidebar extends Panel {
    private final Router router;

    public Sidebar(Router router, Runnable onLogout) {
        super(new LinearLayout(Direction.VERTICAL));
        this.router = router;
        
        // Add default navigation items
        addComponent(new StyledButton("Home", () -> router.navigate("home")));
        addComponent(new StyledButton("Profile", () -> router.navigate("profile")));
        addComponent(new StyledButton("Settings", () -> router.navigate("settings")));
        
        addComponent(new com.googlecode.lanterna.gui2.Label(" ")); // Spacer
        addComponent(new StyledButton("Logout", onLogout));
    }
}
