package donnees.simulation;

import dessin.DummyDessinSimulation;
import ia.strategie.ChefPompier;

/**
 * Adaptation du simulateur permettant de ne pas lancer l'interface graphique et d'éxécuter la simulation en entier.
 * Utilisée pour les tests
 */
public class SimulateurScript extends Simulateur {
	/**
	 * Construit un simulateur sans interface graphique, qui pour chaque pas exécute la simulation sur un temps donné
	 * @param donneeSimulation les données initiales de la simulation
	 * @param chefPompier le chef pompier opérant pour cette simulation
	 * @param maxExec le temps maximal d'exécution, pour chaque pas
	 */
	public SimulateurScript(DonneeSimulation donneeSimulation, ChefPompier chefPompier, long maxExec) {
		super();
		this.donneeSimulation = new DonneeSimulation(donneeSimulation);
		this.donneeSimulationIntiale = null;
		this.dessinSimulateur = new DummyDessinSimulation();
		this.chefPompier = chefPompier;
		this.tempsMinimalPas = maxExec;
		this.tempsMaximalPas = Long.MAX_VALUE;
		init();
	}

}
