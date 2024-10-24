package view;

import models.EtapeJeu;
import models.Joueur;
import models.pions.Bateau;
import models.pions.Creature;
import models.pions.Pion;
import models.pions.PionExplorateur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static view.TheIsland.HEIGHT_BOX;
import static view.TheIsland.WIDTH_BOX;


public class CadranReserve extends JPanel {
    public static int WIDTH_CADRANT = 180;
    private final CallbackPionExplorateur callbackPionExplorateur;
    private final CallbackPasseTour callbackPasseTour;

    public CadranReserve(List<Pion> listePionsReserve, CallbackPionExplorateur callbackPionExplorateur, CallbackPasseTour callbackPasseTour) {
        this.callbackPionExplorateur = callbackPionExplorateur;
        this.callbackPasseTour = callbackPasseTour;
        setPreferredSize(new Dimension(WIDTH_CADRANT, HEIGHT));
        setBackground(new Color(222, 221, 197));
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
    }

    public void afficherJoueurEnCours(Joueur joueurEnCours, boolean avecSousEtape, EtapeJeu etapeJeu) {
        JPanel conteneurNomJoueur = new JPanel();
        conteneurNomJoueur.setLayout(new GridLayout(avecSousEtape ? 5 : 4, 1));
        if (avecSousEtape) {
            JLabel etapeLabel = new JLabel("Étape: " + etapeJeu);
            etapeLabel.setFont(new Font("CALIBRI", Font.PLAIN, 14));
            etapeLabel.setHorizontalAlignment(JLabel.CENTER);
            conteneurNomJoueur.add(etapeLabel);
        }
        JLabel joueurEnCoursLabel = new JLabel("JOUEUR EN COURS:");
        joueurEnCoursLabel.setFont(new Font("CALIBRI", Font.PLAIN, 14));
        joueurEnCoursLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel joueurLabel = new JLabel(joueurEnCours.getPseudo());
        joueurLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        joueurLabel.setHorizontalAlignment(JLabel.CENTER);
        Color couleurPion;
        switch (joueurEnCours.getCouleurPion()) {
            case ROUGE -> couleurPion = Color.RED;
            case JAUNE -> couleurPion = Color.YELLOW;
            case VERT -> couleurPion = Color.GREEN;
            case BLEU -> couleurPion = Color.BLUE;
            default -> couleurPion = Color.WHITE;
        }

        JLabel pionsSauvesLabel = new JLabel(avecSousEtape ? joueurEnCours.getExplorateursSauves().size() + " pion(s) sauvé(s)" : "");
        pionsSauvesLabel.setHorizontalAlignment(JLabel.CENTER);
        JButton sauterButton = new JButton("Passer tour");
        sauterButton.addActionListener(e -> callbackPasseTour.onPassantTour());
        joueurLabel.setForeground(couleurPion);
        conteneurNomJoueur.add(joueurEnCoursLabel);
        conteneurNomJoueur.add(avecSousEtape ? sauterButton : new JLabel());
        conteneurNomJoueur.add(joueurLabel);
        conteneurNomJoueur.add(pionsSauvesLabel);
        add(conteneurNomJoueur, BorderLayout.SOUTH);
    }

    public void afficherPionsReserve(List<Pion> pions) {
        JPanel pionPanel = new JPanel();
        pionPanel.setLayout(new GridLayout(Math.round(Integer.valueOf(pions.size()).floatValue() / 2), 2));
        String pathPion = "";
        for (Pion pion : pions) {
            if (pion instanceof Bateau) {
                pathPion = ConstanteImage.PION_BATEAU;
            } else if (pion instanceof Creature) {
                switch ((Creature) pion) {
                    case REQUIN -> pathPion = ConstanteImage.PION_REQUIN;
                    case BALEINE -> pathPion = ConstanteImage.PION_BALEINE;
                    case SERPENT -> pathPion = ConstanteImage.PION_SERPENT;
                }
            }
            JLabel imagePion = new JLabel();
            imagePion.setHorizontalAlignment(JLabel.CENTER);
            imagePion.setBackground(new Color(222, 221, 197));
            imagePion.setIcon(new ImageIcon(new ImageIcon(pathPion).getImage().getScaledInstance(WIDTH_BOX / 2, 2 * (HEIGHT_BOX - 6) / 3, Image.SCALE_DEFAULT)));
            pionPanel.add(imagePion);
        }

        pionPanel.setOpaque(false);
        add(pionPanel, BorderLayout.NORTH);
    }

    public void afficherPionsNonJoues(Joueur joueur) {
        JPanel pionsNonJouesPanel = new JPanel();
        JPanel conteneurPionNonJoue = new JPanel();
        List<PionExplorateur> explorateurNonJoues = joueur.getExplorateursNonJoues();
        GridLayout gridLayout = new GridLayout(Math.round(Integer.valueOf(explorateurNonJoues.size()).floatValue() / 2), 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        conteneurPionNonJoue.setLayout(new FlowLayout());
        JLabel text = new JLabel("<html>Choisir un pion et placer le sur la carte</html>");
        text.setPreferredSize(new Dimension(130, 50));
        conteneurPionNonJoue.add(text);
        conteneurPionNonJoue.add(pionsNonJouesPanel);
        pionsNonJouesPanel.setLayout(gridLayout);
        Color couleurPion;
        switch (joueur.getCouleurPion()) {
            case ROUGE -> couleurPion = Color.RED;
            case JAUNE -> couleurPion = Color.YELLOW;
            case VERT -> couleurPion = Color.GREEN;
            case BLEU -> couleurPion = Color.BLUE;
            default -> couleurPion = Color.WHITE;
        }
        for (PionExplorateur pionExplorateur : explorateurNonJoues) {
            JButton numero = new JButton(String.valueOf(pionExplorateur.getNumero()));
            numero.addActionListener(e -> callbackPionExplorateur.enSelectionDePion(pionExplorateur));
            numero.setPreferredSize(new Dimension(60, 30));
            numero.setBackground(couleurPion);
            numero.setOpaque(true);
            numero.setBorderPainted(false);
            pionsNonJouesPanel.add(numero);
        }
        add(conteneurPionNonJoue, BorderLayout.CENTER);
    }
}
