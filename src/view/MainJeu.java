package view;

import models.*;
import models.pions.Bateau;
import models.pions.Creature;
import models.pions.Pion;
import models.pions.PionExplorateur;
import models.tuiles.Contour;
import models.tuiles.ElementAction;
import models.tuiles.FaceAction;
import models.tuiles.Tuile;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static models.pions.Creature.*;
import static view.ConstanteImage.REGLES;
import static view.TheIsland.*;

public class MainJeu extends JPanel {

    enum EtapeJeu {
        DISTRIBUTION_PIONS, DISTRIBUTION_BATEAUX, JEU_EN_COURS
    }

    private TheIsland islandPanel;
    private CadranReserve cadrant;
    private EtapeJeu etapeJeu;
    private PionExplorateur pionExplorateurSelectionne;
    private Pion pionADeplacer;
    private Position dernierePosition;
    private ElementAction derniereAction;
    private Jeu jeu;

    public MainJeu(List<Joueur> joueurs) {
        jeu = new Jeu(joueurs, etapeJeu -> deciderChangementEtapeJeu(etapeJeu));
        etapeJeu = EtapeJeu.JEU_EN_COURS;
        // Map et contenu du jeu
        islandPanel = new TheIsland();
        islandPanel.setLayout(null);
        // Cadrant reserve
        cadrant = new CadranReserve(Collections.emptyList(), pionExplorateur -> {
            pionExplorateurSelectionne = pionExplorateur;
            JOptionPane.showMessageDialog(islandPanel, "Tu as selectionné: " + pionExplorateur.getNumero() + "\n Deplacez le pion dans une case vide");
        }, () -> {
            // Force passage du tour
            jeu.prochaineSousEtape(true);
            misAJourCadrant();
            if (dernierePosition != null) {
                islandPanel.caseJeuByPosition(dernierePosition).misAJourPions(jeu.pionsDunePosition(dernierePosition), false);
            }
        });
        cadrant.setSize(300, HEIGHT);
        BorderLayout borderLayout = new BorderLayout();
        // Main panel qui hoste les deux
        setLayout(borderLayout);
        add(islandPanel, BorderLayout.WEST);
        add(cadrant, BorderLayout.EAST);
        ajoutComposantsInitiaux();
    }

    private void deciderChangementEtapeJeu(models.EtapeJeu etapeJeu) {
        misAJourCadrant();
        if (dernierePosition != null) {
            islandPanel.caseJeuByPosition(dernierePosition).misAJourPions(jeu.pionsDunePosition(dernierePosition), false);
        }
        switch (etapeJeu) {
            case JOUER_TUILE_EN_RESERVE -> {
                pionADeplacer = null;
                dernierePosition = null;
                if (jeu.getJoueurEnCours().getFacesActionEnReserve().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vous avez aucune tuile de réserve, déplacez vos pions");
                    jeu.prochaineSousEtape(false);
                } else {
                    afficherReserveEnCours();
                }
            }
            case DEPLACER_PION -> {
                pionADeplacer = null;
                dernierePosition = null;
            }
            case RETIRER_TUILE -> {
                pionADeplacer = null;
            }
            case LANCER_DE -> {
                final DeCreature resultatCreature = jeu.getDernierResultatDe();
                if (jeu.existeCreatureDeType(resultatCreature.getCreature())) {
                    JOptionPane.showMessageDialog(this, "Dé lancé !!! Le résultat est " + resultatCreature);
                } else {
                    JOptionPane.showMessageDialog(this, "Dé lancé !!! Le résultat est " + resultatCreature + " !!! Mais aucune créature de type " + resultatCreature + " n'existe dans le plateau");
                    jeu.prochaineSousEtape(true);
                }
            }
        }
    }

