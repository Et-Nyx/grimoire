package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String name;
    private String description;
    private int quantity;
    private double weight;
    private double cost; // In Tibars (T$)
}
