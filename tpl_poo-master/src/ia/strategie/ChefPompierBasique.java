package ia.strategie;

import donnees.Incendie;
import donnees.robots.Robot;
import donnees.simulation.Simulateur;
import ordres.Ordre;
import ordres.TypeOrdre;

import java.util.*;

/**
 * Le chef pompier correspondant à la première stratégie basique du sujet
 */
public class ChefPompierBasique implements ChefPompier {
	/**
	 * Le calepin du chef pompier recensant les incendies auxquels il a affecté un robot
	 */
	private Set<Incendie> incendieAffect;

	/**
	 * Crée le chef pompier
	 */
	public ChefPompierBasique() {
		incendieAffect = new HashSet<>();
	}

	@Override
	public void donnerOrdres(Simulateur simulateur) {
		List<Incendie> incendies = simulateur.getDonneeSimulation().getIncendies();
		List<Robot> robots = simulateur.getDonneeSimulation().getRobots();
		
		// S'il n'y a pas d'incendies ou de robot, notre chef pompier peut retourner lire son journal
		if (incendies.size() == 0 || robots.size() == 0)
			return;
		
		// Sinon pour tous les incendies non eteints
		for (Incendie incendie : incendies) {
			
			// S'il est dans le calepin on l'ignore
			if (incendieAffect.contains(incendie))
				continue;
			
			// Sinon on cherche on donne l'ordre aux robots de la list de l'eteindre, jusqu'a ce qu'un robot pompier puisse réaliser l'ordre
			for (Robot robot : robots) {
				if (robot.recevoirOrdre(simulateur, new Ordre(TypeOrdre.ETEINDRE_INCENDIE, incendie))) {
					// Dans ce cas le chef pompier note dans son calepin que cet incendie est traite
					this.incendieAffect.add(incendie);
					break;
				}
			}
		}
		return;
	}

}
