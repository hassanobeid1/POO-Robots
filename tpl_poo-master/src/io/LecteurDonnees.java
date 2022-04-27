package io;


import java.io.*;
import java.util.*;

import donnees.Incendie;
import donnees.robots.*;
import donnees.simulation.DonneeSimulation;
import donnees.terrain.Carte;
import donnees.terrain.Case;
import donnees.terrain.NatureTerrain;



/**
 * Lecteur de cartes au format spectifié dans le sujet.
 * Les données sur les cases, robots puis incendies sont lues dans le fichier,
 * puis simplement affichées.
 * A noter: pas de vérification sémantique sur les valeurs numériques lues.
 *
 * IMPORTANT:
 *
 * Cette classe ne fait que LIRE les infos et les afficher.
 * A vous de modifier ou d'ajouter des méthodes, inspirées de celles présentes
 * (ou non), qui CREENT les objets au moment adéquat pour construire une
 * instance de la classe DonneesSimulation à partir d'un fichier.
 *
 * Vous pouvez par exemple ajouter une méthode qui crée et retourne un objet
 * contenant toutes les données lues:
 *    public static DonneesSimulation creeDonnees(String fichierDonnees);
 * Et faire des méthode creeCase(), creeRobot(), ... qui lisent les données,
 * créent les objets adéquats et les ajoutent ds l'instance de
 * DonneesSimulation.
 */
public class LecteurDonnees {


    /**
     * Lit et affiche le contenu d'un fichier de donnees (cases,
     * robots et incendies).
     * Ceci est méthode de classe; utilisation:
     * LecteurDonnees.lire(fichierDonnees)
     * @param fichierDonnees nom du fichier à lire
     */
    public static DonneeSimulation lire(String fichierDonnees)
        throws FileNotFoundException, IllegalArgumentException {
        LecteurDonnees lecteur = new LecteurDonnees(fichierDonnees);
        Carte carte = lecteur.lireCarte();
        List<Incendie> incendies =  lecteur.lireIncendies(carte);
        List<Robot> robots = lecteur.lireRobots(carte);
        scanner.close();
        
        return new DonneeSimulation(incendies, carte, robots);
    }




    // Tout le reste de la classe est prive!

    private static Scanner scanner;

    /**
     * Constructeur prive; impossible d'instancier la classe depuis l'exterieur
     * @param fichierDonnees nom du fichier a lire
     */
    private LecteurDonnees(String fichierDonnees)
        throws FileNotFoundException {
        scanner = new Scanner(new File(fichierDonnees));
        scanner.useLocale(Locale.US);
    }

