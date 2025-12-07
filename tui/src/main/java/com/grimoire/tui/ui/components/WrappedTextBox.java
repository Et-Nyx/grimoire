package com.grimoire.tui.ui.components;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextBox;
import com.grimoire.tui.util.TextUtils;

/**
 * TextBox com word wrap automático.
 * Quebra o texto pela largura do componente, mostrando scroll vertical em vez de horizontal.
 */
public class WrappedTextBox extends TextBox {
    
    private int wrapWidth;
    private String originalText = "";
    
    /**
     * Cria um WrappedTextBox com tamanho e estilo especificados.
     * 
     * @param size Tamanho preferido (largura usada para wrap)
     */
    public WrappedTextBox(TerminalSize size) {
        super(size, "", Style.MULTI_LINE);
        this.wrapWidth = size.getColumns() - 2; // Margem para bordas
    }
    
    /**
     * Cria um WrappedTextBox com tamanho, texto inicial e estilo.
     * 
     * @param size Tamanho preferido
     * @param initialText Texto inicial (será aplicado wrap)
     */
    public WrappedTextBox(TerminalSize size, String initialText) {
        super(size, "", Style.MULTI_LINE);
        this.wrapWidth = size.getColumns() - 2;
        setWrappedText(initialText);
    }
    
    /**
     * Define o texto aplicando word wrap automático.
     * 
     * @param text Texto original (sem quebras artificiais)
     */
    public void setWrappedText(String text) {
        this.originalText = text != null ? text : "";
        String wrapped = TextUtils.wrapText(this.originalText, this.wrapWidth);
        super.setText(wrapped);
    }
    
    /**
     * Obtém o texto original, removendo quebras artificiais de wrap.
     * Preserva quebras de linha intencionais (parágrafos).
     * 
     * @return Texto sem quebras artificiais de wrap
     */
    public String getUnwrappedText() {
        String currentText = super.getText();
        if (currentText == null || currentText.isEmpty()) {
            return "";
        }
        
        // Preservar quebras duplas (parágrafos) e converter quebras simples em espaços
        StringBuilder result = new StringBuilder();
        String[] lines = currentText.split("\n", -1);
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            if (i > 0) {
                // Verificar se a linha anterior terminava em um ponto final de frase
                String prevLine = lines[i - 1];
                boolean wasParagraphEnd = prevLine.isEmpty() || 
                    prevLine.endsWith(".") || 
                    prevLine.endsWith("!") || 
                    prevLine.endsWith("?") ||
                    prevLine.endsWith(":");
                
                // Linha vazia indica parágrafo
                if (line.isEmpty() || wasParagraphEnd) {
                    result.append("\n");
                } else {
                    result.append(" ");
                }
            }
            
            result.append(line.trim());
        }
        
        return result.toString().trim();
    }
    
    /**
     * Atualiza a largura de wrap e reaplica o wrap no texto atual.
     * 
     * @param width Nova largura
     */
    public void setWrapWidth(int width) {
        this.wrapWidth = width;
        setWrappedText(getUnwrappedText());
    }
    
    /**
     * Obtém a largura atual de wrap.
     */
    public int getWrapWidth() {
        return wrapWidth;
    }
}
