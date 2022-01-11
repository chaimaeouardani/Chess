package Jeu;
import java.util.ArrayList;
import java.util.List;

/** Une pièce du jeu d'échecs
 * @author Alexandre, ???
 */
public abstract class Piece {

	/** Coordonnée verticale de la pièce sur le plateau */
	protected int x;

	/**  Coordonnée horizontale de la pièce sur le plateau. */
	protected int y;
	
	/** Le joueur possédant la pièce */
	private Joueur proprietaire; 
	
	/**  L'échiquier auquel la pièce appartient */
	protected Echiquier echiquier;
	
	/** La valeur de la pièce */
	protected int valeur;
	
	/** Le biais à ajouter à la valeur de la pièce suivant sa position */
	protected double[][] eval;
	
	/** Le caractère codant la pièce pour la FEN */
	protected char code;
	
	/** Création d'une pièce du jeu d'Echecs
	 * @param x position verticale
	 * @param y position horizontale
	 * @param proprietaire de la pièce
	 * @param plateau auquel elle appartient
	 */
	protected Piece(int x, int y, Joueur proprietaire, Echiquier plateau) {
		this.x = x;
		this.y = y;
		this.proprietaire = proprietaire;
		this.echiquier = plateau;
	}

	/** Renvoie la position x de la pièce sur le plateau (le 1 est en haut du plateau).
	 * @return le x de la pièce
	 */
	public int getX() {
		return x;
	}

	/** Renvoie la position y de la pièce sur le plateau (le 1 est à gauche du plateau).
	 * @return le y de la pièce
	 */
	public int getY() {
		return y;
	}
	
	/** Renvoie le propriétaire de la pièce.
	 * @return le propriétaire de la pièce
	 */
	public Joueur getProprio() {
		return proprietaire;
	}
	
	/** Renvoie la valeur de la pièce en fonction de sa position sur l'échiquier et de sa couleur.
	 * La valeur est négative pour une pièce noire et positive pour une blanche.
	 * @return la valeur de la pièce
	 */
	public double getValeur() {
		return (valeur + eval[x-1][y-1]) * getProprio().getSigne();
	}
	
	/** Renvoie le code de la pièce i.e. l'initiale du nom anglais.
	 * @return le code la pièce
	 */
	public char getCode() {
		return code;
	}
	
	/** Renvoie le code SAN de la pièce. Même chose que le code pour toutes les pièces
	 * sauf le pion qui est une chaîne vide. Majuscule si blanche noire sinon.
	 * @return le code SAN de la pièce
	 */
	public String getSAN() {
		return Character.toString(getCouleur().equals(Couleur.BLANC) ? 
				Character.toUpperCase(getCode()) : getCode());
	}
	
	/** Renvoie la couleur du proprietaire de la pièce.
	 * C'est équivalent à la couleur de la pièce.
	 * @return le couleur de la pièce (BLANC ou NOIR)
	 */
	public Couleur getCouleur() {
		return proprietaire.getCouleur();
	}
	
	/** Renvoie la ligne de la pièce suivant la notation SAN.
	 * La string d'un entier entre 1 et 8 avec le 1 en bas.
	 * @return la ligne de la pièce suivant la notation SAN
	 */
	public String getLigne() {
		return String.valueOf(9 - x);
	}
	
	/** Renvoie la colonne de la pièce suivant la notation SAN.
	 * La string d'un caractère entre a et h avec le a à gauche.
	 * @return la colonne de la pièce suivant la notation SAN
	 */
	public String getColonne() {
		return String.valueOf((char)((int)('a') - 1 + y));
	}
	
	public Echiquier getEchiquier() {
		return echiquier;
	}
	
	/** Renvoie le nom de la pièce.
	 * @return le nom de la pièce
	 */
	public String getNomPiece() {
		return this.getClass().getSimpleName() ;
	}
	
	/** Donne le nom de la pièce, sa couleur, sa poisition et son nombre de déplacements.
	 */
	public String toString() {
		return this.getClass().getSimpleName() + " " + proprietaire.getCouleur() + " en " + x + ", " + y ;
	}
	
