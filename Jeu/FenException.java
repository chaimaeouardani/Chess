package Jeu;

/** L'exception levée lorsqu'une FEN est incorrecte 
 * @author Alexandre
 */
@SuppressWarnings("serial")
public class FenException extends Exception {
	
	public FenException(String message) {
		super(message);
	}

}
