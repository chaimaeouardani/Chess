package Jeu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.text.AttributeSet.ColorAttribute;

import Bot.Basique;
import Jeu.Historique;
import Jeu.TourDeJeu;
import Jeu.Couleur;
/** L'interface graphique d'une partie.
 * @author Chaimae, Alexandre, Briag
 *
 */
public class EchecSwing {

	/** La couleur utilisée pour les cases blanches de l'échiquier */
	final static Color couleurBlanc = new Color(232, 235, 239);

	/** La couleur utilisée pour les cases noirs de l'échiquier */
	final static Color couleurNoir = new Color(125, 135, 150);

	/** La couleur utilisée pour les cases valides d'un déplacement */
	final static Color couleurVert = new Color(97, 189, 112);
	
	final static Color couleurRose = new Color( 249, 165, 155 );

	/** les dimensions de la fenetre */
	private static Dimension dimFenetre = new Dimension(1400, 800);
	
	/** La fenêtre */
	JFrame fenetre;
	
	
	/** Cases du jeu */
	public final PiecePanel[][] cases = new PiecePanel[8][8];
	
	/** L'échiquier représenté */
	private Echiquier echiquier;
	
	/** Le timer du joueur blanc */
	public Timer timerBlanc;
	
	/** Le timer du joueur noir */
	public Timer timerNoir;
	final static Border blackline = BorderFactory.createLineBorder(Color.black);
	/** listes des pieces mangées*/
	private  JPanel PiecemangeeBlanc;
	private  JPanel PiecemangeeNoir;
	/** l'historique des coups*/
	JTextArea coups;
	
