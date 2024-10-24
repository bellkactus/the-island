package models.pions;

import models.Couleur;

public class PionExplorateur implements Pion {
    private int numero;
    private boolean estNajeur;
    private Couleur couleur;
    private boolean estSurBateau;

    public PionExplorateur(int numero, Couleur couleur) {
        this.numero = numero;
        this.couleur = couleur;
        this.estNajeur = false;
        this.estSurBateau = false;
    }

    public boolean getEstNajeur() {
        return estNajeur;
    }

    public void setEstNajeur(boolean estNajeur) {
        this.estNajeur = estNajeur;
    }

    public boolean getEstSurBateau() {
        return estSurBateau;
    }

    public void setEstSurBateau(boolean estSurBateau) {
        this.estSurBateau = estSurBateau;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    @Override
    public String toString() {
        return "Pion " + this.couleur.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PionExplorateur && ((PionExplorateur) obj).numero == numero && ((PionExplorateur) obj).couleur.equals(couleur);
    }
}