    void ajoutComposantsInitiaux() {
        // Linker des cases virtuelles vides
        for (Position position : jeu.getPositions()) {
            islandPanel.ajouterCase(new CaseJeu(W2 + position.getX() * (WIDTH_BOX + PRECISION) - WIDTH_BOX / 2 - position.getY() * ((WIDTH_BOX + PRECISION) / 2),
                    H2 - HEIGHT_BOX / 2 + PRECISION - position.getY() * (HEIGHT_BOX - WIDTH_BOX / 2 + 4 * 2),
                    position,
                    (caseJeu) -> {
                        switch (etapeJeu) {
                            case DISTRIBUTION_PIONS -> {
                                if (pionExplorateurSelectionne != null) {
                                    if (caseJeu.ajouterPremierPion(pionExplorateurSelectionne)) {
                                        // Réinitialiser pion et passer au next
                                        boolean restePionsNonJoues = jeu.deplacerPionNonJoue(pionExplorateurSelectionne, position);
                                        caseJeu.misAJourPions(jeu.getPionsEnJeu().get(position), false);
                                        if (!restePionsNonJoues) {
                                            cadrant.removeAll();
                                            cadrant.revalidate();
                                            etapeJeu = EtapeJeu.DISTRIBUTION_BATEAUX;
                                        }
                                        misAJourCadrant();
                                        pionExplorateurSelectionne = null;
                                    } else {
                                        // Dans ce cas y a deja un pion qui est dans la case selectionneé, afficher une alerte
                                        JOptionPane.showMessageDialog(caseJeu, "Il faut déplacer le pion dans une tuile vide");
                                    }
                                }
                            }
                            case DISTRIBUTION_BATEAUX -> {
                                if (jeu.estPositionAdjacenteAUneTuile(position) && jeu.estPositionDansMer(position)) {
                                    Bateau bateau = jeu.getJoueurEnCours().getBateauxNonJoues().get(0);
                                    caseJeu.ajouterPremierBateau(bateau);
                                    boolean resteBateauxNonJoues = jeu.placerBateauNonJoue(bateau, position);
                                    if (!resteBateauxNonJoues) {
                                        etapeJeu = EtapeJeu.JEU_EN_COURS;
                                        jeu.commencerJouer();
                                    }
                                    misAJourCadrant();
                                } else {
                                    JOptionPane.showMessageDialog(caseJeu, "Il faut choisir une case mer à côté d'une tuile");
                                }

                            }
                            case JEU_EN_COURS -> {
                                deciderEtapeEnCours(caseJeu);
                            }
                        }
                    }));
        }

        // Afficher les tuiles de base
        for (Map.Entry<Tuile, Position> entreeTuile : jeu.getTuilesEnJeu().entrySet()) {
            islandPanel.caseJeuEnPosition(entreeTuile.getValue()).ajouterTuile(entreeTuile.getKey());
        }
        // Pour affichage des pions serpent
        for (Map.Entry<Position, List<Pion>> entreePion : jeu.getPionsEnJeu().entrySet()) {
            islandPanel.caseJeuEnPosition(entreePion.getKey()).misAJourPions(entreePion.getValue(), false);
        }

        misAJourCadrant();

        // TODO: 21/05/2022, supprimer après d'ici
        jeu.commencerJouer();
    }

    private void deciderEtapeEnCours(CaseJeu caseJeu) {
        switch (jeu.getEtapeEnCours()) {
            case JOUER_TUILE_EN_RESERVE -> actionTuileEnReserve(caseJeu);
            case DEPLACER_PION -> actionEnDeplacerPion(caseJeu);
            case RETIRER_TUILE -> actionEnRetraitTuile(caseJeu);
            case LANCER_DE -> actionEnLancementDe(caseJeu);
        }
    }

