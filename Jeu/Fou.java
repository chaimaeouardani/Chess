package Jeu;

/** Le fou du jeu d'échec
 * @author ???, Alexandre
 */
public class Fou extends Piece{
	
	/** La valeur de la pièce pour calculer le score d'un plateau */
	private final double[][][] evalFou = {
			{// Biais à ajouter à un fou blanc en fonction de sa position
				{-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
				{-1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
				{-1.0,  0.0,  0.5,  1.0,  1.0,  0.5,  0.0, -1.0},
				{-1.0,  0.5,  0.5,  1.0,  1.0,  0.5,  0.5, -1.0},
				{-1.0,  0.0,  1.0,  1.0,  1.0,  1.0,  0.0, -1.0},
				{-1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0, -1.0},
				{-1.0,  0.5,  0.0,  0.0,  0.0,  0.0,  0.5, -1.0},
				{-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0}
			},
			{// Biais à ajouter à un fou noir en fonction de sa position
				{-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
				{-1.0,  0.5,  0.0,  0.0,  0.0,  0.0,  0.5, -1.0},
				{-1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0, -1.0},
				{-1.0,  0.0,  1.0,  1.0,  1.0,  1.0,  0.0, -1.0},
				{-1.0,  0.5,  0.5,  1.0,  1.0,  0.5,  0.5, -1.0},
				{-1.0,  0.0,  0.5,  1.0,  1.0,  0.5,  0.0, -1.0},
				{-1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
				{-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0}
			}
	};
	

	public Fou (int x, int y, Joueur proprietaire, Echiquier plateau) {
		super(x, y, proprietaire, plateau);
		valeur=30;
		eval = evalFou[proprietaire.getCouleur().ordinal()];
		code = 'b';
	}
	
	@Override
	public boolean deplacementValide(int nx, int ny){
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
		return new Fou(this.getX(), this.getY(), getProprio(), e);
	}
}