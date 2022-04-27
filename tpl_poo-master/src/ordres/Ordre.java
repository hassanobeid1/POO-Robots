package ordres;

/**
 * Un ordre donné par un chef pompier à un robot pompier
 */
public class Ordre {
	/**
	 * Les informations contenues dans l'ordre
	 */
	private Object data;
	/**
	 * Le type d'ordre
	 */
	private TypeOrdre type;
	
	/**
	 * Construit un ordre
	 * @param type le type d'ordre
	 * @param data les données associées
	 */
	public Ordre(TypeOrdre type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	/**
	 * @return les données associées à l'ordre
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * @return le type d'ordre
	 */
	public TypeOrdre getType() {
		return type;
	}
	

}
