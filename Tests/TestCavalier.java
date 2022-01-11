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
import Jeu.Cavalier;

public class TestCavalier {

	private Arbitre arbitre;
	private Echiquier echiquier; 
	private Joueur j1, j2;
	private Cavalier cavalier;

	@Before
	public void setUp() throws Exception {
		arbitre = new Arbitre();
		j1 = new Joueur("j1", Couleur.BLANC, arbitre);
		j2 = new Joueur("j2", Couleur.NOIR, arbitre);
		FEN fen = new FEN("k7/8/8/3N4/8/8/8/7K w kqKQ - 0 1");
		echiquier = new Echiquier(j1, j2, fen, arbitre);
		cavalier = (Cavalier) echiquier.getListePieces().get(1);
	}

	//Vérification de la validité des déplacements sur un échiquier libre
	@Test
	public void testDeplacementsLibresSurEchiquier() {
		for (int i = 1; i<9; i++) {
			for (int j = 1; j < 9; j++) {
				int dx = i-cavalier.getX();
				int dy = j-cavalier.getY();
				boolean coupValide = (Math.abs(dx)==1&&Math.abs(dy)==2)||(Math.abs(dx)==2&&Math.abs(dy)==1);
				assertEquals(coupValide, cavalier.deplacementValide(i, j));
			}
		}
	}

	//Vérification déplacement autorisée si on prend une piece ennemie
	@Test
	public void testDeplacementPrise() {
		Pion p = new Pion(cavalier.getX()+1, cavalier.getY()+2, j2, echiquier);
		echiquier.ajouterPiece(p);
		assertTrue(cavalier.deplacementValide(cavalier.getX()+1, cavalier.getY()+2));
	}

}
