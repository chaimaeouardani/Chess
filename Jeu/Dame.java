package Jeu;

/** La dame du jeu d'échecs.
 * @author Alexandre
 */
public class Dame extends Piece {
	
	/** La valeur de la pièce pour calculer le score d'un plateau */
	private final double[][][] evalDame = {
			{// Biais à ajouter à une dame blanche en fonction de sa position
				{-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
				{-1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
				{-1.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
				{-0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
				{-0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
				{-1.0,  0.5,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
				{-1.0,  0.0,  0.5,  0.0,  0.0,  0.0,  0.0, -1.0},
				{-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
			}, 
			{// Biais à ajouter à une dame noire en fonction de sa position
				{-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
				{-1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
				{-1.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
				{-0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
				{-0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
				{-1.0,  0.5,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
				{-1.0,  0.0,  0.5,  0.0,  0.0,  0.0,  0.0, -1.0},
				{-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
			}
	};

	public Dame(int x, int y, Joueur proprietaire, Echiquier plateau) {
		super(x, y, proprietaire, plateau);
		valeur=90;
		eval = evalDame[proprietaire.getCouleur().ordinal()];
		code = 'q';
	}

	@Override
	public boolean deplacementValide(int nx, int ny) {
		return deplacementTourValide(nx, ny) || deplacementFouValide(nx, ny);
	}
	
	private boolean deplacementTourValide(int nx, int ny) {
		assert(nx > 0);
		assert(ny > 0);
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
	
	private boolean deplacementFouValide(int nx, int ny){
		int dx = nx-getX(); int dy = ny-getY();
		
		//On vérifie les conditions de déplacement
		if (dx == 0)
			return false;
		if (Math.abs(dx)!=Math.abs(dy))
			return false;
		
		
		//On vérifie le chemin entre la position actuelle et la destination
		for (int i = 1; i < Math.abs(dx); i++) {
			boolean libre = this.echiquier.caselibre(getX() + (dx > 0 ? i : -i), getY() + (dy > 0 ? i : -i));
			if (!libre)
				return false;
		}
		
		//On vérifie la destination
		Piece p = this.echiquier.getPiece(getX() + dx, getY() + dy);
		if (p != null && p.getProprio().getCouleur() == getProprio().getCouleur())
			return false; 
		
		return true;
	}
	
	@Override
	public Piece copy(Echiquier e) {
		return new Dame(this.getX(), this.getY(), getProprio(), e);
	}
 
}