    /**
     * Action à faire suite au lancement de dé
     *
     * @param caseJeu case sélectionnée
     */
    private void actionTuileEnReserve(CaseJeu caseJeu) {
        if (derniereAction != null) {
            // Aucun pion n'est sélectionné, preciser la sélection avant
            if (pionADeplacer == null) {
                switch (derniereAction) {
                    case DAUPHIN -> {
                        List<Pion> pionsNageurs = getPionsNageurDeCouleur(caseJeu, jeu.getJoueurEnCours().getCouleurPion());
                        if (!pionsNageurs.isEmpty()) {
                            pionADeplacer = pionsNageurs.get(0);
                            dernierePosition = caseJeu.getPosition();
                            islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
                        } else {
                            JOptionPane.showMessageDialog(this, "Il faut choisir l'un de vos pions nageurs");
                        }
                    }
                    case BATEAU -> {
                        Bateau bateau = getBateauPossibleAController(caseJeu, jeu.getJoueurEnCours().getCouleurPion());
                        if (bateau != null) {
                            pionADeplacer = bateau;
                            dernierePosition = caseJeu.getPosition();
                            islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
                        } else {
                            JOptionPane.showMessageDialog(this, "Il faut choisir un bateau que vous pouvez controller");
                        }
                    }
                    case REQUIN -> choisirCreatureDuneCase(caseJeu, REQUIN);
                    case BALEINE -> choisirCreatureDuneCase(caseJeu, BALEINE);
                    case SERPENT -> choisirCreatureDuneCase(caseJeu, SERPENT);
                }
            } else {
                switch (derniereAction) {
                    case DAUPHIN, BATEAU -> {
                        if (dernierePosition != caseJeu.getPosition() && caseJeu.getPosition().getListeAdjascence().contains(dernierePosition) && jeu.estCaseMer(caseJeu.getPosition())) {
                            jeu.deplacerPion(pionADeplacer, dernierePosition, caseJeu.getPosition(), false);
                            islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
                            islandPanel.caseJeuByPosition(dernierePosition).misAJourPions(jeu.pionsDunePosition(dernierePosition), false);
                            jeu.prochaineSousEtape(false);
                            dernierePosition = caseJeu.getPosition();
                        } else {
                            JOptionPane.showMessageDialog(this, "Il faut déplacer le " + pionADeplacer + " d'une seule case et pas dans un terrain");
                        }
                    }
                    case REQUIN, BALEINE, SERPENT -> {
                        if (dernierePosition != caseJeu.getPosition()
                                && jeu.estCaseMer(caseJeu.getPosition())
                                && jeu.pionsDunePosition(caseJeu.getPosition()).isEmpty()) {
                            jeu.deplacerPion(pionADeplacer, dernierePosition, caseJeu.getPosition(), false);
                            islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
                            islandPanel.caseJeuByPosition(dernierePosition).misAJourPions(jeu.pionsDunePosition(dernierePosition), false);
                            jeu.prochaineSousEtape(true);
                        } else {
                            JOptionPane.showMessageDialog(this, "Il faut déplacer le " + pionADeplacer + " dans une autre case mer inoccupée");
                        }
                    }
                }
            }
        } else {
            jeu.prochaineSousEtape(true);
        }
    }

    /**
     * Choisir créature de type passé en paramètres dans la case jeu
     * @param caseJeu case jeu
     * @param typeCreature type de créature
     */
    private void choisirCreatureDuneCase(CaseJeu caseJeu, Creature typeCreature) {
        if (caseJeu.getPions().contains(typeCreature)) {
            pionADeplacer = typeCreature;
            dernierePosition = caseJeu.getPosition();
            islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
        } else {
            JOptionPane.showMessageDialog(this, "Il faut choisir un " + typeCreature);
        }
    }

