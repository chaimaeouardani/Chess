package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Jeu.Arbitre;
import Jeu.Couleur;
import Jeu.CoupInvalideException;
import Jeu.Echiquier;
import Jeu.FEN;
import Jeu.Joueur;
import Jeu.Pion;

public class TestPion {

	private Arbitre arbitre;
	private Echiquier echiquier; 
	private Joueur j1, j2;
	private Pion pion;

	@Before
	public void setUp() throws Exception {
		arbitre = new Arbitre();
		j1 = new Joueur("j1", Couleur.BLANC, arbitre);
		j2 = new Joueur("j2", Couleur.NOIR, arbitre);
		FEN fen = new FEN("k7/8/8/8/8/8/P7/7K w kqKQ - 0 1");
		echiquier = new Echiquier(j1, j2, fen, arbitre);
		pion = (Pion) echiquier.getListePieces().get(1);
	}

	//Vérification de la validité des déplacements sur un échiquier libre
	@Test
	public void testDeplacementsLibresSurEchiquier() {
		for (int i = 1; i<9; i++) {
			for (int j = 1; j < 9; j++) {
				int dx = i - pion.getX();
				boolean coupValide = ((dx == -2)||(dx==-1))&&(j==pion.getY());
				assertEquals(coupValide, pion.deplacementValide(i, j));
			}
		}
	}

	//Vérification bloquage par une pièce alliée ou ennemie sur le chemin
	//On ne teste pas la case même car c'est déjà testé par la classe Piece.
	@Test
	public void testDeplacementInterditPieceChemin() {
		Pion pb = new Pion(pion.getX()-1, pion.getY(), j1, echiquier);
		echiquier.ajouterPiece(pb);
		assertFalse(pion.deplacementValide(pion.getX()-2, pion.getY()));
	}

	//Vérification déplacement autorisée si on prend une piece ennemie
	@Test
	public void testDeplacementPrise() {
		Pion p2 = new Pion(pion.getX()-1, pion.getY()+1, j2, echiquier);
		echiquier.ajouterPiece(p2);
		assertTrue(pion.deplacementValide(pion.getX()-1, pion.getY()+1));
	}

	//Vérification de la prise en passant
	@Test
	public void testPriseEnPassant() {
		Pion p2 = new Pion(pion.getX()-2, pion.getY()+1, j2, echiquier);
		echiquier.ajouterPiece(p2);
		try {
			pion.deplacer(pion.getX()-2, pion.getY());
		} catch (CoupInvalideException e) {
			fail();
		}
		assertTrue(p2.deplacementValide(p2.getX()+1, p2.getY()-1));
	}
}
