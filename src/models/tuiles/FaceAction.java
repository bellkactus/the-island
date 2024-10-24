package models.tuiles;

/**
 * 
 */
public class FaceAction {

    public FaceAction(Contour contour, ElementAction contenu) {
        this.contour = contour;
        this.contenu = contenu;
    }

    private Contour contour;
    private ElementAction contenu;

    public Contour getContour() {
        return contour;
    }

    public ElementAction getContenu() {
        return contenu;
    }

    public void setContour(Contour contour){
        this.contour=contour;
    }
    public void setContenu(ElementAction contenu){
        this.contenu=contenu;
    }

    @Override
    public String toString() {
        return "tuile_" + contour + "_" + contenu;
    }
}