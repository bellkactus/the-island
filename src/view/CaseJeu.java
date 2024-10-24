package view;

import models.Position;
import models.pions.Bateau;
import models.pions.Creature;
import models.pions.Pion;
import models.pions.PionExplorateur;
import models.tuiles.Terrain;
import models.tuiles.Tuile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static view.ConstanteImage.PLATEAU;
import static view.ConstanteImage.POLYOGON_SELECTION;
import static view.TheIsland.HEIGHT_BOX;
import static view.TheIsland.WIDTH_BOX;

public class CaseJeu extends JPanel {
    private final int x;
    private final int y;
    private Position position;
    private Tuile tuile;
    private Bateau bateau;
    private List<Pion> pions;
    private final CallbackCaseJeu callback;

    public CaseJeu(int x, int y, Position position, CallbackCaseJeu callback) {
        this.x = x;
        this.y = y;
        this.position = position;
        this.tuile = null;
        this.bateau = null;
        this.pions = null;
        this.callback = callback;
        this.setBounds(x, y, WIDTH_BOX, HEIGHT_BOX);
        this.setLayout(new OverlayLayout(this));
        this.setOpaque(false);
        afficherElements(false);
        initialiserActions();
    }

    public Position getPosition() {
        return position;
    }

    public List<Pion> getPions() {
        if (bateau != null) {
            List<Pion> list = new ArrayList<>();
            list.addAll(pions != null ? pions : new ArrayList<>());
            list.add(bateau);
            return list;
        }
        return pions != null ? pions : new ArrayList<>();
    }

    public Tuile getTuile() {
        return tuile;
    }

    void ajouterTuile(Tuile tuile) {
        this.tuile = tuile;
        afficherElements(false);
    }

    void supprimerTuile() {
        this.tuile = null;
        afficherElements(false);
    }

    void misAJourPions(List<Pion> pions, boolean selectionne) {
        this.pions = new ArrayList<>();
        Bateau bateau = null;
        for (Pion pion : pions) {
            if (pion instanceof Bateau) {
                bateau = (Bateau) pion;
            } else {
                this.pions.add(pion);
            }
        }
        this.bateau = bateau;
        afficherElements(selectionne);
    }

    private void afficherElements(boolean selectionne) {
        removeAll();
        if (selectionne) {
            afficherSelection();
        }
        if (pions != null && !pions.isEmpty()) {
            afficherPions(this.pions, bateau != null);
        }
        if (bateau != null) {
            afficherBateau(this.bateau);
        }
        if (tuile != null) {
            afficherTerrain(this.tuile.getFaceTerrain());
        }
        revalidate();
        repaint();
    }

