package Bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.Random;
import javax.swing.Timer;
import Jeu.*;
/** Bot basique qui teste tous les coups possibles jusqu’à une profondeur donnée et 
 * calcule le score associé puis joue le “meilleur” coup possible
 * @author Alexandre
 */
public class Basique extends Bot {
	
	/** La profondeur maximale de recherche du bot */
	private int profondeurMax;
	
	/** Le maximum des points que le bot peut attribuer à un coup */
	private final static int PLAFOND = 10000;
	
	/** Compteur du nombre de coups recherchés, utilisé à des fins de débuggage */
	@SuppressWarnings("unused")
	private int i;	
	
//	/** Le generateur de nombres aléatoire.*/
//	private Random biaisAleatoire = new Random();
	
	/** Constructeur de base du bot sans limite de temps 
	 * @param couleur La couleur du bot (blanc ou noir)
	 * @param profondeur La profondeur de recherche maximale
	 * @param arbitre L'arbitre qui surveille ce bot
	 */
	public Basique(Couleur couleur, int profondeur, Arbitre arbitre) {
		this(couleur, profondeur, arbitre, 0);
	}
	
	/** Constructeur de base du bot sanas limite de temps 
	 * @param couleur La couleur du bot (blanc ou noir)
	 * @param profondeur La profondeur de recherche maximale
	 * @param arbitre L'arbitre qui surveille ce bot
	 * @param temps La limite de temps sur la partie pour le bot
	 */
	public Basique(Couleur couleur, int profondeur, Arbitre arbitre, float temps ) {
		super("Bot basique", couleur, arbitre, temps);
		profondeurMax = profondeur;
	}

	/** Calcule le score de l'échiquier donné 
	 * @param echiquier L'échiquier dont on veut calculer le score
	 * @return La valeur brut de l'échiquier (plutôt négative si les noirs ont 
	 * l'avantage et positive si ce sont les blanc)
	 */
	public double valeurPlateau(Echiquier echiquier) {
		double somme = 0;
		for (Piece piece : echiquier.getListePieces()) {
			somme += piece.getValeur();
		}
		return somme;
	}
	
	/** Initialisation de la recherche du meilleur coup avec alpha-beta pruning.
	 * @param profondeur La profondeur maximum de recherche
	 * @param echiquier L'échiquier sur lequel on travaille
	 * @return Le meilleur coup trouvé
	 */
	public Coup miniMaxInit(int profondeur, Echiquier echiquier) {
		Coup meilleurCoup = new Coup(getSigne());
		for (Piece piece : echiquier.piecesDuJoueur(this)) {
			for (Case caseFin : piece.listeDeplacementsValidesReels(echiquier)) {
				i++;
				Coup nouveauCoup = new Coup(piece.copy(echiquier), caseFin);
				try {
					nouveauCoup.eval = miniMaxOpti(profondeur - 1, echiquier.copyAvecCoup(nouveauCoup), PLAFOND, -PLAFOND, -getSigne());
					if (getSigne() == 1) {
						if (nouveauCoup.eval > meilleurCoup.eval) {
							meilleurCoup = nouveauCoup;
						}
					} else if (nouveauCoup.eval < meilleurCoup.eval) {
							meilleurCoup = nouveauCoup;
					}
				} catch (CoupInvalideException e) {
//					System.out.println("un bug ?");
//					System.out.println(piece);
//					System.out.println(caseFin);
				}
			}
		}
		return meilleurCoup; 
	}
	
