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
import Jeu.Tour;

public class TestTour {

	private Arbitre arbitre;
	private Echiquier echiquier; 
	private Joueur j1, j2;
	private Tour tour;

	@Before
	public void setUp() throws Exception {
		arbitre = new Arbitre();
		j1 = new Joueur("j1", Couleur.BLANC, arbitre);
		j2 = new Joueur("j2", Couleur.NOIR, arbitre);
		FEN fen = new FEN("k7/8/8/3R4/8/8/8/7K w kqKQ - 0 1");
		echiquier = new Echiquier(j1, j2, fen, arbitre);
		tour = (Tour) echiquier.getListePieces().get(1);
	}

	//Vérification de la validité des déplacements sur un échiquier libre
	@Test
	public void testDeplacementsLibresSurEchiquier() {
		for (int i = 1; i<9; i++) {
			for (int j = 1; j < 9; j++) {
				boolean coupValide = (i == tour.getX() || j== tour.getY())&&!(i==tour.getX()&&j==tour.getY());
				assertEquals(coupValide, tour.deplacementValide(i, j));
			}
		}
	}

	//Vérification bloquage par une pièce alliée ou ennemie sur le chemin
	//On ne teste pas la case même car c'est déjà testé par la classe Piece.
	@Test
	public void testDeplacementInterditPieceChemin() {
		Pion pb = new Pion(tour.getX()+1, tour.getY(), j1, echiquier);
		echiquier.ajouterPiece(pb);
		Pion pn = new Pion(tour.getX()-1, tour.getY(), j2, echiquier);
		echiquier.ajouterPiece(pn);
		assertFalse(tour.deplacementValide(tour.getX()+2, tour.getY()));
		assertFalse(tour.deplacementValide(tour.getX()-2, tour.getY()));
		assertTrue(tour.deplacementValide(tour.getX(), tour.getY()+2)); //On vérifie que ça n'interfère pas sur les autres déplacements
	}

	//Vérification déplacement autorisée si on prend une piece ennemie
	@Test
	public void testDeplacementPrise() {
		Pion p = new Pion(tour.getX()+1, tour.getY(), j2, echiquier);
		echiquier.ajouterPiece(p);
		assertTrue(tour.deplacementValide(tour.getX()+1, tour.getY()));
	}

}
