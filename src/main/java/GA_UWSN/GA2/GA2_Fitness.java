package GA_UWSN.GA2;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;
import UWSN_Simulator_1.Tour;
import UWSN_Simulator_1.VoIEvaluation;

public class GA2_Fitness {

	// Calculate inidividuals fittness by comparing it to our candidate solution
	static double getFitness(final ArrayList<Integer> T, final SimulationMap Map, final int NumNodes, final int NumAUVs,
			final double Speed, final String DistanceType, final double DistanceScale, final double TimeStamp) {
		final ArrayList<Integer> Tour = new ArrayList<Integer>();
		double fitness = 0;
		for (int i = 0; i < T.size(); i++) {
			final int NodetoAdd = T.get(i);
			if (NodetoAdd != -3) {
				Tour.add(NodetoAdd);
			}
		}
		fitness = CreateAndEvaluateTour(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp, Tour);
		return fitness;
	}

	public static double CreateAndEvaluateTour(final SimulationMap Map, final int NumNodes, final int NumAUVs,
			final double Speed, final String DistanceType, final double DistanceScale, final double TimeStamp,
			final ArrayList<Integer> Tour) {
		final Tour T = new Tour("Create From Input", NumAUVs, NumNodes, Map, DistanceType, -1, Speed, DistanceScale,
				TimeStamp, Tour);
		final VoIEvaluation X = new VoIEvaluation();
		T.setVoIAccumulatedByTour(X.VoIAllAUVTours("Transmit", T, Map, TimeStamp, Speed, DistanceScale, DistanceType));
		return T.getVoIAccumulatedByTour();
	}

}