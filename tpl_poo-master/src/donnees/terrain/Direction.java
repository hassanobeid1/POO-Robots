package donnees.terrain;

/**
 * Énumération représentant les différentes directions permises pour les robots
 */
public enum Direction {
	NORD(0, -1), EST(1, 0), SUD(0, 1), OUEST(-1, 0);

	/**
	 * Déplacement en ligne représenté par la direction
	 */
	private int ligne;
	/**
	 * Déplacement en colonne représenté par la direction
	 */
	private int colonne;

	/**
	 * Constructeur interne permettant de décrire une direction
	 * 
	 * @param deplColonne déplacement en colonne
	 * @param deplLigne   déplacement en ligne
	 */
	private Direction(int deplColonne, int deplLigne) {
		ligne = deplLigne;
		colonne = deplColonne;
	}

	/**
	 * @return le déplacement en ligne représenté par la direction
	 */
	public int getLigne() {
		return ligne;
	}

	/**
	 * @return le déplacement en colonne représenté par la direction
	 */
	public int getColonne() {
		return colonne;
	}

	/**
	 * Retourne une direction depuis le déplacement qu'elle représente
	 * 
	 * @param ligneOffset   le déplacement en ligne, normalisé
	 * @param colonneOffset le déplacement en colonne, normalisé
	 * @return la direction correspondante, ou null si aucune direction ne
	 *         correspond
	 */
	public static Direction fromOffset(int ligneOffset, int colonneOffset) {
		for (Direction d : values()) {
			if (d.ligne == ligneOffset && d.colonne == colonneOffset)
				return d;
		}
		return null;
	}
}
