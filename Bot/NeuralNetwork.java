package Bot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class NeuralNetwork {
	
	short[][] weights_1;
	byte[][] weights_2, weights_3;
	short[] bias_1;
	int[] bias_2, bias_3;
	byte[] weights_4;
	int bias_4;
	
//	int[][] weights_1, weights_2, weights_3;
//	int[] bias_1, bias_2, bias_3, weights_4;
//	int bias_4;
	
	double l_rate = 0.001;
	
	final int dim1 = 256;
	final int dim2 = 41024;
	final int dim3 = 512;
	final int dim4 = 32;
	
	public NeuralNetwork() {
//		weights_1 = init(dim1, dim2, 16);
//		bias_1 = init(dim1, 16);
//		
//		weights_2 = init(dim4, dim3, 8);
//		bias_2 = init(dim4, 32);
//
//		weights_3 = init(dim4, dim4, 8);
//		bias_3 = init(dim4, 32);
//
//		weights_4 = init(dim4, 8);
		
		weights_1 = init(new short[dim1][dim2]);
		bias_1 = init(new short[dim1]);
		
		weights_2 = init(new byte[dim4][dim3]);
		bias_2 = init(new int[dim4]);
		
		weights_3 = init(new byte[dim4][dim4]);
		bias_3 = init(new int[dim4]);
		
		weights_4 = init(new byte[dim4]);	
		bias_4 = 0;
	}

	private int[] init(int dim, int bits) {
		Random r = new Random();
		int[] mat = new int[dim];
		for (int i = 0; i < dim; i++) {
//			mat[i] = r.nextInt(2^bits);
			mat[i] = 1;
		}
		return mat;
	}

	private int[][] init(int dim1, int dim2, int bits) {
		int[][] mat = new int[dim1][dim2];
		for (int i = 0; i < dim1; i++) {
			mat[i] = init(dim2, bits);
		}
		return mat;
	}

	public double predict(int[] xB, int yB, int[] xN, int yN, char sideToMove) {
		
		// Passage dans l'input layer
		int[] input = new int[dim3];
		int k = sideToMove == 'w' ? 1 : 0;
		
		for (int i = 0; i < dim1; i++ ) {
			int ind = i + (1 - k) * dim1;
			int sum = bias_1[i];
			for (int j = 0; j < 641; j++) {
				sum += xB[j] * weights_1[i][j + yB * 641];
			}
			if (sum < 0) {
				input[ind] = 0;
			} else if (sum > 127) {
				input[ind] = 127;
			} else {
				input[ind] = sum;
			}
		}
		
		for (int i = 0; i < dim1; i++ ) {
			int ind = i + k * dim1;
			int sum = bias_1[i];
			for (int j = 0; j < 641; j++) {
				sum += xN[j] * weights_1[i][j + yN * 641];
			}
			if (sum < 0) {
				input[ind] = 0;
			} else if (sum > 127) {
				input[ind] = 127;
			} else {
				input[ind] = sum;
			}
		}
		
		// First Hidden layer
		
		int[] hidden1 = new int[dim4];
		for (int i = 0; i < dim4; i++) {
			int sum = bias_2[i];
			for (int j = 0; j < dim3; j++) {
				sum += input[j] * weights_2[i][j];
			}
			sum = sum/64;
			if (sum < 0) {
				hidden1[i] = 0;
			} else if (sum > 127) {
				hidden1[i] = 127;
			} else {
				hidden1[i] = sum;
			}
		}
		
		// Second Hidden layer
		
		int[] hidden2 = new int[dim4];
		for (int i = 0; i < dim4; i++) {
			int sum = bias_3[i];
			for (int j = 0; j < dim4; j++) {
				sum += hidden1[j] * weights_3[i][j];
			}
			sum = sum/64;
			if (sum < 0) {
				hidden2[i] = 0;
			} else if (sum > 127) {
				hidden2[i] = 127;
			} else {
				hidden2[i] = sum;
			}
		}
		
		// Output  layer
		
		double output = bias_4;
		for (int i = 0; i < dim4; i++) {
			output += hidden2[i] * weights_4[i];
		}
		
		return output/16;
	}
	
	
	public void fit(DataSet[] setOfDataSet, int epochs) {
		String pwd = System.getProperty("user.dir");
		File filew1 = new File(pwd + "/w1.txt");
		File filew2 = new File(pwd + "/w2.txt");
		File filew3 = new File(pwd + "/w3.txt");
		File filew4 = new File(pwd + "/w4.txt");
		File fileb1 = new File(pwd + "/b1.txt");
		File fileb2 = new File(pwd + "/b2.txt");
		File fileb3 = new File(pwd + "/b3.txt");
		File fileb4 = new File(pwd + "/b4.txt");
		try {
			FileWriter FWw1 = new FileWriter(filew1);
			FileWriter FWw2 = new FileWriter(filew2);
			FileWriter FWw3 = new FileWriter(filew3);
			FileWriter FWw4 = new FileWriter(filew4);
			FileWriter FWb1 = new FileWriter(fileb1);
			FileWriter FWb2 = new FileWriter(fileb2);
			FileWriter FWb3 = new FileWriter(fileb3);
			FileWriter FWb4 = new FileWriter(fileb4);
		
			for(int i = 0; i < epochs; i++) {
				double error = 0;
				Random r = new Random();
				for (int j = 0; j < setOfDataSet.length; j++) {
					error = this.train(setOfDataSet[r.nextInt(setOfDataSet.length)]);
				}
				System.out.println("Epoch " + i + ", Erreur = " + error);
			}
			
			for (int i = 0; i < dim1; i++) {
				for (int j = 0; j < dim2; j++) {
					int num = weights_1[i][j];
					FWw1.write(num + " ");
				}
				FWw1.write("\n");
				int num = bias_1[i];
				FWb1.write(num + " ");
			}
			
			for (int i = 0; i < dim4; i++) {
				ecrire(FWw2, dim3, weights_2[i]);
				FWw2.write("\n");
				ecrire(FWw3, dim4, weights_3[i]);
				FWw3.write("\n");
				FWb2.write(bias_2[i] + " ");
				FWb3.write(bias_3[i] + " ");
				int num = weights_4[i];
				FWw4.write(num + " ");
			}
			FWb4.write(Integer.toString(bias_4));
			
			FWw1.close();
			FWw2.close();
			FWw3.close();
			FWw4.close();
			FWb1.close();
			FWb2.close();
			FWb3.close();
			FWb4.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ecrire(FileWriter fw, int dim, byte[] tab) throws IOException {
		for (int j = 0; j < dim; j++) {
			int num = tab[j];
			fw.write(num + " ");
		}
	}
	
	public double train(DataSet data) {
		return train(data.getxB(), data.getyB(),
					 data.getxN(), data.getyN(),
					 data.getSideToMove(),
					 data.getScore());
	}
	
	public double train(int[] xB, int yB,
						int[] xN, int yN,
						char sideToMove,
						double score) {

		// Passage dans l'input layer
		byte[] input = new byte[dim3];
		int[] inter11 = new int[dim3]; // Résultat avant fonction d'activation
		int k = sideToMove == 'w' ? 1 : 0;
		
		for (int i = 0; i < dim1; i++ ) {
			int ind = i + (1 - k) * dim1;
			inter11[i] = bias_1[i]; // Ajouts du biais
			for (int j = 0; j < 641; j++) {
				inter11[i] += xB[j] * weights_1[i][j + yB * 641]; // Multiplication poids
			}
			if (inter11[i] < 0) { // Fonction d'activation ClippedReLu
				input[ind] = 0;
			} else if (inter11[i] > 127) {
				input[ind] = 127;
			} else {
				input[ind] = (byte) inter11[i];
			}
		}
		
		int[] inter12 = new int[dim3]; // Résultat avant fonction d'activation
		for (int i = 0; i < dim1; i++ ) {
			int ind = i + k * dim1;
			inter12[i] = bias_1[i];
			for (int j = 0; j < 641; j++) {
				inter12[i] += xN[j] * weights_1[i][j + yN * 641];
			}
			if (inter12[i] < 0) {
				input[ind] = 0;
			} else if (inter12[i] > 127) {
				input[ind] = 127;
			} else {
				input[ind] = (byte) inter12[i];
			}
		}
		
		// First Hidden layer
		
		byte[] hidden1 = new byte[dim4];
		int[] inter2 = new int[dim4]; // Résultat avant fonction d'activation
		for (int i = 0; i < dim4; i++) {
			inter2[i] = bias_2[i];
			for (int j = 0; j < dim3; j++) {
				inter2[i] += input[j] * weights_2[i][j];
			}
			inter2[i] = inter2[i]/64;
			if (inter2[i] < 0) {
				hidden1[i] = 0;
			} else if (inter2[i] > 127) {
				hidden1[i] = 127;
			} else {
				hidden1[i] = (byte) inter2[i];
			}
		}
		
		// Second Hidden layer
		
		byte[] hidden2 = new byte[dim4];
		int[] inter3 = new int[dim4]; // Résultat avant fonction d'activation
		for (int i = 0; i < dim4; i++) {
			inter3[i] = bias_3[i];
			for (int j = 0; j < dim4; j++) {
				inter3[i] += hidden1[j] * weights_3[i][j];
			}
			inter3[i] = inter3[i]/64;
			if (inter3[i] < 0) {
				hidden2[i] = 0;
			} else if (inter3[i] > 127) {
				hidden2[i] = 127;
			} else {
				hidden2[i] = (byte) inter3[i];
			}
		}
		
		// Output  layer
		
		double output = bias_4;
		for (int i = 0; i < dim4; i++) {
			output += hidden2[i] * weights_4[i];
		}
		output = output/16;
		
		// Backpropagation
		
		// Calcul erreur 
		double error = (score - output)*(score - output)/2;
		// Calcul premier gradient
		double delta4 = score - output;
		
		// Calcul du second gradient
		double[] delta3 = new double[dim4];
		for (int i = 0; i < dim4; i++) {
			if(inter3[i] >= 0 && inter3[i] <= 127) {
				delta3[i] = weights_4[i] * delta4;
			} else {
				delta3[i] = 0;
			}
		}
		
		// Calcul du troisième gradient
		double[] delta2 = new double[dim4];
		for (int i = 0; i < dim4; i++) {
			delta2[i] = 0;
			if (inter2[i] >= 0 && inter2[i] <= 127) {
				for (int j = 0; j < dim4; j++) {
					delta2[i] += delta3[j] * weights_3[j][i];
				}
			}
		}
		
		// Calcul du quatrième gradient
		double[] delta1 = new double[dim3];
		for (int i = 0; i < dim3; i++) {
			delta1[i] = 0;
			if (inter11[i] >= 0 && inter11[i] <= 127) {
				for (int j = 0; j < dim4; j++) {
					delta1[i] += delta2[j] * weights_2[j][i];
				}
			}
		}
		
		
		// Correction des weights et des bias
		
		// Les 4
		bias_4 -= l_rate * delta4;
		for (int i =0; i < dim4; i++) {
			int nouveau = weights_4[i] - (int) (hidden2[i] * delta4 * l_rate);
			if (nouveau < -128) {
				weights_4[i] = -128;
			} else if (nouveau > 127) {
				weights_4[i] = 127;
			} else {
				weights_4[i] = (byte) nouveau;
			}
		}
		
		// Les 3
		for (int i = 0; i < dim4; i++) {
			bias_3[i] -= (l_rate * delta3[i]);
			for (int j = 0; j < dim4; j++) {
				 int nouveau = weights_3[i][j] - (int) (l_rate * hidden1[j] * delta3[i]);
				if (nouveau < -128) {
					weights_3[i][j] = -128;
				} else if (nouveau > 127) {
					weights_3[i][j] = 127;
				} else {
					weights_3[i][j] = (byte) nouveau;
				}
			}
		}
		
		// Les 2
		for (int i = 0; i < dim4; i++) {
			bias_2[i] -= l_rate * delta2[i];
			for (int j = 0; j < dim3; j++) {
				int nouveau = weights_2[i][j] - (int) (l_rate * input[j] * delta2[i]);
				if (nouveau < -128) {
					weights_2[i][j] = -128;
				} else if (nouveau > 127) {
					weights_2[i][j] = 127;
				} else {
					weights_2[i][j] = (byte) nouveau;
				}
			}
		}
		
		// Les 1
		for (int i = 0; i < dim1; i++) {
			bias_1[i] -= l_rate * (delta1[i] + delta1[i + dim1]);
			int iB = i + (1 - k) * dim1;
			int iN = i + k * dim1;
			for (int j = 0; j < 641; j++) {
				// On modifie uniquement les poids qui ont servis, pas les 10 502 144
				int nouveauB = weights_1[i][j + yB * 641] - (int) (xB[j] * delta1[iB]);
				if (nouveauB < -32768) {
					weights_1[i][j + yB * 641] = -32768;
				} else if (nouveauB > 32767) {
					weights_1[i][j + yB * 641] = 32767;
				} else {
					weights_1[i][j + yB * 641] = (short) nouveauB;
				}
				int nouveauN = weights_1[i][j + yN * 641] - (int) (xN[j] * delta1[iN]);
				if (nouveauN < -32768) {
					weights_1[i][j + yN * 641] = -32768;
				} else if (nouveauN > 32767) {
					weights_1[i][j + yN * 641] = 32767;
				} else {
					weights_1[i][j + yN * 641] = (short) nouveauN;
				}
			}
		}
		
		return error;
	}
	

	
	public double[][] init(double[][] weights) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = init(new double[weights[0].length]);
		}
		return weights;
	}
	
	public double[] init(double[] bias) {
//		Random r = new Random();
		for (int i = 0; i < bias.length; i++) {
//			bias[i] = (r.nextGaussian() + 1)/2;
			bias[i] = 1;
		}
		return bias;
	}	
	
	public byte[][] init(byte[][] weights) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = init(new byte[weights[0].length]);
		}
		return weights;
	}
	
	public byte[] init(byte[] bias) {
//		Random r = new Random();
		for (int i = 0; i < bias.length; i++) {
//			bias[i] = (byte) (r.nextInt(2^8) - (2^7));
			bias[i] = 1;
		}
		return bias;
	}
	
	public short[][] init(short[][] weights) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = init(new short[weights[0].length]);
		}
		return weights;
	}
	
	public short[] init(short[] bias) {
//		Random r = new Random();
		for (int i = 0; i < bias.length; i++) {
//			bias[i] = (short) (r.nextInt(2^16) - (2^15));
			bias[i] = 1;
		}
		return bias;
	}
	
	private int[] init(int[] bias) {
//		Random r = new Random();
		for (int i = 0; i < bias.length; i++) {
//			bias[i] = (int) (r.nextInt() * r.nextGaussian());
			bias[i] = 1;
		}
		return bias;
	}


}
