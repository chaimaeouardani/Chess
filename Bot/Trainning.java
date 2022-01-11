package Bot;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.io.*;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.obj.SerializeObject;
import org.encog.util.simple.EncogUtility;

import Jeu.Arbitre;
import Jeu.FenException;
import Jeu.Piece;

public class Trainning  {
	
	public static String FILENAME = "resume.ser";
	
	public static void main(String[] args) {
				// create a neural network, without using a factory
				BasicNetwork network = new BasicNetwork();
				network.addLayer(new BasicLayer(null, true, 772));
				network.addLayer(new BasicLayer(new ActivationTANH(), true, 64*24));
				network.addLayer(new BasicLayer(new ActivationLOG(), true, 64*12));
				network.addLayer(new BasicLayer(new ActivationTANH(), true, 64*6));
				network.addLayer(new BasicLayer(new ActivationLOG(), true, 64*3));
				network.addLayer(new BasicLayer(new ActivationTANH(), true, 64));
				network.addLayer(new BasicLayer(new ActivationLOG(), false, 1));
				network.getStructure().finalizeStructure();
				String pwd = System.getProperty("user.dir");
				MLDataSet trainingSet = createTrainingSet(pwd + "/donnees.txt");
				ResilientPropagation train = null;
				try{
					TrainingContinuation cont = (TrainingContinuation)SerializeObject.load(new File(FILENAME));
					train = new ResilientPropagation(network, trainingSet);
					train.resume(cont);
					System.out.println("resume");
				} catch (Exception e) {
					System.out.println("catch");
					network.reset();
					new ConsistentRandomizer(-1,1,500).randomize(network);
					train = new ResilientPropagation(network, trainingSet);
					train.fixFlatSpot(false);
				}
				// train the neural network
				int epoch = 1;
				try {
					do {
						
						train.iteration();
						EncogDirectoryPersistence.saveObject(new File("NeuralNetwork.nt"), network);
						SerializeObject.save(new File("NeuralNetwork.nt"), network);
						System.out
								.println("Epoch #" + epoch + " Error:" + train.getError());
						epoch++;
					} while(train.getError() > 0.01 && epoch < 100);
//					TrainingContinuation cont = train.pause();
//					SerializeObject.save(new File(FILENAME),cont);
						
					
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				// test the neural network
				System.out.println("Neural Network Results:");
				int i = 0;
				for(MLDataPair pair: trainingSet ) {
					final MLData output = network.compute(pair.getInput());
					System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
						+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
					if (i++>100) {
						break;
					}
				}
				System.out.println("le network saved");
				network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("NeuralNetwork.nt"));
				System.out.println("Neural Network Results:");
				i = 0;
				for(MLDataPair pair: trainingSet ) {
					final MLData output = network.compute(pair.getInput());
					System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
						+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
					if (i++>100) {
						break;
					}
				}
				
				Encog.getInstance().shutdown();
	}
	
	public static MLDataSet createTrainingSet(String Filename){
		MLDataSet trainingSet = null;
		try {
			File file = new File(Filename);
			int nbrLine = 0;
		    // Créer l'objet File Reader
		    FileReader fr = new FileReader(file);
		    // Créer l'objet BufferedReader
		    BufferedReader br = new BufferedReader(fr);
		    // Lire le contenu du fichier pour compter le nombre de lignes
//		    while (br.readLine() != null) {
//		       //Pour chaque ligne, incrémentez le nombre de lignes
//		       nbrLine++;
//		    }
		    br.close();
		    fr.close();
		    double[][] output = new double[100][];
			double [][] input = new double [100][];
		    fr = new FileReader(file);  
		    // Créer l'objet BufferedReader        
		    br = new BufferedReader(fr);  
		    String fenval;
			for (int i = 0; i < 100; i++ ) {
				fenval = br.readLine();
				String[] fenvaltab = fenval.split(" score ");
				input[i] = fenToMat(fenvaltab[0]);
				output[i] = new double[1];
				float score = Float.parseFloat(fenvaltab[1]);
				output[i][0] = fenvaltab[0].split(" ")[1].contains("w") ? score : -score;
			}
		    br.close();
			fr.close();
			trainingSet= new BasicMLDataSet(input, output);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Ce fichier n'existe pas");
			System.exit(1);
		}
		return trainingSet;
	}
	
