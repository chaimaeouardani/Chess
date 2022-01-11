package Profils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Jeu.Arbitre;
import Jeu.Historique;
import Jeu.TourDeJeu;

/**
 * Enregistre le profil d'un joueur. Le profil constitue un répertoire dont le
 * nom est l'identifiant du joueur. Les informations concernant le joueur sont
 * stockées dans un fichier, toutes ses parties (historiques) sont également
 * enregistrées dans des fichiers.
 * 
 * @author Fabio
 *
 */
public class Profils {

	@SuppressWarnings("unused")
	/**
	 * Le répertoire dans lequel se trouve toutes les informations pour le profil
	 * considéré.
	 */
	private Path repertoireProfil;

	/** Le chemin du fichier contenant les informations personnelles. */
	private File informations;

	/**
	 * Liste de toutes les parties du joueur (stockées sous forme de fichier texte).
	 */
	private List<File> historique;
	
	/**
	 * Le pseudo du profil sélectionné.
	 */
	private String nom, mdp;
	
	/**
	 * Les statistiques du profil
	 */
	private int victoires, defaites, nulles;
	
	/**
	 * Un objet correspondant à un profil de joueur.
	 * @param nom du joueur
	 * @param mdp
	 */
	private Profils(String nom, String mdp) {
		this.nom = nom;
		this.mdp = mdp;
		repertoireProfil = Paths.get("Profils",nom);
		
		// Chemin d'accès sous la forme Profils/Pseudo/Pseudo.txt
		Path chemin = repertoireProfil.resolve(nom+".txt");
		informations = new File(chemin.toString());
	}

	private void enregistrerDonnees() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(informations);
		writer.println(nom);
		writer.println(mdp);
		writer.println(victoires);
		writer.println(defaites);
		writer.println(nulles);
		writer.close();
	}

	/**
	 * On enregistre la partie dans un fichier qu'on ajoute à la liste des parties
	 * du joueur.
	 * @throws IOException 
	 */
	public void enregistrerPartie(Arbitre jeu) throws IOException {
		//Détermination du vainqueur
		Historique historique = jeu.getHistorique();
		
		boolean victoireBlanc = false;
		if(!jeu.getHistorique().getCoupsJoues().lastElement().toString().startsWith("0-1")) {
			victoireBlanc = true;
		}
		boolean estJoueurBlanc = false;
		if(historique.getJ1().compareTo(nom) == 0) {
			estJoueurBlanc = true;
		}
		
		boolean estVictoire = victoireBlanc == estJoueurBlanc;
		
		//J'ai pas trouvé comment étaient implantées les nulles
		if(estVictoire)
			victoires++;
		else
			defaites++;
		
		// Création de l'historique d'une partie
		// Nom du fichier de la forme date.txt
		File partie = new File(this.repertoireProfil.resolve(historique.getDate().toString() + ".pgn").toString());
		partie.createNewFile();
		PrintWriter writer = new PrintWriter(partie);
		writer.println("[Date " + historique.getDate().toString()+"]");
		writer.println("[White \"" + historique.getJ1()+"\"]");
		writer.println("[Black \"" + historique.getJ2()+"\"]");
		writer.println();
		for (TourDeJeu tour : historique.getCoupsJoues()) {
			writer.print(tour.toString());
		}
		writer.println();
		writer.close();
		this.historique.add(partie);
		this.enregistrerDonnees();
	}

	public void changerMDP(String mdp, String newmdp) throws IOException {
		FileReader reader = new FileReader(this.informations);
		BufferedReader buffer = new BufferedReader(reader);
		// On parcourt le fichier informations pour aller à la deuxième ligne
		// et vérifier le mot de passe
		buffer.readLine();
		String truemdp = buffer.readLine();
		if (mdp != truemdp) {
			System.out.println("Mot de passe erroné !");
		}
		buffer.close();
		// Il faut ensuite modifier la deuxième ligne du fichier pour modifier le mdp
		String pseudo = this.getPseudo();
		PrintWriter writer = new PrintWriter(this.informations);
		writer.println(pseudo);
		writer.println(newmdp);
		writer.close();
	}

	public String getPseudo() {
		return nom;
	}
	
	public static Profils creerNouveauProfil(String nom,String mdp) throws IOException, ExceptionProfil {
		/*if (Files.notExists(Paths.get("Profils"))) {
			Files.createDirector, arg1)
		}*/
		
		// Si le répertoire existe, le pseudo est déjà utilisé
		// Chemin du répertoire
		Profils p = new Profils(nom, "");
		if (Files.exists(p.repertoireProfil)) {
			System.out.println("Pseudo déjà utilisé !");
			throw new ExceptionProfil("Profil deja créé");
		}
		p.repertoireProfil = Files.createDirectories(p.repertoireProfil);
		p.victoires = 0;
		p.defaites = 0;
		p.nulles = 0;
		
		p.informations.createNewFile();
		p.enregistrerDonnees();
		return p;
	}

	public static Profils lireProfil(String nom, String mdp) throws ExceptionProfil {
		Profils p = new Profils(nom, "");
		if(Files.notExists(p.repertoireProfil) || Files.notExists(p.informations.toPath())){
			System.out.println("Profil non existant ou corrompu");
			throw new ExceptionProfil("Ce profil n'existe pas ou est corrompu");
		}
		
		FileReader reader;
		try {
			reader = new FileReader(p.informations);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ExceptionProfil("Impossible d'accéder au profil");
		}
		BufferedReader buffer = new BufferedReader(reader);
		// On parcourt le fichier informations pour aller à la deuxième ligne
		// et vérifier le mot de passe
		try {
			buffer.readLine();
			buffer.readLine();
			String victoires = buffer.readLine();
			p.victoires = Integer.parseInt(victoires);

			String defaites = buffer.readLine();
			p.defaites = Integer.parseInt(defaites);

			String nulles = buffer.readLine();
			p.nulles = Integer.parseInt(nulles);
			buffer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExceptionProfil("Le profil n'est pas au on format");
		}	
		
		return p;
	}
	
	public static String[] listerProfils() {
		File test = new File("Profils/");
		return test.list();
	}
}
