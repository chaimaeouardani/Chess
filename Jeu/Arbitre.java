package Jeu;
import java.util.List;

import javax.swing.Timer;

import Bot.Bot;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/** Classe Arbitre, entité qui encadre une partie 
 * @author Alexandre, chaimae
 */
public class Arbitre {

	/** Le joueur qui joue les blancs dans la partie */
	protected Joueur joueurBlanc;
	
	/** Le joueur qui joue les noirs dans la partie */
	protected Joueur joueurNoir;
	
	/** Le joueur dont c'est le tour de jouer */
	protected Joueur doitJouer;
	
	/** L'échiquier sur lequel la partie se déroule */
	protected Echiquier echiquier;
	
	/** Boolean indiquant si la partie est terminée ou non */
	protected boolean finie;
	
	private boolean replay;
	
	/** L'interface graphique pour la la partie en cours */
	public EchecSwing vue;
	
	/** Le panel de départ pour un coup */
	PiecePanel source;
	
	/** Le panel d'arrivée pour un coup */
	PiecePanel destination;
	
	/** La pièce déplacée pour un coup */
	protected Piece depart;
	
	/** L'historique de la partie */
	protected Historique historique;
	
	/** La FEN décrivant la position de départ de la partie, utilisée pour recommencer */ 
	protected FEN fenDepart;
	
	/** Le tour en cours */
	protected TourDeJeu tour;
	
	/** Construit un nouvel arbitre à partir de 2 joueurs.
	 * @param joueurBlanc Le joueur blanc
	 * @param joueurNoir Le joueur noir
	 */
    public Arbitre(Joueur joueurBlanc,Joueur joueurNoir) {
		this.joueurBlanc = joueurBlanc;
		this.joueurNoir = joueurNoir;
	}
    
    /** Méthode utilisée pour créer un arbitre factice 
     * @param echiquier L'échiquier utilisé
     * @param historique L'historique utilisé
     * @param vue La vue utilisée
     */
    public Arbitre(Echiquier echiquier, Historique historique, EchecSwing vue) {
    	joueurBlanc = echiquier.getJoueurBlanc();
    	joueurNoir = echiquier.getJoueurNoir();
    	this.echiquier = echiquier;
    	this.historique = historique;
    	this.vue = vue;
    }
    
    /** Créer un arbitre vide. Doit ensuite être initialiser avec init(...). */
    public Arbitre() {}

	/** Initialise l'arbitre avec les joueurs blanc et noir et créer un nouvel historique
     * @param joueurBlanc le joueur blanc
     * @param joueurNoir le joueur noir
     */
    public void init(Joueur joueurBlanc,Joueur joueurNoir) {
		this.joueurBlanc = joueurBlanc;
		this.joueurNoir = joueurNoir;
	}
    
	/** Arbitre la partie décrite par une chaine FEN 
	 * (position des pièces, le joueur devant jouer, les roques possibles,
	 * la possibilité d'une prise en passant, halfmove counter et fullmove counter).
	 * @param fen la chaine de caractères en question
	 * @throws FenException 
	 */
	public void arbitrer(FEN fen) throws FenException {
		fenDepart = fen;
		replay = false;
		echiquier = new Echiquier(joueurBlanc, joueurNoir, fen, this);
		doitJouer = fen.getTour() == 'w' ? joueurBlanc : joueurNoir ;
		finie = false;
		vue = new EchecSwing(echiquier);
		historique = historique == null ? new Historique(joueurBlanc, joueurNoir)
										: historique;
		tour = new TourDeJeu(1);
		doitJouer.jouer(echiquier);
	}
	
