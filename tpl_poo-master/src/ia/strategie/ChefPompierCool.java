package ia.strategie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import donnees.Incendie;
import donnees.Reservoir;
import donnees.robots.Robot;
import donnees.robots.RobotRoues;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import donnees.terrain.Carte;
import ia.chemins.Chemin;
import ordres.Ordre;
import ordres.TypeOrdre;

/**
 * Ce chef pompier affecte chaque robot à l'incendie le plus facile à éteindre
 * pour lui
 */
public class ChefPompierCool implements ChefPompier {

	@Override
	public void donnerOrdres(Simulateur simulateur) {
		// On prend la liste de tous les robots disponibles actuellement
		List<Robot> robotsDispos = getRobotsDipos(simulateur.getDonneeSimulation().getRobots());

		// Si aucun robot n'est disponible, le chef pompier continue de siroter son café
		if (robotsDispos.size() == 0)
			return;

		// Sinon pour chaque robot disponible on cherche un incendie a lui affecter
		for (Robot r : robotsDispos) {
			// On cherche l'incendie que le robot eteindra le plus vite
			Incendie i = getIncendieCoutMinimal(simulateur.getDonneeSimulation(), r);
			// S'il existe un incendie eteignable par le robot, on lui donne l'ordre de
			// l'eteindre
			if (i != null)
				r.recevoirOrdre(simulateur, new Ordre(TypeOrdre.ETEINDRE_INCENDIE, i));
		}
	}

	/**
	 * Méthode interne permettant de récupérer un l'incendie le plus facile à
	 * éteindre pour un robot donné
	 * 
	 * @param donnees les données de simulation
	 * @param r       le robot pompier considéré
	 * @return l'incedie le moins coûteux à éteindre pour ce robot, ou null si le
	 *         robot ne peut éteindre aucun incendie
	 */
	private Incendie getIncendieCoutMinimal(DonneeSimulation donnees, Robot r) {
		Carte carte = donnees.getCarte();
		return donnees.getIncendies().stream().min(new Comparator<Incendie>() {
			@Override
			public int compare(Incendie i1, Incendie i2) {
				return (int) Math.signum(calculerCoutExtinction(carte, r, i1) - calculerCoutExtinction(carte, r, i2));
			}
		}).orElse(null);
	}

	/**
	 * Méthode retournant les robots disponibles pour aller éteindre un incendie
	 * 
	 * @param robots liste des robots de la simulation
	 * @return Sous-liste de la liste fournie ne contenant que les robots
	 *         disponibles
	 */
	private List<Robot> getRobotsDipos(List<Robot> robots) {
		return robots.stream().filter(r -> r.isReady(TypeOrdre.ETEINDRE_INCENDIE)).collect(Collectors.toList());
	}

	/**
	 * Calcule le coût d'extinction d'un incendie par un robot
	 * 
	 * @param carte    la carte de la simulation
	 * @param r        le robot pompier
	 * @param incendie l'incendie
	 * @return un coût qui n'a de sens qu'en comparaison d'autres coût pour ce même
	 *         robot (voir pour des coûts provenant d'autre robots s'ils ont le même
	 *         algorithme de recherche de chemin)
	 */
	private double calculerCoutExtinction(Carte carte, Robot r, Incendie incendie) {
		// On calcule le cout pour se rendre jusqu'a l'incendie
		Chemin cheminInitial = r.calculerCheminVers(carte, incendie.getPosition());
		if (cheminInitial == null) {
			return Double.MAX_VALUE;
		}
		double cout = cheminInitial.getCout();

		Reservoir resRobot = r.getReservoir();
		// Si la capacite du robot est limite, il aura peut-etre besoin de faire des
		// allers-retours pour se remplir
		if (resRobot.getCapacite() != Reservoir.CAPACITE_INFINIE) {
			// On estime le nombre de fois qu'il devra se recharger vu l'intensite du feu et
			// son volume actuel
			int nbAllersRetours = (int) Math
					.ceil((incendie.getIntensite() - resRobot.getVolumeActuel()) / (float) resRobot.getCapacite());

			// Si on doit faire au moins un aller-retour on ajoute les surcouts engendres
			if (nbAllersRetours >= 1) {
				Chemin cheminEau = r.trouverEauPlusProcheDepuis(carte, incendie.getPosition());
				if (cheminEau == null) {
					return Double.MAX_VALUE;
				}
				// Sinon on ajoute le cout des allers-retours pour aller chercher de l'eau
				cout += nbAllersRetours * 2 * cheminEau.getCout();
			}
		}

		return cout;
	}

}
