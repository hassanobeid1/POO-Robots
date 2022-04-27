package ia.chemins;

import java.util.function.Predicate;

import donnees.robots.Robot;
import donnees.terrain.Carte;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;

/**
 * Classe abstraite proposant des méthodes permettant de trouver des chemins optimaux pour les robots
 */
public abstract class RechercheChemin {
	
	/**
	 * Permet de trouver le plus court chemin vers une case voisine à une case du type voulue
	 * Méthode particulièrement utile pour que les robots aillent chercher de l'eau.
	 * @param robot le robot cherchant le chemin
	 * @param carte la carte de la simulation
	 * @param objectif la nature de terrain de la case dont la case d'arrivée devra être voisine
	 * @return un chemin vers une case qui a une case adjacente de nature désirée
	 */
	public Chemin trouverCaseAdjacenteA(Robot robot, Carte carte, NatureTerrain objectif)
	{
		return trouverPlusProche(robot, carte, c -> c.getVoisines(carte).stream().anyMatch(vois -> vois.getNature() == objectif));
	}
	
	/**
	 * Permet de trouver la case de nature désirée la plus proche du robot et le chemin vers celle-ci
	 * @param robot le robot cherchant le chemin
	 * @param carte la carte de la simulation
	 * @param objectif la nature désirée de la case
	 * @return le chemin s'il existe, null sinon
	 */
	public Chemin trouverPlusProche(Robot robot, Carte carte, NatureTerrain objectif)
	{
		return trouverPlusProche(robot, carte, c -> c.getNature() == objectif);
	}
	
	/**
	 * Permet de trouver la case la plus proche du robot parmi les cases qui satisfassent le prédicat
	 * @param r le robot cherchant le chemin
	 * @param carte la carte de la simulation
	 * @param estCaseCorrecte prédicat indiquant si une case est une case de destination souhaitée
	 * @return le chemin s'il existe, null sinon
	 */
	public abstract Chemin trouverPlusProche(Robot r, Carte carte, Predicate<Case> estCaseCorrecte);
	
	/**
	 * Permet de calculer le plus court chemin entre le robot et une position voulue
	 * @param robot le robot cherchant le chemin
	 * @param carte la carte de la simulation
	 * @param objectif la position de destination souhaitée
	 * @return le chemin s'il existe, null sinon
	 */
	public abstract Chemin cheminVers(Robot robot, Carte carte, Case objectif);

}
