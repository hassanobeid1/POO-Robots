package donnees.simulation;

import java.util.ArrayList;
import java.util.List;

import donnees.Incendie;
import donnees.robots.Robot;
import donnees.terrain.Carte;
import donnees.terrain.Case;
import donnees.terrain.Direction;

/**
 * Classe regroupant toutes les données de la simulation pour l'état courant
 */
public class DonneeSimulation {
	/**
	 * Liste des incendies actuellement allumés
	 */
	private List<Incendie> incendies;
	/**
	 * Carte du terrain de la simulation
	 */
	private Carte carte;
	/**
	 * Liste des robots pompiers
	 */
	private List<Robot> robots;

	/**
	 * Construit la structure des données de simulation à partir des données
	 * initiale de la simulation
	 * 
	 * @param incendies liste des incendies
	 * @param carte     carte du terrain
	 * @param robots    liste des robots
	 */
	public DonneeSimulation(List<Incendie> incendies, Carte carte, List<Robot> robots) {
		// On fait une copie profonde des liste, pour permettre de faire une copie
		// indépendante des
		// données de simulation
		this.incendies = new ArrayList<>(incendies.size());
		for (Incendie incendie : incendies)
			this.incendies.add(new Incendie(incendie));

		this.robots = new ArrayList<>(robots.size());
		for (Robot robot : robots)
			this.robots.add(robot.copy());

		// Carte est un objet non modifiable, pas besoin de le copier
		this.carte = carte;
	}

	/**
	 * Construit un objet {@link DonneeSimulation} en faisant une copie profonde
	 * 
	 * @param d l'objet à copier
	 */
	public DonneeSimulation(DonneeSimulation d) {
		this(d.incendies, d.carte, d.robots);
	}

	/**
	 * Permet de déplacer effectivement un robot dans la direction voulue, si cela
	 * est réalisable
	 * 
	 * @param r   le robot à déplacer
	 * @param dir la direction dans laquelle le robot doit se déplacer
	 * @return vrai si le robot a pu se déplacer, faux sinon
	 */
	public boolean deplacerRobot(Robot r, Direction dir) {
		Case newPos = getCaseInDirection(r.getPosition(), dir);
		if (newPos != null && r.getVitesse(newPos.getNature()) != 0) {
			r.setPosition(newPos);
			return true;
		}

		System.err.println("Déplacement impossible");
		return false;
	}

	/**
	 * Méthode interne permettant de retourner une case voisine dans la direction
	 * souhaitée
	 * 
	 * @param cs  la case de départ
	 * @param dir la direction où se trouve la voisine voulue
	 * @return la case voisine ou null si cette case n'existe pas
	 */
	private Case getCaseInDirection(Case cs, Direction dir) {
		return this.carte.getCase(cs.getLigne() + dir.getLigne(), cs.getColonne() + dir.getColonne());
	}

	/**
	 * Permet de prendre en compte l'intervention d'un robot
	 * 
	 * @param r  le robot pompier
	 * @param ic l'incendie sur lequel le robot intervient
	 * @return vrai si jamais l'incendie a été éteint, faux sinon
	 */
	public boolean interventionRobot(Robot r, Incendie ic) {
		boolean eteint = r.intervention(ic);
		// Si l'incendie est eteint, on l'enleve de la liste des incendies
		if (eteint)
			this.incendies.remove(ic);
		return eteint;
	}

	/**
	 * Accède à un incendie depuis une position
	 * 
	 * @param cs la position testée
	 * @return l'incendie qui est à cette position, ou null si aucun incendie ne se
	 *         trouve à cette position
	 */
	public Incendie getSingleIncendie(Case cs) {
		// On a bien redefini la methode equals() de Case pour verifier uniquement les
		// coordonnees des cases
		return this.incendies.stream().filter(incendie -> cs.equals(incendie.getPosition())).findAny().orElse(null);
	}

	/**
	 * Permet d'ajouter un incendie dans les données
	 * 
	 * @param incendie l'incendie à ajouter
	 * @throws IllegalArgumentException si un incendie se trouve à cette position
	 */
	public void ajouterIncendie(Incendie incendie) throws IllegalArgumentException  {
		if (getSingleIncendie(incendie.getPosition()) != null)
			throw new IllegalArgumentException("Un incendie se trouve déjà à cette position");
		incendies.add(incendie);
	}

	/**
	 * Retourne la liste des incendies
	 * 
	 * @return les incendies
	 */
	public List<Incendie> getIncendies() {
		// Pour garantir une encapsulation parfaite on devrait faire une copie ici
		return incendies;
	}

	/**
	 * Retourne la liste des robots
	 * 
	 * @return les robots
	 */
	public List<Robot> getRobots() {
		// Pour garantir une encapsulation parfaite on devrait faire une copie ici
		return robots;
	}

	/**
	 * Retourne la carte
	 * 
	 * @return la carte
	 */
	public Carte getCarte() {
		return carte;
	}
}
