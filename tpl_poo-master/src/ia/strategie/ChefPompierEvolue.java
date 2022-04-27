package ia.strategie;

import donnees.Incendie;
import donnees.robots.Robot;
import donnees.simulation.Simulateur;
import donnees.terrain.Case;
import ia.chemins.Chemin;
import ordres.Ordre;
import ordres.TypeOrdre;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Implémente le chef pompier un peu plus évolué décrit dans le sujet, avec une
 * option de parallélisation pour augmenter les performances de calcul sur les
 * processeurs multicoeurs
 */
public class ChefPompierEvolue implements ChefPompier {
	/**
	 * Calepin du pompier avec l'ensemble des incendies déjà traités
	 */
	private Set<Incendie> incendieAffect;
	/**
	 * Le simulateur sur lequel exerce le chef pompier
	 */
	private Simulateur simu;
	/**
	 * Le nombre de subordonnés maximal que le chef pompier peut employer pour
	 * calculer les plus court chemins
	 */
	private int nbThreadsMax;
	/**
	 * Constante privée indiquant si le chef pompier peut exploiter des subordonnés
	 * pour calculer les plus court chemins
	 */
	private final static boolean PARALELLIZE = false;

	/**
	 * Crée le chef pompier
	 */
	public ChefPompierEvolue() {
		incendieAffect = new HashSet<>();
		nbThreadsMax = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void donnerOrdres(Simulateur simulateur) {
		simu = simulateur;
		List<Incendie> incendies = simu.getDonneeSimulation().getIncendies();
		List<Robot> robots = simu.getDonneeSimulation().getRobots();

		// S'il n'y a pas d'incendies ou de robots alors le chef pompier peut retourner
		// à sa sieste
		if (incendies.size() == 0 || robots.size() == 0)
			return;

		// Sinon, pour tous les incendies non traités on envoie un robot (si cela est
		// possible)
		for (Incendie incendie : incendies) {
			if (incendieAffect.contains(incendie))
				continue;

			sendRobot(robots, incendie);
		}
	}

	/**
	 * Méthode interne permettant d'envoyer un robot vers un incendie non affecté,
	 * si cela est possible
	 * 
	 * @param robots   la liste des robots de la simulation
	 * @param incendie l'incendie à traiter
	 * @return vrai si un robot a pu être envoyé, faux sinon
	 */
	private boolean sendRobot(List<Robot> robots, Incendie incendie) {
		Ordre ordre = new Ordre(TypeOrdre.ETEINDRE_INCENDIE, incendie);
		// Le chef pompier trouve le robot le plus proche pouvant satisfaire cet ordre,
		// en employant
		// des subordonnés (oui, des threads) ou non
		Robot rclose = PARALELLIZE ? findRClosestThreaded(robots, ordre) : findRClosest(robots, ordre);
		if (rclose == null)
			return false;

		// Si un robot est trouve, le chef pompier ajoute l'incendie au calepin et
		// envoie l'ordre au robot
		this.incendieAffect.add(incendie);
		rclose.recevoirOrdre(simu, ordre);
		return true;
	}

	/**
	 * Le chef pompier calcule lui même le robot le plus proche pouvant satisfaire
	 * un ordre
	 * 
	 * @param robots la liste des robots
	 * @param ordre  l'ordre à exécuter
	 * @return le robot le plus proche ou null si aucun robot ne peut satisfaire cet
	 *         ordre
	 */
	private Robot findRClosest(List<Robot> robots, Ordre ordre) {
		Case pos = ((Incendie) ordre.getData()).getPosition();

		return robots.stream().filter(r -> r.isReady(ordre)) // On enleve les robots ne pouvant pas satisfaire cet ordre
				.map(r -> {
					Chemin chemin = r.calculerCheminVers(simu.getDonneeSimulation().getCarte(), pos);
					return new ResultIncendie(r, chemin == null ? Integer.MAX_VALUE : chemin.getCout());
				}). // On ajoute transforme la liste en ajoutant l'information du chemin
				filter(r -> r.getCout() != Integer.MAX_VALUE) // On enleve les robots ne pouvant pas se rendre a la
																// position voulue
				.min(ResultIncendie.COMPARATEUR) // On prend le resultat le plus proche
				.orElse(ResultIncendie.DEFAULT) // Ou le resultat par defaut (robot null) si aucun robot ne satisfait
												// les conditions
				.getRobot(); // Et on retourne le robot
	}

	/**
	 * Le chef pompier fait calculer à ses subordonnés le robot le plus proche
	 * pouvant satisfaire un ordre
	 * 
	 * @param robots la liste des robots
	 * @param ordre  l'ordre à exécuter
	 * @return le robot le plus proche ou null si aucun robot ne peut satisfaire cet
	 *         ordre
	 */
	private Robot findRClosestThreaded(List<Robot> robots, Ordre ordre) {
		double chemMin = Double.MAX_VALUE;
		Robot rclose = null;
		ExecutorService executor = Executors.newFixedThreadPool(nbThreadsMax);
		List<Future<ResultIncendie>> list = new ArrayList<>();
		// On ajoute les threads de calcul
		for (Robot robot : robots) {
			if (robot.isReady(ordre)) {
				Callable<ResultIncendie> worker = new RunnableRobot(robot, simu.getDonneeSimulation().getCarte(),
						(Incendie) ordre.getData());
				Future<ResultIncendie> future = executor.submit(worker);
				list.add(future);
			}
		}
		// On attend / termine l'execution des thread
		shutdownAndAwaitTermination(executor);
		// On recupere les donnees
		for (Future<ResultIncendie> fut : list) {
			try {
				if (fut != null) {
					if (chemMin > fut.get().getCout()) {
						rclose = fut.get().getRobot();
						chemMin = fut.get().getCout();
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return rclose;
	}

	/**
	 * Permet d'attendre les résultats fournis par les subordonnés
	 * @param pool l'ensemble des threads participant aux calculs
	 */
	void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(1, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

}
