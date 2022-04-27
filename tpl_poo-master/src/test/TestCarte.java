package test;

import io.LecteurDonnees;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Scanner;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import ia.strategie.ChefPompier;
import ia.strategie.ChefPompierBasique;
import ia.strategie.ChefPompierEfficace;
import ia.strategie.ChefPompierEvolue;
import ia.strategie.ChefPompierCool;

/**
 * Permet de tester l'éxécution de la simulation. Le programme demande à
 * l'utilisateur de rentrer les différents paramètres avant le lancement de la
 * simulation.
 */
public class TestCarte {

	public static void main(String[] args) {

		ChefPompier chef;
		ChefPompier[] chefsDisponibles = { new ChefPompierBasique(), new ChefPompierEvolue(), new ChefPompierEfficace(),
				new ChefPompierCool() };
		String[] descChefs = { "Chef pompier basique", "Chef pompier évolué", "Chef pompier efficace",
				"Chef pompier cool" };

		// Non monsieur le linter d'eclipse, il ne faut pas fermer un scanner sur
		// System.in
		Scanner sc = new Scanner(System.in);

		String carte;
		long deltaMin, deltaMax;
		int indice;

		do {
			System.out.println("Nom de la carte à utiliser (juste le nom du fichier) : ");
			carte = sc.nextLine();
		} while (!Files.isReadable(Path.of("cartes/" + carte)));

		// On demande le temps minimal
		do {
			System.out.println("Temps minimal entre deux pas de simulation (défaut : 1) : ");
			try {
				deltaMin = sc.nextLong();
			} catch (InputMismatchException e) {
				System.out.println("Veuillez saisir un nombre valide");
				sc.nextLine();
				deltaMin = -1;
			}
		} while (deltaMin <= 0);
		// On demande le temps maximal
		do {
			System.out.println("Temps maximal entre deux pas de simulation (-1 pour infini) : ");
			try {
				deltaMax = sc.nextLong();
				if (deltaMax == -1)
					deltaMax = Long.MAX_VALUE;
			} catch (InputMismatchException e) {
				System.out.println("Veuillez saisir un nombre valide");
				sc.nextLine();
				deltaMax = -1;
			}
		} while (deltaMax < deltaMin);
		// On demande la strategie choisie
		do {
			System.out.println("Choix du pompier en chef :");
			for (int i = 0; i < chefsDisponibles.length; i++)
				System.out.println((i + 1) + ") " + descChefs[i]);

			try {
				indice = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Veuillez saisir un nombre valide");
				indice = -1;
			}
		} while (indice <= 0 && indice > chefsDisponibles.length);

		indice--;
		chef = chefsDisponibles[indice];

		System.out.println("Lancement de la simulation sur la carte " + carte + " avec un " + descChefs[indice]
				+ " et un temps de simulation [" + deltaMin + "," + deltaMax + "]");

		try {
			DonneeSimulation donneeSimulation = LecteurDonnees.lire("cartes/" + carte);
			Simulateur simulateur = new Simulateur(donneeSimulation, chef, deltaMin, deltaMax);
		} catch (FileNotFoundException e) {
			System.out.println("fichier " + args[0] + " inconnu ou illisible");
		} catch (IllegalArgumentException e) {
			System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
		}
	}

}
