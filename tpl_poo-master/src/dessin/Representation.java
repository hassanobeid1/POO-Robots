package dessin;

import java.awt.Color;

import gui.GraphicalElement;

/**
 * Classe abstraite permettant de représenter un élément sur la fenêtre
 * graphique
 * 
 * @see RepresentationOval
 * @see RepresentationRectangle
 */
public abstract class Representation {
	/**
	 * La couleur de contour de l'élément
	 */
	private Color drawColor;
	/**
	 * La couleur de fond de l'élément
	 */
	private Color fillColor;
	/**
	 * La taille de l'élément
	 */
	private int size;

	/**
	 * Constructeur permettant aux classes dérivées d'intialiser les caractéristique
	 * de base
	 * 
	 * @param drawColor couleur de contour
	 * @param fillColor couleur de fond
	 * @param size      taille en pixels
	 */
	protected Representation(Color drawColor, Color fillColor, int size) {
		this.drawColor = drawColor;
		this.fillColor = fillColor;
		this.size = size;
	}

	/**
	 * @return la taille de l'élément
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return la couleur de contour de l'élément
	 */
	public Color getDrawColor() {
		return drawColor;
	}

	/**
	 * @return la couleur de fond de l'élément
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Permet de passer du repère de grille utilisé dans la simulation au repère en
	 * pixels utilisé dans le librairie graphique
	 * 
	 * @param coordinate l'abscisse ou ordonnée dans le repère de la grille
	 * @param tailleCase la taille d'une case en pixels
	 * @return la valeur dans le repère graphique
	 */
	public int gridToPosition(int coordinate, int tailleCase) {
		return coordinate * tailleCase + tailleCase / 2;

	}

	/**
	 * Permet de retourner l'élément graphique utilisé dans la librairie graphique correspondant à cette représentation graphique
	 * @param x abscisse de l'élément en pixels
	 * @param y ordonné de l'élément en pixels
	 * @param tailleCase taille d'une case en pixels
	 * @return Un objet de type {@link GraphicalElement}
	 */
	public abstract GraphicalElement toGraphicalElement(int x, int y, int tailleCase);
}
