package view;

import models.Couleur;
import models.Position;
import models.pions.Bateau;
import models.pions.Creature;
import models.pions.Pion;
import models.pions.PionExplorateur;
import models.tuiles.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TheIsland extends JPanel {

    private static final long serialVersionUID = 1L;
    final static int WIDTH = 840;
    final static int HEIGHT = 735;
    final static int WIDTH_BOX = 58;
    final static int HEIGHT_BOX = 73;
    final static int PRECISION = 3;
    final static int W2 = WIDTH / 2;
    final static int H2 = HEIGHT / 2;

    private List<CaseJeu> casesJeux;

    public TheIsland() {
        this.casesJeux = new ArrayList<>();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public CaseJeu caseJeuByPosition(Position position) {
        for (CaseJeu caseJeu : casesJeux) {
            if (caseJeu.getPosition().equals(position)) return caseJeu;
        }
        return null;
    }

    public CaseJeu caseJeuEnPosition(Position position) {
        for (CaseJeu caseJeux : this.casesJeux) {
            if (caseJeux.getPosition().equals(position)) {
                return caseJeux;
            }
        }
        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        try {
            Image backgroundImage = ImageIO.read(new File(ConstanteImage.PLATEAU));
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ajouterCase(CaseJeu caseJeu) {
        casesJeux.add(caseJeu);
        add(caseJeu);
    }

    public void supprimerTuile(JPanel panel) {
        this.remove(panel);
    }
}
