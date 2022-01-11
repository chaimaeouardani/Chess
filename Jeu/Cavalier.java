package Jeu;
/** Le cavalier du jeu d'échecs.
 * @author ???, Alexandre
 */
public class Cavalier extends Piece {
	
	/** Le biais de la pièce pour calculer le score d'un plateau */
	private final double[][][] evalCavalier = {
			{ // Biais à ajouter à un cavalier blanc en fonction de sa position
				{-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
				{-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
				{-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
				{-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
				{-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
				{-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
				{-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
				{-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
			},
			{ // Biais à ajouter à un cavalier noir en fonction de sa position
				{-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
				{-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
				{-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
				{-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
				{-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
				{-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
				{-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
				{-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
			}
	}; 
	
	public Cavalier (int x, int y, Joueur proprietaire, Echiquier echiquier) {
		super(x, y, proprietaire, echiquier);
		valeur = 30;
		eval = evalCavalier[proprietaire.getCouleur().ordinal()];
		code = 'n';
	}
	
	@Override
	public boolean deplacementValide(int nx, int ny) {
		int dx=nx-this.getX();
		int dy=ny-this.getY();
		boolean libre= this.echiquier.caselibre(nx, ny);
		boolean possible;
		
		if (dx==1 && Math.abs(dy)==2) {
			possible=true; 
		
		}else if (dx==2 && Math.abs(dy)==1) {
			possible=true;
				
		}else if (dx==-1 && Math.abs(dy) == 2) {
			possible=true;
		
		}else if (dx == -2 && Math.abs(dy) == 1) {
			possible=true;	
		
		}else {
			possible=false;
		}
		return possible && (libre || this.echiquier.getJoueur(nx, ny)!=this.getProprio());
	}
	
	@Override
	public Piece copy(Echiquier e) {
		return new Cavalier(this.getX(), this.getY(), getProprio(), e);
	}
}
