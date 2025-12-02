package com.grimoire.client.tui.components;

import com.googlecode.lanterna.gui2.*;
import com.grimoire.client.tui.framework.Router;

import com.googlecode.lanterna.TerminalSize;
import java.util.Arrays;

/**
 * The main application window structure.
 */
public class AppShell extends BasicWindow {
    private final Panel mainPanel;
    private final Panel contentArea;
    private final Sidebar sidebar;
    private final Router router;
    private final com.grimoire.client.service.ApiClient apiClient;

    public AppShell(com.grimoire.client.service.ApiClient apiClient) {
        super();
        this.apiClient = apiClient;
        setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
        
        mainPanel = new Panel(new BorderLayout());
        setComponent(mainPanel);

        // 1. Sidebar (Left) - Fixed Width
        // We initialize the router later, but Sidebar needs it. 
        // Let's create the contentArea first so we can create the router.
        contentArea = new Panel(new BorderLayout()); 
        router = new Router(contentArea, this);

        sidebar = new Sidebar(router, () -> {
            apiClient.logout();
            close();
        });
        
        mainPanel.addComponent(
            sidebar.withBorder(Borders.singleLine("MENU")),
            BorderLayout.Location.LEFT
        );

        // 2. Content Area (Center) - Expandable
        mainPanel.addComponent(contentArea, BorderLayout.Location.CENTER);

        // 3. Footer (Bottom)
        Panel footer = new Panel(new LinearLayout(Direction.HORIZONTAL));
        String username = apiClient.getCurrentUser() != null ? apiClient.getCurrentUser().getUsername() : "Guest";
        footer.addComponent(new Label("STATUS: Connected as " + username));
        footer.addComponent(new EmptySpace(new TerminalSize(1, 0))); // Spacer
        footer.addComponent(new Label("| [ESC] Back"));
        
        mainPanel.addComponent(
            footer.withBorder(Borders.singleLine()),
            BorderLayout.Location.BOTTOM
        );
    }

    @Override
    public boolean handleInput(com.googlecode.lanterna.input.KeyStroke key) {
        if (key.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape) {
            router.back();
            return true;
        }
        return super.handleInput(key);
    }

    public Router getRouter() {
        return router;
    }
}
