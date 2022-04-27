package test;

import java.io.File;
import java.io.FileNotFoundException;

import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import donnees.simulation.SimulateurScript;
import ia.strategie.ChefPompier;
import ia.strategie.ChefPompierBasique;
import ia.strategie.ChefPompierEfficace;
import ia.strategie.ChefPompierEvolue;
import ia.strategie.ChefPompierCool;
import io.LecteurDonnees;

/**
 * Permet de tester l'efficacité des différents chefs pompiers sur les différentes cartes
 */
public class TestChefPompier {

	private final static ChefPompier[] CHEFS_DISPONIBLES = { new ChefPompierBasique(), new ChefPompierEvolue(), new ChefPompierEfficace(),
			new ChefPompierCool() };
	private final static String[] DESC_CHEFS = { "Chef pompier basique", "Chef pompier évolué", "Chef pompier efficace",
			"Chef pompier cool" };

	public static void main(String[] args) {
		
		for (File f : (new File("cartes")).listFiles())
		{
			if (f.canRead() && f.getPath().endsWith(".map"))
				executerTest(f.getAbsolutePath());
		}
	}

	private static void executerTest(String path) {

		String nomCarte = getCarteName(path);
		System.out.println("Test sur la carte " + nomCarte);
		DonneeSimulation donneeSimulation;
		try {
			donneeSimulation = LecteurDonnees.lire(path);
		} catch (FileNotFoundException | IllegalArgumentException e) {
			System.err.println("Carte invalide !");
			return;
		}		
		
		long max = 500000;
		for (int i = 0; i < CHEFS_DISPONIBLES.length; i++)
		{
			Simulateur simulateur = new SimulateurScript(donneeSimulation, CHEFS_DISPONIBLES[i], max);
			simulateur.next();
			//[i].reset();
			System.out.println("\t" + DESC_CHEFS[i] + "\t" + (simulateur.getDate() >= max ? "ne converge pas (sur " + max + "s)" : simulateur.getDate() + "s"));
		}
		
	}

	private static String getCarteName(String path) {
		String fileName = (new File(path)).getName();
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

}
