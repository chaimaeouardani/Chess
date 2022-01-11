package Bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * A simple and efficient client to run Stockfish from Java
 * 
 * @author Rahul A R http://rahular.com/stockfish-port-for-java/
 * 
 */
public class Stockfish {

	private Process engineProcess;
	private BufferedReader processReader;
	private OutputStreamWriter processWriter;

	private String PATH;
	
	public Stockfish(String path) {
		PATH = path;
	}

	/**
	 * Starts Stockfish engine as a process and initializes it
	 * 
	 * @param None
	 * @return True on success. False otherwise
	 */
	public boolean startEngine() {
		try {
			engineProcess = Runtime.getRuntime().exec(PATH);
			processReader = new BufferedReader(new InputStreamReader(
					engineProcess.getInputStream()));
			processWriter = new OutputStreamWriter(
					engineProcess.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Takes in any valid UCI command and executes it
	 * 
	 * @param command
	 */
	public void sendCommand(String command) {
		try {
			processWriter.write(command + "\n");
			processWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println();
			System.out.println();
		}
	}

	/**
	 * This is generally called right after 'sendCommand' for getting the raw
	 * output from Stockfish
	 * 
	 * @param waitTime
	 *            Time in milliseconds for which the function waits before
	 *            reading the output. Useful when a long running command is
	 *            executed
	 * @return Raw output from Stockfish
	 */
	public String getOutput(int waitTime) {
		StringBuffer buffer = new StringBuffer();
		try {
			Thread.sleep(waitTime);
			sendCommand("isready");
			while (true) {
				String text = processReader.readLine();
				if (text.equals("readyok"))
					break;
				else
					buffer.append(text + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
			System.out.println();
		}
		return buffer.toString();
	}

	/**
	 * This function returns the best move for a given position after
	 * calculating for 'waitTime' ms
	 * 
	 * @param fen
	 *            Position string
	 * @param waitTime
	 *            in milliseconds
	 * @return Best Move in PGN format
	 */
		public String getBestMove(String fen, int waitTime) {
			sendCommand("position fen " + fen);
			sendCommand("go movetime " + waitTime);
		return getOutput(waitTime + 20).split("bestmove ")[1].split(" ")[0];
	}

	/**
	 * Stops Stockfish and cleans up before closing it
	 */
	public void stopEngine() {
		try {
			sendCommand("quit");
			processReader.close();
			processWriter.close();
		} catch (IOException e) {
		}
	}

//	/**
//	 * Get a list of all legal moves from the given position
//	 * 
//	 * @param fen
//	 *            Position string
//	 * @return String of moves
//	 */
//	public String getLegalMoves(String fen) {
//		sendCommand("position fen " + fen);
//		sendCommand("d");
//		return getOutput(0).split("Legal moves: ")[1];
//	}

	/**
	 * Draws the current state of the chess board
	 * 
	 * @param fen
	 *            Position string
	 */
	public void drawBoard(String fen) {
		sendCommand("position fen " + fen);
		sendCommand("d");

		String[] rows = getOutput(0).split("\n");

		for (int i = 1; i < 18; i++) {
			System.out.println(rows[i]);
		}
	}
	
	/** Permet d'obtenir l'évaluation de stockfish de la partie donnée par
	 * la FEN passée en argument.
	 * @param fen la chaîne de caractères décrivant la partie que l'on souhaite évaluer
	 * @return le score donné par stockfish à cette partie
	 */
	public double getEval(String fen) {
		sendCommand("position fen " + fen);
		getOutput(10);
		sendCommand("eval");
		double eval = 0;
		String out = getOutput(10);
		String[] dump = out.split("\n");
		for (int i = dump.length - 1; i >= 0; i--) {
			if (dump[i].contains("evaluation:") && !dump[i].contains("none")) {
				String evalStr = dump[i].split("evaluation:")[1].strip().split(" ")[0];
				try {
					eval = Double.parseDouble(evalStr);
				} catch (Exception e) {
					System.out.println("Exception : " + e.getMessage());
					System.out.println(dump[i]);
				}
				if (eval != 0) {
					break;
				}
			}
		}
		return eval;
	}

	/**
	 * Get the evaluation score of the best move of a given board position
	 * @param fen Position string
	 * @param waitTime in milliseconds to find the best move
	 * @return evalScore
	 */
	public float getEvalScore(String fen, int waitTime) {
		sendCommand("position fen " + fen);
		getOutput(5);
		getOutput(5);
		sendCommand("go movetime " + waitTime);

		float evalScore = 0.0f;
		String out = getOutput(waitTime + 10);
		String[] dump = out.split("\n");
		for (int i = dump.length - 1; i >= 0; i--) {
			if (dump[i].startsWith("info depth ")) {
				if (dump[i].contains("score cp")) {
					try {
						String text = dump[i].split("score cp ")[1] .split(" nodes")[0];
						evalScore = Float.parseFloat(text);
						if (evalScore != 0.0f) {
							break;
						}
					} catch(Exception e) {
						try {
							String text = dump[i].split("score cp ")[1] .split(" upperbound nodes")[0];
							evalScore = Float.parseFloat(text);
							if (evalScore != 0.0f) {
								break;
							}
						} catch (Throwable e1) {
							continue;
						}
					}
				} else if (dump[i].contains("score mate")) {
					try {
						String text = dump[i].split("score mate ")[1] .split(" nodes")[0];
						evalScore = Float.parseFloat(text) * 1000;
						if (evalScore != 0.0f) {
							break;
						}
					} catch(Exception e) {
						try {
							String text = dump[i].split("score mate ")[1] .split(" upperbound nodes")[0];
							evalScore = Float.parseFloat(text) * 1000;
							if (evalScore != 0.0f) {
								break;
							}
						} catch (Throwable e1) {
							continue;
						}
					}
				}
			}
		}
		return evalScore/100;
	}
}