	public void rejouer(FEN fen, Historique hist) throws FenException {
		fenDepart = fen;
		replay = true;
		echiquier = new Echiquier(joueurBlanc, joueurNoir, fen, this);
		doitJouer = fen.getTour() == 'w' ? joueurBlanc : joueurNoir ;
		finie = false;
		vue = new EchecSwing(echiquier);
		historique = new Historique(joueurBlanc, joueurNoir);
		tour = new TourDeJeu(1);
		int i = 0;
		for (TourDeJeu tour : hist.getCoupsJoues()){
			Coup coupBlanc = tour.coupBlanc;
			Coup coupNoir = tour.coupNoir;
			PiecePanel depart1 = vue.getCase(coupBlanc.getcaseDepart());
			PiecePanel arrivee1 = vue.getCase(coupBlanc.getCaseFin());
			Timer timer = new Timer(500 + i * 2000, new ActionClique(depart1));
			timer.setRepeats(false);
			timer.start();
			Timer timer2 = new Timer(1000 + i * 2000, new ActionClique(arrivee1));
			timer2.setRepeats(false);
			timer2.start();
			PiecePanel depart2 = vue.getCase(coupNoir.getcaseDepart());
			PiecePanel arrivee2 = vue.getCase(coupNoir.getCaseFin());
			Timer timer3 = new Timer(1500 + i * 2000, new ActionClique(depart2));
			timer3.setRepeats(false);
			timer3.start();
			Timer timer4 = new Timer(2000 + i * 2000, new ActionClique(arrivee2));
			timer4.setRepeats(false);
			timer4.start();
			i++;
		}
	}
	
	public void rejouer(Historique hist) {
		echiquier = new Echiquier(joueurBlanc, joueurNoir, this);
		doitJouer = joueurBlanc;
		replay = true;
		finie = false;
		fenDepart = new FEN(this);
		vue = new EchecSwing(echiquier);
		historique = new Historique(joueurBlanc, joueurNoir);
		tour = new TourDeJeu(1);
		for (TourDeJeu tour : hist.getCoupsJoues()){
			Coup coupBlanc = tour.coupBlanc;
			Coup coupNoir = tour.coupNoir;
			PiecePanel depart1 = vue.getCase(coupBlanc.getcaseDepart());
			PiecePanel arrivee1 = vue.getCase(coupBlanc.getCaseFin());
			Timer timer = new Timer(50, new ActionClique(depart1));
			timer.setRepeats(false);
			timer.start();
			timer = new Timer(100, new ActionClique(arrivee1));
			timer.setRepeats(false);
			timer.start();
			PiecePanel depart2 = vue.getCase(coupNoir.getcaseDepart());
			PiecePanel arrivee2 = vue.getCase(coupNoir.getCaseFin());
			timer = new Timer(150, new ActionClique(depart2));
			timer.setRepeats(false);
			timer.start();
			timer = new Timer(200, new ActionClique(arrivee2));
			timer.setRepeats(false);
			timer.start();
		}
	}

	public void AnnulerCoup(){	
			if (this.historique.taille() > 0){
				TourDeJeu dernierTour = this.historique.supprimerDernier();
				Coup dernierCoupBlanc = dernierTour.coupBlanc;
				Coup dernierCoupNoir = dernierTour.coupNoir;
				Coup Coup1, Coup2;
				PiecePanel depart, arrivee;
				
				Joueur j = doitJouer;
				if (doitJouer.getCouleur() == Couleur.NOIR){
					Coup1 = dernierCoupBlanc;
					Coup2 = dernierCoupNoir;
					doitJouer = joueurBlanc;
				}else{
					Coup2 = dernierCoupBlanc;
					Coup1 = dernierCoupNoir;
					doitJouer = joueurNoir;
				}

				//annulation du coup blanc
				depart = vue.getCase(Coup1.getCaseFin());
				arrivee = vue.getCase(Coup1.getPiece());
				Timer timer = new Timer(50, new ActionClique(depart));
				timer.setRepeats(false);
				timer.start();
				timer = new Timer(100, new ActionClique(arrivee));
				timer.setRepeats(false);
				timer.start();
				//annulation du coup noir
				doitJouer = joueurNoir;
				depart = vue.getCase(Coup2.getCaseFin());
				arrivee = vue.getCase(Coup2.getPiece());
				timer = new Timer(50, new ActionClique(depart));
				timer.setRepeats(false);
				timer.start();
				timer = new Timer(100, new ActionClique(arrivee));
				timer.setRepeats(false);
				timer.start();

				doitJouer = j;
			}
		
	}

