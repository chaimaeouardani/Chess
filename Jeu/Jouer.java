package Jeu;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Profils.Profils;

/** Classe permettant de jouer une partie.
 * @author Alexandre 
 */
public class Jouer {
	
	/** L'arbitre de la partie */
	public Arbitre arbitre;
	
	/** La limite de temps pour la partie */
	private float temps;
	
	/** Le profil chargé */
	private Profils profil = null;
	
	/** Le profil est un joueur */
	private boolean utiliser_profil = false;
	
	public Jouer(Profils profil) {
		this();
		this.profil = profil;
	}
	
	public Jouer() {}

	/** Méthode permettant de lancer une partie. */
	public void jouer() throws HeadlessException {
		arbitre = new Arbitre();
		temps = 0;
		messageSelectionMode();
		messageSelectionJoueur(Couleur.BLANC);
		if (arbitre.getBlanc() != null) {
			messageSelectionJoueur(Couleur.NOIR);
			if (arbitre.getNoir() != null)  {
				JFrame dialog = new JFrame();
				try {
					String fenString = (String) JOptionPane.showInputDialog(dialog,
							"Veuillez entrer l'enregistrement FEN décrivant l'état de la partie\n"
							+ "que vous souhaitez démarrer. Laissez par défaut pour une partie"
							+ "classique.", "Choix de la position de départ",
							JOptionPane.QUESTION_MESSAGE, null,
							null, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
					FEN fen = new FEN(fenString);
					arbitre.arbitrer(fen);
				} catch (FenException e) {
					JOptionPane.showMessageDialog(dialog, e.getLocalizedMessage() + "\n La configuration de base"
							+ " sera utilisée !");
					arbitre.arbitrer();
					if(profil != null && utiliser_profil) {
						try {
							profil.enregistrerPartie(arbitre);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/** Affichage d'un dialogue permettant de choisir le joueur correspondant à couleur.
	 * @param couleur La couleur du joueur à créer.
	 */
	void messageSelectionJoueur(Couleur couleur) {
		JFrame dialog = new JFrame();
		JButton bHumain = new JButton("Humain");
		bHumain.addActionListener(new ActionChoixJoueur(dialog, couleur));
		JButton bNaif = new JButton("Naif");
		bNaif.addActionListener(new ActionChoixJoueur(dialog, couleur));
		bNaif.addActionListener(new ActionFermer(dialog));
		JButton bBasique = new JButton("Basique");
		bBasique.addActionListener(new ActionChoixJoueur(dialog, couleur));
		JButton bGM = new JButton("GrandMaster");
//		bGM.addActionListener(new ActionChoixJoueur(dialog, couleur));
		JButton[] options = {bHumain, bNaif, bBasique, bGM};
		JOptionPane.showOptionDialog(dialog, "Veuillez choisir le type de joueur " + couleur, "Choix du joueur",
				 JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, 
				 EchecSwing.images.get(couleur), options, options[0]);
	}
	
	/** Affichage d'un dialogue permettant de choisir le mode de jeu, i.e. le temps.
	 */
	void messageSelectionMode() {
		JFrame dialog = new JFrame();
		JButton bullet = new JButton("Bullet");
		JButton blitz = new JButton ("Blitz");
		JButton rapide = new JButton ("Rapide");
		JButton illimite = new JButton("Illimité");
		rapide.addActionListener(new ActionMode(dialog));
		bullet.addActionListener(new ActionMode(dialog));
		blitz.addActionListener(new ActionMode(dialog));
		illimite.addActionListener(new ActionMode(dialog));
		JButton[] options= {bullet, blitz, rapide, illimite};
		JOptionPane.showOptionDialog(dialog, "Veuillez choisir le mode de jeu ",null,
				 JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				  options, options[0]);
		
	}
	
	// Les classes action
	
	/** La classe action pour créer un joueur. */
	class ActionChoixJoueur implements ActionListener {
		
		/** La fenêtre de dialogue */
		JFrame dialog;
		
		/** La couleur du joueur */
		Couleur couleur;
		
		protected ActionChoixJoueur(JFrame dialog, Couleur couleur) {
			this.dialog = dialog;
			this.couleur = couleur;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("humain")) {
				utiliser_profil = (!utiliser_profil) && (profil != null) && JOptionPane.showConfirmDialog(null, 
						"Souhaitez vous utiliser votre profil ?", "Profil", JOptionPane.YES_NO_OPTION)
						== JOptionPane.YES_OPTION;
				String nom;
				if(utiliser_profil)
					nom = profil.getPseudo();
				else
					nom = (String) JOptionPane.showInputDialog(dialog,
						"Veuillez entrer le nom du joueur", "Choix du nom",
						JOptionPane.QUESTION_MESSAGE, EchecSwing.images.get(couleur),
						null, null);
				if (nom != null) {
					if (temps == 0) {
						arbitre.nouveauHumain(couleur, nom);
					} else {
						arbitre.nouveauHumain(couleur, nom, temps);
					}
					dialog.dispose();
				}
			} else if (e.getActionCommand().equalsIgnoreCase("Basique")) {
				String[] options = {"1","2","3","4", "5"};//, "6", "7", "8", "9", "10", "11", "12", "13"};
				String profondeur = (String) JOptionPane.showInputDialog(dialog, 
						"Veuillez donner la profondeur de recherche pour\n le meilleur"
						+ " coup.\n Au delà de 3, les coups peuvent prendre jusqu'à "
						+ "\nplusieurs dizaines de secondes", "Choix difficulté",
						JOptionPane.QUESTION_MESSAGE, EchecSwing.images.get(couleur),
						options, "1");
				if (profondeur != null) {
					try {
						arbitre.nouveauBot(couleur, Class.forName("Bot." + 
								e.getActionCommand()), Integer.parseInt(profondeur),temps);
						dialog.dispose();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					arbitre.nouveauBot(couleur, Class.forName("Bot." + e.getActionCommand()),temps);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
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
	
	/** La classe action permettant de choisir le mode de jeu. */
	class ActionMode implements ActionListener{
		
		JFrame dialog;
		
		public ActionMode(JFrame dialog) {
			this.dialog=dialog;
		}
	
		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			if  (cmd.contentEquals("Bullet")) {
				temps = 60;
			} else if ( cmd.contentEquals("Blitz")) {
				temps = 180;
				
			} else if (cmd.contentEquals("Rapide")) {
				temps = 600;
			} else if (cmd.contentEquals("Illimité")) {
				temps = 0;
			}
			dialog.dispose();
		}
	}
	
}
