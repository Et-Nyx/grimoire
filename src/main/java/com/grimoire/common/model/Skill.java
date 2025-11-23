package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    private String name;
    private int total;
    private int halfLevel;
    private int attrMod;
    private int training;
    private int others;
    private boolean isTrained;
}
