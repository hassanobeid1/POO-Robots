package donnees.robots;

import dessin.Representation;
import donnees.Incendie;
import donnees.Reservoir;
import donnees.evenements.DeplacementRobot;
import donnees.evenements.Evenement;
import donnees.evenements.InterventionRobot;
import donnees.evenements.RemplissageRobot;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import donnees.terrain.Carte;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;
import gui.GraphicalElement;
import ia.chemins.Chemin;
import ia.chemins.RechercheAStar;
import ia.chemins.RechercheChemin;
import ordres.Ordre;
import ordres.TypeOrdre;

/**
 * Classe abstraite représentant un robot pompier Cette classe regroupe toutes
 * les caractéristiques communes et par défaut des robots
 */
public abstract class Robot {
	/**
	 * Constante représentant une vitesse infinie
	 */
	protected final static int VITESSE_INFINIE = -1;
	/**
	 * Taille en pixels du robot
	 */
	protected final static int TAILLE = 20;

	/**
	 * Case ou se trouve le robot
	 */
	private Case position;
	/**
	 * Vitesse maximale du robot, en km/h
	 */
	private final double vitesseMax;
	/**
	 * Vitesse du robot, en km/h
	 */
	private double vitesse;
	/**
	 * Intervention unitaire du robot, en L / s La première case du tableau
	 * correspond au volume de l'intervention et la deuxième à sa durée
	 */
	protected int[] intervention = new int[2];
	/**
	 * Reservoir du robot
	 */
	protected final Reservoir reservoir;
	/**
	 * La forme et couleur du robot
	 */
	private Representation representation;
	/**
	 * L'incendie qu'on vise couramment
	 */
	private Incendie incendieObjectif;
	/**
	 * Le chemin pré-calculé du robot
	 */
	private Chemin cheminActuel;
	/**
	 * L'algorithme de recherche de chemin du robot
	 */
	protected RechercheChemin algoRecherche;
	/**
	 * Booléen indiquant si le robot a planifié un événement dans le simulateur ou
	 * s'il est actuellement inactif
	 */
	private boolean inactif;

	/**
	 * Permet aux classes dérivées de construire le robot
	 * 
	 * @param position          position initiale du robot
	 * @param vitesse           vitesse de déplacement du robot
	 * @param capaciteReservoir capacité du réservoir du robot
	 * @param tempsRemplissage  temps de remplissage du réservoir du robot
	 * @param vitesseMax        vitesse maximale pour les robots dont la vitesse est
	 *                          paramétrable
	 * @param intervention      caractéristiques de l'intervention unitaire du robot
	 * @param representation    représentation graphique du robot
	 * @throws IllegalArgumentException si la vitesse donnée est invalide
	 */
	protected Robot(Case position, double vitesse, int capaciteReservoir, int tempsRemplissage, double vitesseMax,
			int[] intervention, Representation representation) throws IllegalArgumentException {
		this.reservoir = new Reservoir(capaciteReservoir, tempsRemplissage);
		this.vitesseMax = vitesseMax;
		this.intervention = intervention;
		this.representation = representation;
		setPosition(position);
		setVitesse(vitesse);
		incendieObjectif = null;
		cheminActuel = null;
		inactif = true;
		algoRecherche = new RechercheAStar();
	}

	/**
	 * @return la position actuelle du robot
	 */
	public Case getPosition() {
		return this.position;
	}

	/**
	 * Définit la position actuelle du robot
	 * 
	 * @param position la nouvelle position
	 */
	public void setPosition(Case position) {
		this.position = position;
	}

	/**
	 * Retourne le vitesse du robot sur un terrain de nature spécifique
	 * 
	 * @param natureTerrain la nature du terrain à tester
	 * @return la vitesse du robot sur le terrain, 0 signifie que le robot ne peut
	 *         franchir ce type de un terrain de cette nature
	 */
	public double getVitesse(NatureTerrain natureTerrain) {
		return getVitesseBase() * getCoefficientVitesse(natureTerrain);
	}

