package models.pions;

import java.util.List;

/**
 *
 */
public enum Creature implements Pion {
    REQUIN(2, List.of(PionExplorateur.class)),
    BALEINE(3, List.of(Bateau.class)),
    SERPENT(1, List.of(PionExplorateur.class, Bateau.class));

    private final int nombreDeplacementMax;
    private final List<Class<? extends Pion>> cibles;

    Creature(int nombreDeplacementMax, List<Class<? extends Pion>> cibles) {
        this.nombreDeplacementMax = nombreDeplacementMax;
        this.cibles = cibles;
    }

    public int getNombreDeplacementMax() {
        return nombreDeplacementMax;
    }

    @Override
    public String toString() {
        switch (this){
            case REQUIN -> {
                return "Requin";
            }
            case BALEINE -> {
                return "Baleine";
            }
            case SERPENT -> {
                return "Serpent";
            }
            default -> {
                return "";
            }
        }
    }

}