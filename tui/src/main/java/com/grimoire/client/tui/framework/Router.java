package com.grimoire.client.tui.framework;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.Panel;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages navigation between views.
 */
public class Router {
    private static final Logger LOGGER = Logger.getLogger(Router.class.getName());
    private final Panel contentArea;
    private final com.googlecode.lanterna.gui2.Window window;
    private final Map<String, View> routes = new HashMap<>();
    private final Stack<String> history = new Stack<>();
    private View currentView;

    public Router(Panel contentArea, com.googlecode.lanterna.gui2.Window window) {
        this.contentArea = contentArea;
        this.window = window;
    }

    public com.googlecode.lanterna.gui2.Window getWindow() {
        return window;
    }

    public void addRoute(String path, View view) {
        routes.put(path, view);
    }

    public void navigate(String path) {
        View view = routes.get(path);
        if (view == null) {
            throw new IllegalArgumentException("No route found for path: " + path);
        }
        
        // Schedule navigation on the GUI thread
        if (contentArea.getTextGUI() != null) {
            contentArea.getTextGUI().getGUIThread().invokeLater(() -> doNavigate(view, path));
        } else {
            doNavigate(view, path);
        }
    }
    
    public void back() {
        if (history.size() > 1) {
            history.pop(); // Current
            String previous = history.peek();
            navigate(previous);
        }
    }

    private void doNavigate(View view, String path) {
        try {
            LOGGER.info("Navigating to: " + path);
            
            if (currentView != null) {
                currentView.onLeave();
            }

            history.push(path);
            currentView = view;
            
            // Clear focus to avoid NPE on removed components
            if (this.window != null) {
                this.window.setFocusedInteractable(null);
            }

            contentArea.removeAllComponents();
            Component newContent = view.getContent();
            contentArea.addComponent(newContent);
            
            // Try to focus the first interactable in the new view
            if (this.window != null) {
                Interactable nextFocus = findFirstInteractable(newContent);
                if (nextFocus != null) {
                    this.window.setFocusedInteractable(nextFocus);
                }
            }
            
            currentView.onEnter();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during navigation to " + path, e);
        }
    }

    private Interactable findFirstInteractable(Component component) {
        if (component instanceof Interactable) {
            return (Interactable) component;
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getChildren()) {
                Interactable found = findFirstInteractable(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
