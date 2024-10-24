package models;

import models.pions.Bateau;
import models.pions.Creature;
import models.pions.Pion;
import models.pions.PionExplorateur;
import models.tuiles.*;
import view.CallbackChangementEtapeJeu;

import java.time.temporal.ValueRange;
import java.util.*;

import static models.pions.Creature.*;
import static models.tuiles.ElementAction.DAUPHIN;
import static models.tuiles.Terrain.*;

public class Jeu {

    public static final int MAX_NOMBRE_TERRAIN_PLAGE = 16;
    public static final int MAX_NOMBRE_TERRAIN_FORET = 16;
    public static final int MAX_NOMBRE_TERRAIN_MONTAGNE = 8;

    private final List<Position> positionsDeSauvetage =
            List.of(
                    new Position(-3, 4, new ArrayList<>()),
                    new Position(7, 5, new ArrayList<>()),
                    new Position(-7, -5, new ArrayList<>()),
                    new Position(3, -4, new ArrayList<>()));

    private List<Joueur> joueurs;

    private final CallbackChangementEtapeJeu callbackChangementEtapeJeu;
    private Map<Tuile, Position> tuilesEnJeu;

    private List<Position> positions;
    private Map<Position, List<Pion>> pionsEnJeu;
    private List<Pion> pionsEnReserve;
    private Joueur joueurEnCours;
    private EtapeJeu etapeEnCours;
    private int sousEtapeEnCours;
    private int maxSousEtapeEnCours;

    private DeCreature dernierResultatDe;

    private final static List<EtapeJeu> ETAPES_JEUX = List.of(EtapeJeu.JOUER_TUILE_EN_RESERVE, EtapeJeu.DEPLACER_PION, EtapeJeu.RETIRER_TUILE, EtapeJeu.LANCER_DE);

    public Jeu(List<Joueur> joueurs, CallbackChangementEtapeJeu callbackChangementEtapeJeu) {
        this.joueurs = joueurs;
        this.callbackChangementEtapeJeu = callbackChangementEtapeJeu;
        this.positions = new ArrayList<>();
        this.pionsEnReserve = new ArrayList<>();
        pionsEnJeu = new HashMap<>();
        etapeEnCours = ETAPES_JEUX.get(0);
        // Initialiser le reste des elements pour ne pas charger le constructeur
        initialiserJeu();
    }

    public List<Position> getPositions() {
        return positions;
    }

    public Map<Position, List<Pion>> getPionsEnJeu() {
        return pionsEnJeu;
    }

    public List<Pion> pionsDunePosition(Position position) {
        for (Map.Entry<Position, List<Pion>> positionListEntry : pionsEnJeu.entrySet()) {
            if (positionListEntry.getKey().equals(position)) return positionListEntry.getValue();
        }
        return new ArrayList<>();
    }

    public Map<Tuile, Position> getTuilesEnJeu() {
        return tuilesEnJeu;
    }

    public List<Pion> getPionsEnReserve() {
        return pionsEnReserve;
    }

    public Joueur getJoueurEnCours() {
        return joueurEnCours;
    }

    public EtapeJeu getEtapeEnCours() {
        return etapeEnCours;
    }

    public DeCreature getDernierResultatDe() {
        return dernierResultatDe;
    }

    /**
     *
     */
    public void initialiserJeu() {
        // Initialiser tous les positions possibles dans la map
        Map<Integer, ValueRange> dispositionsTousPositions = new HashMap<>();
        dispositionsTousPositions.put(6, ValueRange.of(0, 6));
        dispositionsTousPositions.put(5, ValueRange.of(-2, 7));
        dispositionsTousPositions.put(4, ValueRange.of(-3, 7));
        dispositionsTousPositions.put(3, ValueRange.of(-3, 6));
        dispositionsTousPositions.put(2, ValueRange.of(-4, 6));
        dispositionsTousPositions.put(1, ValueRange.of(-5, 6));
        dispositionsTousPositions.put(0, ValueRange.of(-5, 5));
        dispositionsTousPositions.put(-1, ValueRange.of(-6, 5));
        dispositionsTousPositions.put(-2, ValueRange.of(-6, 4));
        dispositionsTousPositions.put(-3, ValueRange.of(-6, 3));
        dispositionsTousPositions.put(-4, ValueRange.of(-7, 3));
        dispositionsTousPositions.put(-5, ValueRange.of(-7, 2));
        dispositionsTousPositions.put(-6, ValueRange.of(-6, 0));
        for (Map.Entry<Integer, ValueRange> entree : dispositionsTousPositions.entrySet()) {
            for (int x = (int) entree.getValue().getMinimum(); x <= (int) entree.getValue().getMaximum(); x++) {
                positions.add(new Position(x, entree.getKey(), new ArrayList<>()));
            }
        }
        calculerAdjacentsPositions();
        // Initialisation des tuiles de base
        tuilesEnJeu = tuilesPositions();

        insererPionsSerpents();
        initialiserPionsReserve();
        // TODO: 21/05/2022 a supprimer ou commenter après
        mockInitialisationJeu();

        joueurEnCours = joueurs.get(new Random().nextInt(joueurs.size()));
    }

