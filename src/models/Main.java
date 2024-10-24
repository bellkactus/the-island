package models;
import models.pions.Bateau;
import models.pions.Creature;
import models.pions.Pion;
import models.tuiles.Contour;
import models.tuiles.ElementAction;
import models.tuiles.Tuile;

import java.util.*;

public class Main {
    public static void main(String[] args) {
//        try {
//            //INITIALISATION
//            boolean estTermine=false;
//            ArrayList<Joueur> listeJoueurs = new ArrayList<>();
//            ArrayList<Tuile> listeTuiles;
//            ArrayList<String> couleurs = new ArrayList<>();
//            Map<Pion, Tuile> mapJeu = new HashMap<>();
//
//            //Initialiser un objet pour la lecture
//            Scanner myScanner = new Scanner(System.in); // Objet pour lire l'input de l'utilisateur
//
//            System.out.println("Combien de joueurs vont jouer?");
//            int nbJoueurs = myScanner.nextInt();
//            //Test de validation des conditions de jeu
//            while (nbJoueurs < 2 || nbJoueurs > 4) {
//                System.out.println("Le nombre de joueurs doit etre entre 2 et 4");
//                System.out.println("Combien de joueurs vont jouer?");
//                nbJoueurs = myScanner.nextInt();
//            }
//            //INITIALISATION DES JOUEURS
//            for (int i = 0; i < nbJoueurs; i++) {
//                System.out.println("Choissez votre pseudo");
//                String pseudoJoueur = myScanner.next();
//
//                System.out.println("Choisissez votre couleur");
//                String couleurJoueur = myScanner.next();
//
//                //VÉRIFIER QU'IL N Y A PAS DES JOUEURS DU MEME COULEUR
//                for (String couleur : couleurs) {
//                    while (couleurJoueur.equals(couleur)) {
//                        System.out.println("Cette couleur est déjà prise");
//                        System.out.println("Choisissez une autre couleur");
//                        couleurJoueur = myScanner.next();
//                    }
//                }
//                Joueur joueur = new Joueur(pseudoJoueur, Couleur.valueOf(couleurJoueur));
//                joueur.setListePions(joueur.initPionsJoueur(joueur.getCouleur()));
//                joueur.setListeBateaux(joueur.listeBateaux());
//                listeJoueurs.add(joueur);
//                couleurs.add(couleurJoueur);
//                System.out.println("Joueur ajouté");
//            }
//            //INITIALISATION DU JEU
//            Jeu jeu = new Jeu(listeJoueurs);
//            listeTuiles=jeu.initialiserJeu();
//            System.out.println("Jeu initialisé");
//            //VERIFIER LES JOUEURS
//            for (Joueur joueur : listeJoueurs) {
//                System.out.println("Pseudo: " + joueur.getPseudo() + " Couleur: " + joueur.getCouleur());
//            }
//            int cpt=0;
//            /*listePositions=jeu.initPositions();
//            for(Position pos : listePositions){
//                System.out.println("x = "+pos.getX()+" y = "+pos.getY());
//            }*/
//            /*for (Tuile tuile : listeTuiles){
//                cpt=cpt+1;
//                System.out.println("x = "+tuile.getPosition().getX()+" y = "+tuile.getPosition().getY());
//                System.out.println("Terrain : "+tuile.getFaceTerrain().toString());
//                if(tuile.getFaceTerrain()!=Terrain.MER){
//                    System.out.println("Face : "+tuile.getFaceAction().getContenu().toString()+"    Contour : "+tuile.getFaceAction().getContour().toString());
//                }
//            }
//            System.out.println("on a "+cpt+" tuiles");*/
//            /*for (Joueur joueur : listeJoueurs){
//                System.out.println("Joueur : "+joueur.getPseudo());
//                System.out.println("Pions de couleur : "+ joueur.getCouleur()+"\n");
//                for (PionExplorateur pionExplorateur : joueur.getListePions()){
//                    System.out.println("Numero: "+pionExplorateur.getNumero()+" couleur : "+pionExplorateur.getCouleur().toString());
//                }
//            }*/
//            //Loop for pour deployer les pions sur les positions souhaitées par le joueur
//            /*for (int nbPions=0; nbPions<3; nbPions++){
//                for (Joueur joueur : listeJoueurs){
//                    System.out.println(joueur.getPseudo()+":  Placer le pion #"+(nbPions+1)+" en donnant les coordonnées x et y de la tuile désirée");
//                    int x = myScanner.nextInt();
//                    System.out.println("x sélectionnée, maintenant choisit y: ");
//                    int y = myScanner.nextInt();
//                    joueur.associerPionTuile(mapJeu,joueur.getListePions().get(nbPions),x,y,listeTuiles);
//                }
//            }*/ //Ca marche!!
//            /*for(Pion pion : mapJeu.keySet()){
//                System.out.println("Position :  "+"x = "+mapJeu.get(pion).getPosition().getX()+" y = "+mapJeu.get(pion).getPosition().getY());
//            }*/
//            //Loop for pour deployer les bateaux de chaque joueur
//            for (int nbBateaux=0; nbBateaux<2; nbBateaux++){
//                for (Joueur joueur : listeJoueurs){
//                    System.out.println(joueur.getPseudo()+":  Placer le bateau #"+(nbBateaux+1)+" en donnant les coordonnées x et y de la tuile désirée");
//                    System.out.println("Selectionne x ");
//                    int xX = myScanner.nextInt();
//                    System.out.println("selectionne y ");
//                    int yY = myScanner.nextInt();
//                    joueur.associerBateauMer(mapJeu,listeTuiles,joueur.getListeBateaux().get(nbBateaux),xX,yY);
//                }
//            }
//            /*for (Pion pion : mapJeu.keySet()){
//                System.out.println("Tuile "+mapJeu.get(pion).getFaceTerrain().toString());
//                System.out.println("Position :  "+"x = "+mapJeu.get(pion).getPosition().getX()+" y = "+mapJeu.get(pion).getPosition().getY());
//            }*/ //Ca marche
//            while (!estTermine){
//                for (Joueur joueur : listeJoueurs){
//                    System.out.println("Le tour de : "+joueur.getPseudo());
//                    //Joueur une tuile de la main du joueur s'il existe
//                    if (joueur.getMainJoueur()!=null){
//                        for (Tuile tuile : joueur.getMainJoueur()){
//                            if(tuile.getFaceAction().getContour().equals(Contour.ROUGE)){
//                                System.out.println("Vous avez une tuile rouge jouer la");
//                                //jeu.jouerTuile();break;
//                            }
//                        }
//                    }
//                    System.out.println("Deplacer les pions");
//                    //Deplacer les pions
//                    int nbDeplacementsDisponible=3;
//                    /*while (nbDeplacementsDisponible!=0){
//                    //Il faut verifier que le pion pris en arg est soit un bateau soit un pion expl
//                     */
//                        System.out.println("Voulez vous deplacer encore? vous aver le droit a "+nbDeplacementsDisponible+" Deplacements possibles");
//                        String choix = myScanner.next();
//                        /*if (choix=="n") {
//                            break;
//                        }*/
//                        //jeu.deplacerPion();
//                    //}
//                    //Enlever tuile
//                    System.out.println("Enlever tuile en donnant x et y");
//                    System.out.println("x = ");
//                    int xTuile = myScanner.nextInt();
//                    System.out.println("y = ");
//                    int yTuile = myScanner.nextInt();
//                    for (Tuile tuile : listeTuiles){
//                        if (tuile.getPosition().getX()==xTuile && tuile.getPosition().getY()==yTuile){
//                            joueur.enleverTuile(tuile,listeTuiles);
//                            if (tuile.getFaceAction().getContenu().equals(ElementAction.VOLCAN)){
//                                estTermine=true;
//                            }
//                            if (tuile.getFaceAction().getContour().equals(Contour.VERT)){
//                                System.out.println("Cette tuile est verte il faut la jouer immediatement : aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//                                joueur.enleverTuile(tuile, listeTuiles);
//                            }
//                            break;
//                        }
//                    }
//                    //Lancer le de et deplacer creature
//                    Creature creature = DeCreature.jouerDe().getCreature();
//                    System.out.println("Deplacer une creature en donnant x et y du creature");
//                    System.out.println("x = ");
//                    int xCreature = myScanner.nextInt();
//                    System.out.println("y = ");
//                    int yCreature = myScanner.nextInt();
//                    jeu.deplacerPion(creature,joueur.getCouleur(),xCreature,yCreature,mapJeu,joueur.getListeBateaux(),listeTuiles,jeu.initPositions());
//                }
//            }
//            //Jeu est termine
//            int max = 0;
//            for (Joueur joueur : listeJoueurs){
//                int resultat = joueur.compterPionSauves(joueur.getExplorateursSauves());
//                if (resultat>max){
//                    max=resultat;
//                }
//            }
//            System.out.println("Le Gagnant est : "+jeu.declarerGagnant(max, listeJoueurs).getPseudo());
//
//        }
//        catch (Exception e){
//            System.out.println("Error: "+e.toString());
//        }
    }
}