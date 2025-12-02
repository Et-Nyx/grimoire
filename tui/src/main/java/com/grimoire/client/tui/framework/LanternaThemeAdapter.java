package com.grimoire.client.tui.framework;

import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.Component;

/**
 * Adapts the application Theme to Lanterna's Theme interface.
 */
public class LanternaThemeAdapter extends SimpleTheme {
    private final Theme theme;

    public LanternaThemeAdapter(Theme theme) {
        super(theme.foreground(), theme.background());
        this.theme = theme;
    }
}
