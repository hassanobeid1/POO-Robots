package dessin;

import donnees.simulation.DonneeSimulation;

/**
 * Classe bouche-trou permettant de ne pas représenter graphiquement une simulation
 * Utilisée dans les tests.
 * 
 * @see SimulateurScript
 */
public class DummyDessinSimulation implements Dessinateur {

	@Override
	public void draw(DonneeSimulation donnees) {		
	}

}
