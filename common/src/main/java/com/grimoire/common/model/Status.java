package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {
    private int hpCurrent;
    private int hpMax;
    private int mpCurrent;
    private int mpMax;
    private int xp;
    private int xpToNext; // Pontos entre aventura
}
