package com.grimoire.client.tui.framework;

import com.googlecode.lanterna.gui2.Component;

/**
 * Represents a page or view in the application.
 */
public interface View {
    /**
     * Called when the view is navigated to.
     */
    void onEnter();

    /**
     * Called when the view is navigated away from.
     */
    void onLeave();

    /**
     * Returns the root component of this view.
     */
    Component getContent();
    
    /**
     * Returns the title of this view.
     */
    String getTitle();
}
