package Application;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

import javax.swing.*;

import Application.N7etMat.setUpProfil;
import Profils.ExceptionProfil;
import Profils.Profils;

/**
 * Classe qui servira de base pour les différents menus de l'application.
 * 
 * @author Alexandre, Fabio
 */
@SuppressWarnings("serial")
public class Menu extends JPanel {

	/** Le menu Règles **/
	public void menuRegles() {
		JFrame fenetre = new JFrame("Règles");
		fenetre.setSize(300, 200);
		JPanel container = new JPanel();
		fenetre.add(container);
		// Boutons pour les différentes règles
		JButton regle1 = new JButton("L’échiquier, le but du jeu");
		JButton regle2 = new JButton("Les règles particulières");
		JButton regle3 = new JButton("Le déplacement des pièces");
		JButton regle4 = new JButton("La fin de la partie");
		container.add(regle1);
		container.add(regle2);
		container.add(regle3);
		container.add(regle4);
		regle1.addActionListener(new ActionOuvrirEchiquier());
		regle2.addActionListener(new ActionOuvrirReglesParticulieres());
		regle3.addActionListener(new ActionOuvrirDeplacementPieces());
		regle4.addActionListener(new ActionOuvrirFinPartie());
		fenetre.setVisible(true);
	}
	
	static Profils profilSelectionne;
	
	/** Le menu Profil 
	 * @param label **/
	public Profils menuProfil(setUpProfil c) {
		JFrame fenetre = new JFrame("Profil");
		fenetre.setSize(300, 100);
		JPanel container = new JPanel();
		container.setLayout(new GridLayout());
		fenetre.add(container);
		JButton creer = new JButton("Nouveau Profil");
		creer.addActionListener(new ActionNouveauProfil(c));
		JButton charger = new JButton("Charger Profil");
		charger.addActionListener(new ActionChargerProfil(c));
		container.add(creer);
		container.add(charger);
		fenetre.setVisible(true);
		
		return profilSelectionne;
	}

	/** Action permettant d'ouvrir un fichier PDF **/
	static class ActionOuvrirEchiquier implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				String inputPdf = "echiquier.pdf";
		        InputStream manualAsStream = getClass().getResourceAsStream(inputPdf);

		        Path tempOutput = Files.createTempFile("TempManual", ".pdf");
		        tempOutput.toFile().deleteOnExit();

		        Files.copy(manualAsStream, tempOutput, StandardCopyOption.REPLACE_EXISTING);

