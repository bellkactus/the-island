package models.tuiles;

/**
 * 
 */
public enum Contour {
    VERT,
    ROUGE,
    ROUGE_BARRE;

    @Override
    public String toString() {
        switch (this) {
            case VERT -> {
                return "vert";
            }
            case ROUGE -> {
                return "rouge";
            }
            case ROUGE_BARRE -> {
                return "rouge_barree";
            }
            default -> {
                return "";
            }
        }
    }
}