	/**
	 * @return la vitesse de déplacement du robot, en mètres / secondes
	 */
	protected double getVitesseBase() {
		return (vitesse * 1000)/ 3600;
	}

	/**
	 * Permet de connaître le coefficient multiplicateur de vitesse du robot sur un
	 * terrain spécifique
	 * 
	 * @param natureTerrain la nature du terrain à tester
	 * @return un coefficient entre 0 et 1, 0 signifiant que le robot ne peut pas
	 *         franchir le terrain
	 */
	protected abstract double getCoefficientVitesse(NatureTerrain natureTerrain);

	/**
	 * Définit la vitesse du robot
	 * 
	 * @param vitesse vitesse voulue
	 * @throws IllegalArgumentException si la vitesse est invalide
	 */
	protected void setVitesse(double vitesse) throws IllegalArgumentException {
		if ((vitesseMax != VITESSE_INFINIE && vitesse > vitesseMax) || vitesse < 0) {
			throw new IllegalArgumentException("La vitesse du robot doit être compris entre 0 et " + vitesseMax);
		}
		this.vitesse = vitesse;
	}

	/**
	 * Remplit entièrement le réservoir du robot
	 */
	public void remplirReservoir() {
		reservoir.remplir();
	}

	/**
	 * Retourne la représentation graphique du robot pour son affichage avec
	 * GUISimulator
	 * 
	 * @param tailleCase la taille d'une case en pixels
	 * @return un élément graphique affichable
	 */
	public GraphicalElement getGraphicalElement(int tailleCase) {
		return representation.toGraphicalElement(position.getColonne(), position.getLigne(), tailleCase);
	}

