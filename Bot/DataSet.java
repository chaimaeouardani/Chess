package Bot;

public class DataSet {
	
	private int[] xB;
	
	private int yB;
	
	private int[] xN;
	
	private  int yN;
	
	private  char sideToMove;
	
	private  double score;
	
	public DataSet(int[] xB, int yB,
						int[] xN, int yN,
						char sideToMove,
						double score) {
		this.xB = xB;
		this.yB = yB;
		this.xN = xN;
		this.yN = yN;
		this.sideToMove = sideToMove;
		this.score = score;
	}
	
	public int[] getxB() {
		return xB;
	}

	public void setxB(int[] xB) {
		this.xB = xB;
	}

	public int getyB() {
		return yB;
	}

	public void setyB(int yB) {
		this.yB = yB;
	}

	public int[] getxN() {
		return xN;
	}

	public void setxN(int[] xN) {
		this.xN = xN;
	}

	public int getyN() {
		return yN;
	}

	public void setyN(int yN) {
		this.yN = yN;
	}

	public char getSideToMove() {
		return sideToMove;
	}

	public void setSideToMove(char sideToMove) {
		this.sideToMove = sideToMove;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}
