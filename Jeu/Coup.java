package Jeu;
/** Comprend la pièce déplacée, la case d'arrivé, la SAN du coup et son évaluation
 * lorsqu'elle est déterminée.
 * @author Alexandre
 */
public class Coup implements Comparable<Coup> {
	
	/** La pièce déplacée */
	private Piece piece;

	/** La case de départ */
	private Case caseDepart;
	
	/** La case d'arrivée */
	private Case caseFin;
	
	/** La SAN du coup */
	private String san;
	
	/** L'évaluation du coup */
	public double eval;
	
	/** Construit un coup à partie de la pièce déplacée et de la case d'arrivée.
	 * @param piece La pièce déplacée
	 * @param caseFin La case d'arrivée
	 */
	public Coup(Piece piece, Case caseFin) {
		this.piece = piece;
		this.caseDepart = new Case(piece.getX(), piece.getY());
		this.caseFin = new Case(caseFin.getX(), caseFin.getY());
		this.san = toSAN();
	}
	
	/** Constructeur utilisé par le bot basique pour initialer le meilleur coup.
	 * On part donc du pire score possible.
	 * @param signe +1 pour les blancs et -1 pour les noirs
	 */
	public Coup(int signe) {
		eval = -10000 * signe;
	}
	
	/** La SAN du coup.
	 * @return La SAN du coup
	 */
	public String getSAN() {
		return san;
	}
	
	/** La pièce déplacée */
	public Piece getPiece() {
		return piece;
	}
	
	/** Fixe la pièce déplacée 
	 * @param piece La pièce déplacée
	 */
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	/** La case d'arrivée */
	public Case getCaseFin() {
		return caseFin;
	}
	
	/** Fixe la case d'arrivée 
	 * @param caseFin la case d'arrivée
	 */
	public void setCaseFin(Case caseFin) {
		this.caseFin = caseFin;
	}
	
	/** L'évaluation du coup */
	public double getEval() {
		return eval;
	}
	
	/** Déduit la SAN correspondant au coup */
	private String toSAN() {
		if (piece.roqueValide(caseFin.getX(), caseFin.getY())) {
			if (piece.getY() - caseFin.getY() == 2) {
				return "O-O-O"; // Grand roque
			} else {
				return "O-O"; // Petit roque
			}
		}
		char code = piece.getCode();
		String san = caseFin.toSAN();
		Echiquier echiquier = piece.getEchiquier();
		if (code == 'p') {
			if (echiquier.getPiece(caseFin) != null || piece.peutPrendreEnPassant(caseFin)) {
				return piece.getColonne() + "x" + san;
			} else { 
				return caseFin.toSAN();
			}
		}
		if (echiquier.getPiece(caseFin) != null) {
			san = "x" + san;
		}
		if (code == 'k') {
			return piece.getSAN() + san;
		}
		boolean ambiguitePiece = false;
		boolean ambiguiteLigne = false;
		boolean ambiguiteColonne = false;
		for (Piece p : echiquier.trouverPieces(code, piece.getProprio())) {
			if (p.deplacementValideReel(caseFin) && 
				!(p.getY() == piece.getY() && p.getX() == piece.getX())) { // On a ambiguité sur la pièce
				ambiguitePiece = true;
				ambiguiteColonne = ambiguiteColonne || p.getY() == piece.getY();
				ambiguiteLigne = ambiguiteLigne || p.getX() == piece.getX();
			}
		}
		if (ambiguitePiece) {
			if (ambiguiteColonne) {
				if (ambiguiteLigne) {
					return piece.getSAN() + piece.getColonne() + piece.getLigne() + san;
				} else {
					return piece.getSAN() + piece.getLigne() + san;
				}
			} else {
				return piece.getSAN() + piece.getColonne() + san;
			}
		} else {
			return piece.getSAN() + san;
		}
	}

	@Override
	public int compareTo(Coup coups) {
		if (eval < coups.eval) {
			return -1;
		} else if (eval > coups.eval) {
			return 1;
		}
		return 0;
	}

	public Case getcaseDepart() {
		return caseDepart;
	}

}
