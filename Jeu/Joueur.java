package Jeu;

import java.util.List;

/** Un joueur humain ou un bot.
 * @author Alexandre, Briag, Chaimae
 */
public class Joueur {
	
	/** Le nom du joueur */
	private String nom;

	/** La couleur du joueur */
	public Couleur couleur; 
	
	/** Le temps restant pour le joueur sur la partie */
	private float temps;
	
	/** Le temps de base du joueur, 0 signifiant aucune limite de temps */
	private float tempsBase;
	

	
	/** L'arbitre sur la partie que le joueur joue */
	protected Arbitre arbitre;
	
	/** Construit un nouveau Joueur à partir de son nom, de sa couleur et de l'arbitre
	 * qui le supervisera.
	 * @param nom le nom du joueur
	 * @param couleur la couleur du joueur
	 * @param arbitre l'arbitre
	 */
	public Joueur(String nom, Couleur couleur, Arbitre arbitre) {
		this(nom, couleur, arbitre, 0);
	}
	
	/** Construit un nouveau Joueur à partir de son nom, de sa couleur et de l'arbitre
	 * qui le supervisera et du temps dont il disposera pour jouer.
	 * @param nom le nom du joueur
	 * @param couleur la couleur du joueur
	 * @param arbitre l'arbitre
	 * @param temps le temps dont le joueur dispose sur la totalité de la partie
	 */
	public Joueur(String nom, Couleur couleur, Arbitre arbitre, float temps) {
		this.nom = nom;
		this.couleur = couleur;
		this.arbitre = arbitre;
		this.temps=temps;
		this.tempsBase = temps;
	}

	/** Renvoie la couleur du joueur.
	 * @return la couleur du joueur
	 */
	public Couleur getCouleur() {
		return this.couleur;
	}
	
	/** Renvoie le nom du joueur.
	 * @return le nom du joueur
	 */
	public String getNom() {
		return nom;
	}
	
	/** Renvoie le signe associé au joueur pour le calcul du score de l'échiquier.
	 * Positif pour les blancs et négatif pour les noirs.
	 * @return le signe du joueur
	 */
	public int getSigne() {
		return 1-2*couleur.ordinal();
	}
	
	/** Méthode permettant au joueur de jouer. Elle est vide base car si c'est un humain
	 * c'est lui qui joue et sinon elle est redéfinie dans les bots avec leur stratégie.
	 * @param echiquier l'échiquier sur lequel jouer
	 */
	public void jouer(Echiquier echiquier) {}
	
	/** Renvoie le nom du joueur et sa couleur.
	 * @return le nom du joueur et sa couleur
	 */
	public String toString() {
		return nom + " " + couleur.toString();
	}
	
	/** Renvoie le temps restant au joueur sur la partie.
	 * @return le temps restant au joueur sur la partie
	 */
	public float getTemps() {
		return temps;
	}
	
	/** Décremente le temps du joueur de 100 millisecondes */
	public void decTemps() {
		temps-=0.1;
	}
	
	/** Renvoie le temps de base du joueur, i.e. celui qu'il avait au début de la partie.
	 * @return le temps de base du joueur
	 */
	public float getTempsBase() {
		return tempsBase;
	}
	public void setTempsnull() {
		
	}
	
	/** Renvoie l'arbitre sur la partie que le joueur joue.
	 * @return l'arbitre du joueur
	 */
	public Arbitre getArbitre() {
		return arbitre;
	}

	/** Renvoie un nouveau joueur identique au précédent avec son temps réinitialisé
	 *  et avec un nouvel arbitre à partir d'un joueur déjà existant.
	 * @param arbitre2 
	 * @return le nouveau joueur 
	 */
	public Joueur copy(Arbitre arbitre) {
		return new Joueur(nom, couleur, arbitre, tempsBase);
	}
	
	
}