	/** Initialisation de la recherche du meilleur coup avec alpha-beta pruning et 
	 * ordonnancement des coups avant recherche en profondeur pour optimiser le pruning.
	 * @param profondeur La profondeur maximum de recherche
	 * @param echiquier L'échiquier sur lequel on travaille
	 * @return Le meilleur coup trouvé
	 */
	public Coup miniMaxInit2(int profondeur, Echiquier echiquier) {
		ArrayList<Coup> coups = new ArrayList<Coup>();
		for (Piece piece : echiquier.piecesDuJoueur(this)) {
			for (Case caseFin : piece.listeDeplacementsValidesReels(echiquier)) {
				i++;
				Coup nouveauCoup = new Coup(piece.copy(echiquier), caseFin);
				try {
					nouveauCoup.eval = miniMaxOpti2(0, echiquier.copyAvecCoup(nouveauCoup), PLAFOND, -PLAFOND, -getSigne());
					coups.add(nouveauCoup);
				} catch (CoupInvalideException e) {
//					System.out.println("un bug ?");
//					System.out.println(piece);
//					System.out.println(caseFin);
				}
			}
		}
		if (getSigne() == 1 ) {
			Collections.sort(coups, Collections.reverseOrder());
		} else {
			Collections.sort(coups);
		}
		Coup meilleurCoup = new Coup(getSigne());
		for (Coup nouveauCoup : coups) {
			i++;
			try {
				nouveauCoup.eval = miniMaxOpti2(profondeur - 1, echiquier.copyAvecCoup(nouveauCoup), PLAFOND, -PLAFOND, -getSigne());
				if (getSigne() == 1) {
					if (nouveauCoup.eval > meilleurCoup.eval) {
						meilleurCoup = nouveauCoup;
					}
				} else if (nouveauCoup.eval < meilleurCoup.eval) {
						meilleurCoup = nouveauCoup;
				}
			} catch (CoupInvalideException e) {
//				System.out.println("un bug ?");
//				System.out.println(nouveauCoup.piece);
//				System.out.println(nouveauCoup.caseFin);
			}
		}
		return meilleurCoup; 
	}
	
//	/** Initialisation de la recherche du meilleur coup sans alpha-beta pruning.
//	 * @param profondeur La profondeur maximum de recherche
//	 * @param echiquier L'échiquier sur lequel on travaille
//	 * @return Le meilleur coup trouvé
//	 */
//	private double miniMaxSimple(int profondeur, Echiquier echiquier, int signe) {
//		if (profondeur == 0) {
//			return valeurPlateau(echiquier);
//		}
//		Coup meilleurCoup = new Coup(signe);
//		Couleur couleur = Couleur.values()[(1-signe)/2];
//		for (Piece piece : echiquier.piecesDuJoueur(couleur)) {
//			for (Case caseFin : piece.listeDeplacementsValidesReels(echiquier)) {
//				Coup nouveauCoup = new Coup(piece.copy(echiquier), caseFin);
//				try {
//					nouveauCoup.eval = miniMaxSimple(profondeur - 1, echiquier.copyAvecCoup(nouveauCoup), -signe);
//					if (couleur.equals(Couleur.BLANC)) {
//						if (nouveauCoup.eval > meilleurCoup.eval) {
//							meilleurCoup = nouveauCoup;
//						}
//					} else if (nouveauCoup.eval < meilleurCoup.eval) {
//						meilleurCoup = nouveauCoup;
//					}
//				} catch (CoupInvalideException e) {
//					//System.out.println("un bug ?");
//					//System.out.println("i = " + i);
//					//System.out.println(piece);
//					//System.out.println(caseFin);
//				}
//			}
//		}		
//		return meilleurCoup.eval;
//	}
	
	/** Méthode permettant le calcul du score du meilleur coup possible depuis 
	 * l'échiquier et en recherchant dans la profondeur donnée en utilisant le
	 * alpha-beta pruning.
	 * @param profondeur Le nombre de coups successifs que l'on va encore chercher
	 * @param echiquier	L'echéqui sur lequel on travaille
	 * @param alpha Le meilleur score noir trouvé jusqu'à présent
	 * @param beta Le meilleur score blanc trouvé jusqu'à présent
	 * @param signe Le signe de la recherche (+1 si cette étape est blanche -1 si noire)
	 * @return Le score du meilleur coup trouvé pour cette recherche
	 */
	private double miniMaxOpti(int profondeur, Echiquier echiquier, double alpha, double beta, int signe) {
		Couleur couleur = Couleur.values()[(1-signe)/2];
		double biaisEchec = 0;
		if (Arbitre.joueurMat(couleur, echiquier)) {
			return PLAFOND * -signe;
		}
		if (Arbitre.partieNulle(echiquier.getListePieces(), couleur, echiquier)) {
			if (valeurPlateau(echiquier) * signe > 0) {
				return PLAFOND / 2 * -signe;
			} else {
				return PLAFOND / 2 * signe;
			}
		}
		if (Arbitre.joueurEchec(couleur, echiquier, echiquier.getListePieces())) {
			biaisEchec += 20 * -signe;
		}
		if (profondeur <= 0) {
			return valeurPlateau(echiquier) + biaisEchec ;//+ (biaisAleatoire.nextDouble() - 0.5) * 20;
		}
		Coup meilleurCoup = new Coup(signe);
		for (Piece piece : echiquier.piecesDuJoueur(couleur)) {
			for (Case caseFin : piece.listeDeplacementsValidesReels(echiquier)) {
				i++;
				Coup nouveauCoup = new Coup(piece.copy(echiquier), caseFin);
				try {
					if (signe == 1) {
						nouveauCoup.eval = miniMaxOpti(profondeur - 1, echiquier.copyAvecCoup(nouveauCoup), alpha, meilleurCoup.eval, -signe);
						if (nouveauCoup.eval > meilleurCoup.eval) {
							meilleurCoup = nouveauCoup;
						} else if (nouveauCoup.eval > alpha) {
							return nouveauCoup.eval;
						}
					} else {
						nouveauCoup.eval = miniMaxOpti(profondeur - 1, echiquier.copyAvecCoup(nouveauCoup), meilleurCoup.eval, beta, -signe);
						if (nouveauCoup.eval < meilleurCoup.eval) {
							meilleurCoup = nouveauCoup;
						} else if (nouveauCoup.eval < beta) {
							return nouveauCoup.eval;
						}
					}
				} catch (CoupInvalideException e) {
//					System.out.println("un bug ?");
//					System.out.println(piece);
//					System.out.println(caseFin);
				}
			}
		}
		return meilleurCoup.eval;
	}
	
