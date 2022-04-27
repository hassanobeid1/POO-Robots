package donnees.robots;

import java.awt.Color;

import dessin.RepresentationOval;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;

/**
 * Un robot ... à roues !
 */
public class RobotRoues extends Robot {
	/**
	 * Vitesse max possible en km/h
	 */
	private static final double VITESSE_MAX = VITESSE_INFINIE;
	/**
	 *  Vitesse par défaut en km/h
	 */
	private static final double VITESSE_DEFAUT = 80;
	/**
	 *  temps pour remplir entièrement le reservoir en secondes
	 */
	private static final int TEMPS_REMPLISSAGE = 600;
	/**
	 *  Capacité du reservoir
	 */
	private static final int CAPACITE_RESERVOIR = 5000;
	/**
	 *  Intervention unitaire du robot, en L / s
	 *  La première case du tableau correspond au volume de l'intervention et la deuxième à sa durée
	 */
	private static final int INTERVENTION[] = { 100, 5 };

	/**
	 * Construit un robot à roues avec une vitesse spécifique
	 * @param position position initiale
	 * @param vitesse vitesse de déplacement
	 * @throws IllegalArgumentException si la vitesse fournie est invalide
	 */
	public RobotRoues(Case position, double vitesse) throws IllegalArgumentException {
		super(position, vitesse, CAPACITE_RESERVOIR, TEMPS_REMPLISSAGE, VITESSE_MAX, INTERVENTION,
				new RepresentationOval(Color.BLACK, Color.CYAN, TAILLE));
	}

	/**
	 * Construit un robot à roues avec la vitesse par défaut
	 * @param position la position initiale
	 * @throws IllegalArgumentException si la vitesse par défaut est invalide
	 */
	public RobotRoues(Case position) throws IllegalArgumentException {
		this(position, VITESSE_DEFAUT);
	}

	@Override
	protected double getCoefficientVitesse(NatureTerrain natureTerrain) {
		switch (natureTerrain) {
		case TERRAIN_LIBRE:
		case HABITAT:
			return 1;
		default:
			return 0;
		}
	}
	
	@Override
	protected Robot doCopy() throws IllegalArgumentException {
		return new RobotRoues(new Case(getPosition()), getVitesseBase());
	}
}
