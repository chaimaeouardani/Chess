package Bot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.obj.SerializeObject;

import Bot.Bot.ActionClique;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import Jeu.Arbitre;
import Jeu.Case;
import Jeu.Couleur;
import Jeu.Coup;
import Jeu.CoupInvalideException;
import Jeu.Echiquier;
import Jeu.Piece;
import Jeu.PiecePanel;
import Jeu.FEN;
		
public class MachineLearning extends Bot{
	
	public static String FILENAME = "resume.ser";
	
	private BasicNetwork network;
	
	public MachineLearning( Couleur couleur, Arbitre arbitre) {
		super("ML",couleur , arbitre);
		this.network = new BasicNetwork();
		this.network.addLayer(new BasicLayer(null,true,64*6+5));
		this.network.addLayer(new BasicLayer(new ActivationSigmoid(),true,64*12));
		this.network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
		this.network.getStructure().finalizeStructure();
		ResilientPropagation train = new ResilientPropagation(network, null);
		try{	
			TrainingContinuation cont = (TrainingContinuation)SerializeObject.load(new File(FILENAME));
			train.resume(cont);
		} catch (Exception e) {
			System.out.println("vous devez dabord entrainer le reseaux de neuronnes");
		}
	}
	
	public Coup miniMaxInit(Echiquier echiquier) {
		Coup meilleurCoup = new Coup(getSigne());
		for (Piece piece : echiquier.piecesDuJoueur(this)) {
			for (Case caseFin : piece.listeDeplacementsValidesReels(echiquier)) {
				Coup nouveauCoup = new Coup(piece.copy(echiquier), caseFin);
				try {
					nouveauCoup.eval = miniMaxOpti( echiquier.copyAvecCoup(nouveauCoup).getArbitre());
					if (getSigne() == 1) {
						if (nouveauCoup.eval > meilleurCoup.eval) {
							meilleurCoup = nouveauCoup;
						}
					} else if (nouveauCoup.eval < meilleurCoup.eval) {
							meilleurCoup = nouveauCoup;
					}
				} catch (CoupInvalideException e) {
//					System.out.println("un bug ?");
//					System.out.println(piece);
//					System.out.println(caseFin);
				}
			}
		}
		return meilleurCoup; 
	}
	
	public double miniMaxOpti(Arbitre arbitre) {
		 FEN fen = new FEN(arbitre);
		 String stfen = fen.toString();
		 double[] inputdouble = Trainning.fenToMat(stfen);
		 MLData input = new BasicMLData(inputdouble);	 
		 MLData output = this.network.compute(input);
		 double outputdouble = output.getData(0);
		 return outputdouble;
	}
	public void jouer(Echiquier echiquier) {
		
		Coup aJouer = miniMaxInit(echiquier);
		
		while (aJouer.getPiece() == null) {
			aJouer = miniMaxInit(echiquier);
		}
		if (aJouer.getPiece() == null) {
			List<Piece> pieces = echiquier.piecesDuJoueur(this);
			int incr = 0;
			while (aJouer.getPiece() == null && incr < pieces.size()) {
				Piece piece = pieces.get(incr++);
				List<Case> cases = piece.listeDeplacementsValidesReels(echiquier);
				if (cases.size() > 0) {
					aJouer.setPiece(piece);
					aJouer.setCaseFin(cases.get(0));
				}
			}
		}
		if (aJouer.getPiece() == null) {
			//System.out.println("Gros problème.");
		}
		PiecePanel depart = arbitre.vue.getCase(aJouer.getPiece());
		PiecePanel arrivee = arbitre.vue.getCase(aJouer.getCaseFin());
		Timer timer = new Timer(50, new ActionClique(depart));
		timer.setRepeats(false);
		timer.start();
		Timer timer2 = new Timer(300, new ActionClique(arrivee));
		timer2.setRepeats(false);
		timer2.start();
	}
	
	public Bot copy(Arbitre arbitre) {
		return new MachineLearning(couleur,arbitre);
	}
}
