package Bot;
import Jeu.*;

import java.util.List;
import java.util.Random;

import javax.swing.Timer;

/** Bot naïf qui joue au hasard 
 * @author Alexandre
 */
public class Naif extends Bot {
	
	/** Le generateur de nombres aléatoire.*/
	private Random r = new Random();
	
	/** Constructeur de base du bot sans limite de temps 
	 * @param couleur La couleur du bot (blanc ou noir)
	 * @param arbitre L'arbitre qui surveille ce bot
	 */
	public Naif(Couleur couleur, Arbitre arbitre) {
		super("Bot naïf", couleur, arbitre);
	}
	
	/** Constructeur de base du bot sanas limite de temps 
	 * @param couleur La couleur du bot (blanc ou noir)
	 * @param arbitre L'arbitre qui surveille ce bot
	 * @param temps La limite de temps sur la partie pour le bot
	 */
	public Naif(Couleur couleur, Arbitre arbitre, float temps) {
		super("Bot naif",couleur, arbitre, temps);
	}
	
	@Override
	public void jouer(Echiquier echiquier)  {
		List<Piece> pieces = echiquier.piecesDuJoueur(this);
		int nbPiece = pieces.size();
		Piece p;
		List<Case> listeCase;
		do {
			p = pieces.get(r.nextInt(nbPiece));
			listeCase = p.listeDeplacementsValidesReels(echiquier); 
		} while (listeCase.size() == 0);
		PiecePanel panel = arbitre.vue.getCase(p);
		Timer timer = new Timer(150, new ActionClique(panel));
		timer.setRepeats(false);
		timer.start();
		Case arrivee = listeCase.get(r.nextInt(listeCase.size()));
		panel = arbitre.vue.getCase(arrivee);
		timer = new Timer(300, new ActionClique(panel));
		timer.setRepeats(false);
		timer.start();
	}
	
	@Override
	public Naif copy(Arbitre arbitre) {
		return new Naif(couleur, arbitre, getTempsBase());
	}
}
