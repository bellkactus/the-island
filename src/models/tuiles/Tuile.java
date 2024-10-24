package models.tuiles;

import models.Position;

import java.util.ArrayList;

/**
 *
 */
public class Tuile {

    public Tuile(Terrain faceTerrain, FaceAction faceAction) {
        this.faceTerrain = faceTerrain;
        this.faceAction = faceAction;
        this.estTourne = false;
        this.estVide = true;
    }

    private boolean estTourne;
    private boolean estVide;
    private Terrain faceTerrain;
    private FaceAction faceAction;

    public Terrain getFaceTerrain() {
        return faceTerrain;
    }

    public void setFaceTerrain(Terrain faceTerrain) {
        this.faceTerrain = faceTerrain;
    }

    public void setEstTourne(boolean estTourne) {
        this.estTourne = estTourne;
    }

    public boolean getEstTourne() {
        return estTourne;
    }

    public boolean getEstVide() {
        return estVide;
    }

    public void setEstVide(boolean estVide) {
        this.estVide = estVide;
    }

    public FaceAction getFaceAction() {
        return faceAction;
    }

    public void setFaceAction(FaceAction faceAction) {
        this.faceAction = faceAction;
    }
}