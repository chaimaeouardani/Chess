package Jeu;
import java.util.List;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
/** La classe de l'échiquier.
 * @author Alexandre, Briag, Fabio
 */
public class Echiquier {
	/**
	 * Taille horizontal (X) et verticale (Y) de l'echiquier
	 */
	public static final int tailleX = 8, tailleY = 8;
	
	/**
	 * Liste de toutes les pièces en Jeu
	 */
	private List<Piece> piecesEnJeu;
	
	/**
	 * Tableau représentant l'échiquier, i.e. la pièce de coordonnées x, y est
	 * référencée dans plateau[x][y].
	 */
	private Piece[][] plateau;
	
	/** Les deux Joueurs de la partie.
	 */
	private Joueur joueurNoir, joueurBlanc;
	
	/** L'arbitre présent sur la partie */
	private Arbitre arbitre;
	
	/** La case pouvant être prise en passant après le déplacement d'un pion */
	Case cibleEnPassant;
	
	/** Pour implémenter la règle des 50 coups. Le compteur est réinitialisé après
	 * une prise ou le déplacement d'un pion et incrémenter sinon.
	 */
	private int halfMove;
	
	/** Compte le nombre de coups effectué dan la partie au total */
	private int fullMove;
	
	/** Vrai si le petit roque blanc est encore possible. */
	boolean K;
	
	/** Vrai si le grand roque blanc est encore possible. */
	boolean Q;
	
	/** Vrai si le petit roque noir est encore possible. */
	boolean k;
	
	/** Vrai si le grand roque noir est encore possible. */
	boolean q;
	
	
	/** Constructeur de l'échiquier et des pièces allant dessus.
	 * On part du principe que le joueur1 joue les blancs.
	 * @param joueurNoir Le premier joueur
	 * @param joueurBlanc Le deuxi�me joueur
	 */
	public Echiquier(Joueur joueurBlanc, Joueur joueurNoir, Arbitre arbitre) {
		this.arbitre = arbitre;
		this.joueurBlanc = joueurBlanc;
		this.joueurNoir = joueurNoir;
		fullMove = 0;
		halfMove = 0;
		piecesEnJeu = new ArrayList<Piece>();
		plateau = new Piece[tailleX + 1][tailleY + 1]; // taille+1 pour aller de 1 e 8
		// x correspond aux  colonnes et y aux lignes
		// Ajout des Pions des 2 Joueurs
		for (int i = 1; i < tailleX + 1; i++) {
			Piece p1 = new Pion(2, i, joueurNoir, this);
			plateau[2][i] = p1;
			piecesEnJeu.add(p1);
			Piece p2 = new Pion(7, i, joueurBlanc, this);
			plateau[7][i] = p2;
			piecesEnJeu.add(p2);
		}
		// Ajout des Tours
		for (int i = 0; i< 2; i++) {
			int x = i*7 + 1;
			Piece t1 = new Tour(1, x, joueurNoir, this);
			plateau[1][x] = t1;
			piecesEnJeu.add(t1);
			Piece t2 = new Tour(8, x, joueurBlanc, this);
			plateau[8][x] = t2;
			piecesEnJeu.add(t2);
		}
		// Ajout des Cavaliers
		for (int i = 0; i< 2; i++) {
			int x = i*5 + 2;
			Piece c1 = new Cavalier(1, x, joueurNoir, this);
			plateau[1][x] = c1;
			piecesEnJeu.add(c1);
			Piece c2 = new Cavalier(8, x, joueurBlanc, this);
			plateau[8][x] = c2;
			piecesEnJeu.add(c2);
		}
		// Ajout des Fous
		for (int i = 0; i< 2; i++) {
			int x = i*3 + 3;
			Piece f1 = new Fou(1, x, joueurNoir, this);
			plateau[1][x] = f1;
			piecesEnJeu.add(f1);
			Piece f2 = new Fou(8, x, joueurBlanc, this);
			plateau[8][x] = f2;
			piecesEnJeu.add(f2);
		}
		// Ajout des Dames
		Piece d1 = new Dame(1, 4, joueurNoir, this);
		plateau[1][4] = d1;
		piecesEnJeu.add(d1);
		Piece d2 = new Dame(8, 4, joueurBlanc, this);
		plateau[8][4] = d2;
		piecesEnJeu.add(d2);
		
		// Ajout des Rois
		Piece r1 = new Roi(1, 5, joueurNoir, this);
		plateau[1][5] = r1;
		piecesEnJeu.add(r1);
		Piece r2 = new Roi(8, 5, joueurBlanc, this);
		plateau[8][5] = r2;
		piecesEnJeu.add(r2);
		
		K = true;
		Q = true;
		k = true;
		q = true;
	}
	