	/** Permet de déplacer la pièce à la position nx, ny si c'est possible
	 * @param nx Nouvelle position verticale
	 * @param ny Nouvelle position horizontale
	 * @throws CoupInvalideException
	 */
	protected void deplacer(int nx, int ny) throws CoupInvalideException {
		if (!deplacementValide(nx, ny)) {
			throw new CoupInvalideException("Ce coup n'est pas autorisé pour cette pièce.");		
		} else if (seMetEnEchec(nx, ny)) {
			throw new CoupInvalideException("Ce coup vous met ou vous laisse en échec.");
		} else {
			echiquier.cibleEnPassant = null;
			this.x = nx;
			this.y = ny;
		}
		
	}
	
	protected void deplacer(Case caseFin) throws CoupInvalideException {
		deplacer(caseFin.getX(), caseFin.getY());
	}
	
	/** Utiliser pour simuler le déplacement et vérifier s'il nous
	 * met nous même en échec.
	 * @param nx Nouvelle position verticale
	 * @param ny Nouvelle position horizontale
	 * @param temp la pièce qui se trouvait en nx, ny 
	 * et que l'on supprime donc dans la simulation
	 * @return vrai si le déplacement nous met en échec
	 */
	public boolean simulerDeplacement(Piece temp, int nx, int ny) {
		int oldx = this.x;
		int oldy = this.y;
		Case oldCase = echiquier.cibleEnPassant;
		echiquier.cibleEnPassant = null;
		this.x = nx;
		this.y = ny;
		List<Piece> pieces = new ArrayList<Piece>(echiquier.getListePieces());
		pieces.remove(temp);
		boolean echec = false;
		if (Arbitre.joueurEchec(proprietaire, echiquier, pieces)) {
			echec = true;
		}
		echiquier.cibleEnPassant = oldCase;
		this.x = oldx;
		this.y = oldy;
		return echec;
	}

	/** Permet de savoir si un déplacement nous met en échec
	 * @param nx nouvelle coordonnée verticale de la pièce
	 * @param ny nouvelle coordonnée horizontale de la pièce
	 * @return vrai si le déplacement de la pièce en nx, ny met le propriétaire en échec
	 */
	public boolean seMetEnEchec(int nx, int ny) {
		return echiquier.metEnEchec(this, nx, ny);
	}
	
	/** Fusion des méthodes roqueValide, deplacementValide et seMetEnEchec
	 * @param nx nouvelle coordonnée verticale de la pièce
	 * @param ny nouvelle coordonnée horizontale de la pièce
	 * @return vrai si le déplacement est valide dans une partie d'échec
	 */
	public boolean deplacementValideReel(int nx, int ny) {
		return roqueValide(nx, ny) || (deplacementValide(nx, ny) && !seMetEnEchec(nx, ny));
	}
	
	/** Fusion des méthodes deplacementValide et seMetEnEchec
	 * @param caseFin La case vers laquelle se déplacer
	 * @return vrai si le déplacement est valide dans une partie d'échec
	 */
	public boolean deplacementValideReel(Case caseFin) {
		return deplacementValideReel(caseFin.getX(), caseFin.getY());
	}
	
	/** Réalise une copie de la pièce pour la placer sur l'échiquier.
	 * @param echiquier l'échiquier sur lequel la pièce sera
	 * @return une nouvelle pièce identique à la précédent sauf pour l'attribut echiquier
	 */
	public abstract Piece copy(Echiquier echiquier);
	
	/** Permet de déterminer si un déplacement est valide.
	 * @param nx La ligne de la case vers laquelle se déplacer
	 * @param ny La colonne de la case vers laquelle se déplacer
	 * @return vrai si le déplacement est possible
	 */
	public abstract boolean deplacementValide(int nx, int ny);
	
