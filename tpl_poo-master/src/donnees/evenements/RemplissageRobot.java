package donnees.evenements;

import donnees.robots.Robot;
import donnees.simulation.Simulateur;

/**
 * Évènement représentant le remplissage du réservoir d'un robot pompier
 */
public class RemplissageRobot extends Evenement {
	/**
	 * Le robot pompier concerné
	 */
    private Robot robot;

    /**
     * Construit l'évènement
     * @param date la date de fin du remplissage
     * @param robot le robot pompier
     */
    public RemplissageRobot(long date, Robot robot) {
        super(date);
        this.robot = robot;
    }
    /**
     * Permet de construire un évènement de remplissage du robot en calculant le temps de remplissage
     * @param dateActuelle date de début du remplissage
     * @param r robot concerné
     * @return un évènement de type {@link RemplissageRobot} correctement construit
     */
    public static RemplissageRobot calculer(long dateActuelle, Robot r){
        return new RemplissageRobot(dateActuelle + r.getReservoir().getTempsRemplissage(), r);
    }

    @Override
    public void executer(Simulateur simulateur) {
        robot.remplir(simulateur.getDonneeSimulation().getCarte());
        robot.planifierEvenement(simulateur);
    }
}
