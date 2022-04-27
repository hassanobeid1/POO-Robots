package ia.strategie;

import java.util.Comparator;

import donnees.robots.Robot;

/**
 * Le résultat d'un calcul de coût d'extinction d'un incendie par un robot
 */
public class ResultIncendie {
	/**
	 * Comparateur permettant de trier les résultats par ordre croissant sur le coût
	 */
	public static final Comparator<ResultIncendie> COMPARATEUR = new Comparator<>() {
		public int compare(ResultIncendie r1, ResultIncendie r2) {
			return (int) Math.signum(r1.getCout() - r2.getCout());
		}
	};
	/**
	 * Le résultat par défaut : aucun robot et un coût maximal
	 */
	public static final ResultIncendie DEFAULT = new ResultIncendie(null, Double.MAX_VALUE);

	/**
	 * Le robot concerné
	 */
	private Robot robot;
	/**
	 * Le coût de l'extinction
	 */
	private double cout;

	/**
	 * Construit un résultat
	 * @param robot le robot
	 * @param cout le coût d'extinction
	 */
	public ResultIncendie(Robot robot, double cout) {
		this.robot = robot;
		this.cout = cout;
	}
	
	/**
	 * @return le robot
	 */
	public Robot getRobot() {
		return robot;
	}
	
	/**
	 * @return le coût d'extinction
	 */
	public double getCout() {
		return cout;
	}

}
