package GA_UWSN.GA1;

import UWSN_Simulator_1.SimulationMap;
import UWSN_Simulator_1.Tour;
import UWSN_Simulator_1.VoIEvaluation;

public class GA1_Fitness {

	static byte[] solution = {};

	/* Public methods */
	// Set a candidate solution as a byte array
	public static void setSolution(final byte[] newSolution) {
		solution = newSolution;
	}

	public static void setSolution(final int newSolution, final int NumOfNodes) {
		final int sol = newSolution;
		setSolution(Integer.toBinaryString(sol), NumOfNodes);
	}

	// To make it easier we can use this method to set our candidate solution
	// with string of 0s and 1s
	public static void setSolution(final String newSolution, final int NumOfNodes) {
		final int genesLength = ((int) (Math.log(NumOfNodes) / Math.log(2)) + 1);
		byte[] sol = new byte[genesLength];
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
		// for (int i = newSolution.length(); i < genesLength; i++) {
		// sol[i] = 0;
		// }
		setSolution(sol);
	}

	// Calculate inidividuals fittness by comparing it to our candidate solution
	static double getFitness(final byte[] genes, final SimulationMap Map, final int NumNodes, final int NumAUVs,
			final double Speed, final String DistanceType, final double DistanceScale, final double TimeStamp,
			final boolean Reversal) {
		double fitness = 0;
		int NumResurfaceStops = binaryToInteger(genes);
		if (NumResurfaceStops > NumNodes) {
			NumResurfaceStops = NumNodes;
		}
		fitness = CreateAndEvaluateTour(NumResurfaceStops, Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale,
				TimeStamp, Reversal);
		return fitness;
	}

	public static double CreateAndEvaluateTour(final int NumResurfaceStops, final SimulationMap Map, final int NumNodes,
			final int NumAUVs, final double Speed, final String DistanceType, final double DistanceScale,
			final double TimeStamp, final boolean Reversal) {
		final Tour T;
		if (Reversal == false) {
			T = new Tour("Resurface After K-Nodes", NumAUVs, NumNodes, Map, DistanceType, NumResurfaceStops, Speed,
					DistanceScale, TimeStamp, null);
		} else {
			T = new Tour("Reversal : Resurface After K-Nodes", NumAUVs, NumNodes, Map, DistanceType, NumResurfaceStops,
					Speed, DistanceScale, TimeStamp, null);
		}
		final VoIEvaluation X = new VoIEvaluation();
		T.setVoIAccumulatedByTour(X.VoIAllAUVTours("Transmit", T, Map, TimeStamp, Speed, DistanceScale, DistanceType));
		return T.getVoIAccumulatedByTour();
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