package donnees;

/**
 * Réservoir d'un robot pompier
 */
public class Reservoir {
	/**
	 * Constante représentant une capacité infinie
	 */
	public static final int CAPACITE_INFINIE = -1;
	
	/**
	 * Capacité du réservoir, en litres
	 */
	private int capacite;
	/**
	 * Temps que prend le réservoir à se remplir, en secondes
	 */
	private int tempsRemplissage;
	/**
	 * Volume actuel du réservoir, en litres
	 */
	private int volumeActuel;

	/**
	 * Construit un réservoir
	 * @param capacite capacité du réservoir, en litres
	 * @param tempsRemplissage temps de remplissage du réservoir, en secondes
	 */
	public Reservoir(int capacite, int tempsRemplissage) {
		this.capacite = capacite;
		//D'apres le scenario 2 les robots sont remplis au debut
		this.volumeActuel = capacite;
		this.tempsRemplissage = tempsRemplissage;
	}
	
	/**
	 * Permet de vider le réservoir d'un volume voulu. Si le réservoir n'est pas assez plein, il se vide de son volume actuel
	 * @param volumeVoulu volume désiré en litres
	 * @return le volume qui a été enlevé du réservoir
	 */
	public int vider(int volumeVoulu)
	{
		if (capacite == CAPACITE_INFINIE)
			return volumeVoulu;
		
		int volume = Math.min(volumeVoulu, volumeActuel);
		volumeActuel -= volume;
		return volume;
	}	
	
	/**
	 * Remplit entièrement le réservoir
	 */
	public void remplir()
	{
		this.volumeActuel = capacite;
	}
	
	/**
	 * @return le temps de remplissage du réservoir, en secondes
	 */
	public int getTempsRemplissage()
	{
		return tempsRemplissage;
	}
	
	/**
	 *  Petite fonction utilitaire pour clarifier la sémantique du code.
	 * @return vrai si le réservoir est vide, faux sinon
	 */
	public boolean estVide()
	{
		// On sait jamais, normalement ne PEUT pas etre negatif
		return capacite != CAPACITE_INFINIE && getVolumeActuel() <= 0;
	}
	
	/**
	 * @return le volume actuel du réservoir, ou {@link #CAPACITE_INFINIE} s'il a un réservoir infini
	 */
	public int getVolumeActuel()
	{
		return capacite == CAPACITE_INFINIE ? CAPACITE_INFINIE : volumeActuel;
	}

	/**
	 * @return la capacité du réservoir en litres ou {@link #CAPACITE_INFINIE}
	 */
	public int getCapacite() {
		return capacite;
	}
	
	@Override 
	public String toString()
	{
		return "Reservoir : " + (capacite == CAPACITE_INFINIE ? "capacite infinie" : volumeActuel + "/" + capacite);
	}
}
