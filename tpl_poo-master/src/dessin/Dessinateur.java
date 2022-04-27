package dessin;

import donnees.simulation.DonneeSimulation;

/**
 * Interface permettant d'afficher les données de simulation
 */
public interface Dessinateur {

	/**
	 * Cette méthode permet d'afficher les données de simulation sous forme graphique ou autre (textuelle par exemple)
	 * @param donnees les données à afficher
	 */
	public void draw(DonneeSimulation donnees);

}
