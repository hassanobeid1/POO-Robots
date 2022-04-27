package ia.strategie;

import donnees.Incendie;
import donnees.robots.Robot;
import donnees.simulation.Simulateur;
import ordres.Ordre;
import ordres.TypeOrdre;

/**
 * Ce chef pompier a pour stratégie d'affecter à chaque robot l'incendie le plus
 * proche, sans vérifier si un incendie est affecté à deux robots différents.
 * Finalement plus proche du {@link ChefPompierEvolue} que j'aimerais l'avouer.
 */
public class ChefPompierEfficace implements ChefPompier {

	@Override
	public void donnerOrdres(Simulateur simulateur) {
		// Pour tous les robots
		for (Robot r : simulateur.getDonneeSimulation().getRobots()) {
			// Si le robot est pret a eteindre un incendie
			if (r.isReady(TypeOrdre.ETEINDRE_INCENDIE)) {
				// On lui affecte l'incendie le plus proche, s'il existe
				Incendie incendie = r.trouverIncendiePlusProche(simulateur.getDonneeSimulation());
				if (incendie != null) {
					Ordre o = new Ordre(TypeOrdre.ETEINDRE_INCENDIE, incendie);
					r.recevoirOrdre(simulateur, o);
				}
			}
		}
	}

}
