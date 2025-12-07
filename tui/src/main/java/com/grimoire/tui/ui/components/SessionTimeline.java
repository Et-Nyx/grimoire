package com.grimoire.tui.ui.components;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SessionTimeline extends AbstractComponent<SessionTimeline> implements Interactable {
    
    private static final Logger LOGGER = Logger.getLogger(SessionTimeline.class.getName());
    
    static {
        try {
            // Ensure .logs directory exists
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(".logs"));
            FileHandler fileHandler = new FileHandler(".logs/timeline-debug.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(java.util.logging.Level.OFF);  // Disable logging in production
        } catch (IOException e) {
            // Silently ignore - logging is optional
            LOGGER.setLevel(java.util.logging.Level.OFF);
        }
    }
    
    private final List<RichTimelineEntry> entries;
    private List<RichTimelineEntry.RenderedLine> flattenedLines;
    private int firstVisibleLine;
    private final AtomicBoolean inFocus;
    private boolean selectionMode = false;
    private int selectedEntryIndex = -1;
    private java.util.function.Consumer<Object> onEntrySelected;

    public SessionTimeline() {
        this.entries = new ArrayList<>();
        this.flattenedLines = new ArrayList<>();
        this.firstVisibleLine = 0;
        this.inFocus = new AtomicBoolean(false);
    }
    
    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        if (selectionMode && !entries.isEmpty() && selectedEntryIndex == -1) {
            selectedEntryIndex = 0;
        }
        invalidate();
    }
    
    public void setOnEntrySelected(java.util.function.Consumer<Object> onEntrySelected) {
        this.onEntrySelected = onEntrySelected;
    }
    
    public void addEntry(RichTimelineEntry entry) {
        entries.add(entry);
        rebuildFlattenedLines();
        LOGGER.info("addEntry: entries=" + entries.size() + ", flattenedLines=" + flattenedLines.size());
        invalidate();
    }
    
    public void clear() {
        entries.clear();
        flattenedLines.clear();
        firstVisibleLine = 0;
    }
    
    public void scrollToBottom() {
        TerminalSize size = getSize();
        if (size == null) {
            LOGGER.warning("scrollToBottom: size is null, cannot scroll");
            return;
        }
        
        if (flattenedLines.isEmpty()) {
            LOGGER.info("scrollToBottom: no lines to scroll");
            firstVisibleLine = 0;
            return;
        }
        
        int viewportHeight = size.getRows();
        int maxScroll = Math.max(0, flattenedLines.size() - viewportHeight);
        
        LOGGER.info("scrollToBottom: flattenedLines=" + flattenedLines.size() + 
                    ", viewportHeight=" + viewportHeight + 
                    ", maxScroll=" + maxScroll + 
                    ", setting firstVisibleLine=" + maxScroll);
        
        firstVisibleLine = maxScroll;
        invalidate();
    }
    
    private void rebuildFlattenedLines() {
        flattenedLines.clear();
        for (RichTimelineEntry entry : entries) {
            List<RichTimelineEntry.RenderedLine> lines = entry.renderToLines();
            flattenedLines.addAll(lines);
        }
    }
    
    @Override
    protected ComponentRenderer<SessionTimeline> createDefaultRenderer() {
        return new TimelineRenderer();
    }
    
    @Override
    public Result handleInput(KeyStroke keyStroke) {
        LOGGER.info("handleInput: keyType=" + keyStroke.getKeyType() + ", isFocused=" + isFocused() + ", firstVisibleLine=" + firstVisibleLine + ", flattenedLines.size=" + flattenedLines.size());
        
        if (selectionMode) {
            if (keyStroke.getKeyType() == KeyType.Enter && selectedEntryIndex >= 0 && selectedEntryIndex < entries.size()) {
                if (onEntrySelected != null) {
                    onEntrySelected.accept(entries.get(selectedEntryIndex).getPayload());
                }
                return Result.HANDLED;
            }
            
            if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                if (selectedEntryIndex > 0) {
                    selectedEntryIndex--;
                    ensureSelectionVisible();
                    invalidate();
                    return Result.HANDLED;
                } else {
                     return Result.MOVE_FOCUS_UP;
                }
            }
            
            if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                if (selectedEntryIndex < entries.size() - 1) {
                    selectedEntryIndex++;
                    ensureSelectionVisible();
                    invalidate();
                    return Result.HANDLED;
                } else {
                    return Result.MOVE_FOCUS_DOWN;
                }
            }
        }

        if (keyStroke.getKeyType() == KeyType.ArrowUp) {
            LOGGER.info("ArrowUp pressed: firstVisibleLine=" + firstVisibleLine);
            if (firstVisibleLine > 0) {
                firstVisibleLine--;
                invalidate();
                LOGGER.info("Scrolled up to: " + firstVisibleLine);
                return Result.HANDLED;
            } else {
                LOGGER.info("At top, moving focus up");
                return Result.MOVE_FOCUS_UP;
            }
        }
        
        if (keyStroke.getKeyType() == KeyType.ArrowDown) {
            TerminalSize size = getSize();
            if (size == null) return Result.HANDLED;
            
            int viewportHeight = size.getRows();
            int maxScroll = Math.max(0, flattenedLines.size() - viewportHeight);
            
            LOGGER.info("ArrowDown pressed: firstVisibleLine=" + firstVisibleLine + ", maxScroll=" + maxScroll);
            
            if (firstVisibleLine < maxScroll) {
                firstVisibleLine++;
                invalidate();
                LOGGER.info("Scrolled down to: " + firstVisibleLine);
                return Result.HANDLED;
            } else {
                LOGGER.info("At bottom, moving focus down");
                return Result.MOVE_FOCUS_DOWN;
            }
        }
        
        if (keyStroke.getKeyType() == KeyType.PageUp) {
            TerminalSize size = getSize();
            if (size == null) return Result.HANDLED;
            
            int viewportHeight = size.getRows();
            int oldLine = firstVisibleLine;
            firstVisibleLine = Math.max(0, firstVisibleLine - viewportHeight);
            invalidate();
            LOGGER.info("PageUp: " + oldLine + " -> " + firstVisibleLine);
            return Result.HANDLED;
        }
        
        if (keyStroke.getKeyType() == KeyType.PageDown) {
            TerminalSize size = getSize();
            if (size == null) return Result.HANDLED;
            
            int viewportHeight = size.getRows();
            int maxScroll = Math.max(0, flattenedLines.size() - viewportHeight);
            int oldLine = firstVisibleLine;
            firstVisibleLine = Math.min(maxScroll, firstVisibleLine + viewportHeight);
            invalidate();
            LOGGER.info("PageDown: " + oldLine + " -> " + firstVisibleLine);
            return Result.HANDLED;
        }
        
        LOGGER.info("Key not handled: " + keyStroke.getKeyType());
        return Result.UNHANDLED;
    }

    private void ensureSelectionVisible() {
        if (entries.isEmpty() || selectedEntryIndex < 0) return;
        
        // Find the range of lines for the selected entry
        int startLine = 0;
        for (int i = 0; i < selectedEntryIndex; i++) {
            startLine += entries.get(i).renderToLines().size();
        }
        int endLine = startLine + entries.get(selectedEntryIndex).renderToLines().size();
        
        TerminalSize size = getSize();
        if (size == null) return;
        int viewportHeight = size.getRows();
        
        if (startLine < firstVisibleLine) {
            firstVisibleLine = startLine;
        } else if (endLine > firstVisibleLine + viewportHeight) {
            firstVisibleLine = endLine - viewportHeight;
        }
    }
    
    @Override
    public boolean isFocused() {
        return inFocus.get();
    }
    
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public Interactable setEnabled(boolean enabled) {
        return this;
    }
    
    @Override
    public InputFilter getInputFilter() {
        return null;
    }
    
    @Override
    public Interactable setInputFilter(InputFilter inputFilter) {
        return this;
    }
    
    @Override
    public void onEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        inFocus.set(true);
    }
    
    @Override
    public void onLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
        inFocus.set(false);
    }
    
    @Override
    public Interactable takeFocus() {
        inFocus.set(true);
        return this;
    }
    
    @Override
    public TerminalPosition getCursorLocation() {
        return null;
    }
    
    private static class TimelineRenderer implements ComponentRenderer<SessionTimeline> {
        @Override
        public TerminalSize getPreferredSize(SessionTimeline component) {
            return new TerminalSize(60, 15);
        }
        
        @Override
        public void drawComponent(TextGUIGraphics graphics, SessionTimeline component) {
            TerminalSize size = graphics.getSize();
            int viewportHeight = size.getRows();
            int viewportWidth = size.getColumns();
            
            List<RichTimelineEntry.RenderedLine> lines = component.flattenedLines;
            int startLine = component.firstVisibleLine;
            int endLine = Math.min(startLine + viewportHeight, lines.size());
            
            LOGGER.info("drawComponent: size=" + size + ", lines.size=" + lines.size() + ", startLine=" + startLine + ", endLine=" + endLine);
            
            graphics.fill(' ');
            
            if (lines.isEmpty()) {
                return;
            }
            
            for (int i = startLine; i < endLine; i++) {
                RichTimelineEntry.RenderedLine line = lines.get(i);
                int row = i - startLine;
                int col = 0;
                
                boolean isSelected = false;
                if (component.selectionMode && component.selectedEntryIndex >= 0 && component.selectedEntryIndex < component.entries.size()) {
                    RichTimelineEntry selectedEntry = component.entries.get(component.selectedEntryIndex);
                    if (line.getParentEntry() == selectedEntry) {
                        isSelected = true;
                    }
                }
                
                if (isSelected) {
                    graphics.setBackgroundColor(com.googlecode.lanterna.TextColor.ANSI.BLUE);
                    graphics.fillRectangle(new TerminalPosition(0, row), new TerminalSize(viewportWidth, 1), ' ');
                } else {
                    graphics.setBackgroundColor(com.googlecode.lanterna.TextColor.ANSI.DEFAULT);
                }
                
                for (RichTimelineEntry.StyledSegment segment : line.getSegments()) {
                    if (col >= viewportWidth) break;
                    
                    String text = segment.text;
                    if (col + text.length() > viewportWidth) {
                        text = text.substring(0, viewportWidth - col);
                    }
                    
                    graphics.setForegroundColor(segment.color);
                    for (com.googlecode.lanterna.SGR style : segment.styles) {
                        graphics.enableModifiers(style);
                    }
                    
                    graphics.putString(col, row, text);
                    
                    for (com.googlecode.lanterna.SGR style : segment.styles) {
                        graphics.disableModifiers(style);
                    }
                    
                    col += text.length();
                }
            }
            
            if (component.isFocused() && lines.size() > viewportHeight) {
                String scrollIndicator = String.format("↑ [%d/%d] ↓", 
                    startLine + 1, 
                    lines.size()
                );
                int indicatorCol = Math.max(0, viewportWidth - scrollIndicator.length() - 1);
                
                graphics.setForegroundColor(com.googlecode.lanterna.TextColor.ANSI.YELLOW);
                graphics.enableModifiers(com.googlecode.lanterna.SGR.BOLD);
                graphics.putString(indicatorCol, 0, scrollIndicator);
                graphics.disableModifiers(com.googlecode.lanterna.SGR.BOLD);
            }
        }
    }
}
