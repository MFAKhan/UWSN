package SimpleGA;

import UWSN_ResurfacingGA.ResurfacingSimulation;

public class FitnessCalc {

	static byte[] solution = new byte[7];

	/* Public methods */
	// Set a candidate solution as a byte array
	public static void setSolution(final byte[] newSolution) {
		solution = newSolution;
	}

	public static void setSolution(final int newSolution) {
		final int sol = newSolution;
		setSolution(Integer.toBinaryString(sol));
	}

	// To make it easier we can use this method to set our candidate solution
	// with string of 0s and 1s
	public static void setSolution(final String newSolution) {
		byte[] sol = new byte[7];
		sol = new byte[newSolution.length()];
		// Loop through each character of our string and save it in our byte
		// array
		for (int i = 0; i < newSolution.length(); i++) {
			final String character = newSolution.substring(i, i + 1);
			if (character.contains("0") || character.contains("1")) {
				sol[i] = Byte.parseByte(character);
			} else {
				sol[i] = 0;
			}
		}
		setSolution(sol);
	}

	// Calculate inidividuals fittness by comparing it to our candidate solution
	static double getFitness(final Individual individual) {
		double fitness = 0;
		// fitness = binaryToInteger(individual.getGeneString());
		fitness = new ResurfacingSimulation().Run(binaryToInteger(individual.getGeneString()));
		return fitness;
	}

	// Get optimum fitness
	static double getMaxFitness() {
		double maxFitness = 0;
		maxFitness = binaryToInteger(solution);
		return maxFitness;
	}

	public static int binaryToInteger(final byte[] binary) {
		int result = 0;
		int count = 0;
		for (int i = binary.length - 1; i >= 0; i--) {
			if (binary[i] == 1) {
				result += (int) Math.pow(2, count);
			}
			count++;
		}
		return result;
	}

}