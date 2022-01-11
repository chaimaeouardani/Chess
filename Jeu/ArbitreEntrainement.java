package Jeu;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Bot.Bot;
import Bot.Stockfish;

public class ArbitreEntrainement extends Arbitre {
	
	private File file;
	
	private FileWriter fileWriter;
	
	private Stockfish stockfish;
	
	public ArbitreEntrainement(Stockfish stockfish) {
		super();
		String pwd = System.getProperty("user.dir");
		this.file = new File(pwd + "/donnees.txt");
		this.fileWriter =  null;
		try {
			this.fileWriter = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.stockfish = stockfish;
	}
	
	@Override
	public void clique(PiecePanel panel) {
		String message = null;
		if (!finie) {
			if (depart == null) {
				if (!echiquier.caselibre(panel.x, panel.y)) {
					if (echiquier.getJoueur(panel.x, panel.y) == doitJouer) {
						depart = echiquier.getPiece(panel.x, panel.y);
						source = panel;
					} else {
						vue.afficherMessage("Erreur ! C'est à " + doitJouer.getNom() +" de jouer", "Mauvais joueur", true);
					}
				}
			} else {
				//annuler le coup si on appuie deux fois sur la meme piece 
				if (panel == source) {
					depart = null;
				} else {
					try {
						//deplacer la piece
						destination = panel;
						Coup leCoup = new Coup(depart, new Case(panel.x, panel.y));
						echiquier.deplacement(panel.x, panel.y, depart);
						tour.ajouterCoup(leCoup);
						
						if (depart.getCode() == 'p' && (depart.getX() == 1 || depart.getX() == 8)) {
							if (doitJouer instanceof Bot) {
								((Bot) doitJouer).transformerPion(depart.getX(), depart.getY());
							} else {
								while (echiquier.getPiece(depart.getX(), depart.getY()).getCode() == 'p') {
									vue.messageTransformation(depart.getX(), depart.getY());
								}
							}
							
						}
						depart = null;
						
						if (echiquier.getJoueurNoir().getTemps() != 0) {
							if (this.getDoitJouer().getCouleur() == Couleur.BLANC){
								vue.timerBlanc.stop();
								vue.timerNoir.start();
							} else {
								vue.timerNoir.stop();
								vue.timerBlanc.start();
							}
						}

						changerDoitJouer();
						String fen = (new FEN(this)).toString();
						try {
							double score = stockfish.getEval(fen.toString());
							if (score != 0.0f) {
								fileWriter.write(fen + " score " + score + "\n");
								fileWriter.flush();
							}
						} catch (Throwable e) {
							System.out.println(e.getMessage());
						}

						if (estMat(trouverRoi(echiquier.getListePieces(), doitJouer))) {
							finie = true;
							changerDoitJouer();
							message = "Le joueur " + doitJouer.getNom() +" a gagné !";
						} else if (partieNulle(echiquier.getListePieces(), doitJouer)) {
							finie = true;
							message = doitJouer.getNom() + " ne peut rien faire, c'est donc une égalité !";
						} else if (echiquier.getHalfMove() == 100 || echiquier.getFullMove() >= 200) {
							finie = true;
							message = "Il n'y a eu aucune prise ni mouvement de pion dans les 50 derniers tours. C'est donc une égalité !";
						}
						if (!finie) {
							doitJouer.jouer(echiquier);
						} else {
							vue.fenetre.dispose();
							try {
								fileWriter.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} catch (CoupInvalideException e) {
						vue.afficherMessage(e.getMessage(), "Coup invalide !", true);
					} catch (Throwable e) {
						throw e;
					}
				}
			}
		} else {
			vue.messageDeFin(message, false);
		}
	}

}
