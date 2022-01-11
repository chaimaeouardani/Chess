package Bot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Timer;

import Bot.Bot.ActionClique;
import Jeu.Arbitre;
import Jeu.Case;
import Jeu.Couleur;
import Jeu.Echiquier;
import Jeu.FEN;
import Jeu.FenException;
import Jeu.PiecePanel;

public class GrandMaster extends Bot {
	
	Stockfish stockfish;

	public GrandMaster(Couleur couleur, Arbitre arbitre) {
		super("Grand Master", couleur, arbitre);
		String pwd = System.getProperty("user.dir");
		System.out.println(pwd);
		stockfish = new Stockfish("engine/stockfish.exe");
		stockfish.startEngine();
		FEN fen = null;
		try {
			fen = new FEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		} catch (FenException e1) {
			e1.printStackTrace();
		}
		stockfish.sendCommand("position fen " + fen);
		stockfish.sendCommand("go movetime " + 15);
		stockfish.getOutput(50);
	}

	public GrandMaster(Couleur couleur, Arbitre arbitre, float temps) {
		super("Grand Master", couleur, arbitre);
		String pwd = System.getProperty("user.dir");
		System.out.println(pwd);
		InputStream input = getClass().getResourceAsStream("Application/engine/stockfish.exe");
		try {
			FileOutputStream output = new FileOutputStream("stockfish.exe");
			byte [] buffer = new byte[4096];
			int bytesRead;
			bytesRead = input.read(buffer);
			while (bytesRead != -1) {
			    output.write(buffer, 0, bytesRead);
			    bytesRead = input.read(buffer);
			}
			output.close();
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stockfish = new Stockfish("stockfish.exe");
		stockfish.startEngine();
		FEN fen = null;
		try {
			fen = new FEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		} catch (FenException e1) {
			e1.printStackTrace();
		}
		stockfish.sendCommand("position fen " + fen);
		stockfish.sendCommand("go movetime " + 15);
		stockfish.getOutput(50);
	}

	@Override
	public void jouer(Echiquier echiquier) {
		String bestMove = stockfish.getBestMove(new FEN(echiquier.getArbitre()).toString(), 100);
		Case depart = new Case(bestMove.substring(0, 2));
		Case arrivee = new Case(bestMove.substring(2, 4));
		PiecePanel panel = arbitre.vue.getCase(depart);
		Timer timer = new Timer(150, new ActionClique(panel));
		timer.setRepeats(false);
		timer.start();
		panel = arbitre.vue.getCase(arrivee);
		timer = new Timer(300, new ActionClique(panel));
		timer.setRepeats(false);
		timer.start();
	}

	@Override
	public Bot copy(Arbitre arbitre) {
		// TODO Auto-generated method stub
		return null;
	}

}
