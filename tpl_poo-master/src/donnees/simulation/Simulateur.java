package donnees.simulation;

import gui.Simulable;
import ia.strategie.ChefPompier;

import java.util.List;
import java.util.ArrayList;

import dessin.DessinSimulation;
import dessin.Dessinateur;
import donnees.Incendie;
import donnees.evenements.Evenement;
import donnees.evenements.ExpansionIncendie;
import donnees.robots.Robot;

/**
 * Le simulateur est le coeur du programme : il gère l'avancement de la
 * simulation et est au coeur de l'interface graphique Notre simulateur possède
 * de base un pas de simulation non linéaire : un pas de simulation est
 * l'avancement vers le prochain évènement de la file d'évènements. Cela est
 * néanmoins paramétrable, et il est notamment possible d'obtenir un simulateur
 * à pas constant.
 */
public class Simulateur implements Simulable {
	/**
	 * Les données de la simulation
	 */
	protected DonneeSimulation donneeSimulation;
	/**
	 * Les données initiales de la simulation, utilisées pour pouvoir réinitialiser
	 * la simulation à son état de départ
	 */
	protected DonneeSimulation donneeSimulationIntiale;
	/**
	 * La gestion graphique du simulateur
	 */
	protected Dessinateur dessinSimulateur;
	/**
	 * La date actuelle de simulation, en secondes
	 */
	private long dateActuelle;
	/**
	 * La file des évènements, on aurait pu utiliser une PriorityQueue qui s'y prête
	 * bien
	 */
	protected List<Evenement> listeEvenements;
	/**
	 * Le chef pompier gérant les pompier pour cette simulation
	 */
	protected ChefPompier chefPompier;
	/**
	 * Booléen indiquant si la simulation est finie (soit elle a été interrompue,
	 * soit il n'y a plus d'incendies)
	 */
	protected boolean estFini;
	/**
	 * Temps minimal d'un pas de simulation, en secondes
	 */
	protected long tempsMinimalPas;
	/**
	 * Temps maximal d'un pas de simulation, en secondes
	 */
	protected long tempsMaximalPas;

	/**
	 * Construit un simulateur avec une spécification des contraintes de temps liées
	 * au pas de simulation
	 * 
	 * @param donneeSimulation les données initiales de la simulation
	 * @param chefPompier      le chef pompier opérant pour cette simulation
	 * @param tempsMinimalPas  le temps de simulation minimal, en secondes, entre
	 *                         chaque pas de simulation
	 * @param tempsMaximalPas  le temps de simulation maximal, en secondes, entre
	 *                         chaque pas de simulation
	 */
	public Simulateur(DonneeSimulation donneeSimulation, ChefPompier chefPompier, long tempsMinimalPas,
			long tempsMaximalPas) {
		if (tempsMaximalPas < tempsMinimalPas || tempsMinimalPas <= 0)
			throw new IllegalArgumentException("Le temps minimal ou  de pas est incorrect");
		this.donneeSimulation = donneeSimulation;
		this.donneeSimulationIntiale = new DonneeSimulation(donneeSimulation);
		this.dessinSimulateur = new DessinSimulation(this);
		this.chefPompier = chefPompier;
		this.tempsMinimalPas = tempsMinimalPas;
		this.tempsMaximalPas = tempsMaximalPas;
		init();
	}

	/**
	 * Construit un simulateur avec le pas de simulation par défaut, c'est à dire
	 * que le simulateur avance d'exactement 1 évènement par pas
	 * 
	 * @param donneeSimulation les données initiales de la simulation
	 * @param strategie        le chef pompier opérant pour cette simulation
	 */
	public Simulateur(DonneeSimulation donneeSimulation, ChefPompier strategie) {
		this(donneeSimulation, strategie, 1, Long.MAX_VALUE);
	}

	/**
	 * Constructeur permettant aux classes dérivées de passer outre l'initialisation
	 * de la fenêtre graphique
	 */
	protected Simulateur() {
	}

	/**
	 * Permet de mettre le simulateur dans l'état initial, qu'il ait ou non déjà été
	 * lancé auparavant
	 */
	protected void init() {
		dateActuelle = 0;
		listeEvenements = new ArrayList<>();
		estFini = false;

		for (Incendie i : donneeSimulation.getIncendies())
			ajouterEvenement(ExpansionIncendie.calculer(dateActuelle, donneeSimulation, i));

		draw();
	}

