package Bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Training {
	
	public static void main(String[] args) {
		String pwd = System.getProperty("user.dir");
		DataSet[] trainingSet = createTrainingSet(pwd + "/donnees.txt");
		NeuralNetwork reseau = new NeuralNetwork();
		reseau.fit(trainingSet, 10);
	}
	
	public static DataSet[] createTrainingSet(String Filename){
		DataSet[] trainingSet = null;
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
		    trainingSet = new DataSet[100];
		    fr = new FileReader(file);  
		    // Créer l'objet BufferedReader        
		    br = new BufferedReader(fr);  
		    String fenval;
			for (int i = 0; i < 100; i++ ) {
				fenval = br.readLine();
				trainingSet[i] = fenToMat(fenval);
			}
		    br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Ce fichier n'existe pas");
			System.exit(1);
		}
		return trainingSet;
	}
	
	public static DataSet fenToMat(String fenScore) {
		String[] tab = fenScore.split(" score ");
		String fen = tab[0];
		double score = Double.parseDouble(tab[1]);
		char sideToMove = fen.split(" ")[1].charAt(0);
		int[] xB = new int[641];
		int yB = 0;
		int[] xN = new int[641];
		int yN = 0;
		for (int i = 0; i < 641; i++) {
			xB[i] = 0;
			xN[i] = 0;
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
		    } else if (c == 'k') {
		    	yN = x * 8 + y;
		    	y++;
		    } else if (c == 'K') {
		    	yB = x * 8 + y;
		    	y++;
		    } else {
		    	int piece = pieceFromCode(c);
		    	if (Character.isUpperCase(c)) {
			    	xB[	2 * piece 		* 64 + x * 8 + y] = 1;
			    	xN[(2 * piece + 1)	* 64 + x * 8 + y] = 1;
		    	} else {
			    	xB[(2 * piece + 1)	* 64 + x * 8 + y] = 1;
			    	xN[	2 * piece 		* 64 + x * 8 + y] = 1;
		    	}
		    	y++;
		    }
		}
		return new DataSet(xB, yB, xN, yN, sideToMove, score);
	}
	
	/** Donne l'entier correspondant à la pièce, i.e. sa place dans l'ordre alphabétique
	 * en partant de 0. Fou = 0, Cavalier = 1, Pion = 2, Dame = 3, Tour = 4
	 * @param c le code de la pièce 
	 * @return la valeur entre  et 5 ou -1 s'il y a une erreur
	 */
	private static int pieceFromCode(char c) {
		switch(c) {
		case 'b' :
		case 'B' :
			return 0;
		case 'n' :
		case 'N' :
			return 1;
		case 'p' :
		case 'P' :
			return 2;
		case 'q' :
		case  'Q' :
			return 3;
		case 'r' :
		case 'R' :
			return 4;
		default :
			System.out.println(c);
			return -1;
		}
	}

}
