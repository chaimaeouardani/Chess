package Jeu;

/** Exception levée lorsqu'un coup invalide est tenté
 * @author Alexandre
 */
public class CoupInvalideException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public CoupInvalideException(String message) {
		super(message);
	}
}