	/** Constructeur qui renvoie une copie de l'Echiquier
	 * passé en paramètre.
	 * @param l'échiquier en cours que l'on souhaite copier
	 */
	public Echiquier(Echiquier aCopier) {
		joueurNoir = aCopier.getJoueurNoir();
		joueurBlanc = aCopier.getJoueurBlanc();
		arbitre = new Arbitre(this, aCopier.getArbitre().getHistorique(), aCopier.getArbitre().vue);
		fullMove = aCopier.getFullMove();
		halfMove = aCopier.getHalfMove();
		plateau = new Piece[tailleX + 1][tailleY + 1];
		piecesEnJeu = new ArrayList<Piece>();
		for (Piece p : aCopier.getListePieces()) {
			Piece piece = p.copy(this);
			piecesEnJeu.add(piece);
			plateau[piece.getX()][piece.getY()] = piece;
		}
		K = aCopier.getk();
		Q = aCopier.getq();
		k = aCopier.getk();
		q = aCopier.getq();
	}
	
	/** Construit l'echiquier à partir d'une chaine de caractère FEN.
	 * @param fen la chaine de caractère en question
	 * @throws FenException 
	 */
	public Echiquier(Joueur joueurBlanc, Joueur joueurNoir, FEN fen, Arbitre arbitre) throws FenException {
		this.arbitre = arbitre;
		this.joueurBlanc = joueurBlanc;
		this.joueurNoir = joueurNoir;
		fullMove = fen.getFullMove();
		halfMove = fen.getHalfMove();
		K = fen.getK();
		Q = fen.getQ();
		k = fen.getk();
		q = fen.getq();
		cibleEnPassant = fen.getEnPassant();// == null ? null : fen.getEnPassant();
		piecesEnJeu = new ArrayList<Piece>();
		plateau = new Piece[tailleX + 1][tailleY + 1];
		String pieces = fen.getPieces();
		int x = 1;
		int y = 1;
		for (int i = 0; i < pieces.length(); i++){
		    char c = pieces.charAt(i);
		    if (c == '/') {
		    	x++;
		    	y = 1;
		    } else if (Character.isDigit(c)) {
		    	y += Character.getNumericValue(c);
		    } else {
		    	if (y>8) {
		    		throw new FenException("Trop de pièces sur la même ligne (" + y + ").");
		    	}
		    	Piece piece = Piece.pieceFromCode(joueurBlanc, joueurNoir, x, y, this, c);
		    	piecesEnJeu.add(piece);
		    	plateau[x][y] = piece;
		    	y++;
		    }
		}
		if (Arbitre.trouverRoi(piecesEnJeu, joueurBlanc) == null) {
			throw new FenException("Le joueur blanc n'a pas de roi !");
		}
		if (Arbitre.trouverRoi(piecesEnJeu, joueurNoir) == null) {
			throw new FenException("Le joueur noir n'a pas de roi !");
		}
	}

	
	public String getSAN(int x, int y) 
    {
		int debut = (int)('a') - 1;
		String notation = String.valueOf((char)(debut + y)) + (tailleX + 1 - x);
		return notation;
	}
	
	/** Méthode permettant d'ordonner le déplacement d'une pièce.
	 * @param nx la coordonée verticale de la case de destination
	 * @param ny la coordonée horizontale de la case de destination
	 * @param p la pièce à déplacer
	 * @throws CoupInvalideException
	 */
	protected void deplacement(int nx, int ny, Piece p) throws CoupInvalideException {
		if (!p.deplacementValideReel(nx, ny)) {
			throw new CoupInvalideException("Ce coup n'est pas autorisé pour cette pièce.");
		}
		int x = p.getX();
		int y = p.getY();
		if (p.roqueValide(nx, ny)) {
			Roi roi = (Roi) p;
			if (ny == 3) {
				int yTour = 1;
				Tour tour = (Tour) plateau[nx][yTour];
				grandRoque(roi, tour);
			} else {
				int yTour = 8;;
				Tour tour = (Tour) plateau[nx][yTour];
				petitRoque(roi, tour);
			}
			reDessiner(x-1);
			halfMove++;
		} else {
			actualiserRoque(p);
			p.deplacer(nx, ny);
			halfMove = ('p' == p.getCode()) ?  0 : ++halfMove;
			supprimerPiece(nx, ny);
			plateau[nx][ny] = p;
			plateau[x][y] = null;
			
		}
		fullMove += p.getCouleur().equals(Couleur.NOIR) ? 1 : 0;
	}

