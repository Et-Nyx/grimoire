package com.grimoire.tui.ui.components;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class RichTimelineEntry {
    
    private final List<StyledSegment> segments;
    private final int maxWidth;
    
    public RichTimelineEntry(int maxWidth) {
        this.segments = new ArrayList<>();
        this.maxWidth = maxWidth;
    }
    
    public void addSegment(String text, TextColor color, EnumSet<SGR> styles) {
        if (text != null && !text.isEmpty()) {
            segments.add(new StyledSegment(text, color, styles));
        }
    }
    
    public void addSegment(String text, TextColor color) {
        addSegment(text, color, EnumSet.noneOf(SGR.class));
    }
    
    public void addPlainText(String text) {
        addSegment(text, TextColor.ANSI.WHITE, EnumSet.noneOf(SGR.class));
    }
    
    public List<RenderedLine> renderToLines() {
        List<RenderedLine> lines = new ArrayList<>();
        RenderedLine currentLine = new RenderedLine();
        currentLine.setParentEntry(this);
        int currentLineWidth = 0;
        
        for (StyledSegment segment : segments) {
            if (segment.text.equals("\n")) {
                lines.add(currentLine);
                currentLine = new RenderedLine();
                currentLine.setParentEntry(this);
                currentLineWidth = 0;
                continue;
            }
            
            String[] words = segment.text.split(" ", -1);
            
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                boolean isLastWord = (i == words.length - 1);
                
                if (word.contains("\n")) {
                    String[] parts = word.split("\n", -1);
                    for (int j = 0; j < parts.length; j++) {
                        if (j > 0) {
                            lines.add(currentLine);
                            currentLine = new RenderedLine();
                            currentLine.setParentEntry(this);
                            currentLineWidth = 0;
                        }
                        
                        if (!parts[j].isEmpty()) {
                            int wordLen = parts[j].length();
                            if (currentLineWidth + wordLen + (currentLineWidth > 0 ? 1 : 0) > maxWidth) {
                                if (currentLineWidth > 0) {
                                    lines.add(currentLine);
                                    currentLine = new RenderedLine();
                                    currentLine.setParentEntry(this);
                                    currentLineWidth = 0;
                                }
                            }
                            
                            if (currentLineWidth > 0) {
                                currentLine.addSegment(" ", segment.color, segment.styles);
                                currentLineWidth++;
                            }
                            
                            currentLine.addSegment(parts[j], segment.color, segment.styles);
                            currentLineWidth += wordLen;
                        }
                    }
                } else {
                    int wordLen = word.length();
                    int spaceNeeded = wordLen + (currentLineWidth > 0 && !isLastWord ? 1 : 0);
                    
                    if (currentLineWidth > 0 && currentLineWidth + spaceNeeded > maxWidth) {
                        lines.add(currentLine);
                        currentLine = new RenderedLine();
                        currentLine.setParentEntry(this);
                        currentLineWidth = 0;
                    }
                    
                    if (currentLineWidth > 0 && !word.isEmpty()) {
                        currentLine.addSegment(" ", segment.color, segment.styles);
                        currentLineWidth++;
                    }
                    
                    if (!word.isEmpty()) {
                        currentLine.addSegment(word, segment.color, segment.styles);
                        currentLineWidth += wordLen;
                    }
                }
            }
        }
        
        if (!currentLine.isEmpty() || lines.isEmpty()) {
            lines.add(currentLine);
        }
        
        return lines;
    }
    
    private Object payload;

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }

    public static class StyledSegment {
        public final String text;
        public final TextColor color;
        public final EnumSet<SGR> styles;
        
        public StyledSegment(String text, TextColor color, EnumSet<SGR> styles) {
            this.text = text;
            this.color = color;
            this.styles = styles;
        }
    }
    
    public static class RenderedLine {
        private final List<StyledSegment> segments = new ArrayList<>();
        private RichTimelineEntry parentEntry;

        public void setParentEntry(RichTimelineEntry parentEntry) {
            this.parentEntry = parentEntry;
        }

        public RichTimelineEntry getParentEntry() {
            return parentEntry;
        }
        
        public void addSegment(String text, TextColor color, EnumSet<SGR> styles) {
            segments.add(new StyledSegment(text, color, styles));
        }
        
        public List<StyledSegment> getSegments() {
            return segments;
        }
        
        public boolean isEmpty() {
            return segments.isEmpty();
        }
    }
}