	/** Arbitre la partie entre joueurBlanc et joueurNoir
	 * @param echiquier échiquier sur lequel les jouueurs vont s'affronter
	 */
	public void arbitrer(Echiquier echiquier) {
		this.echiquier = echiquier;
		replay = false;
		doitJouer = joueurBlanc;
		finie = false;
		fenDepart = new FEN(this);
		vue = new EchecSwing(echiquier);
		historique = historique == null ? new Historique(joueurBlanc, joueurNoir)
										: historique;
		tour = new TourDeJeu(1);
		doitJouer.jouer(echiquier);
	}
	
	/** Créer un nouvel échiquier de base avec joueurBlanc et joueurNoir et arbitre
	 * la partie entre les 2.
	 */
	public void arbitrer() {
		arbitrer(new Echiquier(joueurBlanc, joueurNoir, this));
	}
	
	/** Permet de créer un nouveau joueur humain et de l'assigné au bon attribut.
	 * @param couleur La couleur du joueur
	 * @param nom Le nom du joueur
	 */
	public void nouveauHumain(Couleur couleur, String nom) {
		if (couleur.equals(Couleur.BLANC)) {
			joueurBlanc = new Joueur(nom, couleur, this);
		} else {
			joueurNoir = new Joueur(nom, couleur, this);
		}
	}

	/** Permet de créer un nouveau joueur humain avec une limite de temps
	 *  et de l'assigné au bon attribut.
	 * @param couleur La couleur du joueur
	 * @param nom Le nom du joueur
	 * @param temps La limite de temps pour le joueur
	 */
	public void nouveauHumain(Couleur couleur, String nom, float temps) {
		if (couleur.equals(Couleur.BLANC)) {
			joueurBlanc = new Joueur(nom, couleur, this, temps);
		} else {
			joueurNoir = new Joueur(nom, couleur, this, temps);
		}
	}

