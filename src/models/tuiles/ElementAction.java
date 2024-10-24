package models.tuiles;

/**
 *
 */
public enum ElementAction {
    VOLCAN,
    TOURBILLON,
    DAUPHIN,
    VENT,
    REQUIN,
    BALEINE,
    SERPENT,
    BATEAU;

    @Override
    public String toString() {
        switch (this) {
            case VOLCAN -> {
                return "volcan";
            }
            case TOURBILLON -> {
                return "tourbillon";
            }
            case DAUPHIN -> {
                return "dauphin";
            }
            case VENT -> {
                return "vent";
            }
            case REQUIN -> {
                return "requin";
            }
            case BALEINE -> {
                return "baleine";
            }
            case SERPENT -> {
                return "serpent";
            }
            case BATEAU -> {
                return "bateau";
            }
            default -> {
                return "";
            }
        }
    }
}