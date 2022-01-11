package Jeu;

import java.util.List;
/** Le roi du jeu d'échecs
 * @author Chaimae, Youssef, Alexandre, Briag
 */
public class Roi extends Piece{

	/** La valeur de la pièce pour calculer le score d'un plateau */
	private final double[][][] evalRoi = {
			{// Biais à ajouter à un roi blanc en fonction de sa position
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
				{-2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0},
				{-1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
				{ 2.0,  2.0,  0.0,  0.0,  0.0,  0.0,  2.0,  2.0},
				{ 2.0,  3.0,  1.0,  0.0,  0.0,  1.0,  3.0,  2.0}
			},
			{// Biais à ajouter à un roi noir en fonction de sa position
				{ 2.0,  3.0,  1.0,  0.0,  0.0,  1.0,  3.0,  2.0},
				{ 2.0,  2.0,  0.0,  0.0,  0.0,  0.0,  2.0,  2.0},
				{-1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
				{-2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0},
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
				{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0}
			}
	};


	public Roi(int x, int y, Joueur proprietaire, Echiquier plateau) {
		super(x, y, proprietaire, plateau);
		valeur=900;
		eval = evalRoi[proprietaire.getCouleur().ordinal()];
		code = 'k';
	}

	@Override
	public boolean deplacementValide(int nx, int ny) {
		assert(nx > 0);
		assert(ny > 0);
		
		if (roqueValide(nx, ny)) {
			return true;
		}
		
		boolean libre =  echiquier.caselibre(nx,ny);
		boolean chemin_valide = false;
				
		if (nx > 8 || ny > 8) {
			
			return false;
		}
		
		//d�placement vertical
		else if (nx == this.getX()) {
			if (ny == this.getY() + 1 || ny == this.getY() - 1) {
				chemin_valide = true;				
			} 
		}
		//d�placement horizontal
		else if (ny == this.getY()) {
			//System.out.print("elsif");
			if (nx == this.getX() + 1 || nx == this.getX() - 1) {
				
				chemin_valide = true;
			//} else if(plateau.rockValide(this, nx, ny)) {
			//	chemin_valide = true;
			//	
			}
		}
		//d�placement diagonal
		else if (nx == this.getX() + 1) {
			if (ny == this.getY()+1 || ny == this.getY()-1) {
				
				chemin_valide = true;
			}
		}
		
		else if (nx == this.getX() - 1) {
			if (ny == this.getY()+1 || ny == this.getY()-1) {
				
				chemin_valide = true;
			}
		}
		 else {
			
			return false;
		}	
		return (chemin_valide && (libre || this.getProprio() != echiquier.getJoueur(nx, ny))) ;
	}
	
	@Override
	public Piece copy(Echiquier e) {
		return new Roi(this.getX(), this.getY(), getProprio(), e);
	}
	
	/** Permet de déterminer si le roi est en échec
	 * @param echiquier L'échiquier sur lequel on veut tester
	 * @param pieces La liste de pièce que l'on veut utiliser
	 * @return Vrai si le roi est en échec sur cet échiquier
	 */
	public boolean estEchec(Echiquier echiquier, List<Piece> pieces) {
		for (Piece piece: pieces) {
			if (pieceEchec(piece)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean roqueValide(int nx, int ny) {
		if ((nx != x) || (nx != 1 && nx != 8) || (ny != 3 && ny != 7) || y != 5) {
			return false;
		} else if (ny == 3) {
			return grandRoqueValide() && !estEchec(echiquier, echiquier.getListePieces());
		} else {
			return petitRoqueValide() && !estEchec(echiquier, echiquier.getListePieces());
		}
	}
	
	/** Permet de déterminer si le grand roque est valide
	 * @return vrai si le grand roque est valide, faux sinon
	 */
	public boolean grandRoqueValide() {
		int nx = x;
		int ny = 3;
		int yTour = 1;
		if (echiquier.caselibre(nx, yTour)) {
			return false;
		}
		if (getCouleur().equals(Couleur.BLANC)) {
			if (!echiquier.getQ()) {
				return false;
			}
		} else {
			if (!echiquier.getq()) {
				return false;
			}
		}
		if (getProprio() != echiquier.getJoueur(nx, yTour)) {
			return false;
		} // Arrivé ici on sait qu'il y a forcément une tour qui n'a pas bougée en nx, yTour 
		for (int i=ny; i < y; i++) {
			if (!echiquier.caselibre(nx, i) || seMetEnEchec(nx, i)) {
				return false;
			}
		}
		return !echiquier.roqueMetEnEchec(this, nx, yTour);
	}
	
	/** Permet de déterminer si le petit roque est valide
	 * @return vrai si le petit roque est valide, faux sinon
	 */
	public boolean petitRoqueValide() {
		int nx = x;
		int ny = 7;
		int yTour = 8;
		if (echiquier.caselibre(nx, yTour)) {
			return false;
		}
		if (getCouleur().equals(Couleur.BLANC)) {
			if (!echiquier.getK()) {
				return false;
			}
		} else {
			if (!echiquier.getk()) {
				return false;
			}
		}
		if (getProprio() != echiquier.getJoueur(nx, yTour)) {
			return false;
		} // Ici on sait qu'il y a forcément une tour qui n'a pas bougée en nx, yTour
		for (int i=y+1; i <= ny; i++) {
			if (!echiquier.caselibre(nx, i) || seMetEnEchec(nx, i)) {
				return false;
			}
		}
		return !echiquier.roqueMetEnEchec(this, nx, yTour);
	}
	
	/** Renvoie vrai si le roque du roi avec la tour met le joueur en échec.
	 * @param t la tour en question
	 * @return Vrai si le roque met en échec
	 */
	public boolean roqueMetEnEchec(Tour t) {
		int yRoi = this.y;
		int yTour = t.getY();
		boolean oldK = echiquier.getK();
		boolean oldQ = echiquier.getQ();
		boolean oldk = echiquier.getk();
		boolean oldq = echiquier.getq();
		echiquier.actualiserRoque(this);
		if (yRoi > yTour) { //Grand roque
			y -=2;
		} else { // Petit roque
			y +=2;
		}
		boolean echec = t.roqueMetEnEchec(yRoi);
		y = yRoi;
		echiquier.K = oldK;
		echiquier.Q = oldQ;
		echiquier.k = oldk;
		echiquier.q = oldq;
		return echec;
	}
	
	/** Permet de déterminer si une pièce p met en échec le roi.
	 * @param p La pièce que l'on test
	 * @return Vrai si p met le roi en échec
	 */
	public boolean pieceEchec(Piece p) {
		if (p == null) {
			return false;
		}
		if (!p.getProprio().getCouleur().equals(this.getProprio().getCouleur()) && p.deplacementValide(this.getX(),this.getY())) {
			return true;
		} else {
			return false;
		}
	}
	
	/** Déplace le roi pour un grand roque. */
	protected void grandRoque() {
		y -= 2;
	}
	
	/** Déplace le roi pour un petit roque. */
	protected void petitRoque() {
		y += 2;
	}
}