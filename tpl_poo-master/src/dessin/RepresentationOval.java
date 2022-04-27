package dessin;

import java.awt.Color;

import gui.GraphicalElement;
import gui.Oval;

/**
 * Une représentation en oval d'un élément
 */
public class RepresentationOval extends Representation {

	/**
	 * Permet de construire l'oval
	 * @param drawColor couleur de contour
	 * @param fillColor couleur de fond
	 * @param size taille en pixels
	 */
	public RepresentationOval(Color drawColor, Color fillColor, int size) {
		super(drawColor, fillColor, size);
	}

	@Override
	public GraphicalElement toGraphicalElement(int x, int y, int tailleCase) {
		return new Oval(gridToPosition(x, tailleCase), gridToPosition(y, tailleCase), getDrawColor(), getFillColor(), getSize());
	}

}
