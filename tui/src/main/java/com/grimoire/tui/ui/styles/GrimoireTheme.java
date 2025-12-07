package com.grimoire.tui.ui.styles;

import com.googlecode.lanterna.TextColor;

public class GrimoireTheme {
    
    public static final TextColor PRIMARY = TextColor.ANSI.CYAN;
    public static final TextColor SECONDARY = TextColor.ANSI.YELLOW;
    public static final TextColor BACKGROUND = TextColor.ANSI.BLACK;
    public static final TextColor CARD_BORDER = new TextColor.RGB(128, 128, 128);
    public static final TextColor TIMESTAMP = new TextColor.RGB(128, 128, 128);
    public static final TextColor TEXT_NORMAL = TextColor.ANSI.WHITE;
    public static final TextColor SUCCESS = TextColor.ANSI.GREEN;
    public static final TextColor ERROR = TextColor.ANSI.RED;
    
    private GrimoireTheme() {
    }
}
