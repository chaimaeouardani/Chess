package Jeu;
/** Le pion du jeu d'échecs
 * @author Alexandre
 */
public class Pion extends Piece {
	
	/** La valeur de la pièce pour calculer le score d'un plateau */
	private final double[][][] evalPion = {
			{ // Biais à ajouter à un pion blanc en fonction de sa position
				{50.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0},
				{ 4.0,  4.0,  4.0,  4.0,  4.0,  4.0,  4.0,  4.0},
				{ 1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0,  1.0},
				{ 0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5,  0.5},
				{ 0.0,  0.0,  0.0,  2.0,  2.0,  0.0,  0.0,  0.0},
				{ 0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5,  0.5},
				{ 0.5,  1.0, 1.0,  -2.0, -2.0,  1.0,  1.0,  0.5},
				{ 0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0}
			},
			{ // Biais à ajouter à un pion noir en fonction de sa position
				{ 0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
				{ 0.5,  1.0, 1.0,  -2.0, -2.0,  1.0,  1.0,  0.5},
				{ 0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5,  0.5},
				{ 0.0,  0.0,  0.0,  2.0,  2.0,  0.0,  0.0,  0.0},
				{ 0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5,  0.5},
				{ 1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0,  1.0},
				{ 4.0,  4.0,  4.0,  4.0,  4.0,  4.0,  4.0,  4.0},
				{50.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0}
			}
	};


	public Pion(int x, int y, Joueur proprietaire, Echiquier plateau) {
		super(x, y, proprietaire, plateau);
		valeur=10;
		eval = evalPion[proprietaire.getCouleur().ordinal()];
		code = 'p';
	}
	
	@Override
	public String getSAN() {
		return "";
	}

	@Override
	public boolean deplacementValide(int nx, int ny) {
		boolean avance;
		boolean diagonal;
		boolean caseLibre;
		boolean mange;

		int sens = this.getProprio().getCouleur().equals(Couleur.BLANC) ? -1 : 1;
		
		// Déplacement vertical
		boolean coup1 = (sens == -1 && x == 7) || (sens == 1 && x == 2);
		avance = ny == y && 
				((nx == x + sens * 2  && coup1 && echiquier.caselibre(x + sens, ny)) || nx == x + sens);
	
		// Déplacement diagonal
		diagonal = nx == super.getX() + sens && 
				(ny == super.getY() + 1 || ny == super.getY() - 1);
		
		
		
		caseLibre = echiquier.caselibre(nx, ny);
		mange = !caseLibre && this.getProprio() != echiquier.getJoueur(nx, ny);
		return 	(avance && caseLibre) || (diagonal && mange)
				|| peutPrendreEnPassant(nx, ny);
	}
	
	@Override
	public boolean peutPrendreEnPassant(int nx, int ny) {
		boolean enPassant;
		boolean diagonal;
		int sens = this.getProprio().getCouleur().equals(Couleur.BLANC) ? -1 : 1;
		diagonal = nx == super.getX() + sens && 
				(ny == super.getY() + 1 || ny == super.getY() - 1);
		enPassant = diagonal && 
				echiquier.cibleEnPassant != null &&
				nx == echiquier.cibleEnPassant.getX() &&
				ny == echiquier.cibleEnPassant.getY();
		return enPassant;
	}
	
	@Override
	public boolean peutPrendreEnPassant(Case caseFin) {
		return peutPrendreEnPassant(caseFin.getX(), caseFin.getY());
	}
	
	@Override
	public void deplacer(int nx, int ny) throws CoupInvalideException {
		int sens = this.getProprio().getCouleur().equals(Couleur.BLANC) ? -1 : 1;
		if (peutPrendreEnPassant(nx, ny)) {
			echiquier.priseEnPassant(nx - sens, ny);
		}
		int oldx = x;
		super.deplacer(nx, ny);
		if (nx == oldx + sens * 2) {
			echiquier.cibleEnPassant = new Case(oldx + sens, y);
		}
	}
	
	@Override
	public Piece copy(Echiquier e) {
		return new Pion(this.getX(), this.getY(), getProprio(), e);
	}

}