    /**
     * Lit et affiche les donnees de la carte.
     * @throws DataFormatException
     */
    private Carte lireCarte() throws IllegalArgumentException {
        ignorerCommentaires();
        Carte carte;
        try {
            int nbLignes = scanner.nextInt();
            int nbColonnes = scanner.nextInt();
            int tailleCases = scanner.nextInt();	// en m
            carte = new Carte(nbLignes, nbColonnes, tailleCases);

            for (int lig = 0; lig < nbLignes; lig++) {
                for (int col = 0; col < nbColonnes; col++) {
                    carte.setCase(lig, col, lireCase(lig, col));
                }
            }
            return carte;

        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Format invalide. "
                    + "Attendu: nbLignes nbColonnes tailleCases");
        }
        // une ExceptionFormat levee depuis lireCase est remontee telle quelle
    }




    /**
     * Lit et affiche les donnees d'une case.
     */
    private NatureTerrain lireCase(int lig, int col) throws IllegalArgumentException {
        ignorerCommentaires();
        try {
        	String chaineNature = scanner.next();
            NatureTerrain nature = NatureTerrain.valueOf(chaineNature);
            verifieLigneTerminee();
            return nature;
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("format de case invalide. "
                    + "Attendu: nature altitude [valeur_specifique]");
        }
    }


    /**
     * Lit et affiche les donnees des incendies.
     */
    private List<Incendie> lireIncendies(Carte carte) throws IllegalArgumentException {
        ignorerCommentaires();
        List<Incendie> incendies = new ArrayList<Incendie>();
        try {
            int nbIncendies = scanner.nextInt();
            for (int i = 0; i < nbIncendies; i++) {
                Incendie incendie = lireIncendie(i);
                incendie.setPosition(carte.getCase(incendie.getPosition().getLigne(),incendie.getPosition().getColonne()));
                incendies.add(incendie);
            }
            return incendies;

        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Format invalide. "
                    + "Attendu: nbIncendies");
        }
    }


    /**
     * Lit et affiche les donnees du i-eme incendie.
     * @param i
     */
    private Incendie lireIncendie(int i) throws IllegalArgumentException {
        ignorerCommentaires();
        Incendie incendie;
        try {
            int lig = scanner.nextInt();
            int col = scanner.nextInt();
            int intensite = scanner.nextInt();
            if (intensite <= 0) {
                throw new IllegalArgumentException("incendie " + i
                        + "nb litres pour eteindre doit etre > 0");
            }
            verifieLigneTerminee();
            incendie = new Incendie(new Case(lig, col), intensite);
            return incendie;

        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("format d'incendie invalide. "
                    + "Attendu: ligne colonne intensite");
        }
    }


    /**
     * Lit et affiche les donnees des robots.
     */
    private List<Robot> lireRobots(Carte carte) throws IllegalArgumentException {
        ignorerCommentaires();
        List<Robot> robots = new ArrayList<>();
        try {
            int nbRobots = scanner.nextInt();
            for (int i = 0; i < nbRobots; i++) {
                Robot robot = lireRobot(i);
                robot.setPosition(carte.getCase(robot.getPosition().getLigne(),robot.getPosition().getColonne()));
                if(robot != null){
                    robots.add(robot);
                }
            }
            return robots;
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Format invalide. "
                    + "Attendu: nbRobots");
        }
    }


    /**
     * Lit et affiche les donnees du i-eme robot.
     * @param i
     */
    private Robot lireRobot(int i) throws IllegalArgumentException {
        ignorerCommentaires();
        Robot robot;
        try {
            int lig = scanner.nextInt();
            int col = scanner.nextInt();
            String type = scanner.next();
            RobotType rType = RobotType.valueOf(type);

            String s = scanner.findInLine("(\\d+)");	// 1 or more digit(s) ?
            // pour lire un flottant:    ("(\\d+(\\.\\d+)?)");
            if (s == null) {
                robot = this.createRobot(lig,col, rType, null);
            } else {
                int vitesse = Integer.parseInt(s);
                robot = this.createRobot(lig,col, rType, (double)vitesse);
            }
            verifieLigneTerminee();
            return robot;
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("format de robot invalide. "
                    + "Attendu: ligne colonne type [valeur_specifique]");
        }
    }

    private Robot createRobot(int lig, int col, RobotType type, Double vitesse) throws IllegalArgumentException {
        switch (type) {
            case DRONE:
                return vitesse != null ? new Drone(new Case(lig, col), vitesse) : new Drone(new Case(lig, col));
            case ROUES:
                return vitesse != null ? new RobotRoues(new Case(lig, col), vitesse) : new RobotRoues(new Case(lig, col));
            case PATTES:
                return new RobotPattes(new Case(lig, col));
            case CHENILLES:
                return vitesse != null ? new RobotChenilles(new Case(lig, col), vitesse) : new RobotChenilles(new Case(lig, col));
            default:
                return null;
        }
    }




    /** Ignore toute (fin de) ligne commencant par '#' */
    private void ignorerCommentaires() {
        while(scanner.hasNext("#.*")) {
            scanner.nextLine();
        }
    }

    /**
     * Verifie qu'il n'y a plus rien a lire sur cette ligne (int ou float).
     * @throws DataFormatException
     */
    private void verifieLigneTerminee() throws IllegalArgumentException {
        if (scanner.findInLine("(\\d+)") != null) {
            throw new IllegalArgumentException("format invalide, donnees en trop.");
        }
    }
}
