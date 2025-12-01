package com.grimoire.common.model;

public enum SkillType {
    // Perícias normais
    ACROBACIA("Acrobacia", "DES", false, true),
    ADESTRAMENTO("Adestramento", "CAR", true, false),
    ATLETISMO("Atletismo", "FOR", false, false),
    ATUACAO("Atuação", "CAR", false, false),
    CAVALGAR("Cavalgar", "DES", false, false),
    CONHECIMENTO("Conhecimento", "INT", true, false),
    CURA("Cura", "SAB", false, false),
    DIPLOMACIA("Diplomacia", "CAR", false, false),
    ENGANACAO("Enganação", "CAR", false, false),
    FORTITUDE("Fortitude", "CON", false, false),
    FURTIVIDADE("Furtividade", "DES", false, true),
    GUERRA("Guerra", "INT", true, false),
    INICIATIVA("Iniciativa", "DES", false, false),
    INTIMIDACAO("Intimidação", "CAR", false, false),
    INTUICAO("Intuição", "SAB", false, false),
    INVESTIGACAO("Investigação", "INT", false, false),
    JOGATINA("Jogatina", "CAR", true, false),
    LADINAGEM("Ladinagem", "DES", true, true),
    LUTA("Luta", "FOR", false, false),
    MISTICISMO("Misticismo", "INT", true, false),
    NOBREZA("Nobreza", "INT", true, false),
    OFICIO("Ofício", "INT", true, false),
    PERCEPCAO("Percepção", "SAB", false, false),
    PILOTAGEM("Pilotagem", "DES", true, false),
    PONTARIA("Pontaria", "DES", false, false),
    REFLEXOS("Reflexos", "DES", false, false),
    RELIGIAO("Religião", "SAB", true, false),
    SOBREVIVENCIA("Sobrevivência", "SAB", false, false),
    VONTADE("Vontade", "SAB", false, false);

    private final String displayName;
    private final String attribute;
    private final boolean trainedOnly;
    private final boolean armorPenalty;

    SkillType(String displayName, String attribute, boolean trainedOnly, boolean armorPenalty) {
        this.displayName = displayName;
        this.attribute = attribute;
        this.trainedOnly = trainedOnly;
        this.armorPenalty = armorPenalty;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAttribute() {
        return attribute;
    }

    public boolean isTrainedOnly() {
        return trainedOnly;
    }

    public boolean hasArmorPenalty() {
        return armorPenalty;
    }
}
