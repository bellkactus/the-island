/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import models.Couleur;
import models.Joueur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static view.ConstanteImage.PATH_LOCAL;


/**
 * @author gtari
 */
public final class MenuJeu extends JFrame {

    private static final String PSEUDO_PLACEHOLDER = "Saisir votre pseudo";

    public JButton getBoutonBleu() {
        return boutonBleu;
    }

    public JButton getBoutonVert() {
        return boutonVert;
    }

    public JButton getBoutonSubmit() {
        return boutonSubmit;
    }

    public JTextField getInputPseudo() {
        return inputPseudo;
    }

    public JButton getRed() {
        return red;
    }


    public JButton getYellow() {
        return yellow;
    }

    public void setYellow(JButton yellow) {
        this.yellow = yellow;
    }

    /**
     * Creates new form Menu
     */
    public MenuJeu() {
        intialiserComposants();
        ajouterPlaceholder(inputPseudo);
    }

    public void intialiserComposants() {
        boutonSubmit = new JButton();
        inputPseudo = new JTextField();
        jLabelPseudoName = new JLabel();
        LabelLogo = new JLabel();
        yellow = new JButton();
        boutonBleu = new JButton();
        boutonVert = new JButton();
        red = new JButton();
        jLabelChoixCouleur = new JLabel();
        jLabelBackground = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(380, 460));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }

            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        //getContentPane().setLayout(new org.lib.awtextra.AbsoluteLayout());
        getContentPane().setLayout(null);
        boutonSubmit.setBackground(Color.BLUE);
        boutonSubmit.setOpaque(true);
        boutonSubmit.setFont(new Font("Segoe UI", 1, 12)); // NOI18N
        boutonSubmit.setForeground(new Color(255, 255, 255));
        boutonSubmit.setText("Suivant");
        boutonSubmit.setBounds(160, 330, 80, 30);
        boutonSubmit.setBorder(null);
        boutonSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        //jButton1.addActionListener(this);
        getContentPane().add(boutonSubmit);

        inputPseudo.setForeground(new Color(204, 204, 204));
        inputPseudo.setText(PSEUDO_PLACEHOLDER);
        inputPseudo.setToolTipText("");
        inputPseudo.setBounds(70, 160, 250, 30);
        inputPseudo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldPseudoNameFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldPseudoNameFocusLost(evt);
            }
        });
        inputPseudo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jTextFieldPseudoNameActionPerformed(evt);
            }
        });
        //getContentPane().add(jTextFieldPseudoName, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, 250, 30));
        getContentPane().add(inputPseudo);

        inputPseudo.getAccessibleContext().setAccessibleName("jTextField1");

        jLabelPseudoName.setFont(new Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabelPseudoName.setForeground(new Color(242, 242, 242));
        jLabelPseudoName.setText("            Tapez votre PseudoName ");
        jLabelPseudoName.setToolTipText("");
        jLabelPseudoName.setBounds(70, 120, 290, 80);
        jLabelPseudoName.setVerticalAlignment(SwingConstants.TOP);
        jLabelPseudoName.setAutoscrolls(true);
        getContentPane().add(jLabelPseudoName);

        LabelLogo.setIcon(new ImageIcon(PATH_LOCAL + "logo_the_island.png")); // NOI18N
        LabelLogo.setAlignmentX(2.0F);
        LabelLogo.setBounds(110, -10, 320, 140);
        LabelLogo.setFocusable(false);
        LabelLogo.setPreferredSize(new Dimension(500, 380));
        getContentPane().add(LabelLogo);

        yellow.setBackground(new Color(255, 255, 51));
        yellow.setOpaque(true);
        yellow.setFont(new Font("Segoe UI", 1, 10)); // NOI18N
        yellow.setText("Jaune");
        yellow.setBounds(70, 250, 50, 30);
        yellow.setBorder(null);
        yellow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                yellowActionPerformed(evt);
            }
        });
        getContentPane().add(yellow);

        boutonBleu.setBackground(new Color(0, 102, 255));
        boutonBleu.setOpaque(true);
        boutonBleu.setFont(new Font("Segoe UI", 1, 10)); // NOI18N
        boutonBleu.setText("Bleu");
        boutonBleu.setToolTipText("");
        boutonBleu.setBounds(135, 250, 50, 30);
        boutonBleu.setBorder(null);
        boutonBleu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                blueActionPerformed(evt);
            }
        });
        getContentPane().add(boutonBleu);

        boutonVert.setBackground(new Color(0, 153, 0));
        boutonVert.setOpaque(true);
        boutonVert.setFont(new Font("Segoe UI", 1, 10)); // NOI18N
        boutonVert.setText("Vert");
        boutonVert.setBounds(200, 250, 50, 30);
        boutonVert.setBorder(null);
        boutonVert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                greenActionPerformed(evt);
            }
        });
        getContentPane().add(boutonVert);

        red.setBackground(new Color(255, 0, 0));
        red.setOpaque(true);
        red.setFont(new Font("Segoe UI", 1, 10)); // NOI18N
        red.setText("Rouge");
        red.setBounds(265, 250, 50, 30);
        red.setBorder(null);
        red.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                redActionPerformed(evt);
            }
        });
        //getContentPane().add(red, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 250, 50, 30));
        getContentPane().add(red);

        jLabelChoixCouleur.setFont(new Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabelChoixCouleur.setForeground(new Color(255, 255, 255));
        jLabelChoixCouleur.setText("              Choisir votre couleur");
        jLabelChoixCouleur.setBounds(60, 210, 290, 100);
        jLabelChoixCouleur.setToolTipText("");
        jLabelChoixCouleur.setVerticalAlignment(SwingConstants.TOP);
        getContentPane().add(jLabelChoixCouleur);

        jLabelBackground.setIcon(new ImageIcon(PATH_LOCAL + "background_menu.png")); // NOI18N
        jLabelBackground.setBounds(0, 0, 380, 440);
        //getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, 440));
        getContentPane().add(jLabelBackground);
        pack();
    }// </editor-fold>//GEN-END:initComponents


    public void ajouterPlaceholder(JTextField jTextFieldPseudoName) {
        Font font = jTextFieldPseudoName.getFont();
        font = font.deriveFont(Font.ITALIC);
        jTextFieldPseudoName.setFont(font);
        jTextFieldPseudoName.setForeground(Color.gray);// couleur de taper votre pseudo


    }

    public void RemovePlaceHolder(JTextField jTextFieldPseudoName) {
        Font font = jTextFieldPseudoName.getFont();
        font = font.deriveFont(Font.BOLD);
        jTextFieldPseudoName.setFont(font);
        jTextFieldPseudoName.setForeground(Color.black);// couleur de taper votre pseudo


    }

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        this.requestFocusInWindow();

    }//GEN-LAST:event_formWindowGainedFocus

    int nbJoueurs = 0;

    public void jButton1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        PrintWriter pseudoFile;
        pseudoFile = null;
        boolean joueurValid = false;
        Couleur couleur = null;
        //String pseudoFile =jTextFieldPseudoName.getText();
        try {
            pseudoFile = new PrintWriter("PseudoName.txt");
            pseudoFile.write(inputPseudo.getText());
            pseudoFile.flush();
            pseudoFile.close();
            if (this.isBluePressed() && !listeCouleurs.contains(Couleur.BLEU)) {
                couleur = Couleur.BLEU;
                boutonBleu.setEnabled(false);
                boutonBleu.setBackground(Color.gray);
            } else if (this.isGreenPressed() && !listeCouleurs.contains(Couleur.VERT)) {
                couleur = Couleur.VERT;
                boutonVert.setEnabled(false);
                boutonVert.setBackground(Color.gray);
            } else if (this.isRedPressed() && !listeCouleurs.contains(Couleur.ROUGE)) {
                couleur = Couleur.ROUGE;
                red.setEnabled(false);
                red.setBackground(Color.gray);
            } else if (this.isYellowPressed() && !listeCouleurs.contains(Couleur.JAUNE)) {
                couleur = Couleur.JAUNE;
                yellow.setEnabled(false);
                yellow.setBackground(Color.gray);
            }
            if (couleur == null) {
                JOptionPane.showMessageDialog(this.rootPane, "Choisir une couleur");
            } else if (inputPseudo.getText().equals(PSEUDO_PLACEHOLDER)) {
                JOptionPane.showMessageDialog(this.rootPane, "Choisir un pseudo");
            } else {
                listeCouleurs.add(couleur);
                joueurValid = true;
            }
            if (joueurValid) {
                Joueur joueur = new Joueur(getInputPseudo().getText(), couleur);
                listeJoueurs.add(joueur);
                System.out.println("Joueur : " + joueur.getPseudo() + " Couleur : " + joueur.getCouleurPion());
                System.out.println(listeJoueurs.size());
                inputPseudo.setText(PSEUDO_PLACEHOLDER);

                if (listeJoueurs.size() == 4) {
                    this.setValid(true);
                    MainJeu mainJeu = new MainJeu(listeJoueurs);
                    mainJeu.setVisible(true);
                    JFrame frame = new JFrame();
                    frame.setContentPane(mainJeu);
                    frame.setResizable(false);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    this.add(frame);
                    JOptionPane.showMessageDialog(this.rootPane,"Have a good gaaaame !!!!");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("" + e);
        }

    }

    private void jTextFieldPseudoNameActionPerformed(ActionEvent evt) {
        RemovePlaceHolder(inputPseudo);
        ajouterPlaceholder(inputPseudo);
    }

    private void jTextFieldPseudoNameFocusLost(java.awt.event.FocusEvent evt) {
        if (inputPseudo.getText().length() == 0) {
            ajouterPlaceholder(inputPseudo);
            inputPseudo.setText(PSEUDO_PLACEHOLDER);

        }
    }

    private void jTextFieldPseudoNameFocusGained(java.awt.event.FocusEvent evt) {
        if (inputPseudo.getText().equals(PSEUDO_PLACEHOLDER)) {
            inputPseudo.setText(null);
            inputPseudo.requestFocus();
            RemovePlaceHolder(inputPseudo);
        }
    }


    private void yellowActionPerformed(ActionEvent evt) {//GEN-FIRST:event_yellowActionPerformed
        //blue.setEnabled(false);
        //green.setEnabled(false);
        //red.setEnabled(false);
        //yellow.setEnabled(false);
        setYellowPressed(true);
        setBluePressed(false);
        setRedPressed(false);
        setGreenPressed(false);
    }//GEN-LAST:event_yellowActionPerformed

    private void blueActionPerformed(ActionEvent evt) {//GEN-FIRST:event_blueActionPerformed
        //yellow.setEnabled(false);
        //green.setEnabled(false);
        //red.setEnabled(false);
        //blue.setEnabled(false);
        setBluePressed(true);
        setRedPressed(false);
        setGreenPressed(false);
        setYellowPressed(false);
    }//GEN-LAST:event_blueActionPerformed

    private void greenActionPerformed(ActionEvent evt) {
        setGreenPressed(true);
        setBluePressed(false);
        setYellowPressed(false);
        setRedPressed(false);
    }

    private void redActionPerformed(ActionEvent evt) {//GEN-FIRST:event_redActionPerformed
        setRedPressed(true);
        setBluePressed(false);
        setGreenPressed(false);
        setYellowPressed(false);
    }

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestBouttonsActions().setVisible(true);
            }
        });
    }*/


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel LabelLogo;
    private JButton boutonBleu;
    private JButton boutonVert;
    private JButton boutonSubmit;
    private JLabel jLabelBackground;
    private JLabel jLabelChoixCouleur;
    private JLabel jLabelPseudoName;
    private JTextField inputPseudo;
    private JButton red;
    private JButton yellow;
    private boolean redPressed;
    private boolean greenPressed;
    private boolean bluePressed;
    private boolean yellowPressed;
    private boolean isValid;

    @Override
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isRedPressed() {
        return redPressed;
    }

    public void setRedPressed(boolean redPressed) {
        this.redPressed = redPressed;
    }

    public boolean isGreenPressed() {
        return greenPressed;
    }

    public void setGreenPressed(boolean greenPressed) {
        this.greenPressed = greenPressed;
    }

    public boolean isBluePressed() {
        return bluePressed;
    }

    public void setBluePressed(boolean bluePressed) {
        this.bluePressed = bluePressed;
    }

    public boolean isYellowPressed() {
        return yellowPressed;
    }

    public void setYellowPressed(boolean yellowPressed) {
        this.yellowPressed = yellowPressed;
    }

    private ArrayList<Couleur> listeCouleurs = new ArrayList<>();
    private ArrayList<Joueur> listeJoueurs = new ArrayList<>();


}
