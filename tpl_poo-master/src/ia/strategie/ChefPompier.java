package ia.strategie;

import donnees.simulation.Simulateur;

/**
 * Le chef pompier donne des ordres et permet de coordonner les actions des robots pompier dans la simulation
 * C'est le chef pompier qui est responsable de la stratégie d'extinction des incendies
 */
public interface ChefPompier {
	/**
	 * Permet au chef pompier de donner ses ordres aux robots pompiers
	 * @param simulateur le simulateur
	 */
	public void donnerOrdres(Simulateur simulateur);
}