	public void deplacement(Case case1, Piece p) throws CoupInvalideException {
		deplacement(case1.getX(), case1.getY(), p);
	}
	
	/** Appeler lors du déplacement de p, permet de mettre à jour les roques possibles.
	 * @param p la pièce déplacée
	 */
	protected void actualiserRoque(Piece p) {
		if (p.getY() == 5) {
			if (p.getX() == 8) {
				K = false;
				Q = false;
			} else if (p.getX() == 1) {
				k = false;
				q = false;
			}
		} else if (p.getY() == 1) {
			if (p.getX() == 8) {
				Q = false;
			} else if (p.getX() == 1) {
				q = false;
			}
		} else if (p.getY() == 8)  {
			if (p.getX() == 8) {
				K = false;
			} else if (p.getX() == 1) {
				k = false;
			}
		}
	}
	
	/** Permet la prise en passatn d'un pion.
	 * @param x La ligne du pion pris
	 * @param y La colonne du pion pris
	 */
	void priseEnPassant(int x, int y) {
		supprimerPiece(x, y);
		plateau[x][y] = null;
		reDessiner(x-1,y-1);
	}
	
	/** Permet de réaliser le grand roque 
	 * @param roi Le roi qui roque
	 * @param tour La tour qui roque
	 */
	private void grandRoque(Roi roi, Tour tour) {
		int yRoi = roi.getY();
		int yTour = tour.getY();
		int x = roi.getX();
		roi.grandRoque();
		tour.grandRoque();
		if (roi.getCouleur().equals(Couleur.BLANC)) {
			K = false;
			Q = false;
		} else {
			k = false;
			q = false;
		}
		plateau[x][yRoi - 2] = roi;
		plateau[x][yRoi] = null;
		plateau[x][yTour + 3] = tour;
		plateau[x][yTour] = null;
	}
	
	/** Permet de réaliser le petit roque 
	 * @param roi Le roi qui roque
	 * @param tour La tour qui roque
	 */
	private void petitRoque(Roi roi, Tour tour) {
		int yRoi = roi.getY();
		int yTour = tour.getY();
		int x = roi.getX();
		roi.petitRoque();
		tour.petitRoque();
		if (roi.getCouleur().equals(Couleur.BLANC)) {
			K = false;
			Q = false;
		} else {
			k = false;
			q = false;
		}
		plateau[x][yRoi + 2] = roi;
		plateau[x][yRoi] = null;
		plateau[x][yTour - 2] = tour;
		plateau[x][yTour] = null;
	}
	
	/** Permet d'actualiser le dessin sur la case x, y.
	 * Attention, les indices vont de 0 à tailleX/Y - 1 !
	 * @param x la coordonnée verticale du panel à actualiser
	 * @param y la coordonnée verticale du panel à actualiser
	 */
	void reDessiner(int x, int y){
		PiecePanel panel = arbitre.vue.cases[x][y];
		panel.removeAll();
		panel.revalidate();
		panel.repaint();
		panel.dessinerCase();
	}
	
	/** Permet d'actualiser le dessin de la ligne x.
	 * Attention, les indices vont de 0 à tailleX - 1 !
	 * @param x la ligne à actualiser
	 */
	private void reDessiner(int x) {
		for (int y=0; y<tailleY; y++) {
			reDessiner(x, y);
		}
	}
	