    private void mockInitialisationJeu() {
        List<Map.Entry<Tuile, Position>> listTuiles = new ArrayList<>(tuilesEnJeu.entrySet());
        for (int i = 0; i < listTuiles.size(); i++) {
            Pion pion = joueurs.get(i / 10).getExplorateursNonJoues().get(i % 10);
            pionsEnJeu.put(listTuiles.get(i).getValue(), new ArrayList<>(List.of(pion)));
        }

        pionsEnJeu.put(positions.get(positions.indexOf(new Position(2, -2, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(1, -3, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-5, -1, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-1, -4, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-2, -4, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-3, -4, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-4, -3, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-2, 2, new ArrayList<>()))), new ArrayList<>(List.of(new Bateau())));
    }

    private void calculerAdjacentsPositions() {
        for (Position position : positions) {
            List<Position> adjacents = new ArrayList<>();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0 || i == 1 && j == -1 || i == -1 && j == 1) {
                        continue;
                    }
                    Position possibleAdj = new Position(position.getX() + i, position.getY() + j, new ArrayList<>());
                    if (positions.contains(possibleAdj)) {
                        adjacents.add(possibleAdj);
                    }
                }
            }
            position.setListeAdjascence(adjacents);
        }
    }

    private void initialiserPionsReserve() {
        // Contours verts
        pionsEnReserve.addAll(Collections.nCopies(6, REQUIN));
        pionsEnReserve.addAll(Collections.nCopies(5, BALEINE));
        pionsEnReserve.addAll(Collections.nCopies(4, new Bateau()));
    }

    //Initialiser les faces des tuiles plage
    private List<FaceAction> facesPlage() {
        List<FaceAction> listeFaces = new ArrayList<>();
        // Contours verts
        listeFaces.addAll(Collections.nCopies(3, new FaceAction(Contour.VERT, ElementAction.BALEINE)));
        listeFaces.addAll(Collections.nCopies(3, new FaceAction(Contour.VERT, ElementAction.REQUIN)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.VERT, ElementAction.BATEAU)));
        // Contours rouges
        listeFaces.addAll(Collections.nCopies(2, new FaceAction(Contour.ROUGE, ElementAction.BATEAU)));
        listeFaces.addAll(Collections.nCopies(3, new FaceAction(Contour.ROUGE, DAUPHIN)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE, ElementAction.SERPENT)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE, ElementAction.REQUIN)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE, ElementAction.BALEINE)));
        // Contours rouges barrés
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE_BARRE, ElementAction.REQUIN)));
        return listeFaces;
    }

    //Initialiser les faces des tuiles foret
    private List<FaceAction> facesForet() {
        List<FaceAction> listeFaces = new ArrayList<>();
        // Contours verts
        listeFaces.addAll(Collections.nCopies(2, new FaceAction(Contour.VERT, ElementAction.BALEINE)));
        listeFaces.addAll(Collections.nCopies(2, new FaceAction(Contour.VERT, ElementAction.REQUIN)));
        listeFaces.addAll(Collections.nCopies(3, new FaceAction(Contour.VERT, ElementAction.BATEAU)));
        listeFaces.addAll(Collections.nCopies(2, new FaceAction(Contour.VERT, ElementAction.TOURBILLON)));
        // Contours rouges
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE, DAUPHIN)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE, ElementAction.SERPENT)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE, ElementAction.REQUIN)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE, ElementAction.BALEINE)));
        // Contours rouges barrés
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE_BARRE, ElementAction.REQUIN)));
        listeFaces.addAll(Collections.nCopies(2, new FaceAction(Contour.ROUGE_BARRE, ElementAction.BALEINE)));
        return listeFaces;
    }

    // Initialiser les faces des tuiles montagne
    private List<FaceAction> facesMontagne() {
        List<FaceAction> listeFaces = new ArrayList<>();
        // Contours verts
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.VERT, ElementAction.REQUIN)));
        listeFaces.addAll(Collections.nCopies(4, new FaceAction(Contour.VERT, ElementAction.TOURBILLON)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.VERT, ElementAction.VOLCAN)));
        // Contours rouges barrés
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE_BARRE, ElementAction.REQUIN)));
        listeFaces.addAll(Collections.nCopies(1, new FaceAction(Contour.ROUGE_BARRE, ElementAction.BALEINE)));
        return listeFaces;
    }

    //INITIALISER LA POSITION DE CHAQUE TUILE
    private Map<Tuile, Position> tuilesPositions() {
        int nombrePlagesEnCours = 0;
        int nombreForetsEnCours = 0;
        int nombreMontagneEnCours = 0;
        List<FaceAction> listeFacesPlage = facesPlage();
        List<FaceAction> listeFacesForet = facesForet();
        List<FaceAction> listeFacesMontagne = facesMontagne();
        Map<Tuile, Position> listeTuiles = new HashMap<>();
        Map<Integer, ValueRange> dispositionsLignes = new HashMap<>();
        dispositionsLignes.put(3, ValueRange.of(0, 3));
        dispositionsLignes.put(2, ValueRange.of(-1, 3));
        dispositionsLignes.put(1, ValueRange.of(-3, 4));
        dispositionsLignes.put(0, ValueRange.of(-3, 3));
        dispositionsLignes.put(-1, ValueRange.of(-4, 3));
        dispositionsLignes.put(-2, ValueRange.of(-3, 1));
        dispositionsLignes.put(-3, ValueRange.of(-3, 0));
        for (Map.Entry<Integer, ValueRange> entree : dispositionsLignes.entrySet()) {
            for (int x = (int) entree.getValue().getMinimum(); x <= (int) entree.getValue().getMaximum(); x++) {
                if (x == 0 && entree.getKey() == 0) {
                    continue;
                }
                Terrain terrain = null;
                FaceAction faceAction = null;
                while (terrain == null) {
                    Terrain terrainRetourne = Terrain.retournerAleatoire();
                    if (terrainRetourne == PLAGE && nombrePlagesEnCours < MAX_NOMBRE_TERRAIN_PLAGE) {
                        terrain = PLAGE;
                        faceAction = listeFacesPlage.get(nombrePlagesEnCours);
                        nombrePlagesEnCours += 1;
                    } else if (terrainRetourne == FORET && nombreForetsEnCours < MAX_NOMBRE_TERRAIN_FORET) {
                        terrain = FORET;
                        faceAction = listeFacesForet.get(nombreForetsEnCours);
                        nombreForetsEnCours += 1;
                    } else if (terrainRetourne == MONTAGNE && nombreMontagneEnCours < MAX_NOMBRE_TERRAIN_MONTAGNE) {
                        terrain = MONTAGNE;
                        faceAction = listeFacesMontagne.get(nombreMontagneEnCours);
                        nombreMontagneEnCours += 1;
                    }
                }
                Tuile tuile = new Tuile(terrain, faceAction);
                listeTuiles.put(tuile, new Position(x, entree.getKey(), new ArrayList()));
            }
        }
        return listeTuiles;
    }

    private void insererPionsSerpents() {
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(0, 0, new ArrayList<>()))), new ArrayList<>(List.of(Creature.SERPENT)));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-2, 5, new ArrayList<>()))), new ArrayList<>(List.of(Creature.SERPENT)));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(7, 4, new ArrayList<>()))), new ArrayList<>(List.of(Creature.SERPENT)));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(-7, -4, new ArrayList<>()))), new ArrayList<>(List.of(Creature.SERPENT)));
        pionsEnJeu.put(positions.get(positions.indexOf(new Position(2, -5, new ArrayList<>()))), new ArrayList<>(List.of(Creature.SERPENT)));
    }

    /**
     * Placer un pion non joué dans les positions en cours
     *
     * @param pion     pion à placer
     * @param position position à mettre
     * @return validité de placement selon la liste des réserves
     */
    public boolean deplacerPionNonJoue(PionExplorateur pion, Position position) {
        pionsEnJeu.put(position, new ArrayList<>(List.of(pion)));
        joueurEnCours.retirerPionNonJoue(pion);
        selectionnerProchainJoueur();
        for (Joueur joueur : joueurs) {
            if (joueur.restePionNonJoue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Placer bateau en réserve au début du jeu dans une position
     *
     * @param bateau   bateau a placer
     * @param position position a placer le bateau
     * @return valider du placement du bateau selon la liste des réserves
     */
    public boolean placerBateauNonJoue(Bateau bateau, Position position) {
        pionsEnJeu.put(position, new ArrayList<>(List.of(bateau)));
        joueurEnCours.retirerBateauNonJoue(bateau);
        selectionnerProchainJoueur();
        for (Joueur joueur : joueurs) {
            if (joueur.resteBateauNonJoue()) {
                return true;
            }
        }
        return false;
    }

    public boolean estPositionAdjacenteAUneTuile(Position position) {
        for (Position adjacente : position.getListeAdjascence()) {
            if (tuilesEnJeu.containsValue(adjacente)) {
                return true;
            }
        }
        return false;
    }

    public boolean estPositionDansMer(Position position) {
        return !tuilesEnJeu.containsValue(position);
    }

    /**
     * Verifier s'il existe une creature de type passé dans les pions en jeux
     *
     * @param creature creature a checker
     * @return l'existence de créature
     */
    public boolean existeCreatureDeType(Creature creature) {
        for (Map.Entry<Position, List<Pion>> entreeListe : pionsEnJeu.entrySet()) {
            for (Pion pion : entreeListe.getValue()) {
                if (pion == creature) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Commencer premier tour après distributions des pions explorateurs et bateau sur la carte
     */
    public void commencerJouer() {
        etapeEnCours = ETAPES_JEUX.get(1);
        sousEtapeEnCours = 0;
        maxSousEtapeEnCours = etapeEnCours.getNombreEtapesMax();
        callbackChangementEtapeJeu.onChangementEtape(etapeEnCours);
    }

    /**
     * Déplacement d'un pion d'une position à une autre, dans le cas ou l'ancienne position est nulle, càd on va ajouter
     * un nouveau pion dans la liste des pions existants dans la nouvellePosition
     *
     * @param pion             pion à déplacer
     * @param anciennePosition position d'origine, si nulle, rajout directement du pion
     * @param nouvellePosition nouvelle position
     * @param dansBateau       s'il faut débarquer ou embarquer un pion dans un bateau, si faux, càd à ne pas considérer le
     *                         déplacement dans le batea
     */
    public void deplacerPion(Pion pion, Position anciennePosition, Position nouvellePosition, boolean dansBateau) {
        // Cas ou meme position et faut déplacer dans un bateau
        if (anciennePosition != null && anciennePosition.equals(nouvellePosition) && dansBateau) {
            Bateau bateau = null;
            List<Pion> pions = pionsDunePosition(nouvellePosition);
            for (Pion pionDeListe : pions) {
                if (pionDeListe instanceof Bateau) {
                    bateau = (Bateau) pionDeListe;
                    break;
                }
            }
            if (bateau != null && pion instanceof PionExplorateur) {
                if (bateau.getPionsOnBateau().contains(pion)) {
                    bateau.debarquerPion((PionExplorateur) pion);
                    pions.add(pion);
                } else if (bateau.existeEncoreDuPlace()) {
                    pions.remove(pion);
                    bateau.embarquerPion((PionExplorateur) pion);
                }
            }
            return;
        }
        boolean nouvellePositionTrouvee = false;
        // Deplacement entre deux positions
        for (Map.Entry<Position, List<Pion>> positionListEntry : pionsEnJeu.entrySet()) {
            if (anciennePosition != null && positionListEntry.getKey().equals(anciennePosition)) {
                positionListEntry.getValue().remove(pion);
            }
            if (positionListEntry.getKey().equals(nouvellePosition)) {
                nouvellePositionTrouvee = true;
                positionListEntry.getValue().add(pion);
            }
        }
        if (!nouvellePositionTrouvee) {
            pionsEnJeu.put(nouvellePosition, new ArrayList<>(List.of(pion)));
        }
        vérifierActionsSurPions(nouvellePosition);
    }

    /**
     * Vérifier actions sur les pions d'une position, pour voir qui reste dans la liste des pions, et par ordre
     * Baleine détruit Bateau
     * Requin mange PionExplorateur
     * Serpent détruit Bateau et mange PionExplorateur
     *
     * @param position position des pions
     */
    private void vérifierActionsSurPions(Position position) {
        List<Pion> pionsDePosition = pionsDunePosition(position);
        List<Pion> listeASupprimer = new ArrayList<>();
        if (pionsDePosition.contains(SERPENT)) {
            for (Pion pion : pionsDePosition) {
                if (pion instanceof PionExplorateur) {
                    listeASupprimer.add(pion);
                } else if (pion instanceof Bateau && !((Bateau) pion).getPionsExplorateur().isEmpty()) {
                    // Le serpent ne détruit que les bateaux dont des explorateurs embarqués
                    listeASupprimer.add(pion);
                }
            }
            for (Pion pion : listeASupprimer) {
                pionsDePosition.remove(pion);
            }
            // Faire un break, car le serpent casse tout
            return;
        }
        // Cas baleine
        List<Pion> pionsAAjouter = new ArrayList<>();
        if (pionsDePosition.contains(BALEINE)) {
            for (Pion pion : pionsDePosition) {
                if (pion instanceof Bateau && !((Bateau) pion).getPionsExplorateur().isEmpty()) {
                    // Débarquer tous les pions explorateurs avant d'enlever le bateau, sinon baleine n'affecte que les bateaux non vides
                    pionsAAjouter.addAll(((Bateau) pion).getPionsExplorateur());
                    listeASupprimer.add(pion);
                }
            }
        }
        pionsDePosition.addAll(pionsAAjouter);
        // Cas requin
        if (pionsDePosition.contains(REQUIN)) {
            for (Pion pion : pionsDePosition) {
                if (pion instanceof PionExplorateur) {
                    listeASupprimer.add(pion);
                }
            }
        }
        for (Pion pion : listeASupprimer) {
            pionsDePosition.remove(pion);
        }
    }

    /**
     * Calculer la prochaine sous étape,
     *
     * @param sauterEtape préciser s'il faut sauter l'étape en cours ou pas
     */
    public void prochaineSousEtape(boolean sauterEtape) {
        sousEtapeEnCours++;
        boolean estEtapeAChanger = sauterEtape || sousEtapeEnCours == maxSousEtapeEnCours;
        if (estEtapeAChanger) {
            etapeEnCours = ETAPES_JEUX.get((ETAPES_JEUX.indexOf(etapeEnCours) + 1) % ETAPES_JEUX.size());
            sousEtapeEnCours = 0;
            if (etapeEnCours == EtapeJeu.LANCER_DE) {
                dernierResultatDe = DeCreature.jouerDe();
                maxSousEtapeEnCours = dernierResultatDe.getCreature().getNombreDeplacementMax();
            } else {
                maxSousEtapeEnCours = etapeEnCours.getNombreEtapesMax();
            }
        }

        // Quand le tour du joueur en cours est terminé, sélectionner le prochain
        if (etapeEnCours == ETAPES_JEUX.get(0)) {
            selectionnerProchainJoueur();
        }
        // Informer le callback du changement
        if (estEtapeAChanger) {
            callbackChangementEtapeJeu.onChangementEtape(etapeEnCours);
        }
    }

    public void recalculerSousEtapeAction(FaceAction action) {
        switch (action.getContenu()) {
            case DAUPHIN, VENT -> maxSousEtapeEnCours = 3;
            case REQUIN, BALEINE, SERPENT -> maxSousEtapeEnCours = 1;
        }
    }

    /**
     * Décider quel type de terrain à choisir par le joueur en cours, en ordre plage, puis forêt puis montagne
     *
     * @return résultat de type de Terrain qu'il faut choisir
     */
    public Terrain prochainTerrainAChoisir() {
        for (Terrain value : Terrain.values()) {
            for (Map.Entry<Tuile, Position> tuilePositionEntry : tuilesEnJeu.entrySet()) {
                if (tuilePositionEntry.getKey().getFaceTerrain() == value) {
                    return value;
                }
            }
        }
        return MONTAGNE;
    }

    /**
     * Jouer action tourbillon dans la position passée en paramètres
     *
     * @param position
     * @return Liste des positions affectées par le tourbillon
     */
    public List<Position> jouerTourbillon(Position position) {
        List<Position> posAffectees = new ArrayList<>();
        pionsDunePosition(position).clear();
        posAffectees.add(position);
        for (Position posAdjacente : position.getListeAdjascence()) {
            if (estCaseMer(posAdjacente)) {
                pionsDunePosition(posAdjacente).clear();
                posAffectees.add(posAdjacente);
            }
        }
        return posAffectees;
    }

    public boolean estCaseMer(Position position) {
        return !tuilesEnJeu.values().contains(position);
    }

    public void ajouterFaceRéserveJoueurEnCours(FaceAction faceAction) {
        joueurEnCours.ajouterFaceReserve(faceAction);
    }

    public void enleverFaceRéserveJoueurEnCours(FaceAction faceAction) {
        joueurEnCours.supprimerFaceReserve(faceAction);
    }

    /**
     * Vérifier si la position passée en paramètre fait partie des positions qu'on peut sauver
     *
     * @param position position à tester
     * @return validité de sauvetage
     */
    public boolean estPositionDeSauvetage(Position position) {
        return positionsDeSauvetage.contains(position);
    }

    /**
     * Retourner un joueur qui peut défendre selon la position passée
     * @param position position passée
     * @param creature creature à tester avec
     * @return
     */
    public Joueur joueurQuiPeutDefendre(Position position, Creature creature) {
        for (Pion pion : pionsDunePosition(position)) {
            if (pion instanceof Bateau && creature == BALEINE) {
                for (PionExplorateur pionExplorateur : ((Bateau) pion).getPionsExplorateur()) {
                    Joueur joueurPion = joueurDuCouleur(pionExplorateur.getCouleur());
                    for (FaceAction faceAction : joueurPion.getFacesActionEnReserve()) {
                        if (faceAction.getContenu() == ElementAction.BALEINE) {
                            return joueurPion;
                        }
                    }
                }

            } else if (pion instanceof PionExplorateur && creature == REQUIN) {
                Joueur joueurPion = joueurDuCouleur(((PionExplorateur) pion).getCouleur());
                for (FaceAction faceAction : joueurPion.getFacesActionEnReserve()) {
                    if (faceAction.getContenu() == ElementAction.REQUIN) {
                        return joueurPion;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sauver pion pour un joueur en cours
     *
     * @param position position depuis laquelle le pion est sauvé
     * @param pion     pion à sauver
     */
    public void sauverPion(Position position, Pion pion) {
        if (pion instanceof Bateau) {
            for (PionExplorateur pionExplorateur : ((Bateau) pion).getPionsExplorateur()) {
                joueurDuCouleur(pionExplorateur.getCouleur()).sauverPion(pionExplorateur);
            }
        } else if (pion instanceof PionExplorateur) {
            joueurDuCouleur(((PionExplorateur) pion).getCouleur()).sauverPion((PionExplorateur) pion);
        }
        pionsDunePosition(position).remove(pion);
    }

    /**
     * Retourner joueur du couleur passée
     *
     * @param couleur
     * @return Joueur portant la couleur
     */
    private Joueur joueurDuCouleur(Couleur couleur) {
        for (Joueur joueur : joueurs) {
            if (joueur.getCouleurPion() == couleur) {
                return joueur;
            }
        }
        return null;
    }

    public void jouerEtape() {
        // TODO implement here
    }

    /**
     * @param tuile
     */
    public void tournerFaceTuile(Tuile tuile) {
        tuilesEnJeu.remove(tuile);
    }

    /**
     * Sélectionner prochain joueur en cours
     */
    public void selectionnerProchainJoueur() {
        joueurEnCours = joueurs.get((joueurs.indexOf(joueurEnCours) + 1) % joueurs.size());
    }

    /**
     *
     */
    public void afficherAide() {
        // TODO implement here
    }

    /**
     *
     */
    public boolean terminerJeu() {
        // TODO implement here
        return true;
    }

    /**
     * Calculer max score des joueurs et retourner le joueur Gagnant
     *
     * @return
     */
    public Joueur declarerGagnant() {
        int max = 0;
        Joueur joueurGagnant = joueurs.get(0);
        for (Joueur joueur : joueurs) {
            if (joueur.compterPionSauves() > max) {
                max = joueur.compterPionSauves();
                joueurGagnant = joueur;
            }
        }
        // TODO: 29/05/2022 au cas ou y a deux de même score
        return joueurGagnant;
    }

    //MANAR
    /*-----------------------------------------------------------------------------*/
    //Deplacer un pion
    public void deplacerPion(Pion pion, Couleur col, int x, int y, Map<Pion, Tuile> mapPionTuile, List<Bateau> listeBateau, List<Tuile> listeTuile, List<Position> listePositions) {
        boolean deplacement = false;
        try {
            if (pion.getClass().equals(PionExplorateur.class)) {
                deplacement = deplacerPionExplorateur(listePositions, (PionExplorateur) pion, x, y, mapPionTuile, listeTuile, listeBateau);
            } else if (pion.getClass().equals(Creature.class)) {
//                deplacement = deplacerPionSurMer(pion, x, y, mapPionTuile, listeTuile);

            } else if (pion.getClass().equals(Bateau.class)) {
                deplacement = deplacerBateau(pion, col, x, y, mapPionTuile, listePositions, listeTuile);
            }

            if (!deplacement) {
                System.out.println("Deplacement non effectué ");
                System.out.println("Veuillez choisir : 1-une autre position     2-un autre pion à deplacer");
                Scanner myScanner = new Scanner(System.in);
                int nombre = myScanner.nextInt();
                if (nombre == 1) {
                    System.out.println("x = ");
                    int newX = myScanner.nextInt();
                    System.out.println("y = ");
                    int newY = myScanner.nextInt();
                    deplacerPion(pion, col, newX, newY, mapPionTuile, listeBateau, listeTuile, listePositions);
                } else if (nombre == 2) {
                    System.out.println("Choisir un autre pion ");
                }
            }

        } catch (Exception e) {
            e.toString();
        }
    }

    /**
     *
     */

    //Récuperer les clées d'une tuile donnée dans la mapPionTuile
    private static List<Pion> getKeys(Map<Pion, Tuile> mapPionTuile, Tuile value) {
        try {
            //System.out.println("88888888888888888888");
            List<Pion> keys = new ArrayList<>();
            //System.out.println(mapPionTuile.keySet());

            if (mapPionTuile.containsValue(value)) {
                for (Map.Entry<Pion, Tuile> entry : mapPionTuile.entrySet()) {
                    System.out.println("ppppp" + entry.getKey());
                    System.out.println("ppppp" + entry.getValue());

                    if (Objects.equals(entry.getValue(), value)) {
                        keys.add(entry.getKey());
                        //return (entry.getKey());

                    }

                }
                //System.out.println(keys);
                return keys;

            }
            return null;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public boolean deplacerBateau(Pion pion, Couleur couleur, int x, int y, Map<Pion, Tuile> mapPionTuile, List<Position> listePositions, List<Tuile> listeTuiles) {
        try {
            int i = 0;
            int j = 0;
            boolean trouve = false;
            Tuile tuileCourante = mapPionTuile.get(pion);
            Tuile tuileDest;
            Position pos = new Position(x, y, null);
            List<Couleur> listeDesControlleurs;
            List<PionExplorateur> listePionsDansBateau;
            listeDesControlleurs = ((Bateau) pion).getControlleurs();
            listePionsDansBateau = ((Bateau) pion).getPionsOnBateau();
//            tuileDest = getTuileByPosition(pos, listeTuiles);
            //Vérifier la tuile destination est une tuile MER
//            if (tuileDest.getFaceTerrain() == MER) {
//                if (sontAdjacente(tuileCourante, tuileDest)) {
//                    if (((Bateau) pion).getPionsOnBateau().size() == 0) {
//                        mapPionTuile.replace(pion, tuileCourante, tuileDest);
//                        return true;
//                    } else {
//                        while (!trouve && j < listeDesControlleurs.size()) {
//                            if (listeDesControlleurs.get(i) == couleur) {
//                                for (Pion pionC : listePionsDansBateau) {
//                                    mapPionTuile.replace(pionC, tuileCourante, tuileDest);
//                                }
//                                mapPionTuile.replace(pion, tuileCourante, tuileDest);
//                                trouve = true;
//                                return true;
//                            } else j++;
//                        }
//                        if (!trouve) {
//                            System.out.println("Le pion n'est pas le controlleur vous ne peut pas deplacer le bateau ");
//                            return false;
//                        }
//
//                    }
//                } else {
//                    System.out.println("La destination n'est pas adjacente au courante");
//                    return false;
//                }
//            } else {
//                System.out.println("On ne peut pas deplacer le bateau que sur une tuile mer");
//                return false;
//            }
            //}
            //i++;
            //}

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }

    // Retourner une tuile à partir de sa poisition
//    public Tuile getTuileByPosition(Position pos, List<Tuile> listeTuile) {
//
//        try {
//            int trouve = 0;
//            int i = 0;
//            while (i < listeTuile.size()) {
//                int cordX = listeTuile.get(i).getPosition().getX();
//                int cordY = listeTuile.get(i).getPosition().getY();
//                if (cordX == pos.getX() && cordY == pos.getY()) {
//                    trouve = 1;
//                    return listeTuile.get(i);
//                }
//
//                i++;
//            }
//            if (trouve == 0) {
//                System.out.println("tuile correspond pour cette postion non trouvee");
//                return null;
//            }
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        return null;
//    }

    //Deplacer un PionExplorateur ou Creature  d'une Tuile Mer à une autre adjacente
//    public Boolean deplacerPionSurMer(Pion pion, int x, int y, Map<Pion, Tuile> mapPionTuile, List<Tuile> listeTuile) {
//        //Boolean trouvePos = false;
//        Boolean deplacement = false;
//        Integer i = 0;
//        Tuile tuileDest;
//        Tuile tuileCourante;
//        try {
//            Position pos = new Position(x, y, null);
//            tuileCourante = mapPionTuile.get(pion);
//            tuileDest = getTuileByPosition(pos, listeTuile);
////            if (tuileDest.getFaceTerrain() == MER) {
////                if (sontAdjacente(tuileCourante, tuileDest)) {
////                    mapPionTuile.replace(pion, tuileCourante, tuileDest);
////                    return true;
////                } else {
////                    System.out.println("La destination pas adjacente au courant");
////                    return false;
////                }
////            } else {
////                System.out.println("La destination n'est pas une tuile mer");
////                return false;
////            }
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        return deplacement;
//    }

    //Deplacer un pion nageur
    public Boolean deplacerPionNageur(PionExplorateur pion, int x, int y, List<Tuile> listetuile, Map<Pion, Tuile> mapPionTuile, List<Position> listePositions, List<Bateau> listeBateau) {
        try {
            System.out.println("Methode pion nageur");
            Position pos = new Position(x, y, null);
            Boolean deplacement = false;
            Boolean estTuileMer = false;
//            Position posCourante = mapPionTuile.get(pion).getPosition();
//            Tuile tuileCourante = mapPionTuile.get(pion);
//            Tuile tuileDest = getTuileByPosition(pos, listetuile);
//            if (tuileDest.getFaceTerrain() == MER) {
//                //le pion est sur la tuile mer que le bateau : il s'embarque sur le bateau
//                if (posCourante.getX() == x && posCourante.getY() == y) {
//                    List<Pion> keys = getKeys(mapPionTuile, tuileCourante);
//                    for (Pion key : keys) {
//                        System.out.println(key.toString());
//                        if (key.getClass().equals(Bateau.class)) {
//                            Integer size = ((Bateau) key).getPionsOnBateau().size();
//                            if (size < 3) {
//                                ((Bateau) key).embarquerPion(pion);
//                                pion.setEstNajeur(false);
//                                pion.setEstSurBateau(true);
//                                return true;
//                            } else {
//                                System.out.println("On ne peut pas ajouter plus que 3 pions");
//                                return false;
//                            }
//                        }
//                    }
//                } else {
//                    //appel fonction deplacer pion sur le mer : il sera nager vers l'autre tuile Mer adjacente
//                    deplacement = deplacerPionSurMer(pion, x, y, mapPionTuile, listetuile);
//                    return deplacement;
//                }
//
//            } else {
//                return false;
//            }
            return deplacement;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    //Le pion est sur le bateau :soit  il saute dans la mer ou il deplace vers un autre bateau situé à une tuile mer adjacente
    public boolean sauterPionduBateau(PionExplorateur pion, int x, int y, Map<Pion, Tuile> pionTuileMap, List<Position> listPositions, List<Tuile> listetuile, List<Bateau> listeBateau) {
//        try {
//            Bateau batCourant = null;
//            Tuile tuileDestination;
//            List<Pion> keys2;
//            Tuile tuileCourante = pionTuileMap.get(pion);
//            Position posBateau = pionTuileMap.get(pion).getPosition();
////            tuileDestination = getTuileByPosition(new Position(x, y, null), listetuile);
//            List<Pion> keys = getKeys(pionTuileMap, tuileCourante);
//            System.out.println(keys);
//            for (Pion pionC : keys) {
//                if (pionC.getClass().equals(Bateau.class)) {
//                    batCourant = (Bateau) pionC;
//                }
//            }
//            if (posBateau.getX() == x && posBateau.getY() == y) {
//                batCourant.debarquerPion(pion);
//                pion.setEstNajeur(true);
//                pion.setEstSurBateau(false);
//                return true;
//            } else {
////                if (sontAdjacente(tuileCourante, tuileDestination)) {
////                    keys2 = getKeys(pionTuileMap, tuileDestination);
////                    if (keys2 != null) {
////                        for (Pion pionB : keys2) {
////                            if (pionB.getClass().equals(Bateau.class)) {
////                                if (((Bateau) pionB).getPionsOnBateau().size() < 3) {
////                                    batCourant.debarquerPion(pion);
////                                    ((Bateau) pionB).embarquerPion(pion);
////                                    pionTuileMap.replace(pion, tuileCourante, tuileDestination);
////                                    return true;
////                                } else {
////                                    System.out.println("Le bateau peut contenir au max 3 pions");
////                                    return false;
////                                }
////                            }
////                        }
////                    }
////
////                } else {
////                    System.out.println(("Les deux tuiles ne sont pas adjacentes"));
////                    return false;
////                }
//            }
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
        return false;
    }

    ;

    //deplacer un Pion Explorateur
    public Boolean deplacerPionExplorateur(List<Position> listePositions, PionExplorateur pion, int x, int y, Map<Pion, Tuile> mapPionTuile, List<Tuile> listetuile, List<Bateau> listeBateau) {
        try {
            Position positionDest = new Position(x, y, null);
            Boolean deplacement = false;
            Tuile tuileDest;
            Tuile tuileCourante = mapPionTuile.get(pion);
//            tuileDest = getTuileByPosition(positionDest, listetuile);
            //Le pion est najeur : appel du méthode deplacer pion nageur
//            if (pion.getEstNajeur() && tuileDest.getFaceTerrain() == MER) {
//                deplacement = deplacerPionNageur(pion, x, y, listetuile, mapPionTuile, listePositions, listeBateau);
//                return deplacement;
//            }
            //Le pion est sur bateau :appel du méthode sauterPionBateau
//            else if ((pion.getEstSurBateau()) && tuileDest.getFaceTerrain() == MER) {
//                deplacement = sauterPionduBateau(pion, x, y, mapPionTuile, listePositions, listetuile, listeBateau);
//                return deplacement;
//            } else {
//                //deplacemant sur un tuile terrain
//                if (tuileDest.getFaceTerrain() != MER) {
//                    //deplacer le pion d'une tuile terrain à  une tuile terrain adjacente
//                    if (tuileCourante.getFaceTerrain() != null && tuileDest.getFaceTerrain() != null) {
//                        if (sontAdjacente(tuileCourante, tuileDest)) {
//                            tuileDest = getTuileByPosition(positionDest, listetuile);
//                            mapPionTuile.replace(pion, tuileCourante, tuileDest);
//                            deplacement = true;
//                        } else {
//                            //Les deux cases sont pas adjacent
//                            deplacement = false;
//                            System.out.println("Les deux tuiles sont pas adjecent");
//                        }
//                    }
//                }
//                //deplacer le pion  tuile terrain à  une tuile mer adjacente
//                else if (tuileDest.getFaceTerrain() == MER) {
//                    Boolean estDeplacerSurBateau = deplacerPionSurBateau(pion, x, y, listePositions, mapPionTuile);
//                    if (estDeplacerSurBateau) {
//                        return true;
//                    } else {
//                        Boolean depl = deplacerPionSurMer(pion, x, y, mapPionTuile, listetuile);
//                        if (depl) {
//                            pion.setEstNajeur(true);
//                            pion.setEstSurBateau(false);
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    }
//                }

//            }
            return false;

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    //Deplacer un pion Explorateur sur un bateau
    public Boolean deplacerPionSurBateau(Pion pion, int x, int y, List<Position> listePositions, Map<Pion, Tuile> mapPionTuile) {
        boolean trouvePos = false;
        int i = 0;
        Tuile tuileDest;
        Tuile tuileCourante = mapPionTuile.get(pion);

//        while (!trouvePos && i < listePositions.size()) {
//            int posX = listePositions.get(i).getX();
//            int posY = listePositions.get(i).getY();
//            if (posX == x && posY == y) {
//                for (Pion pionBateau : mapPionTuile.keySet()) {
//                    if (pionBateau.getClass().equals(Bateau.class)) {
//                        Position pos = mapPionTuile.get(pionBateau).getPosition();
//                        if (pos.getX() == x && pos.getY() == y) {
//                            trouvePos = true;
//                            tuileDest = mapPionTuile.get(pionBateau);
//                            if (sontAdjacente(tuileDest, tuileCourante)) {
//                                int size = ((Bateau) pionBateau).getPionsOnBateau().size();
//                                if (size < 3) {
//                                    ((Bateau) pionBateau).embarquerPion(((PionExplorateur) pion));
//                                    mapPionTuile.replace(pion, tuileCourante, tuileDest);
//                                    ((PionExplorateur) pion).setEstSurBateau(true);
//                                    ((PionExplorateur) pion).setEstNajeur(false);
//                                    return true;
//
//                                } else {
//                                    return false;
//                                }
//                            }
//                        }
//                    }
//
//                }
//            }
//            i++;
//        }
        return false;
    }
}