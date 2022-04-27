package donnees.robots;

import java.awt.Color;
import dessin.RepresentationRectangle;
import donnees.Reservoir;
import donnees.terrain.Carte;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;

/**
 * Un robot à pattes (sisi je vous jure)
 */
public class RobotPattes extends Robot {
	/**
	 * Vitesse par défaut en km/h
	 */
	private static final double VITESSE_DEFAUT = 30;
	/**
	 * temps pour remplir entièrement le reservoir en secondes
	 */
	private static final int TEMPS_REMPLISSAGE = 0;
	/**
	 * Capacité du reservoir, en litres
	 */
	private static final int CAPACITE_RESERVOIR = Reservoir.CAPACITE_INFINIE;
	/**
	 * Intervention unitaire du robot, en L / s La première case du tableau
	 * correspond au volume de l'intervention et la deuxième à sa durée
	 */
	private static final int INTERVENTION[] = { 10, 1 };

	/**
	 * Construction d'un robot à pattes
	 * @param position position initiale du robot
	 * @throws IllegalArgumentException si jamais la vitesse par défaut du robot est invalide
	 */
	public RobotPattes(Case position) throws IllegalArgumentException {
		super(position, VITESSE_DEFAUT, CAPACITE_RESERVOIR, TEMPS_REMPLISSAGE, VITESSE_DEFAUT, INTERVENTION,
				new RepresentationRectangle(Color.ORANGE, Color.BLACK, TAILLE));
	}

	@Override
	protected double getCoefficientVitesse(NatureTerrain natureTerrain) {
		switch (natureTerrain) {
		case ROCHE:
			return 0.333;
		case EAU:
			return 0;
		default:
			return 1;
		}
	}

	@Override
	protected Robot doCopy() throws IllegalArgumentException {
		return new RobotPattes(new Case(getPosition()));
	}

	@Override
	public boolean peutSeRemplir(Carte c) {
		return false;
	}
}
