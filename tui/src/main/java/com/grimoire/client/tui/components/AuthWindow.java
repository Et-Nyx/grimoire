package com.grimoire.client.tui.components;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.grimoire.client.service.ApiClient;
import com.grimoire.client.tui.framework.Router;
import com.grimoire.client.tui.views.LoginView;
import com.grimoire.client.tui.views.RegisterView;
import com.grimoire.client.tui.views.StartView;

import java.util.Arrays;
import java.util.Collections;

/**
 * Window for the authentication flow (Start, Login, Register).
 */
public class AuthWindow extends BasicWindow {
    private final Router router;
    private final ApiClient apiClient;
    private boolean authenticated = false;

    public AuthWindow(ApiClient apiClient) {
        super();
        this.apiClient = apiClient;
        setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
        
        Panel contentPanel = new Panel();
        setComponent(contentPanel);
        
        this.router = new Router(contentPanel, this);
        
        // Register Auth Views
        router.addRoute("start", new StartView(router));
        router.addRoute("login", new LoginView(apiClient, router, this::onLoginSuccess));
        router.addRoute("register", new RegisterView(apiClient, router));
        
        router.navigate("start");
    }

    private void onLoginSuccess() {
        this.authenticated = true;
        close();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
