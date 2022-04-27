package donnees.evenements;

import donnees.simulation.Simulateur;

/**
 * Évènement représentant la fin de la simulation. Utilisé par les scénarios
 */
public class FinSimulation extends Evenement {

	/**
	 * L'explication de l'arrêt du simulateur
	 */
	private String cause;

	/**
	 * Construit un évènement de fin de simulation
	 * 
	 * @param date  la date effective
	 * @param cause l'explication de cet arrêt
	 */
	public FinSimulation(long date, String cause) {
		super(date);
		this.cause = cause;
	}

	@Override
	public void executer(Simulateur simulateur) {
		simulateur.terminer();
		System.out.println("Simulation terminée : " + cause);
	}

}
