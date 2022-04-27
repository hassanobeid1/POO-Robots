package ia.chemins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import donnees.robots.Robot;
import donnees.terrain.Carte;
import donnees.terrain.Case;
import java.util.Comparator;

/**
 * Implémente l'algorithme de recherche de chemin A*. En pratique, lorsqu'on
 * connaît la case objectif, on utilise comme fonction heuristique la taille du
 * chemin "à vol d'oiseau".
 * 
 * Lorsqu'on ne connaît pas la case objectif, on utilise comme fonction
 * heuristique la fonction h: x-> 0. Dans ce cas A* est équivalent à Dijkstra.
 */
public class RechercheAStar extends RechercheChemin {

	/**
	 * Classe interne permettant de comparer deux noeuds suivant leur distance plus
	 * le résultat de la fonction heuristique
	 */
	private static class HeuristicComparator implements Comparator<Node> {

		@Override
		public int compare(Node n1, Node n2) {
			return (int) Math.signum(n1.esperance - n2.esperance);
		}
	}

	/**
	 * Noeud dans le graphe orienté pour la recherche de chemin
	 */
	private static class Node {
		/**
		 * Case de la carte représentée par le noeud
		 */
		public Case pos;
		/**
		 * Noeud précédent dans le chemin
		 */
		public Node prev;
		/**
		 * Distance au point de départ, soit la somme des coûts des arcs conduisant du
		 * noeud de départ à ce noeud.
		 */
		public double distance;
		/**
		 * Distance au point de départ plus le résultat de la fonction heuristique
		 */
		public double esperance;

		/**
		 * Construit un noeud dans le graphe
		 * 
		 * @param pos  la position de la carte représentée par ce noeud
		 * @param prev le noeud précédent ou null si ce noeud n'a pas d'antécédent
		 */
		public Node(Case pos, Node prev) {
			this.pos = pos;
			this.prev = prev;
			this.distance = 0;
			this.esperance = 0;
		}

		/**
		 * Construit un noeud sans antécédent
		 * 
		 * @param pos position de la carte représentée par ce noeud
		 */
		public Node(Case pos) {
			this(pos, null);
		}

		/**
		 * Méthode récursive privée permettant de reconstruire le chemin, utilisée par
		 * {@link #toChemin()}
		 * 
		 * @param liste la liste de cases permettant de rejoindre la destination depuis
		 *              ce noeud
		 * @param cout  le coût global du chemin
		 * @return le chemin correspondant
		 */
		private Chemin toChemin(List<Case> liste, double cout) {
			liste.add(0, pos);
			if (prev == null)
				return new Chemin(liste, cout);
			else
				return prev.toChemin(liste, cout);
		}

		/**
		 * @return le chemin représenté par ce noeud et ses antécédents
		 */
		public Chemin toChemin() {
			return toChemin(new ArrayList<>(), distance);
		}
	}

	/**
	 * Exécution de l'algorithme A* sur la carte.
	 * 
	 * La fonction heuristique doit être admissible (c'est-à-dire qu'elle ne doit
	 * jamais surestimer le coût total du chemin restant) et cohérente (c'est à dire
	 * qu'à un noeud elle associera toujours une valeur inférieure ou égale au coût
	 * estimé de tout sommet voisin à l'objectif, plus le coût pour atteindre ce
	 * sommet)
	 * 
	 * @param robot             robot cherchant son chemin
	 * @param carte             carte de la simulation
	 * @param pathCompleted     prédicat déterminant si une case est une fin de
	 *                          chemin valide (c'est-à-dire une solution de la
	 *                          recherche de chemin)
	 * @param heuristicFunction fonction heuristique déterminant le coût estimé du
	 *                          reste du chemin jusqu'à l'objectif
	 * @return le chemin calculé ou null si aucun objectif n'a pu être atteint
	 */
	private Chemin calculerChemin(Robot robot, Carte carte, Predicate<Case> pathCompleted,
			Function<Node, Double> heuristicFunction) {
		// La taille des cases est constante, du coup n'importe quelle constante fait
		// l'affaire
		final double TAILLE_CASE = 1;
		// L'ensemble des cases visites
		Set<Case> visites = new HashSet<>();
		// Les noeuds a visiter, ordonnes par priorite croissante (cout croissant)
		PriorityQueue<Node> pendingNodes = new PriorityQueue<>(new HeuristicComparator());

		// Initialisation, on ajoute le noeud correspondant a la position du robot dans
		// la liste
		pendingNodes.add(new Node(robot.getPosition()));
		// Tant qu'on a des noeuds a visiter, on a pas fini
		while (!pendingNodes.isEmpty()) {
			// On selectionne le noeud le plus interessant
			Node currentNode = pendingNodes.poll();

			// Si on est arrive a la fin alors on retourne le chemin
			if (pathCompleted.test(currentNode.pos))
				return currentNode.toChemin();

			// On parcourt les case voisines du noeud courant
			for (Case posVoisin : currentNode.pos.getVoisines(carte)) {
				// On prend une case voisine que si elle existe et qu'elle n'a pas ete traitee
				if (posVoisin == null || visites.contains(posVoisin))
					continue;

				// On cree le noeud correspondant a la case
				Node nextNode = new Node(posVoisin, currentNode);
				double vitesse = robot.getVitesse(nextNode.pos.getNature());
				// Si le terrain est accessible
				if (vitesse != 0) {
					// On defini la distance du noeud
					nextNode.distance = currentNode.distance + TAILLE_CASE / vitesse;
					// On ajoute l'estimation de la fonction heuristique
					nextNode.esperance = nextNode.distance + heuristicFunction.apply(nextNode);
					// On ajoute le noeud aux noeuds a visiter
					pendingNodes.add(nextNode);
					// On s'assure que le noeud ne pourra plus etre ajoute dans la liste des noeuds
					// a visiter
					visites.add(posVoisin);
				}
			}
		}

		// Si jamais on arrive ici, c'est qu'on a parcouru toutes les cases atteignables
		// sans atteindre une case satisfaisant les objectifs
		return null;
	}

	@Override
	public Chemin trouverPlusProche(Robot robot, Carte carte, Predicate<Case> estCaseCorrecte) {

		// System.out.println("Objective is some random pos : " + robot);
		return calculerChemin(robot, carte, estCaseCorrecte,
				n -> (double) carte.getNbColonnes() + carte.getNbLignes() + 1);
	}

	@Override
	public Chemin cheminVers(Robot robot, Carte carte, Case objectif) {

		// System.out.println("Objective is ["+ligne + ";" + colonne + "]");
		return calculerChemin(robot, carte, c -> c.equals(objectif), n -> 0.0);
	}
}
