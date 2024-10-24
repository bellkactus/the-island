package models;

import models.pions.Creature;

import java.util.Random;

public enum DeCreature {
    REQUIN(Creature.REQUIN),
    BALEINE(Creature.BALEINE),
    SERPENT(Creature.SERPENT);

    private final Creature creature;

    DeCreature(Creature creature) {
        this.creature = creature;
    }

    public Creature getCreature() {
        return creature;
    }

    public static DeCreature jouerDe() {
        return DeCreature.values()[new Random().nextInt(DeCreature.values().length)];
    }

    @Override
    public String toString() {
        return creature.toString();
    }
}