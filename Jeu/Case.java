package Jeu;

/** Créer une case, i.e, un couple de deux entiers. 
 * @author Fabio, Alexandre
 */
public class Case {
	private int x;
	private int y;

	public Case(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public int getX() {
		return x;
	}

	void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	void setY(int y) {
		this.y = y;
	}
	
	public Case copy() {
		return new Case(x, y);
	}
	
	/** Créer une case à partir de sa SAN.
	 * @param SAN la SAN à utiliser
	 */
	public Case(String SAN) {
		int debut = (int)('a') - 1;
		y = ((int) SAN.charAt(0)) - debut;
		x = Echiquier.tailleX + 1 - Character.getNumericValue(SAN.charAt(1));
		if (x<1 || x>8 || y<1 || y>8) {
			throw new RuntimeException("La case indiquée pour la prise en passant"
					+ " est invalide.");
		}
	}
	
	/** Renvoie la short algebraic notation (SAN) de la case, i.e. avec des lettres
	 * pour les colonnes et le bon point de départ pour les lignes.
	 * @return la SAN
	 */
	public String toSAN() {
		int debut = (int)('a') - 1;
		return String.valueOf((char)(debut + y)) + (Echiquier.tailleX + 1 - x);
	}
	
	public String toString() {
		return "[" + getX() + ", " + getY() + "]";
	}
}