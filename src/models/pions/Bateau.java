package models.pions;

import models.Couleur;

import java.util.*;


/**
 *
 */
public class Bateau implements Pion {
    private List<PionExplorateur> pionsExplorateur;

    /**
     * Default constructor
     */
    public Bateau() {
        this.pionsExplorateur = new ArrayList<>();
    }

    /**
     *
     */

    public List<PionExplorateur> getPionsExplorateur() {
        return pionsExplorateur;
    }

    /**
     * @return
     */
    public List<Couleur> getControlleurs() {
        /*Cette méthode retourne la liste des pions explorateurs qui peuvent conduire le bateau*/
        try {
            List<Couleur> colorsOfPions = new ArrayList<>();
            List<Couleur> listOfColors = new ArrayList<>();
            for (int i = 0; i < this.pionsExplorateur.size(); i++) {
                listOfColors.add(pionsExplorateur.get(i).getCouleur());
            }
            Set<Couleur> listofColorsSansOcc = new HashSet<>(listOfColors);
            if (pionsExplorateur.size() == listofColorsSansOcc.size()) {
                for (int i = 0; i < pionsExplorateur.size(); i++) {
                    colorsOfPions.add(pionsExplorateur.get(i).getCouleur());
                }
            } else {
                // Set<Couleur> unique = new HashSet<Couleur>(listOfColors);
                Object obj = Collections.max(listofColorsSansOcc);
                for (int i = 0; i < pionsExplorateur.size(); i++) {
                    if (pionsExplorateur.get(i).getCouleur() == obj) {
                        colorsOfPions.add(pionsExplorateur.get(i).getCouleur());
                    }
                }
            }
            return colorsOfPions;
        } catch (Exception e) {
            System.out.println(e);
        }
        ;
        return null;
    }

    public void embarquerPion(PionExplorateur pion) {
        try {
            if (pionsExplorateur.size() < 3) {
                System.out.println(pion.getNumero());
                pion.setEstSurBateau(true);
                pion.setEstNajeur(false);
                this.pionsExplorateur.add(pion);
            } else {
                System.out.println("Can add maximum 3 explorateurs ");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public List<PionExplorateur> getPionsOnBateau() {
        for (int i = 0; i < this.pionsExplorateur.size(); i++) {
            System.out.println("Pion numéro : " + pionsExplorateur.get(i).getNumero() + " | " + "Pion couleur : " + pionsExplorateur.get(i).getCouleur());
        }
        return this.pionsExplorateur;
    }

    /**
     * @param pion
     */
    public void debarquerPion(PionExplorateur pion) {
        try {
            int size = pionsExplorateur.size();
            if (size > 0) {

                this.pionsExplorateur.remove(pion);
                pion.setEstSurBateau(false);
                pion.setEstNajeur(true);
            } else {
                System.out.println("Il y'a aucun explorateurPion a debarquer du bateau");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public boolean existeEncoreDuPlace() {
        return getPionsExplorateur().size() < 3;
    }

    @Override
    public String toString() {
        return "Bateau";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bateau && pionsExplorateur.size() == ((Bateau) obj).getPionsExplorateur().size();
    }
}
