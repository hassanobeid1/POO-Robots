package donnees;

import java.awt.Color;

import java.util.List;

import dessin.RepresentationOval;
import donnees.simulation.Simulateur;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;
import gui.GraphicalElement;

/**
 * Classe représentant un incendie dans la simulation
 */
public class Incendie implements Comparable<Incendie> {
	/**
	 * Position de l'incendie
	 */
	private Case position;
	/**
	 * Intensité actuelle de l'incendie, qui représente le nombre de litres qu'il faut lui appliquer pour l'éteindre
	 */
	private int intensite;
	/**
	 * Intensité maximale d'un incendie
	 */
	private final static int MAX_INTENSITE = 300000;

	/**
	 * Construit un incendie à partir de ses paramètres
	 * @param position position de l'incendie
	 * @param volumeEau volume d'eau nécessaire pour éteindre l'incendie
	 * @throws IllegalArgumentException si la nature de la case à la position donnée ne permet pas un incendie
	 */
	public Incendie(Case position, int volumeEau) throws IllegalArgumentException {
		if (position.getNature() == NatureTerrain.EAU || position.getNature() == NatureTerrain.ROCHE)
			throw new IllegalArgumentException("Un incendie ne peut pas se déclarer sur de l'eau ou de la roche");
		
		this.position = position;
		setIntensite(volumeEau);
	}
	
	/**
	 * Constructeur par copie d'un incendie
	 * @param incendie l'incendie à copier
	 */
	public Incendie(Incendie incendie) {
		this(incendie.position, incendie.intensite);
	}

	/**
	 * @return la position de l'incendie
	 */
	public Case getPosition() {
		return position;
	}
	
	/**
	 * Définit la position de l'incendie (utilisé dans le chargement mais bon pas ouf)
	 * @param position nouvelle position
	 */
	public void setPosition(Case position) {
		this.position = position;
	}

	/**
	 * @return l'intensité courante de l'incendie
	 */
	public int getIntensite() {
		return intensite;
	}
	
	/**
	 * Définit l'intensité courante de l'incendie
	 * @param intensite nouvelle intensité
	 */
	public void setIntensite(int intensite) {
		this.intensite = Math.min(MAX_INTENSITE, intensite);
	}

	/** 
	 * Retourne l'élément graphique de l'incendie permettant sa représentation
	 * @param tailleCase taille d'une case en pixels
	 * @return l'élement graphique de la librairie fournie
	 */
	public GraphicalElement getGraphicalElement(int tailleCase) {
		int maxIntensite = 100000, minTaille = 3, maxTaille = tailleCase;
		int tailleIncendie = (int) (intensite /(double) maxIntensite * tailleCase);
			
		// La taille de l'incendie depend de son intensite et on s'assure qu'elle est dans un intervalle donne
		if (tailleIncendie > maxTaille)
			tailleIncendie = maxTaille;
		else if (tailleIncendie < minTaille)
			tailleIncendie = minTaille;
		
		return (new RepresentationOval(Color.YELLOW, Color.RED, tailleIncendie))
				.toGraphicalElement(position.getColonne(), position.getLigne(), tailleCase);
	}
	
	/**
	 * Propage l'incendie aux cases voisines, si celles-ci sont de nature viables pour le développement d'un incendie.
	 * Propage la moitié de l'intensité de l'incendie, réparti entre les différents voisins
	 * Si un incendie est déjà présent dans une des cases voisines, seule une petite proportion est propagée
	 * @param simulateur le simulateur
	 */
	public void propager(Simulateur simulateur)
	{
		// On propage 50% de l'intensite, partage entre les voisins
		List<Case> voisins = position.getVoisines(simulateur.getDonneeSimulation().getCarte());
		int intensitePropagee = intensite / (2 * voisins.size()); 
		if (intensitePropagee <= 0)
			return;
		
		for (Case voisine : voisins)
		{
			// Si les cases sont de l'eau ou de la roche l'incendie ne s'y propage pas
			if (voisine.getNature() != NatureTerrain.EAU && voisine.getNature() != NatureTerrain.ROCHE)
			{
				// On ajoute l'intensite a un incendie existant, ou alors on ajoute un incendie dans la simulation
				Incendie incendieVoisin = simulateur.getDonneeSimulation().getSingleIncendie(voisine);
				if (incendieVoisin != null)
					// L'intensite propagee est reduite si un incendie existe deja
					incendieVoisin.setIntensite(incendieVoisin.getIntensite() + intensitePropagee / 4);
				else
					simulateur.ajouterIncendie(new Incendie(voisine, intensitePropagee));
			}
				
		} 
	}
	
	/**
	 * Retourne le temps nécessaire à la propagation de cet incendie
	 * @param tailleCase taille d'une case en mètres
	 * @return le temps de la prochaine propagation
	 */
	public int getTempsPropagation(int tailleCase) {
		double coeff = 0;
		switch(position.getNature())
		{
		case FORET:
			coeff = 1;
			break;
		case HABITAT:
			coeff = 0.4;
			break;
		case TERRAIN_LIBRE:
			coeff = 0.2;
			break;
		default: //Impossible d'etre la normalement
		return -1;
		}
		// En metre par secondes, pour une intensite de 10
		double vitesseBase =  0.1;
		// La vitesse de propgation evolue avec le log de son intensite
		double vitessePropagation = coeff * vitesseBase * Math.log10(intensite);
		return (int) (tailleCase / vitessePropagation);
	}
	
	@Override
	public int compareTo(Incendie other) {
		return intensite - other.intensite;
	}

}
