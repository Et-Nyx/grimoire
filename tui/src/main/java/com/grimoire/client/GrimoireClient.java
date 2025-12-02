package com.grimoire.client;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.grimoire.client.service.ApiClient;
public class GrimoireClient {
    
    public static void main(String[] args) {
        try {
            String serverUrl = args.length > 0 ? args[0] : "http://localhost:8080";
            ApiClient apiClient = new ApiClient(serverUrl);
            
            // NixOS compatibility: set stty path
            String sttyPath = System.getenv("STTY_PATH");
            if (sttyPath == null) {
                // Try to find stty in PATH
                sttyPath = findStty();
            }
            if (sttyPath != null) {
                System.setProperty("com.googlecode.lanterna.terminal.UnixTerminal.sttyCommand", sttyPath);
            }

            // New Modular TUI
            com.grimoire.client.tui.framework.TuiApp app = com.grimoire.client.tui.framework.TuiApp.getInstance();
            try {
                app.start();
            } finally {
                app.stop();
            }
            
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(GrimoireClient.class.getName()).log(java.util.logging.Level.SEVERE, "Fatal error", e);
            System.err.println("Application crashed: " + e.getMessage());
            System.err.println("Check tui-debug.log for details.");
        }
    }
    
    private static String findStty() {
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
