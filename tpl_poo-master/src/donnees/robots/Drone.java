package donnees.robots;

import java.awt.Color;
import dessin.RepresentationOval;
import donnees.terrain.Carte;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;
import ia.chemins.Chemin;

/**
 * Un robot pompier de type Drone
 */
public class Drone extends Robot {
	/**
	 *  Vitesse max possible, en km/h
	 */
	private static final double VITESSE_MAX = 150;
	/**
	 *  Vitesse par défaut, en km/h
	 */
	private static final double VITESSE_DEFAUT = 100;
	/**
	 *  temps nécessaire pour remplir entièrement le reservoir, en secondes
	 */
	private static final int TEMPS_REMPLISSAGE = 1800;
	/**
	 *  Capacité du reservoir, en litres
	 */
	private static final int CAPACITE_RESERVOIR = 10000;
	/**
	 *  Intervention unitaire du robot, en L / s
	 *  La première case du tableau correspond au volume de l'intervention et la deuxième à sa durée
	 */
	private static final int INTERVENTION[] = { CAPACITE_RESERVOIR, 30 };

	/**
	 * Construit un drone avec une vitesse spécifique
	 * @param position position du drone
	 * @param vitesse vitesse du drone
	 * @throws IllegalArgumentException si la vitesse est invalide
	 */
	public Drone(Case position, double vitesse) throws IllegalArgumentException {
		super(position, vitesse, CAPACITE_RESERVOIR, TEMPS_REMPLISSAGE, VITESSE_MAX, INTERVENTION,
				new RepresentationOval(Color.BLACK, Color.DARK_GRAY, TAILLE));
	}

	/**
	 * Construit un drone avec la vitesse par défaut
	 * @param position la position du drone
	 * @throws IllegalArgumentException si la vitesse par défaut est invalide
	 */
	public Drone(Case position) throws IllegalArgumentException {
		this(position, VITESSE_DEFAUT);
	}

	@Override
	protected double getCoefficientVitesse(NatureTerrain natureTerrain) {
		return 1;
	}
	
	@Override
	protected Chemin calculerCheminRemplissage(Carte carte)
	{
		return algoRecherche.trouverPlusProche(this, carte, NatureTerrain.EAU);
	}

	@Override
	protected Robot doCopy() throws IllegalArgumentException {
		return new Drone(new Case(getPosition()), getVitesseBase());
	}
	
	@Override
	public boolean peutSeRemplir(Carte c)
	{
		return getPosition().getNature() == NatureTerrain.EAU;
	}
}
