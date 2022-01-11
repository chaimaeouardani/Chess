package Jeu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ProfilePanel extends JPanel {
	
    @SuppressWarnings("unused")
	private Echiquier echiquier;
    private Couleur Couleur;
    private Joueur j;

    public ProfilePanel(Echiquier echiquier, Couleur couleur){
        super();
        super.setLayout(new BorderLayout());
        this.echiquier = echiquier;
        super.setBorder(EchecSwing.blackline);
        super.setPreferredSize(new Dimension(250, 300));
        this.Couleur = couleur;
        if (Couleur == Couleur.BLANC){
            j = echiquier.getJoueurBlanc();
        } else{
            j = echiquier.getJoueurNoir();
        }
        String lien = "src/Jeu/" + Couleur.toString().toLowerCase() + ".png";
        //JLabel image = new JLabel(new ImageIcon(lien));
        JLabel info = new JLabel("    Joueur " + j.getNom() + " : " + couleur);
        info.setIcon(new ImageIcon(lien) );
        info.setFont(new Font("Arial", Font.BOLD , 14));
        //super.add(image, BorderLayout.EAST);
        super.add(info, BorderLayout.NORTH);

    }
}
