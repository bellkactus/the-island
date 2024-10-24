package models;

/**
 *
 */
public enum EtapeJeu {
    JOUER_TUILE_EN_RESERVE(1, 1),
    DEPLACER_PION(2, 3),
    RETIRER_TUILE(3, 1),
    LANCER_DE(4, -1); // Cette etape depend de lancement de dé, variable de 1 a 3

    private final int prioriteJeu;
    private final int nombreEtapesMax;

    EtapeJeu(int prioriteJeu, int nombreEtapesMax) {
        this.prioriteJeu = prioriteJeu;
        this.nombreEtapesMax = nombreEtapesMax;
    }

    public int getPrioriteJeu() {
        return prioriteJeu;
    }

    public int getNombreEtapesMax() {
        return nombreEtapesMax;
    }

    @Override
    public String toString() {
        switch (this) {
            case JOUER_TUILE_EN_RESERVE -> {
                return "Tuile en réserve";
            }
            case DEPLACER_PION -> {
                return "Déplacer pions";
            }
            case RETIRER_TUILE -> {
                return "Retirer tuile";
            }
            case LANCER_DE -> {
                return "Dé créature";
            }
            default -> {
                return "";
            }
        }
    }
}