	/** Permet de déterminer si une pièce peut prendre en passant vers la case [x,y].
	 * Renvoie forcément faux si la pièce n'est pas un pion.
	 * @param x la ligne d'arrivée
	 * @param y la colonne d'arrivée
	 * @return vrai si la pièce peut prendre en passant en allant sur la case [x,y]
	 */
	public boolean peutPrendreEnPassant(int x, int y) {
		return false;
	}
	
	/** Permet de déterminer si une pièce peut prendre en passant vers la case donnée.
	 * Renvoie forcément faux si la pièce n'est pas un pion.
	 * @param caseFin La case d'arrivée
	 * @return vrai si la pièce peut prendre en passant en allant sur la case donnée
	 */
	public boolean peutPrendreEnPassant(Case caseFin) {
		return false;
	}
	
	
	/** Obtenir la liste des cases où le déplacement est valide.
	 * @param echiquier l'echiquier à considérer
	 * @return la liste des cases valides
	 */
	public List<Case> listeDeplacementsValidesReels(Echiquier echiquier){
		List<Case> listeDeplacementsValidesReels = new ArrayList<Case>();
		for (int i = 1; i < Echiquier.tailleX + 1; i++) {
			for (int j = 1; j < Echiquier.tailleY + 1; j++) {
				if ((i != x || j != y) && deplacementValideReel(i, j)) {
					listeDeplacementsValidesReels.add(new Case(i,j));
				}
			}
		}
		return listeDeplacementsValidesReels;
	}

	/** Renvoie vrai si le rock de la pièce à destination de nx, ny est possible.
	 * @param nx la coordonnée verticale vers laquelle rocker
	 * @param ny la coordonnée horizontale versc laquelle rocker
	 * @return vrai si le rock de la pièce à destination de nx, ny est possible
	 */
	public boolean roqueValide(int nx, int ny) {
		return false;
	}

	/** Permet de lancer un roque.
	 * @param nx la nouvelle position x du roi
	 * @param ny la nouvelle position y du roi
	 * @throws CoupInvalideException si le roque est impossible (pas un roi ou 
	 * roque invalide)
	 */
	protected void roquer(int nx, int ny) throws CoupInvalideException {
		throw new CoupInvalideException("Cette pièce ne peut pas rocker.");
	}
	
	/** Créer une nouvelle pièce à partir de son code.
	 * @param joueurBlanc Le joueur associé aux blancs
	 * @param joueurNoir Le joueur associé aux noirs
	 * @param x La ligne de la pièce
	 * @param y La colonne de l apièce
	 * @param echiquier L'échiquier auquel la pièce appartient
	 * @param code Le code de la pièce
	 * @throws RuntimeException si le code n'existe pas
	 * @return La pièce créée
	 */
	public static Piece pieceFromCode(Joueur joueurBlanc, Joueur joueurNoir, int x, int y, 
			Echiquier echiquier, char code) {
		Piece piece;
		switch(code) {
		case 'p':
			piece = new Pion(x, y, joueurNoir, echiquier);
			break;
		case 'P':
			piece = new Pion(x, y, joueurBlanc, echiquier);
			break;
		case 'r':
			piece = new Tour(x, y, joueurNoir, echiquier);
			break;
		case 'R':
			piece = new Tour(x, y, joueurBlanc, echiquier);
			break;
		case 'n':
			piece = new Cavalier(x, y, joueurNoir, echiquier);
			break;
		case 'N':
			piece = new Cavalier(x, y, joueurBlanc, echiquier);
			break;
		case 'b':
			piece = new Fou(x, y, joueurNoir, echiquier);
			break;
		case 'B':
			piece = new Fou(x, y, joueurBlanc, echiquier);
			break;
		case 'q':
			piece = new Dame(x, y, joueurNoir, echiquier);
			break;
		case 'Q':
			piece = new Dame(x, y, joueurBlanc, echiquier);
			break;
		case 'k':
			piece = new Roi(x, y, joueurNoir, echiquier);
			break;
		case 'K':
			piece = new Roi(x, y, joueurBlanc, echiquier);
			break;
		default:
			throw new RuntimeException("Code fourni inexistant.");
		}
		return piece;
	}
}