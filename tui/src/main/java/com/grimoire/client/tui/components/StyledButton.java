package com.grimoire.client.tui.components;

import com.googlecode.lanterna.gui2.Button;

/**
 * A button with custom styling capabilities.
 */
public class StyledButton extends Button {
    public StyledButton(String label, Runnable action) {
        super(label, () -> {
            java.util.logging.Logger.getLogger(StyledButton.class.getName()).info("Button clicked: " + label);
            action.run();
        });
    }
}
