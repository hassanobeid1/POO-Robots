package donnees.terrain;

/**
 * Représentation du terrain de la simulation
 */
public class Carte {
	/**
	 * Taille d'une case, en mètres
	 */
	private int tailleCase;
	/**
	 * Tableau deux dimensions répertoriant toutes les cases du terrain
	 */
	private Case[][] cases;

	/**
	 * Construit une carte vide
	 * 
	 * @param nbLignes   nombre de lignes de la carte (hauteur)
	 * @param nbColonnes nombre de colonnes de la carte (largeur)
	 * @param tailleCase taille d'une case en mètres
	 */
	public Carte(int nbLignes, int nbColonnes, int tailleCase) {
		this.cases = new Case[nbLignes][nbColonnes];
		this.tailleCase = tailleCase;
	}

	/**
	 * @return le nombre de lignes de la carte (hauteur)
	 */
	public int getNbLignes() {
		return this.cases.length;
	}

	/**
	 * @return le nombre de colonnes de la carte (largeur)
	 */
	public int getNbColonnes() {
		return this.cases[0].length;
	}

	/**
	 * @return la taille d'une case, en mètres
	 */
	public int getTailleCase() {
		return tailleCase;
	}

	/**
	 * Retourne une case du terrain à partir de sa position
	 * 
	 * @param lig ligne de la case
	 * @param col colonne de la case
	 * @return la case correspondante, ou null si la position est en dehors du
	 *         terrain
	 */
	public Case getCase(int lig, int col) {
		if (lig < 0 || lig >= getNbLignes() || col < 0 || col >= getNbColonnes())
			return null;

		return this.cases[lig][col];
	}

	/**
	 * Défini la nature de terrain à une position donnée
	 * 
	 * @param lig  ligne de la case
	 * @param col  colonne de la case
	 * @param type nature de la case
	 */
	public void setCase(int lig, int col, NatureTerrain type) {
		this.cases[lig][col] = new Case(lig, col, type);
	}

	/**
	 * Retourne la case voisine d'une case donnée
	 * 
	 * @param src la case de départ
	 * @param dir la direction voulue
	 * @return la case voisine dans la direction voulue, ou null si celle-ci est en
	 *         dehors du terrain
	 */
	public Case getVoisin(Case src, Direction dir) {
		int ligne = src.getLigne() + dir.getLigne();
		int colonne = src.getColonne() + dir.getColonne();

		return getCase(ligne, colonne);
	}
}