	public EchecSwing(Echiquier echiquier) {

		this.echiquier = echiquier;
		// Créer les cases du plateau
		for (int i = 0; i < this.cases.length; i++) 
            {
			for (int j = 0; j < this.cases[i].length; j++) {
				this.cases[i][j] = new PiecePanel(i + 1, j + 1, this.echiquier, this);
				this.cases[i][j].setPreferredSize(new Dimension(10, 10));
				this.cases[i][j].setOpaque(true); 
			}
		}
				
		this.fenetre = new JFrame(echiquier.getJoueurBlanc().getNom() + " Blanc " +
				" vs " + echiquier.getJoueurNoir().getNom() + " Noir ");
		this.fenetre.addKeyListener(new KeyAdapter() {
	        @Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == 'h') { 
					System.out.println("annuler Coup"); // Il faut la remplacer par la fct qui annulera le coup
				}
			}
		});
		
		//Determiner la position de la fenetre
		this.fenetre.setLocation(800, 200);
		
		// Determiner la position de la fenetre
		this.fenetre.setLocation(500, 200);

		// Determiner les dimensions de la fenetre
		this.fenetre.setSize(dimFenetre);

		// Construire le contrôleur (gestion des événements)
		this.fenetre.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// afficher la fenêtre
		java.awt.Container contenu = fenetre.getContentPane();
		contenu.setLayout(new java.awt.BorderLayout());

		// panel plateau
		JPanel plateau = new JPanel();
		plateau.setLayout(new java.awt.GridLayout(8, 8));
		plateau.setBorder(blackline);
		contenu.add(plateau, BorderLayout.CENTER);
		
		// panel timer et info joueur
		JPanel info = new JPanel();
		info.setLayout(new java.awt.FlowLayout());
		info.setPreferredSize(new Dimension(320, 800));
		contenu.add(info, BorderLayout.EAST);
		ProfilePanel j1 = new ProfilePanel(this.echiquier, Couleur.NOIR);
		ProfilePanel j2 = new ProfilePanel(this.echiquier, Couleur.BLANC);
		j1.setLayout(new java.awt.GridLayout(3, 1) );
		j2.setLayout(new java.awt.GridLayout(3, 1) );
		JLabel tempsNoir= new JLabel("Temps restant : "+echiquier.getJoueurNoir().getTemps() + "s");
		JLabel tempsBlanc= new JLabel("Temps restant : "+echiquier.getJoueurNoir().getTemps() + "s");
		j1.add(tempsBlanc);
		j2.add(tempsNoir);
		//Piece mangee
		this.PiecemangeeBlanc = new  JPanel();
		j1.add(this.PiecemangeeBlanc);
		PiecemangeeBlanc.setLayout(new java.awt.GridLayout(2,8));
		this.PiecemangeeNoir = new  JPanel();
		PiecemangeeNoir.setLayout(new java.awt.GridLayout(2,8));
		j2.add(this.PiecemangeeNoir);
		info.add(j1);
		info.add(j2);
		
		
		//timer
		if (echiquier.getJoueurNoir().getTemps() != 0) {
			timerBlanc= new Timer(100, new ActionTempsBlanc(tempsNoir));
			timerNoir= new Timer(100, new ActionTempsNoir(tempsBlanc));
			timerBlanc.start();
		}
		
		
		
		//panel historique et pièces mangées 
		JPanel panelGauche = new JPanel(new BorderLayout());
		//panelGauche.setLayout(new java.awt.FlowLayout());
		panelGauche.setPreferredSize(new Dimension(320, 800));
		contenu.add(panelGauche, BorderLayout.WEST);
		JPanel historique = new JPanel();
		JLabel hist = new JLabel("Les coups joués :");
		this.coups = new JTextArea();
		//coups.setPreferredSize(new Dimension(100, 200));
		historique.add(hist);
		historique.add(coups);
		panelGauche.add(historique, BorderLayout.CENTER);

		JButton annuler = new JButton("Annuler");
		//annuler.addActionListener(new ActionAnnulerCoup());
		panelGauche.add(annuler, BorderLayout.NORTH);

		JButton Rejouer = new JButton("Rejouer la partie");
		Rejouer.addActionListener(new ActionRejouer());
		panelGauche.add(Rejouer, BorderLayout.PAGE_END);

		


		// ajout des cases
		for (int i = 0; i < this.cases.length; i++) {
			for (int j = 0; j < this.cases[i].length; j++) {
				plateau.add(cases[i][j]);
			}
		}

		// Le menu pricipale
		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));
		JMenu menu = new JMenu("Menu N7 et Mat");
		JMenuItem nouv = new JMenuItem("Recommencer");
		nouv.addActionListener(new ActionRecommencer());
		JMenuItem quit = new JMenuItem("Quitter");
		quit.addActionListener(new ActionQuitter());
		JMenuItem abandon = new JMenuItem("Abandonner");
		JMenuItem nulle = new JMenuItem("Partie nulle");
		nulle.addActionListener(new ActionNulle());
		abandon.addActionListener(new ActionAbandonner());
		menu.add(nouv);
		menu.add(quit);
		menu.add(nulle);
		menu.add(abandon);
		menuBar.add(menu);
		
		//le menu indice
		JMenu indice = new JMenu("Indice");
		JMenuItem indicePiece = new JMenuItem("Indice Piece");
		JMenuItem indiceCoups = new JMenuItem("Indice Coups");
		indicePiece.addActionListener(new ActionIndicePiece());
		indiceCoups.addActionListener(new ActionIndiceCoups());
		indice.add(indicePiece);
		indice.add(indiceCoups);
		menuBar.add(indice);
		
		// Le menu historique
		JMenu menuHist = new JMenu("Historique");
		JMenuItem ouvrir = new JMenuItem("Afficher l'historique");
		ouvrir.addActionListener(new ActionAfficher());
		JMenuItem reprend = new JMenuItem("Rejouer la partie");
		reprend.addActionListener(new ActionRejouer());
		JMenuItem supp = new JMenuItem("Supprimer l'historique");
		menuHist.add(ouvrir);
		menuHist.add(reprend);
		menuHist.add(supp);
		menuBar.add(menuHist);

		fenetre.setJMenuBar(menuBar);
		this.fenetre.setVisible(true);
	}

	public void visualisezPossibilites(Piece piece) {
		for (int i = 0; i < this.cases.length; i++) {
			for (int j = 0; j < this.cases[i].length; j++) {
				if (piece == null) {
					PiecePanel panel = cases[i][j];
					if ((panel.leX() % 2 == 0 && panel.leY() % 2 == 0)
							|| (panel.leX() % 2 != 0 && panel.leY() % 2 != 0)) {
						panel.setBackground(EchecSwing.couleurBlanc);
					} else {
						panel.setBackground(EchecSwing.couleurNoir);
					}
				} else if (piece.deplacementValideReel(i + 1, j + 1)) {
					PiecePanel panel = cases[i][j];
					if ((panel.leX() % 2 == 0 && panel.leY() % 2 == 0)
							|| (panel.leX() % 2 != 0 && panel.leY() % 2 != 0)) {
						panel.setBackground(EchecSwing.couleurVert);
					} else {
						panel.setBackground(EchecSwing.couleurVert);
					}
				}
			}
		}
	}

	public PiecePanel getCase(int x, int y) {
		return cases[x - 1][y - 1];
	}

	public PiecePanel getCase(Piece piece) {
		return getCase(piece.getX(), piece.getY());
	}

	public PiecePanel getCase(Case laCase) {
		return getCase(laCase.getX(), laCase.getY());
	}

	void recommencer() {
		fenetre.dispose();
		Arbitre arbitre = new Arbitre();
		Joueur joueurBlanc = echiquier.getJoueurBlanc().copy(arbitre); 
		Joueur joueurNoir = echiquier.getJoueurNoir().copy(arbitre);
		arbitre.init(joueurBlanc, joueurNoir);
		try {
			arbitre.arbitrer(echiquier.getArbitre().getFenDepart());
		} catch (FenException e) {
			arbitre.arbitrer();
		}
	}
		

	public void updateHist(JLabel label, String text){
		label.setText(text);
	}
	

	// Les méthodes pour afficher des dialogues/messages

	// les images à utiliser en fonction de l'état du jeu.
	public static final Map<Couleur, ImageIcon> images = new HashMap<Couleur, ImageIcon>();
	{
		images.put(Couleur.BLANC, new ImageIcon(getClass().getResource("blanc.jpg")));
		images.put(Couleur.NOIR, new ImageIcon(getClass().getResource("noir.jpg")));
	}

	/**
	 * Affiche un simple message d'information, se ferme en cliquant sur ok.
	 * 
	 * @param message Le message à afficher
	 * @param titre   Le titre de la fenêtre
	 * @param replay 
	 */
	void afficherMessage(String message, String titre, boolean replay) {
		if (!replay) {
			JFrame dialog = new JFrame();
			JOptionPane.showConfirmDialog(dialog, message, titre, JOptionPane.DEFAULT_OPTION, JOptionPane.OK_OPTION,
					images.get(echiquier.getArbitre().getDoitJouer().getCouleur()));
		}
	}

	/**
	 * Affiche un message signalant la fin de la partie.
	 * 
	 * @param message La cause de la fin de la partie (victoire d'un joueur ou
	 *                égalité)
	 * @param replay 
	 */
	void messageDeFin(String message, boolean replay) {
		if (!replay) {
			JFrame dialog = new JFrame();
	
			JButton bNouvelle = new JButton("Recommencer");
			bNouvelle.addActionListener(new ActionFermer(dialog));
			bNouvelle.addActionListener(new ActionRecommencer());
			JButton bQuitter = new JButton("Quitter le jeu");
			bQuitter.addActionListener(new ActionFermer(dialog));
			bQuitter.addActionListener(new ActionQuitter());
			JButton[] options = { bNouvelle, bQuitter };
			JOptionPane.showOptionDialog(dialog, message, "Fin de partie", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, images.get(echiquier.getArbitre().getDoitJouer().getCouleur()), options, options[1]);
		}
	}

	/**
	 * Affiche un dialogue permettant de choisir en quoi transformer un pion qui
	 * serait arriver à l'autre bout du plateau.
	 * 
	 * @param x La coordonnée verticale du pion
	 * @param y La coordonnée horizontale du pion
	 */
	void messageTransformation(int x, int y) {
		JFrame dialog = new JFrame();
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JButton bDame = new JButton("Dame");
		bDame.addActionListener(new ActionCreerPiece(x, y, "Dame", dialog));
		JButton bFou = new JButton("Fou");
		bFou.addActionListener(new ActionCreerPiece(x, y, "Fou", dialog));
		JButton bCavalier = new JButton("Cavalier");
		bCavalier.addActionListener(new ActionCreerPiece(x, y, "Cavalier", dialog));
		JButton bTour = new JButton("Tour");
		bTour.addActionListener(new ActionCreerPiece(x, y, "Tour", dialog));
		JButton[] options = { bDame, bFou, bCavalier, bTour };
		JOptionPane.showOptionDialog(dialog,
				"Votre pion est arrivé " + "au bout de plateau, vous pouvez le transformer en une autre pièce.",
				"Transformation Pion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				images.get(echiquier.getArbitre().getDoitJouer().getCouleur()), options, options[0]);
	}

	void messageNull() {
		JFrame dialog = new JFrame();
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JButton accepter = new JButton("Accepter");
		JButton refuser = new JButton("Refuser");
		accepter.addActionListener(new ActionAccepter(dialog));
		refuser.addActionListener(new ActionRefuser(dialog));
		JButton[] options = { accepter, refuser };
		JOptionPane.showOptionDialog(dialog, "L'adversaire propose une partie nulle. Que choisissez vous?", "Demande de nulle",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				images.get(echiquier.getArbitre().getDoitJouer().getCouleur()), options, options[0]);

	}


	// Les classes Actions

	/** Rejouer la partie 
	 * @author chaimae
	 */
	class ActionRejouer implements ActionListener {
		
		public void actionPerformed(ActionEvent ev) {
			Historique hist = echiquier.getArbitre().getHistorique();

			fenetre.dispose();
			Arbitre arbitre = new Arbitre();
			Joueur joueurBlanc = new Joueur(echiquier.getJoueurBlanc().getNom(), Couleur.BLANC, arbitre); 
			Joueur joueurNoir = new Joueur(echiquier.getJoueurNoir().getNom(), Couleur.NOIR, arbitre); 
			arbitre.init(joueurBlanc, joueurNoir);
			try {
				arbitre.rejouer(echiquier.getArbitre().getFenDepart(), hist);
			} catch (FenException e) {
				arbitre.rejouer(hist);
			}
		}

	}

	class ActionAnnulerCoup implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			echiquier.getArbitre().AnnulerCoup();
		}
	}


	/**Mettre à jour l'historique affiché dans la fenetre principale */
	void MajHist(){
		Historique history = echiquier.getArbitre().getHistorique();
		if (history != null){
				coups.setText(history.toString());
		}
	}
	

	/** Action permettant de quitter le jeu. */
	class ActionQuitter implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			fenetre.dispose();
		}
	}

	/** Action permettant de commencer une nouvelle partie. */
	class ActionRecommencer implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			recommencer();
		}
	}

	class ActionNulle implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			messageNull();
		}

	}

	class ActionRefuser implements ActionListener {
		private JFrame dialogue;

		public ActionRefuser(JFrame dialogue) {
			this.dialogue = dialogue;
		}

		public void actionPerformed(ActionEvent ev) {
			this.dialogue.dispose();
		}
	}

	class ActionAccepter implements ActionListener {
		private JFrame dialogue;

		public ActionAccepter(JFrame dialogue) {
			this.dialogue = dialogue;
		}

		public void actionPerformed(ActionEvent ev) {
			messageDeFin("Le match s'est terminé par une partie nulle", false);
			dialogue.dispose();

		}
	}

	class ActionAbandonner implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			messageDeFin(echiquier.getArbitre().getDoitJouer().getNom() + " a perdu par abandon", false);

		}
	}

	class ActionIndicePiece implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			int profondeurMax = 3;
			Basique aide = new Basique(echiquier.getArbitre().getDoitJouer().couleur, profondeurMax, echiquier.getArbitre());
			Coup aJouer = aide.miniMaxInit(profondeurMax, echiquier);
			int profondeur = profondeurMax;
			while (aJouer.getPiece() == null && profondeur > 1) {
				aJouer = aide.miniMaxInit(--profondeur, echiquier);
			}
			PiecePanel panel = cases[aJouer.getPiece().getX() - 1][aJouer.getPiece().getY() - 1];
			panel.setBackground(couleurVert);
		}

	}

	class ActionIndiceCoups implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			int profondeurMax = 3;
			Basique aide = new Basique(echiquier.getArbitre().getDoitJouer().couleur, profondeurMax, echiquier.getArbitre());
			Coup aJouer = aide.miniMaxInit(profondeurMax, echiquier);
			int profondeur = profondeurMax;
			while (aJouer.getPiece() == null && profondeur > 1) {
				aJouer = aide.miniMaxInit(--profondeur, echiquier);
			}
			PiecePanel panel = cases[aJouer.getPiece().getX() - 1][aJouer.getPiece().getY() - 1];
			PiecePanel panelArrivee = cases[aJouer.getCaseFin().getX() - 1][aJouer.getCaseFin().getY() - 1];
			panel.setBackground(couleurRose);
			panelArrivee.setBackground(couleurVert);

		}
	}

	/** Action permettant de fermer une JFrame. */
	class ActionFermer implements ActionListener {

		JFrame f;

		public ActionFermer(JFrame jf) {
			f = jf;
		}

		public void actionPerformed(ActionEvent ev) {
			f.dispose();
		}
	}

	/**
	 * Action permettant de creer une nouvelle pièce à partir de ses coordonnées x,
	 * y et de son nom.
	 * 
	 * @author Alexandre
	 */
	class ActionCreerPiece implements ActionListener {

		int x;
		int y;
		String piece;
		JFrame f;

		protected ActionCreerPiece(int x, int y, String piece, JFrame jf) {
			this.x = x;
			this.y = y;
			this.piece = piece;
			this.f = jf;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			echiquier.transformerPion(x, y, piece);
			f.dispose();
		}
	

	}
	
	class ActionTempsBlanc implements ActionListener {

		JLabel tempsJ1;

		public ActionTempsBlanc(JLabel tempsJ1) {
			this.tempsJ1=tempsJ1;
		}

		public void actionPerformed(ActionEvent e) {
			float temps= echiquier.getJoueurBlanc().getTemps();
			echiquier.getJoueurBlanc().decTemps();
			if (temps<=0) {
				timerBlanc.stop();
				echiquier.getJoueurBlanc().setTempsnull();
				messageDeFin(echiquier.getJoueurBlanc().getNom()+" a perdu au temps", false);
			}
			maj(temps);
			
			
		}

		public void maj(float temps) {
			tempsJ1.setText("Temps restant : "+ (int) temps + " s");
			tempsJ1.setFont(new Font("Arial", Font.CENTER_BASELINE, 12));
		}
	}
	
	class ActionTempsNoir implements ActionListener {

		JLabel tempsJ2;

		public ActionTempsNoir(JLabel tempsJ2) {
			this.tempsJ2=tempsJ2;
		}

		public void actionPerformed(ActionEvent e) {
			float temps = echiquier.getJoueurNoir().getTemps();
			echiquier.getJoueurNoir().decTemps();
			if (temps<=0) {
				timerNoir.stop();
				echiquier.getJoueurNoir().setTempsnull();
				messageDeFin(echiquier.getJoueurNoir().getNom()+" a perdu au temps", false);
			}
			maj(temps);
			
		}

		public void maj(float temps) {
			tempsJ2.setText("Temps restant : "+ (int) temps + " s");
			tempsJ2.setFont(new Font("Arial", Font.CENTER_BASELINE, 12));

		}
	}
	

	public class ActionAfficher implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, echiquier.getArbitre().getHistorique());
		}

	}
	public void majPieceMangee(Piece piece) {
		String lienImg = "img/";
		if (piece.getCouleur()==Couleur.NOIR) {
			lienImg+="noir/"+piece.getNomPiece().toLowerCase() + ".png";
			java.net.URL imageURL = getClass().getResource(lienImg);
			ImageIcon im = new ImageIcon(imageURL);
			Image imageScaled = im.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			this.PiecemangeeNoir.add(new JLabel(new ImageIcon(imageScaled)));
		}else {
			lienImg+="blanc/"+piece.getNomPiece().toLowerCase() + ".png";
			java.net.URL imageURL = getClass().getResource(lienImg);
			ImageIcon im = new ImageIcon(imageURL);
			Image imageScaled = im.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			this.PiecemangeeBlanc.add(new JLabel(new ImageIcon(imageScaled)));
		}
	}
}

