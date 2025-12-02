package com.grimoire.client.tui.framework;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.grimoire.client.tui.components.AppShell;
import com.grimoire.client.tui.components.AuthWindow;

import java.io.IOException;

/**
 * Main application class for the TUI.
 */
public class TuiApp {
    private Screen screen;
    private WindowBasedTextGUI gui;
    private static TuiApp instance;

    private TuiApp() {}

    public static TuiApp getInstance() {
        if (instance == null) {
            instance = new TuiApp();
        }
        return instance;
    }

    public void start() throws IOException {
        LoggerConfig.setup();
        java.util.logging.Logger.getLogger(TuiApp.class.getName()).info("Starting TuiApp...");

        // NixOS compatibility: set stty path
        String sttyPath = System.getenv("STTY_PATH");
        if (sttyPath == null) {
            sttyPath = findStty();
        }
        if (sttyPath != null) {
            System.setProperty("com.googlecode.lanterna.terminal.UnixTerminal.sttyCommand", sttyPath);
        }

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();

        gui = new MultiWindowTextGUI(screen);
        gui.setTheme(new LanternaThemeAdapter(Theme.createDefault()));

        // Initialize ApiClient
        String serverUrl = System.getProperty("server.url", "http://localhost:8080");
        com.grimoire.client.service.ApiClient apiClient = new com.grimoire.client.service.ApiClient(serverUrl);

        boolean running = true;
        while (running) {
            // 1. Show Auth Window
            AuthWindow authWindow = new AuthWindow(apiClient);
            try {
                gui.addWindowAndWait(authWindow);
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(TuiApp.class.getName()).log(java.util.logging.Level.SEVERE, "Error in AuthWindow", e);
                throw e;
            }

            // 2. If Authenticated, Show Main Dashboard
            if (authWindow.isAuthenticated()) {
                AppShell appShell = new AppShell(apiClient);
                
                // Register Routes
                appShell.getRouter().addRoute("home", new com.grimoire.client.tui.views.HomeView(apiClient, appShell.getRouter()));
                appShell.getRouter().addRoute("profile", new com.grimoire.client.tui.views.ProfileView(apiClient));
                appShell.getRouter().addRoute("settings", new com.grimoire.client.tui.views.SettingsView());
                appShell.getRouter().addRoute("campaign-create", new com.grimoire.client.tui.views.CampaignCreateView(apiClient, appShell.getRouter()));
                appShell.getRouter().addRoute("campaign-join", new com.grimoire.client.tui.views.CampaignJoinView(apiClient, appShell.getRouter()));
                appShell.getRouter().addRoute("campaign-view", new com.grimoire.client.tui.views.CampaignView(apiClient, appShell.getRouter()));
                appShell.getRouter().addRoute("sheet-view", new com.grimoire.client.tui.views.CharacterSheetView(apiClient, appShell.getRouter()));
                appShell.getRouter().addRoute("player-detail", new com.grimoire.client.tui.views.PlayerDetailView(apiClient, appShell.getRouter()));
                
                // Navigate to initial route
                appShell.getRouter().navigate("home");

                gui.addWindowAndWait(appShell);
            } else {
                // User cancelled/exited from AuthWindow
                running = false;
            }
        }
    }

    public void stop() throws IOException {
        if (screen != null) {
            screen.stopScreen();
        }
    }

    public WindowBasedTextGUI getGui() {
        return gui;
    }
    
    public Screen getScreen() {
        return screen;
    }

    private String findStty() {
        String[] paths = System.getenv("PATH").split(":");
        for (String dir : paths) {
            java.io.File stty = new java.io.File(dir, "stty");
            if (stty.exists() && stty.canExecute()) {
                return stty.getAbsolutePath();
            }
        }
        return null;
    }
}
