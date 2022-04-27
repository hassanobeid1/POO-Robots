package donnees.evenements;

import donnees.Incendie;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;

/**
 * Évènement représentant l'expansion d'un incendie vers les cases adjacentes
 */
public class ExpansionIncendie extends Evenement {

		/**
		 * L'incendie qui s'étend
		 */
	private Incendie incendieDepart;

		/**
		 * Contruit l'évènement
		 * @param date la date effective
		 * @param incendie l'incendie de départ de l'expansion
		 */
	public ExpansionIncendie(long date, Incendie incendie) {
		super(date);
		this.incendieDepart = incendie;
	}

	/**
	 * Permet de créer un évènement d'expansion d'incendie en calculant le temps nécessaire à sa réalisation
	 * @param dateActuelle date à laquelle l'incendie s'est déclaré, ou date de dernière expansion de l'incendie
	 * @param donnee les données de la simulation
	 * @param incendie l'incendie concerné
	 * @return Un évènement de type {@see ExpansionIncendie} correctement construit
	 */
	public static ExpansionIncendie calculer(long dateActuelle, DonneeSimulation donnee, Incendie incendie) {
		return new ExpansionIncendie(dateActuelle + incendie.getTempsPropagation(donnee.getCarte().getTailleCase()),
				incendie);
	}

	@Override
	public void executer(Simulateur simulateur) {
		if (incendieDepart.getIntensite() <= 0)
			return;

		incendieDepart.propager(simulateur);
		simulateur.ajouterEvenement(calculer(simulateur.getDate(), simulateur.getDonneeSimulation(), incendieDepart));
	}

}
