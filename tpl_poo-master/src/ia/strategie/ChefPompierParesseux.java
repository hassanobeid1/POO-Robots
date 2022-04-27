package ia.strategie;

import donnees.simulation.Simulateur;

/**
 * Un chef pompier qui est persuadé que les feux vont s'éteindre tout seuls, ou
 * en tout cas que quelqu'un se chargera de les éteindre...
 * Utilisé pour les scénarios de test, c'est le candidat idéal pour être sûr qu'aucun ordre ne sera donné
 */
public class ChefPompierParesseux implements ChefPompier {

	@Override
	public void donnerOrdres(Simulateur simulateur) {
	}

}
