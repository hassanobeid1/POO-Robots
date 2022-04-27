package donnees.evenements;

import donnees.Incendie;
import donnees.robots.Robot;
import donnees.simulation.Simulateur;

/**
 * Évènement représentant l'intervention d'un robot sur un incendie
 */
public class InterventionRobot extends Evenement {
	/**
	 * Le robot pompier
	 */
    private Robot robot;
    /**
     * L'incendie sur lequel le {@link robot} intervient
     */
    private Incendie incendie;

    /**
     * Construit un évènement d'intervention
     * @param date la date de fin d'intervention
     * @param robot le robot pompier
     * @param incendie l'incendie à éteindre
     */
    public InterventionRobot(long date, Robot robot, Incendie incendie) {
        super(date);
        this.robot = robot;
        this.incendie = incendie;
    }
    /**
	 * Permet de créer un évènement d'intervention en calculant le temps nécessaire à sa réalisation
     * @param dateActuelle la date à laquelle le robot commence son intervention
     * @param r le robot pompier
     * @param incendie l'incendie à éteindre
     * @return Un évènement de type {@link InterventionRobot} correctement construit
     */
    public static InterventionRobot  calculer(long dateActuelle, Robot r, Incendie incendie){
        return new InterventionRobot(dateActuelle + r.getTempsIntervention(), r, incendie);
    }
    @Override
    public void executer(Simulateur simulateur) {
        simulateur.getDonneeSimulation().interventionRobot(this.robot, this.incendie);
        robot.planifierEvenement(simulateur);
    }
}
