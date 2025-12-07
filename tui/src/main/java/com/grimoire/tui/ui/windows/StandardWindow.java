package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.gui2.*;
import com.grimoire.tui.ui.styles.GrimoireTheme;

public abstract class StandardWindow extends BasicWindow {

    private final Panel tabBarPanel;
    private final Label footerLabel;
    private final java.util.List<Tab> tabs = new java.util.ArrayList<>();
    private int currentTabIndex = 0;
    private final Panel rootPanel;
    private final Panel contentContainer;
    
    private static class Tab {
        String title;
        Component content;
        
        Tab(String title, Component content) {
            this.title = title;
            this.content = content;
        }
    }

    public StandardWindow(String title) {
        super(title);
        
        setHints(java.util.Arrays.asList(Hint.CENTERED, Hint.MODAL));

        rootPanel = new Panel(new BorderLayout());
        
        // Tab Bar
        tabBarPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        rootPanel.addComponent(tabBarPanel, BorderLayout.Location.TOP);
        
        // Container for the actual content of the window
        contentContainer = new Panel(new BorderLayout());
        rootPanel.addComponent(contentContainer, BorderLayout.Location.CENTER);

        // Standard Footer
        Panel footerPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        footerLabel = new Label(
            "[↑↓] Navegar | [Enter] Selecionar | [Esc] Fechar"
        );
        footerLabel.setForegroundColor(GrimoireTheme.TIMESTAMP);
        footerPanel.addComponent(footerLabel);
        
        rootPanel.addComponent(footerPanel, BorderLayout.Location.BOTTOM);

        // We call super.setComponent to set our root panel as the window's component
        super.setComponent(rootPanel);
    }
    
    public void addTab(String title, Component content) {
        tabs.add(new Tab(title, content));
        if (tabs.size() == 1) {
            currentTabIndex = 0;
            switchTab(0);
        }
        refreshTabBar();
        updateFooter();
    }
    
    private void switchTab(int index) {
        if (index < 0 || index >= tabs.size()) return;
        
        currentTabIndex = index;
        contentContainer.removeAllComponents();
        Component content = tabs.get(index).content;
        contentContainer.addComponent(content, BorderLayout.Location.CENTER);
        
        // Try to focus the first interactable component in the new tab
        Interactable firstInteractable = findFirstInteractable(content);
        if (firstInteractable != null) {
            setFocusedInteractable(firstInteractable);
        }
    }
    
    private Interactable findFirstInteractable(Component component) {
        if (component instanceof Interactable) {
            return (Interactable) component;
        }
        
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getChildren()) {
                Interactable found = findFirstInteractable(child);
                if (found != null) {
                    return found;
                }
            }
        }
        
        return null;
    }
    
    private void refreshTabBar() {
        tabBarPanel.removeAllComponents();
        if (tabs.isEmpty()) return;
        
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            String labelText = " " + tab.title + " ";
            Label label = new Label(labelText);
            
            if (i == currentTabIndex) {
                label.setForegroundColor(GrimoireTheme.BACKGROUND);
                label.setBackgroundColor(GrimoireTheme.PRIMARY);
                label.addStyle(com.googlecode.lanterna.SGR.BOLD);
            } else {
                label.setForegroundColor(GrimoireTheme.PRIMARY);
                label.setBackgroundColor(GrimoireTheme.BACKGROUND);
            }
            
            tabBarPanel.addComponent(label);
            
            if (i < tabs.size() - 1) {
                tabBarPanel.addComponent(new Label(" | ").setForegroundColor(GrimoireTheme.CARD_BORDER));
            }
        }
    }
    
    private void updateFooter() {
        String baseFooter = "[↑↓] Navegar | [Enter] Selecionar | [Esc] Fechar";
        if (tabs.size() > 1) {
            footerLabel.setText(baseFooter + " | [Tab] Próxima Aba");
        } else {
            footerLabel.setText(baseFooter);
        }
    }

    @Override
    public void setComponent(Component component) {
        // When subclasses call setComponent, we actually put their component 
        // inside our contentContainer, preserving the footer.
        // We need to clear it first if it has anything (though usually called once)
        contentContainer.removeAllComponents();
        
        // If the component is a Panel, we can just add it. 
        // If it's something else, we might need to be careful, but usually it's a Panel.
        // Since contentContainer is BorderLayout, we add to CENTER.
        contentContainer.addComponent(component, BorderLayout.Location.CENTER);
    }
    
    // Helper to access the root panel if absolutely needed (rare)
    protected Panel getRootPanel() {
        return rootPanel;
    }

    @Override
    public boolean handleInput(com.googlecode.lanterna.input.KeyStroke key) {
        if (key.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape) {
            close();
            return true;
        }
        
        if (key.getKeyType() == com.googlecode.lanterna.input.KeyType.Tab && tabs.size() > 1) {
            int nextIndex = (currentTabIndex + 1) % tabs.size();
            switchTab(nextIndex);
            refreshTabBar();
            return true;
        }
        
        return super.handleInput(key);
    }
    
    protected void navigateTo(Window newWindow) {
        if (getTextGUI() != null) {
            setVisible(false);
            getTextGUI().addWindowAndWait(newWindow);
            setVisible(true);
        }
    }
}
