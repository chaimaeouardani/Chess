package Jeu;

/** Classe permettant de créer la Forsyth-Edwards Notation décrivant un échiquier
 *  et inversement.
 * @author Alexandre
 */
public class FEN {
	
	/** Décrit uniquement la position des pièce sur le plateau. */
	private String pieces;
	
	/** 'w' si c'est au blanc de jouer, 'b' sinon. */
	private char tour;
	
	/** Vrai si le petit roque blanc est encore possible. */
	private boolean K;
	
	/** Vrai si le grand roque blanc est encore possible. */
	private boolean Q;
	
	/** Vrai si le petit roque noir est encore possible. */
	private boolean k;
	
	/** Vrai si le grand roque noir est encore possible. */
	private boolean q;
	
	/** Case qui peut être la cible d'une prise en passant ou null. */
	private Case enPassant;
	
	/** Compteur des demi-coups. */
	private int halfMove;
	
	/** Compteur des coups. */
	private int fullMove;

	/** Décrit l'état du jeu (position des pièces, le joueur devant jouer,
	 * les roques possibles, la possibilité d'une prise en passant, halfmove counter
	 * et fullmove counter).
	 * @param arbitre
	 * @return la chaine de caractère regroupant toutes ces infos
	 */
	public FEN(Arbitre arbitre) {
		Echiquier echiquier = arbitre.getEchiquier();
		pieces = echiquierToString(echiquier);
		tour = arbitre.getDoitJouer().getCouleur().equals(Couleur.BLANC) ? 'w' : 'b';
		K = echiquier.getK();
		Q = echiquier.getQ();
		k = echiquier.getk();
		q = echiquier.getq();
		enPassant = arbitre.getEchiquier().cibleEnPassant;
		halfMove = arbitre.getEchiquier().getHalfMove();
		fullMove = arbitre.getEchiquier().getFullMove();
	}
	
	/** Construit une FEN à partir d'une chaîne de caractères.
	 * @param fen la chaîne de caractères utilisée
	 * @throws FenException s'il y a des problèmes de notation
	 */
	public FEN(String fen) throws FenException {
		if (fen == null) {
			throw new FenException("L'enregistrement de FEN fourni est null.");
		}
		String[] param = fen.split(" ");
		if (param.length != 6) {
			throw new FenException("L'enregistrement de FEN fourni ne comprend pas"
						+ "le bon nombre de paramètres\n "
						+ "(https://fr.wikipedia.org/wiki/Notation_Forsyth-Edwards).");
		}
		if (param[0].split("/").length != 8) {
			throw new FenException("Problème dans le nombre de colonne des pièces.");
		}
		pieces = param[0];
		tour = param[1].charAt(0);
		if (tour != 'w' && tour != 'b') {
			throw new FenException("Le tour doit être indiqué avec le caractère"
					+ " 'w' ou 'b' après les pièces.");
		}
		K = param[2].contains("K");
		Q = param[2].contains("Q");
		k = param[2].contains("k");
		q = param[2].contains("q");
		enPassant = param[3].equals("-") ? null : new Case(param[3]);
		halfMove = Integer.valueOf(param[4]);
		fullMove = Integer.valueOf(param[5]);
		if (halfMove < 0 || fullMove < 0 || fullMove < halfMove) {
			throw new FenException("Problème dans le nombre de demi-coups/coups.");
		}
	}
	
	/** Décrit uniquement la position des pièce sur le plateau.
	 * @return la chaine de caractères décrivant les pièce sur le plateau
	 */
	public String getPieces() {
		return pieces;
	}
	
	/** Renvoie 'w' si c'est au blanc de jouer, 'b' sinon.
	 * @return un caractère indiquant le joueur dont c'est le tour
	 */
	public char getTour() {
		return tour;
	}
	
	/** Vrai si le petit roque blanc est encore possible.
	 * @return Vrai si le petit roque blanc est encore possible
	 */
	public boolean getK() {
		return K;
	}
	
	/** Vrai si le grand roque blanc est encore possible.
	 * @return Vrai si le grand roque blanc est encore possible
	 */
	public boolean getQ() {
		return Q;
	}
	
	/** Vrai si le petit roque noir est encore possible.
	 * @return Vrai si le petit roque noir est encore possible
	 */
	public boolean getk() {
		return k;
	}
	
	/** Vrai si le grand roque noir est encore possible.
	 * @return Vrai si le grand roque noir est encore possible
	 */
	public boolean getq() {
		return q;
	}
	
	/** Case qui peut être la cible d'une prise en passant ou null.
	 * @return une copie de la case
	 */
	public Case getEnPassant() {
		return enPassant;
	}
	
	/** Compteur des demi-coups.
	 * @return le nombre de demi-coups dans cet état
	 */
	public int getHalfMove() {
		return halfMove;
	}
	/** Compteur des coups.
	 * @return le nombre de coups dans cette partie
	 */
	public int getFullMove() {
		return fullMove;
	}
	
	/** Renvoie la FEN correspondant à l'état du jeu (position des pièces,
	 * le joueur devant jouer, les roques possibles, la possibilité d'une
	 * prise en passant, halfmove counter et fullmove counter).
	 */
	public String toString() {
		String fen = pieces + " " + tour + " ";
		String roque = K ? "K" : "";
		roque += Q ? "Q" : "";
		roque += k ? "k" : "";
		roque += q ? "q" : "";
		if (roque.isEmpty()) {
			roque = "-";
		}
		fen += roque + " ";
		if (enPassant == null) {
			fen += "- ";
		} else {
			fen += enPassant.toSAN();
			fen += " ";
		}
		fen += halfMove + " ";
		fen += fullMove;
		return fen;
	}
	
	/** Décrit uniquement la position des pièce sur le plateau.
	 * @param echiquier l'échiquier à transcrire 
	 * @return la chaine de caractère décrivant le plateau suivant la
	 * notation de Forsyth-Edwards
	 */
	public static String echiquierToString(Echiquier echiquier) {
		String fen = "";
		for (int i = 1; i < Echiquier.tailleX + 1; i++) {
			int vide = 0;
			for (int j = 1; j < Echiquier.tailleY + 1; j++) {
				if (echiquier.caselibre(i, j)) {
					vide++;
				} else {
					if (vide != 0) {
						fen += Integer.toString(vide);
						vide = 0;
					}
					Piece piece = echiquier.getPiece(i, j);
					fen += piece.getCouleur().equals(Couleur.BLANC) ? 
							Character.toUpperCase(piece.getCode()) : piece.getCode();
				}
			}
			if (vide != 0) {
				fen += Integer.toString(vide);
			}
			if (i!=Echiquier.tailleX) {
				fen += '/';
			}
		}
		return fen;
	}

}
