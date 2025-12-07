package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    private String name;
    private String description;
    private int quantity;
    private double weight;
    private double cost; // In Tibars (T$)
    @Builder.Default
    private String type = "Geral"; // Arma, Armadura, Consumível, Ferramenta, Geral
}
