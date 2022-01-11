package Jeu;

import java.util.ArrayList;
/** La tour du jeu d'échecs
 * @author ???, Alexandre
 */

public class Tour extends Piece {
	
	/** La valeur de la pièce pour calculer le score d'un plateau */
	private final double[][][] evalTour = {
			{// Biais à ajouter à une tour blanche en fonction de sa position
				{ 0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
				{ 0.5,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{ 0.0,  0.0,  0.0,  0.5,  0.5,  0.0,  0.0,  0.0}
			},
			{// Biais à ajouter à une tour noire en fonction de sa position
				{ 0.0,  0.0,  0.0,  0.5,  0.5,  0.0,  0.0,  0.0},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
				{ 0.5,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  0.5},
				{ 0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0}
			}
	};
	
	public Tour(int x, int y, Joueur proprietaire, Echiquier plateau) {
		super(x, y, proprietaire, plateau);
		valeur=50;
		eval = evalTour[proprietaire.getCouleur().ordinal()];
		code = 'r';
	}
	
	@Override 
	public boolean deplacementValide(int nx, int ny) {
		assert(nx > 0);
		assert(ny > 0);

		if (roqueValide(nx, ny)) {
			return true;
		}
		// dx et dy correspondent au déplacement
		int dx = nx - this.getX();
		int dy = ny - this.getY();
		// On regarde si le mouvement est théoriquement possible
		if (dx != 0 && dy != 0) {
			return false;
		} else if (dx == 0 & dy == 0) {
			return false;
		} else if (dy > 8 | dy < -8) {
			return false;
		} else if (dx > 8 | dx < -8) {
			return false;
		}
		// Regardons maintenant si le mouvement est en effet possible
		Boolean occupe = false;
		int deplacement;
		if (dx == 0) {
			deplacement = dy;
		} else {
			deplacement = dx;
		}
		// On vérifie si les cases intermédiaires sont occupées
		for (int i = 1; i < (deplacement > 0 ? deplacement : -deplacement); i++) {
			if (dx == 0) {
				occupe = !this.echiquier.caselibre(nx, this.getY() + (deplacement>0?i:-i));
			} else {
				occupe = !this.echiquier.caselibre(this.getX() + (deplacement>0?i:-i), ny);
			}
			if (occupe) {
				return false;
			}
		}
		// On vérifie maintenant si la case où on veut aller 
		// est occupée, si elle est occupée on vérifie si c'est par
		// une pièce alliée ou ennemie
		occupe = !this.echiquier.caselibre(nx, ny);
		Boolean allie = false;
		if (occupe) {
			allie = (this.echiquier.getJoueur(nx, ny) == this.getProprio());
		}
		return !allie;
	}
	
	@Override
	public Piece copy(Echiquier e) {
		return new Tour(this.getX(), this.getY(), getProprio(), e);
	}
	
	/** Renvoie vrai si le roque du roi avec la tour met le joueur en échec.
	 * @param yRoi la colonne du roi en question
	 * @return Vrai si le roque met en échec
	 */
	public boolean roqueMetEnEchec(int yRoi) {
		int yTour = y;
		boolean oldK = echiquier.getK();
		boolean oldQ = echiquier.getQ();
		boolean oldk = echiquier.getk();
		boolean oldq = echiquier.getq();
		echiquier.actualiserRoque(this);
		if (yTour < yRoi) {// Grand roque
			y += 3;
		} else { // Petit roque
			y -= 2;
		}
		boolean echec = Arbitre.joueurEchec(getProprio(), echiquier, new ArrayList<Piece>(echiquier.getListePieces()));
		y = yTour;
		echiquier.K = oldK;
		echiquier.Q = oldQ;
		echiquier.k = oldk;
		echiquier.q = oldq;
		return echec;
	}

	/** Déplace la tour pour un grand roque. */
	protected void grandRoque() {
		y += 3;
	}
	
	/** Déplace la tour pour un petit roque. */
	protected void petitRoque() {
		y -= 2;
	}
}