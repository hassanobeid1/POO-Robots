package test;

import io.LecteurDonnees;

import java.io.FileNotFoundException;

import donnees.evenements.DeplacementRobot;
import donnees.evenements.Evenement;
import donnees.evenements.FinSimulation;
import donnees.robots.Robot;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import donnees.terrain.Direction;
import ia.strategie.ChefPompierParesseux;

/**
 * Exécution du scénario 1 de test, inclus dans le sujet
 */
public class TestScenarioBasique {

	private final static String NOM_CARTE = "cartes/carteSujet.map";
	
	private static long ajouterEvenement(Simulateur s, Evenement e)
	{
		s.ajouterEvenement(e);
		return e.getDate();
	}
	public static void main(String[] args) {

		try {
			DonneeSimulation donneeSimulation = LecteurDonnees.lire(NOM_CARTE);
			Simulateur simulateur = new Simulateur(donneeSimulation, new ChefPompierParesseux());

			Robot r = simulateur.getDonneeSimulation().getRobots().get(0);
			long t = simulateur.getDate();
			for (int i = 0; i < 5; i++)
				t = ajouterEvenement(simulateur, 
						DeplacementRobot.calculer(t, donneeSimulation, r, Direction.NORD));
			t = ajouterEvenement(simulateur, new FinSimulation(t, "Fin du scénario 1"));

		} catch (FileNotFoundException e) {
			System.out.println("fichier " + args[0] + " inconnu ou illisible");
		} catch (IllegalArgumentException e) {
			System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
		}
	}

}
