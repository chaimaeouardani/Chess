package Jeu;
/** Classe permettant d'enregistrer un tour de jeu.
 * @author Alexandre
 */
public class TourDeJeu {
	
	/** Le numéro du tour */
	int nb;
	
	/** Le coup du joueur blanc */
	Coup coupBlanc;
	
	/** Le coup du joueur noir */
	Coup coupNoir;
	
	/** Créer un TourDeJeu à partir de son numéro.
	 * @param nb le numéro du tour créé
	 */
	public TourDeJeu(int nb) {
		this.nb = nb;
	}

	/** Ajoute un Coup au tour.
	 * @param coup Le coup à ajouter
	 * @throws RuntimeException si les 2 coups du tour sont déjà enregistrés
	 */
	public void ajouterCoup(Coup coup) {
		if (coupNoir != null) {
			throw new RuntimeException("Ce tour est déjà plein.");
		}
		if (coup.getPiece().getCouleur().equals(Couleur.BLANC)) {
			coupBlanc = coup;
		} else {
			coupNoir = coup;
		}
	}
	
	@Override
	public String toString() {
		if (coupBlanc == null) {
			return "0-1 {White checkmated}";
		}
		String message =  nb + ". " + coupBlanc.getSAN() + " ";
		message += coupNoir != null ? coupNoir.getSAN() + " " : "1-0 {Black checkmated}" ;
		return message;
	}

}