	/** Simule le déplacement de pièce en nx, ny et renvoie un booléen
	 * indiquant si ce déplacement met le propriétaire de la pièce en échec.
	 * @param piece la pièce à déplacer
	 * @param nx le nouveau x
	 * @param ny le nouveau y
	 * @return propriétaire en echec après la simulation
	 */
	public boolean metEnEchec(Piece piece, int nx, int ny) {
		int oldx = piece.getX();
		int oldy = piece.getY();
		Piece temp = plateau[nx][ny];
		plateau[nx][ny] = piece;
		plateau[oldx][oldy] = null;
		boolean echec = piece.simulerDeplacement(temp, nx, ny);
		plateau[nx][ny] = temp;
		plateau[oldx][oldy] = piece;
		return echec;
	}
	
	/** Simule le rock du roi avec la tour en nx, ny et renvoie un booléen
	 * indiquant si ce déplacement met le propriétaire du roi en échec.
	 * @param roi le roi à déplacer
	 * @param x le x de la tour et du roi
	 * @param yTour le y de la tour
	 * @return vrai si propriétaire en echec après la simulation
	 */
	protected boolean roqueMetEnEchec(Roi roi, int x, int yTour) {
		int yRoi = roi.getY();
		Tour tour = (Tour) plateau[x][yTour];
		boolean echec = true;
		if (yTour < yRoi) { // Grand roque
			plateau[x][yRoi - 2] = roi;
			plateau[x][yRoi] = null;
			plateau[x][yTour + 3] = tour;
			plateau[x][yTour] = null;
			echec = roi.roqueMetEnEchec(tour);
			plateau[x][yRoi - 2] = null;
			plateau[x][yRoi] = roi;
			plateau[x][yTour + 3] = null;
			plateau[x][yTour] = tour;
		} else { // Petit roque
			plateau[x][yRoi + 2] = roi;
			plateau[x][yRoi] = null;
			plateau[x][yTour - 2] = tour;
			plateau[x][yTour] = null;
			echec = roi.roqueMetEnEchec(tour);
			plateau[x][yRoi + 2] = null;
			plateau[x][yRoi] = roi;
			plateau[x][yTour - 2] = null;
			plateau[x][yTour] = tour;
		}
		return echec;
	}
	
	/** Permet de déterminer si une case de l'échiquier est libre.
	 * @param nx La ligne de la case 
	 * @param ny La colonne de la case
	 * @return vrai si la case est libre
	 */
	public boolean caselibre(int nx, int ny) {
		if (1 > nx || 8 < nx || 1 > ny || 8 < ny ) {
			return false;
		}
		return plateau[nx][ny] == null;
	}
	
	/** Supprime la pièce située en x, y.
	 * @param x la coordonnée horizontale de la pièce à supprimer
	 * @param y la coordonnée verticale de la pièce à supprimer
	 */
	private void supprimerPiece(int x, int y) {
		supprimerPiece(plateau[x][y]);
		
	}
	
	/** Supprime la pièce.
	 * @param p le pièce à supprimer, peut être null
	 */
	private void supprimerPiece(Piece p){
		if (p != null) {
			actualiserRoque(p);
			halfMove = 0;
			plateau[p.getX()][p.getY()] = null;
			piecesEnJeu.remove(p);
		}
	}
	