	/** Renvoie une matrice en 1 dimensions à donner au réseau de neurones. Chaque couche
	 * représente une pièce dans l'ordre alphabétique de leur abréviation,
	 * i.e. b, k, n, p, q, r pour Fou, Roi, Cavalier, Pion, Dame, Tour
	 * Ensuite on a un 1 à la case [2 * 64 + x * 8 + y] s'il y a un cavalier blanc en x, y
	 * -1 s'il est noir et 0 sinon.
	 * @param fen la string de fen donnant l'état de la partie
	 * @return la matrice bon format pour la passer dans le réseau de neurones
	 */
//	public static double[] fenToMat(String fen) {
//		int sideToMove = fen.split(" ")[1].charAt(0) == 'w' ? 1 : 0;
//		double[] matrice = new double[389];
//		for (int i = 0; i < 389; i++) {
//			matrice[i] = 0;
//		}
//		String pieces = fen.split(" ")[0];
//		int x = 0;
//		int y = 0;
//		for (int i = 0; i < pieces.length(); i++) {
//		    char c = pieces.charAt(i);
//		    if (c == '/') {
//		    	x++;
//		    	y = 0;
//		    } else if (Character.isDigit(c)) {
//		    	y += Character.getNumericValue(c);
//		    } else {
//		    	double val = Character.isUpperCase(c) ? 1 : -1;
//		    	int piece = pieceFromCode(c);
//		    	matrice[piece * 64 + x * 8 + y] = val * sideToMove;
//		    	y++;
//		    }
//		}
//		matrice[384] = fenToFullMove(fen);
//		double[] roque = fenToRoque(fen);
//		matrice[385] = roque[0];
//		matrice[386] = roque[1];
//		matrice[387] = roque[2];
//		matrice[388] = roque[3];
//		return matrice;
//	}
	
	public static double[] fenToMat(String fen) {
		int sideToMove = fen.split(" ")[1].charAt(0) == 'w' ? 1 : 0;
		double[] matrice = new double[772];
		for (int i = 0; i < 772; i++) {
			matrice[i] = 0;
		}
		String pieces = fen.split(" ")[0];
		int x = 0;
		int y = 0;
		for (int i = 0; i < pieces.length(); i++) {
		    char c = pieces.charAt(i);
		    if (c == '/') {
		    	x++;
		    	y = 0;
		    } else if (Character.isDigit(c)) {
		    	y += Character.getNumericValue(c);
		    } else {
		    	int piece = pieceFromCode(c);
		    	if (Character.isUpperCase(c)) {
		    		matrice[piece * 64 + x * 8 + y + (1 - sideToMove) * 384] = 1;
		    	} else {
		    		matrice[piece * 64 + x * 8 + y + sideToMove * 384] = 1;
		    	}
		    	y++;
		    }
		}
		double[] roque = fenToRoque(fen);
		matrice[768] = roque[0];
		matrice[769] = roque[1];
		matrice[770] = roque[2];
		matrice[771] = roque[3];
		return matrice;
	}
	
//	public static double[] fenToMat(String fen) {
//		double[] matrice = new double[641];
//		for (int i = 0; i < 641; i++) {
//			matrice[i] = 0;
//		}
//		String pieces = fen.split(" ")[0];
//		int x = 0;
//		int y = 0;
//		for (int i = 0; i < pieces.length(); i++) {
//		    char c = pieces.charAt(i);
//		    if (c == '/') {
//		    	x++;
//		    	y = 0;
//		    } else if (Character.isDigit(c)) {
//		    	y += Character.getNumericValue(c);
//		    } else if (c == 'k' || c == 'K') {
//		    	y++;
//		    } else {
//		    	int val = Character.isUpperCase(c) ? 1 : 0;
//		    	int piece = pieceFromCode(c);
//		    	matrice[piece * 64 + x * 8 + y] = val;
//		    	y++;
//		    }
//		}
//		return matrice;
//	}
	
	/** Donne l'entier correspondant à la pièce, i.e. sa place dans l'ordre alphabétique
	 * en partant de 0. Fou = 0, Roi = 1, Cavalier = 2, Pion = 3, Dame = 4, Tour = 5
	 * @param c le code de la pièce 
	 * @return la valeur entre  et 5 ou -1 s'il y a une erreur
	 */
	private static int pieceFromCode(char c) {
		switch(c) {
		case 'b' :
		case 'B' :
			return 0;
		case 'k' :
		case 'K' :
			return 1;
		case 'n' :
		case 'N' :
			return 2;
		case 'p' :
		case 'P' :
			return 3;
		case 'q' :
		case  'Q' :
			return 4;
		case 'r' :
		case 'R' :
			return 5;
		default :
			System.out.println(c);
			return -1;
		}
	}
	
	/** Renvoie le nombre de tour écoulés dans la partie.
	 * @param fen la chaine de caractère décrivant l'état de la partie
	 * @return le nombre de fullmoves
	 */
	public static double fenToFullMove(String fen) {
		return Integer.parseInt(fen.split(" ")[5]);
	}
	
	/** Donne un vecteur indiquant les roques encore possible.
	 * 1 si le petit roque blanc encore possible 0 sinon
	 * 1 si le grand roque blanc encore possible 0 sinon
	 * 1 si le petit roque noir encore possible 0 sinon
	 * 1 si le grand roque noir encore possible 0 sinon
	 * @param fen
	 * @return le vecteur contenant l'information
	 */
	public static double[] fenToRoque(String fen) {
		double[] roque = new double[4];
		String[] param = fen.split(" ");
		roque[0] = param[2].contains("K") ? 1 : 0;
		roque[1] = param[2].contains("Q") ? 1 : 0;
		roque[2] = param[2].contains("k") ? 1 : 0;
		roque[3] = param[2].contains("q") ? 1 : 0;
		return roque;
	}

}
