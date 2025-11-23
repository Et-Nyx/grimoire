package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Magic {
    private int keyAttributeVal;
    private int cdResistance;
    private int pmMax;
    private List<Spell> spells = new ArrayList<>();
}
