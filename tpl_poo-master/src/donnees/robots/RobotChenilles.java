package donnees.robots;

import java.awt.Color;
import dessin.RepresentationRectangle;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;

/**
 * Classe représentant un robot à chenilles
 */
public class RobotChenilles extends Robot {
	/**
	 * Vitesse max possible en km/h
	 */
	private static final double VITESSE_MAX = 80;
	/**
	 * Vitesse par défaut en km/h
	 */
	private static final double VITESSE_DEFAUT = 60;
	/**
	 * temps pour remplir entièrement le reservoir en secondes
	 */
	private static final int TEMPS_REMPLISSAGE = 300;
	/**
	 * Capacité du reservoir, en litres
	 */
	private static final int CAPACITE_RESERVOIR = 2000;
	/**
	 * Intervention unitaire du robot, en L / s La première case du tableau
	 * correspond au volume de l'intervention et la deuxième à sa durée
	 */
	private static final int INTERVENTION[] = { 100, 8 };

	/**
	 * Construit un robot à chenille à partir d'une vitesse spécifique
	 * 
	 * @param position position initiale
	 * @param vitesse  vitesse du robot
	 * @throws IllegalArgumentException si la vitesse est invalide
	 */
	public RobotChenilles(Case position, double vitesse) throws IllegalArgumentException {
		super(position, vitesse, CAPACITE_RESERVOIR, TEMPS_REMPLISSAGE, VITESSE_MAX, INTERVENTION,
				new RepresentationRectangle(Color.GRAY, Color.ORANGE, TAILLE));
	}

	/**
	 * Construit un robot à chenille à partir de la vitesse par défaut
	 * 
	 * @param position position initiale
	 * @throws IllegalArgumentException si la vitesse par défaut du robot à chenille
	 *                                  est invalide
	 */
	public RobotChenilles(Case position) throws IllegalArgumentException {
		this(position, VITESSE_DEFAUT);
	}

	@Override
	protected double getCoefficientVitesse(NatureTerrain natureTerrain) {
		switch (natureTerrain) {
		case FORET:
			return .5;
		case EAU:
		case ROCHE:
			return 0;
		default:
			return 1;
		}
	}

	@Override
	protected Robot doCopy() throws IllegalArgumentException {
		return new RobotChenilles(new Case(getPosition()), getVitesseBase());
	}
}
