package donnees.evenements;

import donnees.simulation.Simulateur;

/**
 * Classe représentant un évènement dans le simulateur L'évènement est la brique
 * de base du simulateur, qui représente un changement d'état du simulateur
 * Chaque évènement possède une date d'écheance
 */
public abstract class Evenement {

	/**
	 * Date à laquelle l'évènement sera effectif
	 */
	private long date;

	/**
	 * Construit un évènement à la date prévue, utilisé par les classes dérivées
	 */
	protected Evenement(long date) {
		this.date = date;
	}

	/**
	 * Permet d'accéder à la date de l'évènement
	 * 
	 * @return date de l'évènement, en secondes
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Execute l'évènement dans le simulateur
	 * 
	 * @param simulateur Le simulateur courant
	 */
	public abstract void executer(Simulateur simulateur);

}