    /**
     * Action à faire dans l'étape de déplacement des pions
     *
     * @param caseJeu case sélectionnée
     */
    private void actionEnDeplacerPion(CaseJeu caseJeu) {
        if (pionADeplacer == null) {
            // Verifier si la case selectionnée contient un pion que le joueur en cours peut déplacer
            if (caseContientPionDeCouleurOuBateau(caseJeu, jeu.getJoueurEnCours().getCouleurPion())) {
                // Charger liste des pions de joueur en cours dans la case sélectionnée
                List<Pion> pionsJoueurs = getPionJoueurEnCours(caseJeu);
                if (pionsJoueurs.size() == 1) {
                    pionADeplacer = pionsJoueurs.get(0);
                    dernierePosition = caseJeu.getPosition();
                    if (pionADeplacer instanceof Bateau) {
                        afficherElementBateau(caseJeu, (Bateau) pionADeplacer);
                    } else {
                        dernierePosition = caseJeu.getPosition();
                        islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
                    }
                } else {
                    afficherElementDeLaCase(caseJeu);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vous n'avez pas un pion de votre couleur dans la case choisit");
            }
        } else {
            // Vérifier si la nouvelle case sélectionnée est soit l'ancienne et parmi les cases adjacentes
            if (caseJeu.getPosition().getListeAdjascence().contains(dernierePosition) || dernierePosition.equals(caseJeu.getPosition())) {
                boolean deplacementDansUnBateau = false;
                if (dernierePosition.equals(caseJeu.getPosition())) {
                    // deuxieme case selectionnée égale a la premiere
                    // Vérifier si la deuxième case contient un bateau, et s'il y a encore de place dans le bateau
                    Bateau bateau = null;
                    for (Pion pion : jeu.pionsDunePosition(caseJeu.getPosition())) {
                        if (pion instanceof Bateau) {
                            bateau = (Bateau) pion;
                            break;
                        }
                    }
                    if (bateau != null && bateau.existeEncoreDuPlace()) {
                        deplacementDansUnBateau = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Pour un deplacement il faut avoir un bateau contenant encore de place");
                    }
                }
                if (jeu.estPositionDeSauvetage(caseJeu.getPosition())) {
                    jeu.sauverPion(dernierePosition, pionADeplacer);
                    misAJourCadrant();
                } else {
                    jeu.deplacerPion(pionADeplacer, dernierePosition, caseJeu.getPosition(), deplacementDansUnBateau);
                }

                islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), false);
                islandPanel.caseJeuByPosition(dernierePosition).misAJourPions(jeu.pionsDunePosition(dernierePosition), false);
                jeu.prochaineSousEtape(false);
                pionADeplacer = null;
                dernierePosition = null;
            } else {
                JOptionPane.showMessageDialog(this, "Vous ne pouvez pas déplacer un pion par plus d'une seule case a la fois");
            }
        }
    }

    /**
     * Action à faire suite à un retrait d'une tuile
     *
     * @param caseJeu case sélectionnée
     */
    private void actionEnRetraitTuile(CaseJeu caseJeu) {
        if (caseJeu.getTuile() != null) {
            if (caseJeu.getTuile().getFaceTerrain() == jeu.prochainTerrainAChoisir()) {
                afficherTuileChoisit(caseJeu);
            } else {
                JOptionPane.showMessageDialog(this, "Il faut choisir une case " + jeu.prochainTerrainAChoisir());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Il faut choisir une case tuile");
        }
    }

    /**
     * Action à faire suite au lancement de dé
     *
     * @param caseJeu case sélectionnée
     */
    private void actionEnLancementDe(CaseJeu caseJeu) {
        final Creature resultatDe = jeu.getDernierResultatDe().getCreature();
        if (pionADeplacer == null) {
            if (jeu.pionsDunePosition(caseJeu.getPosition()).contains(resultatDe)) {
                // indice de la créature dans la case en cours
                int indice = jeu.pionsDunePosition(caseJeu.getPosition()).indexOf(resultatDe);
                // remplir pion à déplacer
                pionADeplacer = jeu.pionsDunePosition(caseJeu.getPosition()).get(indice);
                dernierePosition = caseJeu.getPosition();
                islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
            } else {
                JOptionPane.showMessageDialog(this, "Il faut choisir une créature de type " + resultatDe + " à déplacer");
            }
        } else {
            if (dernierePosition != caseJeu.getPosition() && caseJeu.getPosition().getListeAdjascence().contains(dernierePosition) && caseJeu.getTuile() == null) {
                if (resultatDe == REQUIN) {

                } else if (resultatDe == BALEINE) {

                }
                jeu.deplacerPion(pionADeplacer, dernierePosition, caseJeu.getPosition(), false);
                islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
                islandPanel.caseJeuByPosition(dernierePosition).misAJourPions(jeu.pionsDunePosition(dernierePosition), false);
                dernierePosition = caseJeu.getPosition();
                jeu.prochaineSousEtape(false);
            } else {
                JOptionPane.showMessageDialog(this, "Il faut déplacer la créature d'une seule case et pas dans un terrain");
            }
        }
    }

    void afficherReserveEnCours() {
        JFrame frame = new JFrame("Sélection réserve");
        List<FaceAction> listeReserve = jeu.getJoueurEnCours().getFacesActionEnReserve();

        String[] nomListeReserve = new String[listeReserve.size()];
        for (int i = 0; i < listeReserve.size(); i++) {
            nomListeReserve[i] = listeReserve.get(i).toString();
        }
        JList listeLabels = new JList(nomListeReserve);
        listeLabels.setCellRenderer(new ReserveListRenderer());

        JScrollPane scroll = new JScrollPane(listeLabels);
        scroll.setPreferredSize(new Dimension(250, 600));
        listeLabels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final JButton boutonSelection = new JButton("Selectionner");
        boutonSelection.addActionListener(e -> {
            if (listeLabels.getSelectedIndices().length > 0) {
                FaceAction elementSelectionne = listeReserve.get(listeLabels.getSelectedIndices()[0]);
                if (elementSelectionne.getContour() == Contour.ROUGE) {
                    frame.dispose();
                    déciderActionEnCours(elementSelectionne);
                } else {
                    JOptionPane.showMessageDialog(this, "Vous ne pouvez pas choisir cet action maintenant, celle est concu pour défendre");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Rien n'est sélectionné");
            }
        });

        final JButton boutonContinuer = new JButton("Continuer");
        boutonContinuer.addActionListener(e -> {
            frame.dispose();
            jeu.prochaineSousEtape(true);
        });

        frame.add(scroll);
        frame.getContentPane().add(boutonSelection, BorderLayout.SOUTH);
        frame.getContentPane().add(boutonContinuer, BorderLayout.EAST);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Décider une action suite à une sélection d'une tuile en réserve
     *
     * @param action action de tuile associée
     */
    private void déciderActionEnCours(FaceAction action) {
        jeu.recalculerSousEtapeAction(action);
        jeu.enleverFaceRéserveJoueurEnCours(action);
        derniereAction = action.getContenu();
        switch (action.getContenu()) {
            case DAUPHIN -> JOptionPane.showMessageDialog(this, "Déplacer l'un de vos pions de 1 à 3 cases");
            case BATEAU ->
                    JOptionPane.showMessageDialog(this, "Déplacer l'un des bateaux que vous pouvez controller de 1 à 3 cases");
            case REQUIN ->
                    JOptionPane.showMessageDialog(this, "Déplacer un requin dans n'importe quelle case mer inoccupée");
            case BALEINE ->
                    JOptionPane.showMessageDialog(this, "Déplacer une baleine dans n'importe quelle case mer inoccupée");
            case SERPENT ->
                    JOptionPane.showMessageDialog(this, "Déplacer un serpent dans n'importe quelle case mer inoccupée");
        }
    }

    void afficherElementDeLaCase(CaseJeu caseJeu) {
        final JDialog modal = new JDialog((JFrame) SwingUtilities.windowForComponent(caseJeu), "Liste des pions dans case", true);
        modal.setLocationRelativeTo(caseJeu);
        modal.getContentPane().add(new JPanel());
        List<Pion> listePions = getPionJoueurEnCours(caseJeu);
        final int MAX = listePions.size();
        // intialiser la liste des elements de la case
        String[] listeNomsPions = new String[MAX];
        for (int i = 0; i < MAX; i++) {
            listeNomsPions[i] = listePions.get(i).toString();
        }
        final JList listeNoms = new JList(listeNomsPions);
        listeNoms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JScrollPane pane = new JScrollPane(listeNoms);
        // creer un bouton avec selection
        final JButton boutonSelection = new JButton("Selectionner");
        boutonSelection.addActionListener(e -> {
            modal.dispose();

            Pion elementSelectionne = listePions.get(listeNoms.getSelectedIndices()[0]);
            if (elementSelectionne instanceof Bateau) {
                afficherElementBateau(caseJeu, (Bateau) elementSelectionne);
            } else {
                pionADeplacer = elementSelectionne;
                dernierePosition = caseJeu.getPosition();
                islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
            }
        });
        modal.setLayout(new BorderLayout());
        modal.getContentPane().add(pane, BorderLayout.CENTER);
        modal.getContentPane().add(boutonSelection, BorderLayout.SOUTH);
        modal.pack();
        modal.setSize(300, 300);
        modal.setVisible(true);
    }

    void afficherElementBateau(CaseJeu caseJeu, Bateau bateau) {
        final JDialog modal = new JDialog((JFrame) SwingUtilities.windowForComponent(caseJeu), "Liste des pions dans bateau", true);
        modal.setLocationRelativeTo(caseJeu);
        modal.getContentPane().add(new JPanel());
        List<Pion> listePions = getPionBateau(bateau);
        listePions.add(0, bateau);
        final int MAX = listePions.size();
        // intialiser la liste des elements de la case
        String[] listeNomsPions = new String[MAX];
        for (int i = 0; i < MAX; i++) {
            listeNomsPions[i] = listePions.get(i).toString();
        }
        final JList listeNoms = new JList(listeNomsPions);
        listeNoms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JScrollPane pane = new JScrollPane(listeNoms);
        // creer un bouton avec selection
        final JButton boutonSelection = new JButton("Selectionner");
        boutonSelection.addActionListener(e -> {
            modal.dispose();

            Pion elementSelectionne = listePions.get(listeNoms.getSelectedIndices()[0]);
            if (elementSelectionne instanceof Bateau) {
                pionADeplacer = elementSelectionne;
                dernierePosition = caseJeu.getPosition();
                islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), true);
            } else {
                jeu.deplacerPion(elementSelectionne, caseJeu.getPosition(), caseJeu.getPosition(), true);
                islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), false);
                jeu.prochaineSousEtape(false);
            }
        });
        modal.setLayout(new BorderLayout());
        modal.getContentPane().add(pane, BorderLayout.CENTER);
        modal.getContentPane().add(boutonSelection, BorderLayout.SOUTH);
        modal.pack();
        modal.setSize(300, 300);
        modal.setVisible(true);
    }

    void afficherTuileChoisit(CaseJeu caseJeu) {
        final JDialog modal = new JDialog((JFrame) SwingUtilities.windowForComponent(caseJeu), "Tuile", true);
        modal.setLocationRelativeTo(caseJeu);
        JPanel conteneurTuile = new JPanel(new GridBagLayout());
        conteneurTuile.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.CENTER;

        JLabel joueurEnCoursLabel = new JLabel("Tuile " + caseJeu.getTuile().getFaceTerrain() + " révelée");
        joueurEnCoursLabel.setHorizontalAlignment(JLabel.CENTER);
        joueurEnCoursLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel imagePion = new JLabel();
        imagePion.setHorizontalAlignment(JLabel.CENTER);
        imagePion.setBackground(new Color(222, 221, 197));
        imagePion.setIcon(new ImageIcon(new ImageIcon(ConstanteImage.PATH_LOCAL + caseJeu.getTuile().getFaceAction().toString() + "_desc.png").getImage().getScaledInstance(600, 180, Image.SCALE_DEFAULT)));

        JButton continuerBouton = new JButton("Continuer");
        continuerBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modal.dispose();
                jeu.tournerFaceTuile(caseJeu.getTuile());
                deciderActionTuile(caseJeu);
                caseJeu.supprimerTuile();
            }
        });
        conteneurTuile.add(joueurEnCoursLabel, gbc);
        conteneurTuile.add(imagePion, gbc);
        conteneurTuile.add(new Label(), gbc);
        conteneurTuile.add(continuerBouton, gbc);

        modal.getContentPane().add(conteneurTuile);
        modal.pack();
        modal.setSize(620, 500);
        modal.setVisible(true);
    }

    void deciderActionTuile(CaseJeu caseJeu) {
        final FaceAction faceAction = caseJeu.getTuile().getFaceAction();
        switch (faceAction.getContour()) {
            case VERT -> {
                switch (faceAction.getContenu()) {
                    case VOLCAN -> {
                        jeu.terminerJeu();
                        JOptionPane.showMessageDialog(this, "Jeu terminé !!!!! Le joueur gagnant est " + jeu.declarerGagnant().getPseudo());
                    }
                    case TOURBILLON -> {
                        for (Position position : jeu.jouerTourbillon(caseJeu.getPosition())) {
                            islandPanel.caseJeuByPosition(position).misAJourPions(jeu.pionsDunePosition(position), false);
                        }
                    }
                    case REQUIN -> apparaitreCreature(REQUIN, caseJeu.getPosition());
                    case BALEINE -> apparaitreCreature(Creature.BALEINE, caseJeu.getPosition());
                    case BATEAU -> {
                        Bateau bateau = null;
                        for (Pion pion : jeu.getPionsEnReserve()) {
                            if (pion instanceof Bateau) {
                                bateau = (Bateau) pion;
                                break;
                            }
                        }
                        if (bateau != null) {
                            jeu.getPionsEnReserve().remove(bateau);
                            jeu.deplacerPion(bateau, null, caseJeu.getPosition(), false);
                            islandPanel.caseJeuByPosition(caseJeu.getPosition()).misAJourPions(jeu.pionsDunePosition(caseJeu.getPosition()), false);
                        } else {
                            JOptionPane.showMessageDialog(this, "Pas de bateau en reserve");
                        }
                    }
                    default -> {
                    }
                }
                misAJourCadrant();
            }
            case ROUGE, ROUGE_BARRE -> jeu.ajouterFaceRéserveJoueurEnCours(faceAction);
        }
        jeu.prochaineSousEtape(true);
    }

    private void apparaitreCreature(Creature creatureACreer, Position position) {
        Creature creature = null;
        for (Pion pion : jeu.getPionsEnReserve()) {
            if (pion == creatureACreer) {
                creature = (Creature) pion;
                break;
            }
        }
        if (creature != null) {
            jeu.getPionsEnReserve().remove(creature);
            jeu.deplacerPion(creature, null, position, false);
        } else {
            JOptionPane.showMessageDialog(this, "Pas de " + creatureACreer + " en reserve");
        }
        islandPanel.caseJeuByPosition(position).misAJourPions(jeu.pionsDunePosition(position), false);
    }

    /**
     * Vérifier si la case contient des pions que le joueur avec la couleur sélectionnée peut controller
     *
     * @param caseJeu case jeu en cours
     * @param couleur couleur de control
     * @return si la case satisfait les conditions
     */
    private boolean caseContientPionDeCouleurOuBateau(CaseJeu caseJeu, Couleur couleur) {
        if (caseJeu.getPions() == null) {
            return false;
        }
        for (Pion pion : caseJeu.getPions()) {
            if (pion instanceof PionExplorateur && ((PionExplorateur) pion).getCouleur() == couleur) {
                return true;
            } else if (pion instanceof Bateau && ((Bateau) pion).getControlleurs().contains(couleur)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get pions nageurs de la case passée avec la couleur
     *
     * @param caseJeu case sélectionnée
     * @param couleur couleur passée
     * @return si la case contient la couleur passée
     */
    private List<Pion> getPionsNageurDeCouleur(CaseJeu caseJeu, Couleur couleur) {
        if (caseJeu.getPions() == null) {
            return new ArrayList<>();
        }
        List<Pion> pionsNageurs = new ArrayList<>();
        for (Pion pion : caseJeu.getPions()) {
            if (pion instanceof PionExplorateur && ((PionExplorateur) pion).getCouleur() == couleur
                    && jeu.estCaseMer(caseJeu.getPosition())) {
                pionsNageurs.add(pion);
            }
        }
        return pionsNageurs;
    }

    /**
     * Retourner le bateau possible à controller pour la couleur et le case passée en paramètres
     *
     * @param caseJeu case jeu sélectionnée
     * @param couleur couleur
     * @return Bateau s'il existe
     */
    private Bateau getBateauPossibleAController(CaseJeu caseJeu, Couleur couleur) {
        if (caseJeu.getPions() == null) {
            return null;
        }
        for (Pion pion : caseJeu.getPions()) {
            if (pion instanceof Bateau && ((Bateau) pion).getControlleurs().contains(couleur)) {
                return (Bateau) pion;
            }
        }
        return null;
    }


    private List<Pion> getPionJoueurEnCours(CaseJeu caseJeu) {
        List<Pion> list = new ArrayList<>();
        for (Pion pion : caseJeu.getPions()) {
            if (pion instanceof PionExplorateur
                    && jeu.getJoueurEnCours().getCouleurPion() == ((PionExplorateur) pion).getCouleur()) {
                list.add(pion);
            } else if (pion instanceof Bateau
                    && ((Bateau) pion).getControlleurs().contains(jeu.getJoueurEnCours().getCouleurPion())) {
                list.add(pion);
            }
        }
        return list;
    }

    private List<Pion> getPionBateau(Bateau bateau) {
        List<Pion> list = new ArrayList<>();
        for (PionExplorateur pion : bateau.getPionsOnBateau()) {
            if (pion.getCouleur() == jeu.getJoueurEnCours().getCouleurPion()) {
                list.add(pion);
            }
        }
        return list;
    }


    private void misAJourCadrant() {
        cadrant.removeAll();
        switch (etapeJeu) {
            case DISTRIBUTION_PIONS -> {
                cadrant.afficherJoueurEnCours(jeu.getJoueurEnCours(), false, null);
                cadrant.afficherPionsNonJoues(jeu.getJoueurEnCours());
            }
            case DISTRIBUTION_BATEAUX -> {
                cadrant.afficherJoueurEnCours(jeu.getJoueurEnCours(), false, null);
                JOptionPane.showMessageDialog(this, jeu.getJoueurEnCours().getPseudo() + "!! : Selectionnez une case mer proche d'une tuile pour placer un bateau");
            }
            case JEU_EN_COURS -> {
                cadrant.afficherJoueurEnCours(jeu.getJoueurEnCours(), true, jeu.getEtapeEnCours());
                cadrant.afficherPionsReserve(jeu.getPionsEnReserve());
            }
        }
        cadrant.revalidate();
        cadrant.repaint();
    }

    public static void main(String[] args) {
//         Declaration des elements jeux, map + cadrant reserve
        Joueur joueur1 = new Joueur("Beckyyyy", Couleur.ROUGE);
        Joueur joueur2 = new Joueur("Mimiiiiiii", Couleur.BLEU);
        Joueur joueur3 = new Joueur("Hrayna", Couleur.VERT);
        Joueur joueur4 = new Joueur("Eriiiij", Couleur.JAUNE);
        JFrame frame = new JFrame();
        MainJeu mainJeu = new MainJeu(List.of(joueur1, joueur2, joueur3, joueur4));
        frame.setContentPane(mainJeu);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);



//        MenuJeu menu = new MenuJeu();
//        menu.setLocationRelativeTo(null);
//        menu.setVisible(true);

        String filePath = REGLES;

    }

    public class ReserveListRenderer extends DefaultListCellRenderer {
        Font font = new Font("helvitica", Font.BOLD, 24);
        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, null, index, isSelected, cellHasFocus);
            label.setIcon(new ImageIcon(new ImageIcon(ConstanteImage.PATH_LOCAL + jeu.getJoueurEnCours()
                    .getFacesActionEnReserve().get(index).toString() + ".png").getImage()
                    .getScaledInstance(240, 180, Image.SCALE_DEFAULT)));
            label.setHorizontalTextPosition(JLabel.RIGHT);
            label.setFont(font);
            return label;
        }
    }
}
