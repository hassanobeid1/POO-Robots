package dessin;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import donnees.Incendie;
import donnees.robots.Robot;
import donnees.simulation.DonneeSimulation;
import donnees.simulation.Simulateur;
import donnees.terrain.Carte;
import donnees.terrain.NatureTerrain;
import gui.GUISimulator;

/**
 * Classe utilisant la librairie graphique fournie pour afficher les données de
 * simulation Elle crée et possède la fenêtre graphique (le GUISimulator)
 */
public class DessinSimulation implements Dessinateur {

	/**
	 * Dictionnaire attribuant à chaque type de terrain une couleur
	 */
	private Map<NatureTerrain, Color> colorMap;
	/**
	 * L'instance de GUISimulator qui gère notamment la fenêtre graphique
	 */
	private GUISimulator gui;
	/**
	 * Taille du côté d'une case en pixels
	 */
	private int tailleCase;
	/**
	 * Constantes codant la taille de la fenêtre graphique désirée
	 */
	private static final int LARGEUR_FENETRE = 850, HAUTEUR_FENETRE = 600;

	/**
	 * Ce constructeur permet la création de la fenêtre graphique, le simulateur
	 * passé en argument est lié au GUISimulator
	 * 
	 * @param simulateur le simulateur qui sera utilisé par GUISimulator et
	 *                   représenté par cette classe
	 */
	public DessinSimulation(Simulateur simulateur) {
		// On calcule la taille desiree des cases, en essayant de ne pas sortir de la
		// fenêtre et en ayant une taille minimale
		Carte carte = simulateur.getDonneeSimulation().getCarte();
		tailleCase = Math.max(Math.min(LARGEUR_FENETRE / carte.getNbColonnes(), HAUTEUR_FENETRE / carte.getNbLignes()),
				20);
		int width = carte.getNbColonnes() * tailleCase, height = carte.getNbLignes() * tailleCase;

		gui = new GUISimulator(width, height, Color.BLACK, simulateur);

		initColorMap();
	}

	/**
	 * Cette méthode initialise {@link #colorMap}
	 */
	private void initColorMap() {
		this.colorMap = new HashMap<>();
		this.colorMap.put(NatureTerrain.EAU, Color.BLUE);
		this.colorMap.put(NatureTerrain.FORET, Color.GREEN);
		this.colorMap.put(NatureTerrain.ROCHE, Color.DARK_GRAY);
		this.colorMap.put(NatureTerrain.TERRAIN_LIBRE, Color.WHITE);
		this.colorMap.put(NatureTerrain.HABITAT, Color.PINK);
	}

	@Override
	public void draw(DonneeSimulation donnees) {
		gui.reset();
		this.drawCase(donnees.getCarte());
		this.drawIncendies(donnees.getIncendies());
		this.drawRobot(donnees.getRobots());

	}

	/**
	 * Permet d'accéder à la taille des cases
	 * 
	 * @return le taille des cases, en pixels
	 */
	public int getTailleCase() {
		return tailleCase;
	}

	/**
	 * Dessine la carte sur la fenêtre graphique
	 * 
	 * @param carte carte à dessiner
	 */
	private void drawCase(Carte carte) {
		int nbLignes = carte.getNbLignes();
		int nbColonnes = carte.getNbColonnes();

		for (int lig = 0; lig < nbLignes; lig++) {
			for (int col = 0; col < nbColonnes; col++) {
				NatureTerrain natureTerrain = carte.getCase(lig, col).getNature();
				Representation representation = new RepresentationRectangle(colorMap.get(natureTerrain),
						colorMap.get(natureTerrain), tailleCase);
				gui.addGraphicalElement(representation.toGraphicalElement(col, lig, tailleCase));
			}
		}
	}

	/**
	 * Dessine les incendies sur la fenêtre graphique
	 * 
	 * @param incendies incendies de la simulation
	 */
	private void drawIncendies(List<Incendie> incendies) {
		for (Incendie incendie : incendies) {
			gui.addGraphicalElement(incendie.getGraphicalElement(tailleCase));
		}
	}

	/**
	 * Dessine les robots sur la fenêtre graphique
	 * 
	 * @param robots robots de la simulation
	 */
	private void drawRobot(List<Robot> robots) {
		for (Robot robot : robots)
			gui.addGraphicalElement(robot.getGraphicalElement(tailleCase));
	}
}
