package com.grimoire.tui;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.grimoire.client.http.GrimoireHttpClient;
import com.grimoire.client.service.AuthService;
import com.grimoire.client.service.CampaignService;
import com.grimoire.client.service.SessionService;
import com.grimoire.client.service.SheetService;
import com.grimoire.tui.ui.windows.LoginWindow;
import com.grimoire.tui.ui.windows.MainMenuWindow;

import java.io.IOException;

public class TuiApplication {
    
    private static final String DEFAULT_SERVER_URL = "http://localhost:8080";
    
    public static void main(String[] args) {
        
        // Configure terminal factory to force text mode
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setForceTextTerminal(true); // OBRIGATÓRIO: Força desenhar no terminal atual
        
        Screen screen = null;
        try {
            // Create screen in text mode
            screen = terminalFactory.createScreen();
            screen.startScreen(); // Isso deve limpar o terminal e deixá-lo pronto para desenho
            
            // Create the GUI over the screen
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            
            // Initialize HTTP client and services
            GrimoireHttpClient httpClient = new GrimoireHttpClient(DEFAULT_SERVER_URL);
            AuthService authService = new AuthService(httpClient);
            SheetService sheetService = new SheetService(httpClient, authService);
            SessionService sessionService = new SessionService(httpClient, authService);
            CampaignService campaignService = new CampaignService(httpClient, authService, sheetService, sessionService);
            
            // Show login window
            LoginWindow loginWindow = new LoginWindow(authService);
            gui.addWindowAndWait(loginWindow);
            
            // Check if login was successful
            if (authService.isLoggedIn()) {
                // Show main menu after successful login
                MainMenuWindow mainMenuWindow = new MainMenuWindow(
                    authService, 
                    campaignService, 
                    sessionService, 
                    sheetService
                );
                gui.addWindowAndWait(mainMenuWindow);
            } else {
                // User cancelled login - exit silently
            }
            
        } catch (IOException e) {
            // Fatal error - exit with error code
            System.exit(1);
        } finally {
            if (screen != null) {
                try {
                    screen.stopScreen();
                } catch (IOException e) {
                    // Ignore cleanup errors
                }
            }
        }
    }
}