package ia.chemins;

import java.util.ArrayList;
import java.util.List;

import donnees.terrain.Case;
import donnees.terrain.Direction;

/**
 * Chemin réalisable entre deux cases
 */
public class Chemin {
	/**
	 * Liste des cases composant le chemin. La première case est la case de départ
	 * et le dernière la case d'arrivée.
	 */
	private List<Case> cases;
	/**
	 * Coût calculé par l'algorithme de recherche de chemin de ce chemin
	 */
	private double cout;

	/**
	 * Construit un chemin à partir de sa liste de case et de son coût
	 * 
	 * @param cases cases du chemin, incluant la position de départ
	 * @param cout  coût du chemin
	 */
	public Chemin(List<Case> cases, double cout) {
		this.cases = new ArrayList<>(cases);
		this.cout = cout;
	}

	/**
	 * Retourne le nombre de case du chemin
	 */
	public int getTaille() {
		return cases.size();
	}

	/**
	 * @return la direction correspondant au déplacement entre la première et la
	 *         deuxième case du chemin, ou null si le chemin a moins de deux cases
	 */
	public Direction getPremiereDirection() {
		if (getTaille() < 2)
			return null;

		int ligneOffset = cases.get(1).getLigne() - getDepart().getLigne();
		int colonneOffset = cases.get(1).getColonne() - getDepart().getColonne();

		return Direction.fromOffset(ligneOffset, colonneOffset);
	}

	/**
	 * Permet d'avancer d'une case sur le chemin : retourne la
	 * {@link #getPremiereDirection() première direction} du chemin et enlève la
	 * première case.
	 * 
	 * @return la direction à prendre pour avancer sur le chemin, ou null si le chemin a moins de deux cases
	 */
	public Direction avancer() {
		if (getTaille() < 2)
			return null;

		Direction dir = getPremiereDirection();
		cases.remove(0);
		return dir;
	}
	
	/**
	 * @return la liste des cases du chemin
	 */
	public List<Case> getListeCases() {
		return cases;
	}
	
	/**
	 * @return la case de départ du chemin
	 */
	public Case getDepart() {
		return getTaille() >= 1 ? cases.get(0) : null;
	}

	/**
	 * @return la case d'arrivée du chemin
	 */
	public Case getArrivee() {
		return getTaille() >= 1 ? cases.get(getTaille() - 1) : null;
	}

	/**
	 * @return le coût calculé par l'algorithme de recherche
	 */
	public double getCout() {
		return cout;
	}

}
