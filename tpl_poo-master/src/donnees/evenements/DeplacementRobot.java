package donnees.evenements;

import donnees.robots.Robot;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import donnees.terrain.Direction;

/**
 * Évènement représentant le déplacement d'un robot pompier
 */
public class DeplacementRobot extends Evenement {
	/**
	 * Robot concerné par le déplacement
	 */
	private Robot robot;
	/**
	 * Direction du déplacement
	 */
	private Direction direction;

	/**
	 * Construit l'évènement de déplacement
	 * @param date date effective du déplacement
	 * @param r robot concerné
	 * @param dir direction voulue
	 */
	public DeplacementRobot(long date, Robot r, Direction dir) {
		super(date);
		if (dir == null)
			throw new IllegalArgumentException("La direction ne doit pas etre nulle");
		this.robot = r;
		this.direction = dir;
	}
	
	/**
	 * Permet de créer un évènement de déplacement en calculant le temps nécessaire à sa réalisation
	 * @param dateActuelle date du début du déplacement
	 * @param donnee données de la simulation
	 * @param r robot concerné par le déplacement
	 * @param dir direction voulue
	 * @return Un évènement de type {@link DeplacementRobot} correctement construit
	 */
	public static DeplacementRobot calculer(long dateActuelle, DonneeSimulation donnee, Robot r, Direction dir)
	{
		//TODO: que faire si ça tombe pas juste ??
		long temps = (long) Math.round(donnee.getCarte().getTailleCase() / r.getVitesse(r.getPosition().getNature()));
		return new DeplacementRobot(dateActuelle + temps, r, dir);
	}

	@Override
	public void executer(Simulateur simulateur) {
		simulateur.getDonneeSimulation().deplacerRobot(robot, direction);
        robot.planifierEvenement(simulateur);
	}

}