	/**
	 * Ajoute un évènement dans la file des évènements, en le plaçant au bon endroit
	 * 
	 * @param e l'évènement à ajouter
	 * @throws IllegalArgumentException si l'évènement a une date antérieure à la
	 *                                  date actuelle de la simulation
	 */
	public void ajouterEvenement(Evenement e) throws IllegalArgumentException {
		if (e.getDate() < dateActuelle)
			throw new IllegalArgumentException("L'évènement a une date passée");

		boolean ajoute = false;
		// On insere l'evenement a la bonne place pour garder une file d'evenement a
		// dates croissantes
		for (int i = 0; i < listeEvenements.size() && !ajoute; i++) {
			if (e.getDate() < listeEvenements.get(i).getDate()) {
				listeEvenements.add(i, e);
				ajoute = true;
			}
		}
		if (!ajoute)
			listeEvenements.add(e);
	}

	/**
	 * Ajoute un incendie dans la simulation
	 * 
	 * @param incendie l'incendie à ajouter
	 * @throws IllegalArgumentException si un incendie se trouve déjà à cette place
	 */
	public void ajouterIncendie(Incendie incendie) throws IllegalArgumentException {
		donneeSimulation.ajouterIncendie(incendie);
		ajouterEvenement(ExpansionIncendie.calculer(dateActuelle, donneeSimulation, incendie));
	}

	/**
	 * Retourne l'évènement correspondant au prochain pas de simulation, le retire
	 * de la file et avance la date de simulation en conséquence. Si aucun évènement
	 * ne correspond au prochain pas (plus d'évènement dans la file, ou alors
	 * évènement plus loin dans l'échelle de temps que ne le permet le
	 * {@link #tempsMaximalPas temps maximal d'un pas}) alors retourne null et ne
	 * modifie pas la file d'évènements.
	 * 
	 * 
	 * @return l'évènement correspondant au prochain pas ou null
	 */
	public Evenement pollNextEvent() {
		if (listeEvenements.isEmpty())
			return null;
		
		// On regarde l'evenement en tete de file (peek)
		Evenement e = listeEvenements.get(0);
		// S'il est trop lointain, on avance la date de simulation mais on retourne null
		if (e.getDate() - dateActuelle > tempsMaximalPas) {
			dateActuelle += tempsMaximalPas;
			return null;
		}
		
		// Si l'evenement n'est pas trop lointain, on le retire de la file (poll)
		listeEvenements.remove(0);
		// On avance la date de la simulation a la date de l'evenement
		dateActuelle = e.getDate();
		// Et on le retourne
		return e;
	}

		/**
		 * @return les données de la simulation
		 */
	public DonneeSimulation getDonneeSimulation() {
		return donneeSimulation;
	}
	/**
	 * @return la date actuelle de la simulation, en secondes
	 */
	public long getDate() {
		return dateActuelle;
	}
	
	/**
	 * Affiche la simulation sur l'interface graphique
	 */
	public void draw() {
		dessinSimulateur.draw(donneeSimulation);
	}

	@Override
	public void next() {

		// Si la simulation est finie, on ne fait rien
		if (estFini)
			return;
		
		long tempsPasse = 0;
		// On execute autant d'evenements qu'il faut pour faire un pas durant au moins tempsMinimalPas secondes
		// On arrete si la simulation est finie
		while (tempsPasse < tempsMinimalPas && !estFini) {
			
			// On donne les ordres si besoin
			chefPompier.donnerOrdres(this);

			// On recupere le prochain evenement
			long datePrecedente = dateActuelle;
			Evenement e = pollNextEvent();
			if (e == null)
				break;

			// On execute l'evenement
			e.executer(this);

			// Si il n'y a plus d'incendies, la simulation est finie
			if (donneeSimulation.getIncendies().size() == 0)
				estFini = true;

			// On met a jour le temps passe pour ce pas de simulation
			tempsPasse += dateActuelle - datePrecedente;
		}
		
		//System.out.println(dateActuelle);

		// On dessine la simulation
		draw();
	}

	@Override
	public void restart() {
		donneeSimulation = new DonneeSimulation(donneeSimulationIntiale);
		init();
	}
	
	/**
	 * Termine la simulation (abort)
	 */
	public void terminer() {
		estFini = true;
	}

	@Override
	public String toString() {
		StringBuilder desc = new StringBuilder("Robots : ");
		for (Robot r : donneeSimulation.getRobots())
			desc.append("\t" + r.getPosition() + " ; " + r.getReservoir());
		return desc.toString();
	}
}
