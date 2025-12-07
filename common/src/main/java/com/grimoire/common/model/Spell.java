package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spell {
    private String name;
    private String school; // e.g., "Evocação"
    private int circle; // 1 to 5
    private String cost; // e.g., "1 PM"
    private String execution; // e.g., "Padrão"
    private String range; // e.g., "Curto"
    private String area; // e.g., "Esfera de 6m"
    private String duration; // e.g., "Instantânea"
    private String resistance; // e.g., "Reflexos reduz à metade"
    private String effect;
}
