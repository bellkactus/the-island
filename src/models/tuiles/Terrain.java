package models.tuiles;

import models.DeCreature;

import java.util.Random;

/**
 *
 */
public enum Terrain {
    PLAGE(1),
    FORET(2),
    MONTAGNE(3);

    private final int priorite;

    Terrain(int priorite) {
        this.priorite = priorite;
    }

    public int getPriorite() {
        return priorite;
    }

    public static Terrain retournerAleatoire() {
        return Terrain.values()[new Random().nextInt(Terrain.values().length)];
    }

    @Override
    public String toString() {
        switch (this) {
            case PLAGE -> {
                return "plage";
            }
            case FORET -> {
                return "foret";
            }
            case MONTAGNE -> {
                return "montagne";
            }
            default -> {
                return "";
            }
        }
    }
}