	/** Permet de créer un nouveau bot avec une limite de temps
	 *  et de l'assigné au bon attribut.
	 * @param couleur La couleur du bot
	 * @param difficulte Le type de bot à utiliser, pour l'instant seulement naïf
	 * @param temps La limite de temps pour le bot
	 */
	public void nouveauBot(Couleur couleur, Class<?> difficulte, float temps) {
		Object[] args = {couleur, this, temps};
		Class<?>[] classes = {couleur.getClass(), this.getClass(), float.class};
		try {
			if (couleur.equals(Couleur.BLANC)) {
				joueurBlanc = (Joueur) difficulte.getConstructor(classes).newInstance(args);
			} else {
				joueurNoir = (Joueur) difficulte.getConstructor(classes).newInstance(args);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/** Permet de créer un nouveau bot de recherche avec une limite de temps
	 *  et de l'assigné au bon attribut.
	 * @param couleur La couleur du bot
	 * @param difficulte Le type de bot à utiliser, pour l'instant seulement basique
	 * @param profondeur La profondeur de recherche maximum du bot
	 * @param temps La limite de temps pour le bot
	 */	
	public void nouveauBot(Couleur couleur, Class<?> difficulte, int profondeur, float temps) {
		Object[] args = {couleur, profondeur, this, temps};
		Class<?>[] classes = {couleur.getClass(), int.class, this.getClass(), float.class};
		try {
			if (couleur.equals(Couleur.BLANC)) {
				joueurBlanc = (Joueur) difficulte.getConstructor(classes).newInstance(args);
			} else {
				joueurNoir = (Joueur) difficulte.getConstructor(classes).newInstance(args);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/** Gère l'action à effectuer quand on clique sur une case 
	 * @param panel
	 */
	public void clique(PiecePanel panel) {
		
		Piece piecemangee=null;
		String message = null;
		if (!finie) {
			if (depart == null) {
				if (!echiquier.caselibre(panel.x, panel.y)) {
					if (echiquier.getJoueur(panel.x, panel.y) == doitJouer) {
						panel.setBackground(EchecSwing.couleurRose);
						depart = echiquier.getPiece(panel.x, panel.y);
						source = panel;
						vue.visualisezPossibilites(depart);
					} else {
						vue.afficherMessage("Erreur ! C'est à " + doitJouer.getNom() +" de jouer", "Mauvais joueur", replay);
					}
				}
			} else {
				//annuler le coup si on appuie deux fois sur la meme piece 
				if (panel == source) {
					depart = null;
					panel.dessinerCase();
					panel.repaint();
					panel.revalidate();
					vue.visualisezPossibilites(depart);
				} else {
					try {
					
						//deplacer la piece
					
						piecemangee=echiquier.getPiece(panel.x, panel.y);
						
						destination = panel;
						Coup leCoup = new Coup(depart, new Case(panel.x, panel.y));
						echiquier.deplacement(panel.x, panel.y, depart);
						tour.ajouterCoup(leCoup);
						
						//actualiser la gui
						destination.dessinerCase();
						source.removeAll();
						source.revalidate();
						source.repaint();
						source.dessinerCase();
						
						
						if (depart.getCode() == 'p' && (depart.getX() == 1 || depart.getX() == 8)) {
							if (doitJouer instanceof Bot) {
								((Bot) doitJouer).transformerPion(depart.getX(), depart.getY());
								
							} else {
								while (echiquier.getPiece(depart.getX(), depart.getY()).getCode() == 'p') {
									vue.messageTransformation(depart.getX(), depart.getY());
								}
							}
							destination.dessinerCase();
							source.dessinerCase();
							source.removeAll();
							source.revalidate();
							source.repaint();
							
						}
						depart = null;
						vue.visualisezPossibilites(depart);
						vue.MajHist();
						if (echiquier.getJoueurNoir().getTemps() != 0) {
							if (this.getDoitJouer().getCouleur() == Couleur.BLANC){
								vue.timerBlanc.stop();
								vue.timerNoir.start();
							} else {
								vue.timerNoir.stop();
								vue.timerBlanc.start();
							}
						}

						changerDoitJouer();
						if (piecemangee!=null) {
							vue.majPieceMangee(piecemangee);
						}
						if (estMat(trouverRoi(echiquier.getListePieces(), doitJouer))) {
							if ( vue.timerBlanc != null && vue.timerNoir != null) {
								vue.timerBlanc.stop();
								vue.timerNoir.stop();
							}
							finie = true;
							changerDoitJouer();
							message = "Le joueur " + doitJouer.getNom() +" a gagné !";
							vue.messageDeFin(message, replay);
						} else if (partieNulle(echiquier.getListePieces(), doitJouer)) {
							if ( vue.timerBlanc != null && vue.timerNoir != null) {
								vue.timerBlanc.stop();
								vue.timerNoir.stop();
							}
							finie = true;
							message = doitJouer.getNom() + " ne peut rien faire, c'est donc une égalité !";
							vue.messageDeFin(message, replay);
						} else if (echiquier.getHalfMove() == 100) {
							if ( vue.timerBlanc != null && vue.timerNoir != null) {
								vue.timerBlanc.stop();
								vue.timerNoir.stop();
							}
							finie = true;
							message = "Il n'y a eu aucune prise ni mouvement de pion dans les 50 derniers tours. C'est donc une égalité !";
							vue.messageDeFin(message, replay);
						} else if (joueurEchec(doitJouer, echiquier, echiquier.getListePieces())) {
							vue.afficherMessage(doitJouer.getNom() + " est en échec !", "En échec !", replay);
						} 
						//System.out.println(FEN.arbitreToString(this));
						if (!finie) {
							doitJouer.jouer(echiquier);
						}
					} catch (CoupInvalideException e) {
						vue.afficherMessage(e.getMessage(), "Coup invalide !", replay);
					} catch (Throwable e) {
						//System.out.println(echiquier.getListePieces());
						throw e;
					}
				}
			}
		} else {
			vue.messageDeFin(message, replay);
		}
	}
	
	/** Renvoie la pièce déplacée pour le coup en cours.
	 * Peut être null.
	 * @return La pièce déplacée
	 */
	public Piece getDepart() {
			return depart;
	}
	
	/** Fixe la pièce déplacée pour le coup en cours.
	 * Peut être null.
	 * @param p La pièce déplacée
	 */
	public void setDepart(Piece p) {
			depart = p;
	}
	
	/** Renvoie le joueur blanc dans la partie en cours
	 * @return Le joueur blanc
	 */
	public Joueur getBlanc() {
		return joueurBlanc;
	}
	
	/** Renvoie le joueur noir dans la partie en cours
	 * @return Le joueur noir
	 */
	public Joueur getNoir() {
		return joueurNoir;
	}
	
	/** Renvoie le joueur dont c'est le tour dans la partie en cours
	 * @return Le joueur qui doit jouer
	 */
	public Joueur getDoitJouer() {
		return doitJouer;
	}
	
	/** Permet de passer d'un joueur au suivant.
	 * Met également à jour l'historique avec les coups joués à ce tour.
	 */
	protected void changerDoitJouer() {
		if (doitJouer == joueurBlanc) {
			doitJouer = joueurNoir;
		} else {
			doitJouer = joueurBlanc;
			historique.ajouter(tour);
			tour = new TourDeJeu(echiquier.getFullMove());
			//historique.afficher();
		}
	}
	
	/** Renvoie l'échiquier sur lequel se déroule la partie.
	 * @return l'échiquier
	 */
	public Echiquier getEchiquier() {
		return echiquier;
	}
	
	/** L'historique de la partie en cours.
	 * @return l'historique
	 */
	public Historique getHistorique() {
		return historique;
	}

	/** Permet de savoir si un joueur est mis en échec par une pièce adverse du plateau
	 * @param joueur le joueur en question
	 * @param echiquier l'échiquier en cours
	 * @param pieceEnJeu le liste des pièces en jeu
	 * @return vrai si le joueur est en échec
	 */
	public static boolean joueurEchec (Joueur joueur, Echiquier echiquier, List<Piece> piecesEnJeu) {
		return joueurEchec(joueur.getCouleur(), echiquier, piecesEnJeu);
	}

	/** Permet de savoir si un joueur est mis en échec par une pièce adverse du plateau
	 * @param couleur la couleur du joueur en question
	 * @param echiquier l'échiquier en cours
	 * @param pieceEnJeu le liste des pièces en jeu
	 * @return vrai si le joueur est en échec
	 */
	public static boolean joueurEchec(Couleur couleur, Echiquier echiquier, List<Piece> piecesEnJeu) {
		Roi roi = (Roi) trouverRoi(piecesEnJeu, couleur);
		return roi.estEchec(echiquier, piecesEnJeu);
	}

	/** Trouve le roi d'un joueur sur le plateau
	 * @param pieces la liste des pieces de la partie
	 * @param proprietaire le joueur dont on cherche le roi
	 * @return le Roi du joueur 
	 */
	public static Roi trouverRoi(List<Piece> pieces, Joueur proprietaire) { 
		return trouverRoi(pieces, proprietaire.getCouleur());
	}
	
	/** Trouve le roi d'une couleur sur le plateau
	 * @param pieces la liste des pieces de la partie
	 * @param couleur la couleur du roi
	 * @return le Roi de même couleur
	 */
	public static Roi trouverRoi(List<Piece> pieces, Couleur couleur) {
		for (Piece piece : pieces) {
			if (piece.getNomPiece().contains("Roi") && piece.getProprio().getCouleur().equals(couleur)){
				return (Roi) piece;
			}	
		}
		return null;
	}
	
	/** Permet de déterminer si un roi est mat
	 * @param roi le roi que l'on teste
	 * @return vrai si le roi est mat
	 */
	public boolean estMat(Roi roi) {
		return estMat(roi, echiquier);
	}
	
	/** Méthode statique pour déterminer si un roi est mat
	 * @param roi le roi en question
	 * @param echiquier l'échiquier sur lequel on joue
	 * @return vrai si le roi est mat
	 */
	public static boolean estMat(Roi roi, Echiquier echiquier) {
		if (!roi.estEchec(echiquier, echiquier.getListePieces())) {
			return false;
		}
		for (Piece piece : echiquier.getListePieces()) {
			if (piece.getProprio() == roi.getProprio()) {
				for (int i = 1; i<9; i++) {
					for (int j = 1; j<9; j++) {
						 if (piece.deplacementValideReel(i, j)) {
							 return false;
						 }
					}
				}
			}
		}
		return true;
	}
	
	/** Méthode statique pour déterminer si un joueur est mat
	 * @param couleur la couleur du joueur en question
	 * @param echiquier l'échiquier sur lequel on joue
	 * @return vrai si le roi est mat
	 */
	public static boolean joueurMat(Couleur couleur, Echiquier echiquier) {
		return estMat(trouverRoi(echiquier.getListePieces(), couleur), echiquier);
	}
	
	/** Permet de déterminer si une partie est nulle, 
	 * i.e. un joueur n'a plus de coup valide mais n'est pas mat
	 * @param pieces le liste des pièces en jeu
	 * @param joueur le joueur en question
	 * @return vrai si le joueur ne peut plus jouer
	 */
	public boolean partieNulle(List<Piece> pieces, Joueur joueur) {
		return partieNulle(pieces, joueur, echiquier);
	}
	
	/** Méthode statique pour déterminer si une partie est nulle, 
	 * i.e. un joueur n'a plus de coup valide mais n'est pas mat
	 * @param pieces le liste des pièces en jeu
	 * @param joueur le joueur en question
	 * @param echiquier l'échiquier sur lequel on joue
	 * @return vrai si le joueur ne peut plus jouer
	 */
	public static boolean partieNulle(List<Piece> pieces, Joueur joueur, Echiquier echiquier) {
		return partieNulle(pieces, joueur.getCouleur(), echiquier);
	}
	
	/** Méthode statique pour déterminer si une partie est nulle, 
	 * i.e. un joueur n'a plus de coup valide mais n'est pas mat
	 * @param pieces le liste des pièces en jeu
	 * @param couleur la couleur du joueur en question
	 * @param echiquier l'échiquier sur lequel on joue
	 * @return vrai si le joueur ne peut plus jouer
	 */
	public static boolean partieNulle(List<Piece> pieces, Couleur couleur, Echiquier echiquier) {
		for (Piece piece : pieces) {
			if (piece.getProprio().getCouleur().equals(couleur)) {
				List<Case> liste = piece.listeDeplacementsValidesReels(echiquier);
				if (liste.size() != 0) {
						 return false;
				 }
			}
		}
		return true;
	}
	
	/** Permet de trouver une pièce grâce à son nom
	 * @param nomPiece le nom de la pièce sous forme d'une chaine de caractères
	 * @param proprio le propriétaire de la pièce que l'on recherche
	 * @return le pièce que l'on a trouvée ou null si elle n'existe pas
	 */
	public Piece trouverPiece(String nomPiece, Joueur proprio) {
		for (Piece p : echiquier.getListePieces()) {
			if (p.getNomPiece().equals(nomPiece) && p.getProprio()== proprio) {
				return p;
			}
		}
		return null;
		
	}

	/** Permet d'obtenir la FEN de la position de départ.
	 * @return la FEN de départ
	 */
	public FEN getFenDepart() {
		return fenDepart;
	}
	
	protected class ActionClique implements ActionListener {
		
		/** Le panel sur lequel cliquer */
		PiecePanel panel;
		
		public ActionClique(PiecePanel panel) {
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clique(panel);
		}
	}

}