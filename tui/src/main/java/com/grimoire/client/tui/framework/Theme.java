package com.grimoire.client.tui.framework;

import com.googlecode.lanterna.TextColor;

/**
 * Defines the color palette and styling for the application.
 */
public record Theme(
    TextColor background,
    TextColor foreground,
    TextColor primary,
    TextColor secondary,
    TextColor border,
    TextColor textMuted
) {
    public static Theme createDefault() {
        return new Theme(
            TextColor.ANSI.DEFAULT, // Background (Terminal Default)
            TextColor.ANSI.WHITE,   // Foreground (White)
            TextColor.ANSI.CYAN,    // Primary (Cyan)
            TextColor.ANSI.YELLOW,  // Secondary (Yellow)
            TextColor.ANSI.WHITE,   // Border (White)
            TextColor.ANSI.DEFAULT  // Muted (Default)
        );
    }
}
