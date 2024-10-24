package models;

import models.pions.Bateau;
import models.pions.Creature;
import models.pions.Pion;
import models.pions.PionExplorateur;
import models.tuiles.Contour;
import models.tuiles.FaceAction;
import models.tuiles.Terrain;
import models.tuiles.Tuile;

import java.util.*;

/**
 *
 */
public class Joueur {

    private String pseudo;
    private Couleur couleurPion;
    private List<PionExplorateur> explorateursPerdus;
    private List<PionExplorateur> explorateursNonJoues;
    private List<Bateau> bateauxNonJoues;
    private List<FaceAction> facesActionEnReserve;
    private List<PionExplorateur> listePions;
    private List<PionExplorateur> explorateursSauves;


    public Joueur(String pseudo, Couleur couleurPion) {
        this.pseudo = pseudo;
        this.couleurPion = couleurPion;
        this.explorateursPerdus = new ArrayList<>();
        this.explorateursSauves = new ArrayList<>();
        this.facesActionEnReserve = new ArrayList<>();
        this.explorateursNonJoues = new ArrayList<>();
        // Initialiser 2 bateaux non joué par défaut pour chaque joueur
        this.bateauxNonJoues = new ArrayList<>(List.of(new Bateau(), new Bateau()));
        initialiserExplorateursNonJoues();
    }

    public Couleur getCouleurPion() {
        return couleurPion;
    }

    public String getPseudo() {
        return pseudo;
    }

    public List<PionExplorateur> getExplorateursSauves() {
        return explorateursSauves;
    }

    public List<Bateau> getBateauxNonJoues() {
        return bateauxNonJoues;
    }

    public void ajouterFaceReserve(FaceAction faceAction) {
        facesActionEnReserve.add(faceAction);
    }

    public void supprimerFaceReserve(FaceAction faceAction) {
        facesActionEnReserve.remove(faceAction);
    }

    public List<FaceAction> getFacesActionEnReserve() {
        return facesActionEnReserve;
    }

    private void initialiserExplorateursNonJoues() {
        Map<Integer, Integer> pionsJoueur = new HashMap<>();
        pionsJoueur.put(1, 3); // 1: numero, 3 : nombre exemplaires
        pionsJoueur.put(2, 2);
        pionsJoueur.put(3, 2);
        pionsJoueur.put(4, 1);
        pionsJoueur.put(5, 1);
        pionsJoueur.put(6, 1);
        pionsJoueur.forEach((numero, exemplaires) -> {
            for (int i = 0; i < exemplaires; i++) {
                explorateursNonJoues.add(new PionExplorateur(numero, couleurPion));
            }
        });

    }

    void retirerPionNonJoue(PionExplorateur pionExplorateur) {
        explorateursNonJoues.remove(pionExplorateur);
    }

    boolean restePionNonJoue() {
        return !explorateursNonJoues.isEmpty();
    }

    void retirerBateauNonJoue(Bateau bateau) {
        bateauxNonJoues.remove(bateau);
    }

    boolean resteBateauNonJoue() {
        return !bateauxNonJoues.isEmpty();
    }

    public List<PionExplorateur> getExplorateursNonJoues() {
        return explorateursNonJoues;
    }

    public void sauverPion(PionExplorateur pionExplorateur) {
        explorateursSauves.add(pionExplorateur);
    }

    public int compterPionSauves() {
        int somme = 0;
        for (PionExplorateur pion : explorateursSauves) {
            somme = somme + pion.getNumero();
        }
        return somme;
    }

}