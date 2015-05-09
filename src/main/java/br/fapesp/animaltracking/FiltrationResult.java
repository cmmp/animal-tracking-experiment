package br.fapesp.animaltracking;

import br.fapesp.myutils.MyUtils;

public class FiltrationResult {

	/**
	 * number of N dimensional holes, starting from 0 (connected components), holes, voids, etc.
	 */
	public int[] nDholes;
	
	/**
	 * maximum lifetime of an N-dimensional hole, starting from 0.
	 */
	public double[] maxHoleLifeTime;
	
	/**
	 * average lifetime of a N-dimensional hole
	 */
	public double[] averageHoleLifeTime;
	
	/**
	 * get the number of revelant holes based on a user-defined threshold
	 */
	public int[] nDrelevantHoles;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Filtration result: ");
		sb.append("ndHoles: " + MyUtils.arrayToString(nDholes));
		sb.append("; maxHoleLifeTime: " + MyUtils.arrayToString(maxHoleLifeTime));
		sb.append("; averageHoleLifeTime: " + MyUtils.arrayToString(averageHoleLifeTime));
		sb.append("; ndRelevantHoles: " + MyUtils.arrayToString(nDrelevantHoles));
		sb.append('\n');
		return sb.toString();
	}
	
}
