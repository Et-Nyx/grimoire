package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attack {
    private String name;
    private String attackBonus; // e.g., "+5"
    private String damage; // e.g., "1d8+2"
    private String critical; // e.g., "19/x2"
    private String type; // e.g., "Corte"
    private String range; // e.g., "Curto"
}
