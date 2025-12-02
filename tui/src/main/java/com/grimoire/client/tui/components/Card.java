package com.grimoire.client.tui.components;

import com.googlecode.lanterna.gui2.Border;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Panel;

/**
 * A container for grouped content.
 */
public class Card extends Panel {
    private final String title;

    public Card(String title) {
        this.title = title;
    }

    public Card() {
        this(null);
    }

    /**
     * Returns this card wrapped in a border.
     */
    public Component withBorder() {
        Border border;
        if (title != null && !title.isEmpty()) {
            border = Borders.singleLine(title);
        } else {
            border = Borders.singleLine();
        }
        border.setComponent(this);
        return border;
    }
}
