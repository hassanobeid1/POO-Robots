package donnees.terrain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Représente une case du terrain et peut être utilisé en tant que position
 */
public class Case {
	/**
	 * Ligne de la case sur le terrain
	 */
	private int ligne;
	/**
	 * Colonne de la case sur le terrain
	 */
	private int colonne;
	/**
	 * Nature du terrain de cette case
	 */
	private NatureTerrain nature;

	/**
	 * Construit une case avec une nature de terrain
	 * 
	 * @param ligne   ligne de la case
	 * @param colonne colonne de la case
	 * @param nature  nature de terrain de la case
	 */
	public Case(int ligne, int colonne, NatureTerrain nature) {
		this.ligne = ligne;
		this.colonne = colonne;
		this.nature = nature;
	}

	/**
	 * Construit une case vide
	 * 
	 * @param ligne   ligne de la case
	 * @param colonne colonne de la case
	 */
	public Case(int ligne, int colonne) {
		this.ligne = ligne;
		this.colonne = colonne;
		this.nature = NatureTerrain.TERRAIN_LIBRE;
	}

	/**
	 * Construit une case en copiant une case existante
	 * 
	 * @param position case à copier
	 */
	public Case(Case position) {
		this(position.ligne, position.colonne, position.nature);
	}

	/**
	 * @return la ligne de la case
	 */
	public int getLigne() {
		return ligne;
	}

	/**
	 * @return la colonne de la case
	 */
	public int getColonne() {
		return colonne;
	}

	/**
	 * @return la nature de la case
	 */
	public NatureTerrain getNature() {
		return nature;
	}

	/**
	 * Retourne la distance en cases entre deux cases, en interdisant les diagonales
	 * 
	 * @param autre la case avec laquelle on veut calculer la distance
	 * @return la distance en case
	 */
	public int distance(Case autre) {
		return Math.abs(autre.ligne - ligne) + Math.abs(autre.colonne - colonne);
	}

	/**
	 * Retourne toutes les voisines valide de la case
	 * 
	 * @param carte terrain de la simulation
	 * @return une liste des voisines valides
	 */
	public List<Case> getVoisines(Carte carte) {
		return Arrays.asList(Direction.values()).stream().map(dir -> carte.getVoisin(this, dir))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null && !(o instanceof Case))
			return false;
		Case c = (Case) o;
		// On fait le check que sur la position
		return this.ligne == c.ligne && this.colonne == c.colonne;
	}

		@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// On fait le check que sur la position
		result = prime * result + colonne;
		result = prime * result + ligne;
		return result;
	}

	@Override
	public String toString() {
		return "[" + colonne + ";" + ligne + "]";
	}
}