    private void initialiserActions() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p1 = new Point(28, 6);
                Point p2 = new Point(56, 22);
                Point p3 = new Point(0, 22);
                Point p4 = new Point(0, 52);
                Point p5 = new Point(56, 52);
                Point p6 = new Point(28, 70);
                if (estDansTriangle(new Point(e.getX(), e.getY()), p1, p2, p3)
                        || (e.getY() > 23 && e.getY() < 52)
                        || estDansTriangle(new Point(e.getX(), e.getY()), p4, p5, p6)) {
                    callback.enSelectionCase(CaseJeu.this);
                }
            }
        });
    }

    boolean ajouterPremierPion(PionExplorateur pionExplorateur) {
        // Verifier si on est sur une tuile et que la liste des pions est vide, sinon on sort
        if (tuile == null || (pions != null && !pions.isEmpty())) {
            return false;
        }
        if (this.pions == null) {
            this.pions = new ArrayList<>();
        }
        pions.add(pionExplorateur);
        afficherElements(false);
        return true;
    }

    boolean ajouterPremierBateau(Bateau bateau) {
        this.pions = new ArrayList<>();
        this.bateau = bateau;
        afficherElements(false);
        return true;
    }

    void afficherTerrain(Terrain terrain) {
        JLabel imageTerrain = new JLabel();
        String pathTerrain = "";
        switch (terrain) {
            case PLAGE -> pathTerrain = ConstanteImage.TERRAIN_PLAGE;
            case FORET -> pathTerrain = ConstanteImage.TERRAIN_FORET;
            case MONTAGNE -> pathTerrain = ConstanteImage.TERRAIN_MONTAGNE;
        }
        imageTerrain.setIcon(new ImageIcon(new ImageIcon(pathTerrain).getImage().getScaledInstance(WIDTH_BOX,
                HEIGHT_BOX, Image.SCALE_DEFAULT)));
        this.add(imageTerrain);
    }

    void afficherSelection() {
        JLabel imageTerrain = new JLabel();
        imageTerrain.setIcon(new ImageIcon(new ImageIcon(POLYOGON_SELECTION).getImage().getScaledInstance(WIDTH_BOX,
                HEIGHT_BOX, Image.SCALE_DEFAULT)));
        this.add(imageTerrain);
    }

    void afficherBateau(Bateau bateau) {
        List<PionExplorateur> explorateursABord = bateau.getPionsExplorateur();
        if (!explorateursABord.isEmpty()) {
            JPanel pionsPanel = new JPanel();
            pionsPanel.setLayout(new BoxLayout(pionsPanel, BoxLayout.Y_AXIS));
            pionsPanel.setMaximumSize(new Dimension(WIDTH_BOX, HEIGHT_BOX));
            pionsPanel.setOpaque(false);
            String pathPion = "";
            //Ajouter margin en haut pour deplacer la liste des pions en bas
            JLabel margin = new JLabel();
            EmptyBorder borderHaut = new EmptyBorder(8, 0, 0, 0);
            margin.setBorder(borderHaut);
            pionsPanel.add(margin);
            EmptyBorder borderGauche = new EmptyBorder(0, 5, 0, 0);
            for (int i = 0; i < explorateursABord.size(); i++) {
                switch (explorateursABord.get(i).getCouleur()) {
                    case ROUGE -> pathPion = ConstanteImage.PION_ROUGE;
                    case JAUNE -> pathPion = ConstanteImage.PION_JAUNE;
                    case VERT -> pathPion = ConstanteImage.PION_VERT;
                    case BLEU -> pathPion = ConstanteImage.PION_BLEU;
                }
                JLabel imagePion = new JLabel();
                imagePion.setBorder(borderGauche);
                imagePion.setIcon(new ImageIcon(new ImageIcon(pathPion).getImage().getScaledInstance(WIDTH_BOX / 3, (HEIGHT_BOX - 6) / 4, Image.SCALE_DEFAULT)));
                pionsPanel.add(imagePion);
            }
            this.add(pionsPanel);
        }


        JLabel imageBateau = new JLabel();
        imageBateau.setIcon(new ImageIcon(new ImageIcon(ConstanteImage.PION_BATEAU).getImage().getScaledInstance(WIDTH_BOX / 2, 2 * (HEIGHT_BOX - 6) / 3,
                Image.SCALE_DEFAULT)));
        this.add(imageBateau);
    }

    void afficherPions(List<Pion> pions, Boolean estBateauDispo) {
        if (!pions.isEmpty()) {
            Boolean estElementUnique = pions.size() == 1;
            JPanel pionsPanel = new JPanel();
            pionsPanel.setLayout(new BoxLayout(pionsPanel, BoxLayout.Y_AXIS));
            pionsPanel.setBorder(BorderFactory.createEmptyBorder(0, estBateauDispo ? 28 : 20, 0, 0));
            pionsPanel.setMaximumSize(new Dimension(WIDTH_BOX, HEIGHT_BOX));
            pionsPanel.setOpaque(false);
            String pathPion = "";
            JLabel margin = new JLabel();
            EmptyBorder borderHaut = new EmptyBorder(estElementUnique ? 20 : 8, 0, 0, 0);
            margin.setBorder(borderHaut);
            pionsPanel.add(margin);
            for (Pion pion : pions) {
                if (pion instanceof PionExplorateur) {
                    switch (((PionExplorateur) pion).getCouleur()) {
                        case ROUGE -> pathPion = ConstanteImage.PION_ROUGE;
                        case JAUNE -> pathPion = ConstanteImage.PION_JAUNE;
                        case VERT -> pathPion = ConstanteImage.PION_VERT;
                        case BLEU -> pathPion = ConstanteImage.PION_BLEU;
                    }

                } else if (pion instanceof Creature) {
                    switch ((Creature) pion) {
                        case REQUIN -> pathPion = ConstanteImage.PION_REQUIN;
                        case BALEINE -> pathPion = ConstanteImage.PION_BALEINE;
                        case SERPENT -> pathPion = ConstanteImage.PION_SERPENT;
                    }
                }
                JLabel imagePion = new JLabel();
                imagePion.setIcon(new ImageIcon(new ImageIcon(pathPion).getImage().getScaledInstance(WIDTH_BOX / (!estElementUnique ? 3 : 2), (HEIGHT_BOX - 6) / (!estElementUnique ? 4 : 2), Image.SCALE_DEFAULT)));
                pionsPanel.add(imagePion);
            }
            this.add(pionsPanel);
        }
    }


    private float signe(Point p1, Point p2, Point p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

    private boolean estDansTriangle(Point pt, Point v1, Point v2, Point v3) {
        float d1, d2, d3;
        boolean estNegatif, aPosition;

        d1 = signe(pt, v1, v2);
        d2 = signe(pt, v2, v3);
        d3 = signe(pt, v3, v1);

        estNegatif = (d1 < 0) || (d2 < 0) || (d3 < 0);
        aPosition = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(estNegatif && aPosition);
    }
}
