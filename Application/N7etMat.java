package Application;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Bot.*;
import Jeu.*;
import Profils.Profils;

/**
 * Classe principale de l'application, c'est elle qui gère la fenêtre de base et
 * ouvre les autres fenêtres lors du lancement d'une ou plusieurs partie(s).
 * 
 * @author Alexandre, Fabio
 */
public class N7etMat {

	/** Les dimensions de la fenêtre */
	private static Dimension dimFenetre = new Dimension(300, 200);

	/** La fenêtre */
	static JFrame fenetre;

	private static Stack<Jouer> parties;

	/** Le profil chargé */
	private static Profils profil = null;
	
	/** Méthode principale. */
	public static void main(String[] args) {

		parties = new Stack<Jouer>();

		fenetre = new JFrame("N7 et Mat");

		// Determiner la position de la fenetre
		fenetre.setLocation(500, 200);

		// Determiner les dimensions de la fenetre
		fenetre.setSize(dimFenetre);

		// Construire le contrôleur (gestion des événements)
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// afficher la fenêtre
		java.awt.Container contenu = fenetre.getContentPane();
		contenu.setLayout(new java.awt.FlowLayout());

		// Panel Principal
		JPanel principal = new JPanel();
		principal.setLayout(new java.awt.GridLayout(6, 1));
		JLabel profilTexte = new JLabel("Aucun profil sélectionné");
		principal.add(profilTexte);
		JButton jouer = new JButton("Jouer");
		jouer.addActionListener(new ActionJouer());
		principal.add(jouer);
		JButton apprentissage = new JButton("Apprentissage");
		principal.add(apprentissage);
		JButton entrainement = new JButton("Générer données");
//		entrainement.addActionListener(new ActionEntrainement());
		principal.add(entrainement);

		// Menu "Règles"
		JButton regles = new JButton("Règles");
		regles.addActionListener(new ActionOuvrirRegles());
		principal.add(regles);

		// Menu "Profil"
		JButton profil = new JButton("Profil");
		profil.addActionListener(new ActionOuvrirProfil(profilTexte));
		principal.add(profil);
		
		contenu.add(principal);

		fenetre.setVisible(true);

	}

	// Les classes Actions

	/** Action permettant de quitter le jeu. */
	class ActionQuitter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			fenetre.dispose();
			System.exit(0);
		}
	}

	/** Action permettant de commencer une partie */
	static class ActionJouer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			Jouer jouer = new Jouer(profil);
			parties.add(jouer);
			jouer.jouer();
		}
	}
	
	/** Action permettant d'ouvrir la fenêtre d'accès aux règles **/
	static class ActionOuvrirRegles implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			Menu menuRegles = new Menu();
			menuRegles.menuRegles();
		}
	}
	
	/** Action permettant d'ouvrir la fenêtre d'accès aux profils **/
	static class ActionOuvrirProfil implements ActionListener {
		JLabel label;
		public ActionOuvrirProfil(JLabel profilTexte) {
			label = profilTexte;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			Menu menuProfil = new Menu();
			menuProfil.menuProfil(new setUpProfil(label));
		}
	}
	
	public static class setUpProfil {
		public JLabel label;
		
		public setUpProfil(JLabel label2) {
			label = label2;
		}

		public String activer(Profils p) {
			profil = p;
			label.setText("Profil :" + profil.getPseudo());
			return "";
		}
	}
	
	/** Classe Action déclanchée par le bouton "Générer données" du menu principal
	 * et permettant de faire tourner une ou plusieurs parties avec des bots naïfs 
	 * pour explorer des positions aléatoires du plateau et en générer le score
	 * correspondant grâce au moteur Stockfish. On obtient ainsi des couples
	 * FENstring score qui seront utilisés pour entraîner notre réseau de neurones.
	 */
	static class ActionEntrainement implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String pwd = System.getProperty("user.dir");
			String chemin = JOptionPane.showInputDialog(
					"Veuillez donnez le chemin jusqu'au dossier src (C:/.../.../src)",
					pwd);
			try {
				int nbParties = Integer.parseInt(JOptionPane.showInputDialog("Veuillez "
					+ " donner le nombre de parties que vous souhaitez lancer.", "5"));
				for (int i = 0; i < nbParties; i++) {
					Stockfish stockfish = new Stockfish(chemin
							+ "/Application/engine/stockfish.exe");
					if (stockfish.startEngine()) {
						System.out.println("Stockfish démarré...");
						FEN fen = null;
						try {
							fen = new FEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
						} catch (FenException e1) {
							e1.printStackTrace();
						}
						stockfish.sendCommand("position fen " + fen);
						stockfish.sendCommand("go movetime " + 15);
						stockfish.getOutput(50);
						Arbitre arbitre = new ArbitreEntrainement(stockfish);
						Joueur blanc = new Naif(Couleur.BLANC, arbitre);
						Joueur noir = new Naif(Couleur.NOIR, arbitre);
						arbitre.init(blanc, noir);
						arbitre.arbitrer();
					} else {
						System.out.println("stockfish pas démarré");
					}
				}
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
			}
		}
		
	}

}
