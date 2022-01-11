package Jeu;
import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.Image;

public class PiecePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Echiquier echiquier;
	int x; // nombre de la ligne de la piece dans le plateau
	int y; // nombre de la colonne de la piece dans le plateau
	EchecSwing jeu;
	
	public PiecePanel(int x, int y, Echiquier echiquier, EchecSwing j) {
		super(new GridBagLayout());
		this.setBorder(EchecSwing.blackline);
		this.x = x;
		this.y = y;
		this.echiquier = echiquier;
		jeu = j;
		dessinerCase();
		addMouseListener(new MouseDeplacement(this));
}
	
	public void dessinerCase() {
		/** supprimer l'image precedente */
		this.removeAll();
		
		//mettre à jour la couleur d'arriere plan
		if ((x % 2 == 0 && y % 2 == 0) || (x % 2 != 0 && y % 2 != 0)) {		
			this.setBackground(EchecSwing.couleurBlanc);					
		}else {
			this.setBackground(EchecSwing.couleurNoir);
		}
		
			
		//choisir une nouvelle image si la case est occup�e
		String lienImg = "img/";
		if (! echiquier.caselibre(this.x, this.y)) {
			Piece piece = echiquier.getPiece(this.x, this.y);
			
			// obtenir la couleur de la piece
			if (piece.getProprio().getCouleur() == Couleur.NOIR) {
				lienImg += "noir/";
			}else {
				lienImg += "blanc/";
			}
			// completer le nom du lien de l'image
			lienImg += piece.getNomPiece().toLowerCase() + ".png";
			//final BufferedImage image = ImageIO.read(new File(lienImg));
			java.net.URL imageURL = getClass().getResource(lienImg);
			ImageIcon im = new ImageIcon(imageURL);
			Image imageScaled = im.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
			add(new JLabel(new ImageIcon(imageScaled)));
		}
		
	}
	
	public Echiquier getEchiquier() {
		return echiquier;
	}
	
	public int leX() {
		return x;
	}
	
	public int leY() {
		return y;
	}
}

