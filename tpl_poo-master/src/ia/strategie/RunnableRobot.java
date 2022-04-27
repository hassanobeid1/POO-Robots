package ia.strategie;

import donnees.Incendie;
import donnees.robots.Robot;
import donnees.terrain.Carte;
import ordres.Ordre;
import ordres.TypeOrdre;

import java.util.concurrent.Callable;

/**
 * Un subordonné du {@link ChefPompierEvolue chef pompier évolué}, qui calcule
 * la distance du plus court chemin entre un robot en un incendie
 */
public class RunnableRobot implements Callable<ResultIncendie> {
	/**
	 * Incendie objectif
	 */
	private Incendie incendie;
	/**
	 * Carte de la simulation
	 */
	private Carte carte;
	/**
	 * Robot pompier
	 */
	private Robot robot;
	
	/**
	 * On affecte au subordonné un robot et un incendie et on lui donne la carte du terrain d'intervention
	 * @param robot le robot pompier
	 * @param carte la carte du terrain d'intervention
	 * @param incendie l'incendie à éteindre
	 */
	public RunnableRobot(Robot robot, Carte carte, Incendie incendie) {
		this.robot = robot;
		this.carte = carte;
		this.incendie = incendie;
	}

	// Allez, travaille le stagiaire
	@Override
	public ResultIncendie call() throws Exception {
		try {
			if (robot.isReady(new Ordre(TypeOrdre.ETEINDRE_INCENDIE, incendie))) {
				return new ResultIncendie(robot, robot.calculerCheminVers(carte, incendie.getPosition()).getCout());
			}
			return new ResultIncendie(robot, Double.MAX_VALUE);
		} catch (NullPointerException e) {
			return new ResultIncendie(robot, Double.MAX_VALUE);
		}
	}
}
