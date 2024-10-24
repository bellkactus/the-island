package models;

/**
 *
 */
public enum Couleur {
    ROUGE,
    JAUNE,
    VERT,
    BLEU;

    @Override
    public String toString() {
        switch (this) {
            case ROUGE -> {
                return "Rouge";
            }
            case JAUNE -> {
                return "Jaune";
            }
            case VERT -> {
                return "Vert";
            }
            case BLEU -> {
                return "Bleu";
            }
            default -> {
                return "";
            }
        }
    }
}