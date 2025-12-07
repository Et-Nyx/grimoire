package com.grimoire.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ability {
    private String name;
    private String description;
    private String type; // e.g., "Racial", "Class", "Origin", "Power"
    private String cost; // e.g., "1 PM"
}
