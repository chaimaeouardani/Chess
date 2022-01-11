package Jeu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseDeplacement extends MouseAdapter {
	
	PiecePanel panel;
	
	public MouseDeplacement(PiecePanel piecePanel) {
		panel = piecePanel;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		panel.getEchiquier().getArbitre().clique(panel);
	}
	
	
		

}