		        File userManual = new File (tempOutput.toFile().getPath());
		        if (userManual.exists()) {
		            Desktop.getDesktop().open(userManual);
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** Action permettant d'ouvrir un fichier PDF **/
	static class ActionOuvrirReglesParticulieres implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				String inputPdf = "regles.pdf";
		        InputStream manualAsStream = getClass().getResourceAsStream(inputPdf);

		        Path tempOutput = Files.createTempFile("TempManual", ".pdf");
		        tempOutput.toFile().deleteOnExit();

		        Files.copy(manualAsStream, tempOutput, StandardCopyOption.REPLACE_EXISTING);

		        File userManual = new File (tempOutput.toFile().getPath());
		        if (userManual.exists()) {
		            Desktop.getDesktop().open(userManual);
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** Action permettant d'ouvrir un fichier PDF **/
	static class ActionOuvrirDeplacementPieces implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				String inputPdf = "deplacement.pdf";
		        InputStream manualAsStream = getClass().getResourceAsStream(inputPdf);

		        Path tempOutput = Files.createTempFile("TempManual", ".pdf");
		        tempOutput.toFile().deleteOnExit();

		        Files.copy(manualAsStream, tempOutput, StandardCopyOption.REPLACE_EXISTING);

		        File userManual = new File (tempOutput.toFile().getPath());
		        if (userManual.exists()) {
		            Desktop.getDesktop().open(userManual);
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** Action permettant d'ouvrir un fichier PDF **/
	static class ActionOuvrirFinPartie implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				String inputPdf = "fin.pdf";
		        InputStream manualAsStream = getClass().getResourceAsStream(inputPdf);

		        Path tempOutput = Files.createTempFile("TempManual", ".pdf");
		        tempOutput.toFile().deleteOnExit();

		        Files.copy(manualAsStream, tempOutput, StandardCopyOption.REPLACE_EXISTING);

		        File userManual = new File (tempOutput.toFile().getPath());
		        if (userManual.exists()) {
		            Desktop.getDesktop().open(userManual);
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** Fenêtre pour la création d'un profil **/
	static private void creerProfil(JFrame fenetre, setUpProfil c) {
		JPanel container = new JPanel();
		container.setLayout(null);
		fenetre.add(container);

		// Nom
		JLabel nom = new JLabel("Nom :");
		nom.setBounds(10, 20, 80, 25);
		container.add(nom);
		JTextField nomTexte = new JTextField(15);
		nomTexte.setBounds(100, 20, 160, 25);
		container.add(nomTexte);

		// Mot de passe
		JLabel mdp = new JLabel("Mot de passe :");
		mdp.setBounds(10, 45, 100, 25);
		container.add(mdp);
		JPasswordField mdpTexte = new JPasswordField(15);
		mdpTexte.setBounds(100, 45, 160, 25);
		container.add(mdpTexte);

		// Bouton valider
		JButton valider = new JButton("Valider");
		valider.setBounds(110, 90, 80, 25);
		container.add(valider);
		valider.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(nomTexte.getText().length() == 0 //Pas de pseudo vide
						|| !Pattern.matches("^\\w+$",nomTexte.getText())) //ni de charatères blancs ou étranges
							return;
					c.activer(Profils.creerNouveauProfil(nomTexte.getText(), mdpTexte.getText()));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Erreur de création du profil",
							"Erreur création", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (ExceptionProfil e) {
					JOptionPane.showMessageDialog(null, "Ce nom est déjà utilisé!",
							"Erreur existence", JOptionPane.ERROR_MESSAGE);
					nomTexte.setText("");
					return;
				}
				fenetre.dispose();
			}});
	}
	
	/** Action permettant d'ouvrir la fenêtre d'accès aux profils **/
	static class ActionNouveauProfil implements ActionListener {
		setUpProfil sup;
		public ActionNouveauProfil(setUpProfil c) {
			sup = c;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			JFrame fenetre = new JFrame("Création du profil");
			fenetre.setSize(300, 150);
			creerProfil(fenetre, sup);
			fenetre.setVisible(true);
		}
	}

	/** Fenêtre pour le chargement d'un profil **/
	static private void chargerProfil(JFrame fenetre, setUpProfil c) {
		JPanel container = new JPanel();
		container.setLayout(null);
		fenetre.add(container);

		// Nom
		JLabel nom = new JLabel("Nom :");
		nom.setBounds(10, 20, 80, 25);
		container.add(nom);
		JComboBox<String> nomTexte = new JComboBox<String>(Profils.listerProfils());
		nomTexte.insertItemAt("", 0);
		nomTexte.setSelectedIndex(0);
		nomTexte.setBounds(100, 20, 160, 25);
		container.add(nomTexte);
		
		// Bouton valider
		JButton valider = new JButton("Charger");
		valider.setBounds(110, 90, 80, 25);
		container.add(valider);
		valider.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(nomTexte.getSelectedIndex() == 0)
						return;
					c.activer(Profils.lireProfil((String)nomTexte.getSelectedItem(), ""));
				} catch (ExceptionProfil e) {
					JOptionPane.showMessageDialog(null, "Ce profil est corrompu!",
							"Erreur chargement", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					return;
				}
				fenetre.dispose();
			}});
	}

	/** Action permettant d'ouvrir la fenêtre d'accès aux profils **/
	static class ActionChargerProfil implements ActionListener {
		setUpProfil sup;
		public ActionChargerProfil(setUpProfil c) {
			sup = c;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			JFrame fenetre = new JFrame("Chargement du profil");
			fenetre.setSize(300, 150);
			chargerProfil(fenetre,sup);
			fenetre.setVisible(true);
		}
	}
}
