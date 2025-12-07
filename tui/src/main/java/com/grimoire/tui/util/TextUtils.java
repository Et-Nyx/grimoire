package com.grimoire.tui.util;

/**
 * Utilitários para manipulação de texto no TUI.
 * Inclui word wrap, truncamento e validação de limites.
 */
public final class TextUtils {
    
    private TextUtils() {
        // Utility class
    }
    
    // === Limites de caracteres por tipo de campo ===
    public static final int MAX_NAME = 50;
    public static final int MAX_TITLE = 100;
    public static final int MAX_DESCRIPTION = 500;
    public static final int MAX_SUMMARY = 500;
    public static final int MAX_NOTE = 1000;
    public static final int MAX_LORE = 2000;
    public static final int MAX_ITEM_DESCRIPTION = 200;
    
    // === Larguras padrão para TextBox ===
    public static final int DEFAULT_WIDTH = 50;
    public static final int SMALL_HEIGHT = 3;
    public static final int MEDIUM_HEIGHT = 5;
    public static final int LARGE_HEIGHT = 8;
    
    /**
     * Quebra texto em linhas com largura máxima, respeitando palavras.
     * 
     * @param text Texto a quebrar
     * @param maxWidth Largura máxima por linha
     * @return Texto com quebras de linha inseridas
     */
    public static String wrapText(String text, int maxWidth) {
        if (text == null || text.isEmpty() || maxWidth <= 0) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        String[] paragraphs = text.split("\n");
        
        for (int p = 0; p < paragraphs.length; p++) {
            String paragraph = paragraphs[p];
            
            if (paragraph.isEmpty()) {
                result.append("\n");
                continue;
            }
            
            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();
            
            for (String word : words) {
                if (line.length() == 0) {
                    line.append(word);
                } else if (line.length() + 1 + word.length() <= maxWidth) {
                    line.append(" ").append(word);
                } else {
                    result.append(line).append("\n");
                    line = new StringBuilder(word);
                }
            }
            
            if (line.length() > 0) {
                result.append(line);
            }
            
            if (p < paragraphs.length - 1) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
    
    /**
     * Trunca texto para um limite máximo de caracteres.
     * 
     * @param text Texto a truncar
     * @param maxLength Comprimento máximo
     * @return Texto truncado com "..." se necessário
     */
    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        
        if (maxLength <= 3) {
            return text.substring(0, maxLength);
        }
        
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Valida se o texto está dentro do limite.
     * 
     * @param text Texto a validar
     * @param maxLength Limite máximo
     * @return true se válido, false se excede
     */
    public static boolean isWithinLimit(String text, int maxLength) {
        return text == null || text.length() <= maxLength;
    }
    
    /**
     * Conta caracteres e retorna mensagem de status.
     * 
     * @param text Texto atual
     * @param maxLength Limite máximo
     * @return Mensagem formatada (ex: "150/500 caracteres")
     */
    public static String getCharacterCount(String text, int maxLength) {
        int count = text != null ? text.length() : 0;
        String status = count > maxLength ? " (EXCEDE!)" : "";
        return count + "/" + maxLength + " caracteres" + status;
    }
    
    /**
     * Remove quebras de linha extras e espaços duplicados.
     * 
     * @param text Texto a limpar
     * @return Texto normalizado
     */
    public static String normalize(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Formata texto para exibição em uma única linha (preview).
     * 
     * @param text Texto completo
     * @param maxLength Comprimento máximo do preview
     * @return Preview do texto
     */
    public static String preview(String text, int maxLength) {
        if (text == null || text.isEmpty()) return "(vazio)";
        String normalized = normalize(text);
        return truncate(normalized, maxLength);
    }
}
