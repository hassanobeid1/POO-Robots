package dessin;

import java.awt.Color;

import gui.GraphicalElement;
import gui.Rectangle;

/**
 * Une représentation rectangulaire d'un élément
 */
public class RepresentationRectangle extends Representation {
	
	/**
	 * Construit le rectangle
	 * @param drawColor couleur de contour
	 * @param fillColor couleur de fond
	 * @param size taille en pixels
	 */
	public RepresentationRectangle(Color drawColor, Color fillColor, int size) {
		super(drawColor, fillColor, size);
	}

	@Override
	public GraphicalElement toGraphicalElement(int x, int y, int tailleCase) {
		return new Rectangle(gridToPosition(x, tailleCase), gridToPosition(y, tailleCase), getDrawColor(), getFillColor(), getSize());
	}

}