	/** Méthode permettant le calcul du score du meilleur coup possible depuis 
	 * l'échiquier et en recherchant dans la profondeur donnée en utilisant le
	 * alpha-beta pruning et l'ordonnancement des coups pour optimiser le pruning.
	 * @param profondeur Le nombre de coups successifs que l'on va encore chercher
	 * @param echiquier	L'echéqui sur lequel on travaille
	 * @param alpha Le meilleur score noir trouvé jusqu'à présent
	 * @param beta Le meilleur score blanc trouvé jusqu'à présent
	 * @param signe Le signe de la recherche (+1 si cette étape est blanche -1 si noire)
	 * @return Le score du meilleur coup trouvé pour cette recherche
	 */
	protected double miniMaxOpti2(int profondeur, Echiquier echiquier, double alpha, double beta, int signe) {
		Couleur couleur = Couleur.values()[(1-signe)/2];
		double biaisEchec = 0;
		if (Arbitre.joueurMat(couleur, echiquier)) {
			return PLAFOND * -signe;
		}
		if (Arbitre.partieNulle(echiquier.getListePieces(), couleur, echiquier)) {
			if (valeurPlateau(echiquier) * signe > 0) {
				return PLAFOND / 2 * -signe;
			} else {
				return PLAFOND / 2 * signe;
			}
		}
		if (Arbitre.joueurEchec(couleur, echiquier, echiquier.getListePieces())) {
			biaisEchec = 20 * -signe;
		}
		if (profondeur == 0) {
			return valeurPlateau(echiquier) + biaisEchec ;//+ (biaisAleatoire.nextDouble() - 0.5) * 20;
		}
		ArrayList<Coup> coups = new ArrayList<Coup>();
		for (Piece piece : echiquier.piecesDuJoueur(couleur)) {
			for (Case caseFin : piece.listeDeplacementsValidesReels(echiquier)) {
				i++;
				Coup nouveauCoup = new Coup(piece.copy(echiquier), caseFin);
				try {
					nouveauCoup.eval = miniMaxOpti2(0, echiquier.copyAvecCoup(nouveauCoup), alpha, beta, -signe);
					coups.add(nouveauCoup);
				} catch (CoupInvalideException e) {
//					System.out.println("un bug ?");
//					System.out.println(piece);
//					System.out.println(caseFin);
				}
			}
		}
		if (signe == 1 ) {
			Collections.sort(coups, Collections.reverseOrder());
		} else {
			Collections.sort(coups);
		}
		Coup meilleurCoup = new Coup(signe);
		for (Coup nouveauCoup : coups) {
			i++; 
			try {
				if (signe == 1) {
					nouveauCoup.eval = miniMaxOpti2(profondeur - 1, echiquier.copyAvecCoup(nouveauCoup), alpha, meilleurCoup.eval, -signe);
					if (nouveauCoup.eval > meilleurCoup.eval) {
						meilleurCoup = nouveauCoup;
					} else if (nouveauCoup.eval > alpha) {
						return nouveauCoup.eval;
					}
				} else {
					nouveauCoup.eval = miniMaxOpti2(profondeur - 1, echiquier.copyAvecCoup(nouveauCoup), meilleurCoup.eval, beta, -signe);
					if (nouveauCoup.eval < meilleurCoup.eval) {
						meilleurCoup = nouveauCoup;
					} else if (nouveauCoup.eval < beta) {
						return nouveauCoup.eval;
					}
				}
			} catch (CoupInvalideException e) {
//				System.out.println("un bug ?");
//				System.out.println(nouveauCoup.piece);
//				System.out.println(nouveauCoup.caseFin);
			}
		}
		return meilleurCoup.eval;
	}
	
	@Override
	public void jouer(Echiquier echiquier) {
		i = 0;
		Coup aJouer = miniMaxInit2(profondeurMax, echiquier);
		//System.out.println("i = " + i);
		int profondeur = profondeurMax;
		while (aJouer.getPiece() == null && profondeur>1) {
			System.out.println("Probleme ! recherche avec profondeur = " + (profondeur-1));
			aJouer = miniMaxInit2(--profondeur, echiquier);
		}
		if (aJouer.getPiece() == null) {
			List<Piece> pieces = echiquier.piecesDuJoueur(this);
			int incr = 0;
			while (aJouer.getPiece() == null && incr < pieces.size()) {
				Piece piece = pieces.get(incr++);
				List<Case> cases = piece.listeDeplacementsValidesReels(echiquier);
				if (cases.size() > 0) {
					aJouer.setPiece(piece);
					aJouer.setCaseFin(cases.get(0));
				}
			}
		}
		if (aJouer.getPiece() == null) {
			//System.out.println("Gros problème.");
		}
		PiecePanel depart = arbitre.vue.getCase(aJouer.getPiece());
		PiecePanel arrivee = arbitre.vue.getCase(aJouer.getCaseFin());
		Timer timer = new Timer(50, new ActionClique(depart));
		timer.setRepeats(false);
		timer.start();
		Timer timer2 = new Timer(300, new ActionClique(arrivee));
		timer2.setRepeats(false);
		timer2.start();
	}

	@Override
	public Basique copy(Arbitre arbitre) {
		return new Basique(couleur, profondeurMax, arbitre, getTempsBase());
	}

}
