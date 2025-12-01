package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private List<Item> items = new ArrayList<>();
    private List<Item> equippedItems = new ArrayList<>();
    private int currency; // T$
    private double loadCurrent;
    private double loadMax;
    private double lift;
}