	/**
	 * Fait une copie profonde du robot
	 * 
	 * @return une nouvelle instance de robot dont les attributs sont identiques
	 */
	public Robot copy() {
		try {
			return doCopy();
		} catch (IllegalArgumentException e) {
			// Impossible d'arriver la normalement, erreur critique
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	/**
	 * Permet de faire une copie profonde du robot Méthode implémenté dans les
	 * sous-classes finales de robot
	 * 
	 * @return la copie du robot
	 * @throws IllegalArgumentException si quelque chose se passe vraiment mal
	 */
	protected abstract Robot doCopy() throws IllegalArgumentException;

	/**
	 * @return le temps d'une intervention unitaire, en secondes
	 */
	public int getTempsIntervention() {
		return intervention[1];
	}

	/**
	 * @return la quantité d'eau déversée dans une intervention unitaire, en litres
	 */
	public int getQuantiteIntervention() {
		return intervention[0];
	}

	/**
	 * @return le réservoir du robot
	 */
	public Reservoir getReservoir() {
		return reservoir;
	}

	/**
	 * Permet de déterminer si le robot est en position pour éteindre un incendie
	 * 
	 * @param ic incendie à tester
	 * @return vrai si le robot est à une position valide pour éteindre l'incendie,
	 *         faux sinon
	 */
	public boolean peutEteindre(Incendie ic) {
		return ic.getPosition().equals(ic.getPosition());
	}

	/**
	 * Permet de remplir le réservoir du robot, si cela est possible
	 * 
	 * @param c la carte de la simulation
	 * @return vrai si le robot a pu se remplir, faux sinon
	 */
	public boolean remplir(Carte c) {
		if (peutSeRemplir(c)) {
			getReservoir().remplir();
			return true;
		}
		return false;
	}

	/**
	 * Permet de déterminer si un robot peut se remplir. L'implémentation de
	 * {@link Robot#peutSeRemplir(Carte)} correspond au comportement par défaut d'un
	 * robot
	 * 
	 * @param c la crte de la simulation
	 * @return vrai si le robot peut se remplir, faux sinon
	 */
	public boolean peutSeRemplir(Carte c) {
		return getPosition().getVoisines(c).stream().anyMatch(v -> v.getNature() == NatureTerrain.EAU);
	}

	/**
	 * Intervention du robot sur l'incendie, si cela est possible au vu de l'état
	 * actuel
	 * 
	 * @param ic incendie à éteindre
	 * @return vrai si le robot a pu intervenir, faux sinon
	 */
	public boolean intervention(Incendie ic) {
		if (!peutEteindre(ic) || getReservoir().estVide()) {
			System.err.println("Action impossible : intervention");
			return false;
		}
		int volumeVoulu = Math.min(ic.getIntensite(), getQuantiteIntervention());
		int volumeUtilise = getReservoir().vider(volumeVoulu);
		ic.setIntensite(ic.getIntensite() - volumeUtilise);

		return ic.getIntensite() == 0;
	}

	/**
	 * Permet de calculer le chemin pour atteindre la source d'eau la plus proche.
	 * L'implémentation de {@link Robot#calculerCheminRemplissage(Carte)} correspond
	 * au comportement par défaut d'un robot
	 * 
	 * @param carte carte du simulateur
	 * @return le chemin calculé, ou null si le remplissage est impossible
	 */
	protected Chemin calculerCheminRemplissage(Carte carte) {
		return algoRecherche.trouverCaseAdjacenteA(this, carte, NatureTerrain.EAU);
	}

	/**
	 * Permet de calculer le chemin du robot vers un objectif
	 * 
	 * @param carte    la carte du simulateur
	 * @param objectif la position d'arrivée voulue
	 * @return le chemin calculé, ou null si l'objectif est inatteignable
	 */
	public Chemin calculerCheminVers(Carte carte, Case objectif) {
		return algoRecherche.cheminVers(this, carte, objectif);
	}

	/**
	 * Permet de planifier le prochain événement du robot
	 * 
	 * @param simulateur le simulateur dans lequel évolue le robot
	 */
	public void planifierEvenement(Simulateur simulateur) {
		Evenement evenement = calculerEvenement(simulateur.getDate(), simulateur.getDonneeSimulation());
		if (evenement != null)
			simulateur.ajouterEvenement(evenement);
	}

	/**
	 * Méthode interne calculant le prochain événement du robot
	 * 
	 * @param date    la date actuelle du simulateur
	 * @param donnees les données du simulateur
	 * @return l'évènement calculé, ou null si aucune action n'est nécessaire /
	 *         possible
	 */
	private Evenement calculerEvenement(long date, DonneeSimulation donnees) {
		// On reintialise le booleen d'inactivite
		inactif = false;

		// Si on a un incendie eteint en objectif, on l'enleve
		if (incendieObjectif != null && incendieObjectif.getIntensite() == 0)
			incendieObjectif = null;

		// Rien a faire si on ne doit pas traiter d'incendie et que notre reservoir est
		// plein
		if (incendieObjectif == null && !reservoir.estVide()) {
			inactif = true;
			return null;
		}

		// Si le reservoir est vide, on cherche a se rendre au point d'eau le plus
		// proche
		if (reservoir.estVide()) {
			// Si on y est deja, on se remplit
			if (peutSeRemplir(donnees.getCarte())) {
				resetChemin(); // Le chemin est fini puisqu'on est arrive
				return RemplissageRobot.calculer(date, this);
			} else if (cheminActuel == null) // Sinon, si le chemin n'est pas deja calcule on le calcule
				cheminActuel = calculerCheminRemplissage(donnees.getCarte());
		}
		/*
		 * Si le reservoir n'est pas vide on cherche a eteindre l'incendie. On a
		 * forcement un incendie non null ici vu qu'on a teste avant
		 * !reservoir.estVide() && incendie null
		 */
		else {
			// Si on est sur l'incendie on l'eteint
			if (position.equals(incendieObjectif.getPosition())) {
				resetChemin(); // Le chemin est fini
				return InterventionRobot.calculer(date, this, incendieObjectif);
			} else if (cheminActuel == null) // Sinon et si le chemin n'est pas calcule, on le calcule
				cheminActuel = algoRecherche.cheminVers(this, donnees.getCarte(), incendieObjectif.getPosition());
		}

		// Si on a pas de chemin possible, on reste inactif
		if (cheminActuel == null) {
			System.out.println("Le robot peut aller nulle part :(");
			inactif = true;
			return null;
		}

		// Ce cas ne devrait jamais arriver, cela voudrait dire que le robot glande sur
		// son objectif
		if (cheminActuel.getTaille() == 1)
			throw new RuntimeException("Le robot ne fait rien une fois arrive a destination");

		// Enfin, si le chemin est valide, on effectue le prochain deplacement
		return DeplacementRobot.calculer(date, donnees, this, cheminActuel.avancer());
	}

	/**
	 * @return l'incendie objectif courant du robot
	 */
	public Incendie getObjectifCourant() {
		return incendieObjectif;
	}

	/**
	 * Méthode interne permettant de réinitialiser le chemin calculé
	 */
	private void resetChemin() {
		cheminActuel = null;
	}

	/**
	 * Permet de savoir si le robot est prêt à traiter un ordre donné
	 * 
	 * @param type le type de l'ordre
	 * @return vrai si jamais le robot est prêt, faux sinon
	 */
	public boolean isReady(TypeOrdre type) {
		switch (type) {
		case ETEINDRE_INCENDIE:
			return !(incendieObjectif != null || reservoir.estVide());
		}
		return inactif;
	}

	/**
	 * Méthode commode permettant d'éxécuter {@link #isReady(TypeOrdre)} sur un ordre déjà construit
	 * @param o l'ordre à tester
	 * @return vrai si jamais le robot est prêt à recevoir un ordre de ce type, faux sinon
	 */
	public boolean isReady(Ordre o) {
		return isReady(o.getType());
	}

	/**
	 * Exécute un ordre, si cela est possible
	 * @param simulateur le simulateur dans lequel le robot évolue
	 * @param o l'ordre a effectuer
	 * @return vrai si jamais le robot a exécuté l'ordre, faux sinon
	 */
	public boolean recevoirOrdre(Simulateur simulateur, Ordre o) {
		if (!isReady(o))
			return false;

		switch (o.getType()) {
		case ETEINDRE_INCENDIE:
			Incendie incendie = (Incendie) o.getData();
			cheminActuel = calculerCheminVers(simulateur.getDonneeSimulation().getCarte(), incendie.getPosition());
			// Si l'incendie est inatteignable, on n'effectue pas l'ordre
			if (cheminActuel == null)
				return false;

			incendieObjectif = incendie;
			break;
		}
		
		// Si jamais le robot est inactif, on relance un évènement maintenant qu'il a quelque chose à faire
		if (inactif)
			planifierEvenement(simulateur);

		return true;
	}

	/**
	 * Permet de trouver l'incendie le plus proche du robot
	 * @param donnees les donnees de simulation
	 * @return un incendie ou null si aucun incendie est atteignable
	 */
	public Incendie trouverIncendiePlusProche(DonneeSimulation donnees) {
		Chemin chemin = algoRecherche.trouverPlusProche(this, donnees.getCarte(),
				pos -> donnees.getSingleIncendie(pos) != null);
		return chemin == null ? null : donnees.getSingleIncendie(chemin.getArrivee());
	}
	
	/**
	 * Permet de trouver la case d'eau la plus proche pour ce robot, s'il était situé autre part
	 * @param carte c'est la carte, c'est la carte
	 * @param depart case à partir de laquelle on cherche le chemin
	 * @return un chemin ou null si aucune case d'eau n'est atteignable depuis la case voulue pour ce robot
	 */
	public Chemin trouverEauPlusProcheDepuis(Carte carte, Case depart) {
		// On se positionne à la case départ
		Case pos = getPosition();
		setPosition(depart);

		// On regarde quel serait le chemin
		Chemin chemin = calculerCheminRemplissage(carte);
		
		// Fin de l'observation, on se remet à notre position
		setPosition(pos);
		return chemin;
	}

}
