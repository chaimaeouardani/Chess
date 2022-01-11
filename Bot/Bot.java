package Bot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Jeu.Arbitre;
import Jeu.Couleur;
import Jeu.Echiquier;
import Jeu.Joueur;
import Jeu.PiecePanel;

/** Classe abstraite servant de base aux bots
 * @author Alexandre
 */
public abstract class Bot extends Joueur{
	
	/** Constructeur de base du bot sans limite de temps 
	 * @param nom Le nom du bot
	 * @param couleur La couleur du bot (blanc ou noir)
	 * @param arbitre L'arbitre qui surveille ce bot
	 */
	public Bot(String nom, Couleur couleur, Arbitre arbitre) {
		super(nom, couleur, arbitre);
	}
	
	/** Constructeur de base du bot sanas limite de temps 
	 * @param nom Le nom du bot
	 * @param couleur La couleur du bot (blanc ou noir)
	 * @param arbitre L'arbitre qui surveille ce bot
	 * @param temps La limite de temps sur la partie pour le bot
	 */
	public Bot(String nom, Couleur couleur, Arbitre arbitre, float temps) {
		super(nom, couleur,arbitre, temps);
	}

	/** Méthode permettant au bot de jouer un coup.
	 * Elle est rédéfinie dans chaque bot car chacun a sa façon de jouer.
	 * @param echiquier L'échiquier sur lequel jouer
	 */
	public abstract void jouer(Echiquier echiquier);
	
	/** Permet d'obtenir une copie du bot avec un nouvel arbitre.
	 * Utilisé quand l'utilisateur demande de recommencer une partie.
	 * @param arbitre Le nouvel arbitre à utiliser avec le nouveau Bot
	 */
	public abstract Bot copy(Arbitre arbitre);
	
	/** On part du principe pour l'instant que tous les bots transformes un Pion
	 * en Dame quand il en a l'occasion.
	 * @param x La ligne du pion
	 * @param y La colonne du pion
	 */
	public void transformerPion(int x, int y) {
		arbitre.getEchiquier().transformerPion(x, y, "Dame");
	}
	
	/** Classe action permettant au bot d'intéragie avec l'échiquier.
	 * @author Alexandre
	 */
	protected class ActionClique implements ActionListener {
		
		/** Le panel sur lequel cliquer */
		PiecePanel panel;
		
		public ActionClique(PiecePanel panel) {
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			arbitre.clique(panel);
		}
	}

}
