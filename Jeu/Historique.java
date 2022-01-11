package Jeu;

import java.time.LocalDateTime;
import java.util.Stack;

/** Enregistre l'historique d'une partie sous la forme d'une pile d'objet de la
 * classe TourDeJeu.
 * @author Chaimae, Alexandre, Fabio
 */
public class Historique {
    
    /** Chaque déplacement est enregistré sous forme de (pieceAvant,pieceApres) dans une pile */
    private Stack<TourDeJeu> coupsJoues;

    /** La date et l'heure ou la partie a été lancée */
    private LocalDateTime datePartie;

    /** les deux joueurs de la partie  */
    @SuppressWarnings("unused")
	private Joueur joueur1;
    @SuppressWarnings("unused")
	private Joueur joueur2;


    /** Constructeur */
    public Historique(Joueur j1, Joueur j2){
        coupsJoues = new Stack<TourDeJeu>();
        datePartie = LocalDateTime.now();
        joueur1 = j1;
        joueur2 = j2;
    }

    /** Enregistrer un coup
     * @param coup Coup
     */
    public void ajouter(TourDeJeu tour){    
        coupsJoues.push(tour);
    }
    
    /** Supprimer le dernier coup et le retourner
     */
    public TourDeJeu supprimerDernier(){ 
        return coupsJoues.pop();
    }

    /** Obtenir le nombre des coups enregistrés dans l'historique */
    public int taille(){ 
        return coupsJoues.size();
    }

    public String toString() { 
        String info = "Partie jouée le " + datePartie + " : \n " ; 
        int i = 0;            
        for (TourDeJeu tour : coupsJoues) {
            info +=  tour + " ";
            i++;
            if (i==3){
                info += "\n";
                i = 0;
            }
        } 
        return info;
    }

    /** Afficher l'historique*/
    public void afficher(){
        System.out.println(this);
    }


    @SuppressWarnings("unused")
	public Stack<TourDeJeu> getCoupsJoues() {
    	return coupsJoues; 
    }
    
    // Méthodes utiles pour la classe Profils
    
    /** Obtenir le nom du joueur 1 **/
    public String getJ1() {
    	return this.joueur1.getNom(); 
    }
    
    /** Obtenir le nom du joueur 2 **/
    public String getJ2() {
    	return this.joueur2.getNom();
    }
    
    /** Obtenir la date et l'heure du début de la partie **/
    public LocalDateTime getDate() {
    	return this.datePartie;
    }

}