	/** Transforme le pion en x, y en une nouvelle pièce.
	 * @param x La ligne du pion à transformer (doit être 8 ou 1)
	 * @param y La colonne du pion à transformer
	 * @param nom Le nom de la nouvelle pièce
	 */
	public void transformerPion(int x, int y, String nom) {
		assert(x == 1 || x == 8);
		try {
			Class<?> c = Class.forName("Jeu." + nom);
			Piece p = getPiece(x, y);
			Joueur proprietaire = p.getProprio();
			Object[] args = {x, y, proprietaire, this};
			Class<?>[] classes = {int.class, int.class, Joueur.class, Echiquier.class};
			Piece newPiece = (Piece) c.getConstructor(classes).newInstance(args);
			supprimerPiece(p);
			piecesEnJeu.add(newPiece);
			plateau[x][y] = newPiece; 
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/** Renvoie le propriétaire de la pièce en x, y s'il y en a une.
	 * @param x la coordonnée horizontale de la pièce
	 * @param y la coordonnée verticale de la pièce
	 * @return le propiétaire de la pièce si elle existe, null sinon
	 */
	public Joueur getJoueur(int x, int y) {
		if (plateau[x][y] == null ){
			return null;
		}
		return plateau[x][y].getProprio();
	}
	
	/** Permet d'obtenir le joueur Noir de l'échiquier.
	 * @return le joueur Noir
	 */
	public Joueur getJoueurNoir() {
		return joueurNoir;
	}
	
	/** Permet d'obtenir le joueur Blanc de l'échiquier.
	 * @return le joueur Blanc
	 */
	public Joueur getJoueurBlanc() {
		return joueurBlanc;
	}
	
	/** Permet d'obtenir l'arbitre de l'échiquier.
	 * @return l'arbitre de l'échiquier
	 */
	public Arbitre getArbitre() {
		return arbitre;
	}

	/** Permet d'obtenir le nombre de demi-coups présentement.
	 * @return le nombre de demi-coups
	 */
	public int getHalfMove() {
		return halfMove;
	}

	/** Permet d'obtenir le nombre de coups présentement.
	 * @return le nombre de coups
	 */
	public int getFullMove() {
		return fullMove;
	}
	
	/**  Obtenir la pièce d'une case
	 * @param x la coordonnée horizontale de la case
	 * @param y la coordonnée verticale de la case
	 * @return le pièce en [x,y]
	 */
	public Piece getPiece(int x, int y) {
		return plateau[x][y];
	}
	
	/**  Obtenir la pièce d'une case
	 * @param laCase La case dont on veut la pièce
	 * @return le pièce en [x,y]
	 */
	public Piece getPiece(Case laCase) {
		return getPiece(laCase.getX(), laCase.getY());
	}
	
	/** Permet d'obtenir la liste des pièces en jeu.
	 * @return pieceEnJeu
	 */
	public List<Piece> getListePieces() {
		return piecesEnJeu;
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
	
	/** Renvoie une copie de l'échiquier avec le coup joué.
	 * @param coup Le coup à jouer
	 * @return une copie de l'échiquier avec le coup
	 * @throws CoupInvalideException si le coup n'est pas valide
	 */
	public Echiquier copyAvecCoup(Coup coup) throws CoupInvalideException {
		Echiquier copy = new Echiquier(this);
		Piece piece = copy.getPiece(coup.getPiece().getX(), coup.getPiece().getY());
		copy.deplacement(coup.getCaseFin().getX(), coup.getCaseFin().getY(), piece);
		return copy;
	}
	
	/** Permet d'obtenir la liste des pièces d'un joueur
	 * @param proprio Le joueur dont on veut les pièces
	 * @return La liste des pièces
	 */
	public List<Piece> piecesDuJoueur(Joueur proprio) {
		return piecesDuJoueur(proprio.getCouleur());
	}

	/** Permet d'obtenir la liste des pièces d'un joueur
	 * @param couleur La couleur du joueur dont on veut les pièces
	 * @return La liste des pièces
	 */
	public List<Piece> piecesDuJoueur(Couleur couleur) {
		List<Piece> pieces = new ArrayList<Piece>();
		for (Piece piece : piecesEnJeu) {
			if (piece.getProprio().getCouleur().equals(couleur)) {
				pieces.add(piece);
			}
		}
		return pieces;
	}
	
	/** Permet de trouver la liste des pièces d'un certain type d'un joueur.
	 * Par exemple peut renvoyer la liste des fou d'un joueur.
	 * @param code le code de la pièce
	 * @param proprio le proprietaire de la pièce
	 * @return la liste des pièces du joueur correspondant à ce code
	 */
	public List<Piece> trouverPieces(char code, Joueur proprio) {
		List<Piece> pieces = new ArrayList<Piece>();
		for (Piece piece : piecesDuJoueur(proprio)) {
			if (piece.getCode() == code) {
				pieces.add(piece);
			}
		}
		return pieces;
	}

	/**
	 * Permet d'ajouter une nouvelle pièce en jeu (sauf un roi)
	 * @param piece à ajouter sur le Terrain
	 * @return si la pièce a pu être ajoutée.
	 */
	public boolean ajouterPiece(Piece piece) {
		if(piece == null || piece.getNomPiece()=="roi") {
			return false;
		}
		assert(this.caselibre(piece.getX(), piece.getY()));
		piecesEnJeu.add(piece);
		plateau[piece.getX()][piece.getY()] = piece;
		return true;
	}

	public void setArbitre(Arbitre arbitre){
		this.arbitre = arbitre;
	}
	
	
	
}
