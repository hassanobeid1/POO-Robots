package test;

import donnees.Incendie;
import donnees.evenements.DeplacementRobot;
import donnees.evenements.Evenement;
import donnees.evenements.FinSimulation;
import donnees.evenements.InterventionRobot;
import donnees.evenements.RemplissageRobot;
import donnees.robots.Robot;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import donnees.terrain.Case;
import donnees.terrain.Direction;
import donnees.terrain.NatureTerrain;
import ia.strategie.ChefPompierParesseux;
import io.LecteurDonnees;

import java.io.FileNotFoundException;

/**
 * Exécution du scénario 2 de test, inclus dans le sujet
 */
public class TestScenarioBasique2 {

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
			long t = simulateur.getDate();
			//Déplacer le 2ième robot (à roues) vers le nord, en (5,5).
			Robot r = simulateur.getDonneeSimulation().getRobots().get(1);
			t = ajouterEvenement(simulateur, 
					DeplacementRobot.calculer(t, donneeSimulation, r, Direction.NORD));
//			Le faire intervenir sur la case où il se trouve.
			Case csi = new Case(5,5, NatureTerrain.HABITAT);
			Incendie incendie = simulateur.getDonneeSimulation().getSingleIncendie(csi);
			t = ajouterEvenement(simulateur, 
					InterventionRobot.calculer(t, r, incendie));
			//Déplacer le robot deux fois vers l’ouest.
			t = ajouterEvenement(simulateur, 
					DeplacementRobot.calculer(t, donneeSimulation, r, Direction.OUEST));
			t = ajouterEvenement(simulateur, 
					DeplacementRobot.calculer(t, donneeSimulation, r, Direction.OUEST));
			//Remplir le robot
			t = ajouterEvenement(simulateur, 
					RemplissageRobot.calculer(t, r)
			);
//			Déplacer le robot deux fois vers l’est.
			t = ajouterEvenement(simulateur, 
					DeplacementRobot.calculer(t, donneeSimulation, r, Direction.EST));
			t = ajouterEvenement(simulateur, 
					DeplacementRobot.calculer(t, donneeSimulation, r, Direction.EST));
			//Le faire intervenir sur la case où il se trouve.
			t = ajouterEvenement(simulateur, 
					InterventionRobot.calculer(t, r, incendie));
			t = ajouterEvenement(simulateur, new FinSimulation(t, "Fin du scénario 2"));
		} catch (FileNotFoundException e) {
			System.out.println("fichier " + args[0] + " inconnu ou illisible");
		} catch (IllegalArgumentException e) {
			System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
		}
	}

}
