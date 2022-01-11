package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Jeu.Arbitre;
import Jeu.Couleur;
import Jeu.Echiquier;
import Jeu.FEN;
import Jeu.Joueur;
import Jeu.Pion;
import Jeu.Fou;

public class TestFou {

	private Arbitre arbitre;
	private Echiquier echiquier; 
	private Joueur j1, j2;
	private Fou fou;

	@Before
	public void setUp() throws Exception {
		arbitre = new Arbitre();
		j1 = new Joueur("j1", Couleur.BLANC, arbitre);
		j2 = new Joueur("j2", Couleur.NOIR, arbitre);
		FEN fen = new FEN("7k/8/8/3B4/8/8/8/K7 w kqKQ - 0 1");
		echiquier = new Echiquier(j1, j2, fen, arbitre);
		fou = (Fou) echiquier.getListePieces().get(1);
	}

	//Vérification de la validité des déplacements sur un échiquier libre
	@Test
	public void testDeplacementsLibresSurEchiquier() {
		for (int i = 1; i<9; i++) {
			for (int j = 1; j < 9; j++) {
				boolean coupValide = (Math.abs(i-fou.getX())==Math.abs(j-fou.getY()))&&!(i==fou.getX()&&j==fou.getY());
				assertEquals(coupValide, fou.deplacementValide(i, j));
			}
		}
	}

	//Vérification bloquage par une pièce alliée ou ennemie sur le chemin
	//On ne teste pas la case même car c'est déjà testé par la classe Piece.
	@Test
	public void testDeplacementInterditPieceChemin() {
		Pion pb = new Pion(fou.getX()+1, fou.getY()+1, j1, echiquier);
		echiquier.ajouterPiece(pb);
		Pion pn = new Pion(fou.getX()-1, fou.getY()-1, j2, echiquier);
		echiquier.ajouterPiece(pn);
		assertFalse(fou.deplacementValide(fou.getX()+2, fou.getY()+2));
		assertFalse(fou.deplacementValide(fou.getX()-2, fou.getY()-2));
		assertTrue(fou.deplacementValide(fou.getX()-2, fou.getY()+2)); //On vérifie que ça n'interfère pas sur les autres déplacements
	}

	//Vérification déplacement autorisée si on prend une piece ennemie
	@Test
	public void testDeplacementPrise() {
		Pion p = new Pion(fou.getX()+1, fou.getY()+1, j2, echiquier);
		echiquier.ajouterPiece(p);
		assertTrue(fou.deplacementValide(fou.getX()+1, fou.getY()+1));
	}

}
