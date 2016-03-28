package GA_UWSN;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;
import UWSN_Simulator_1.Tour;
import UWSN_Simulator_1.VoIEvaluation;

public class GA2_Fitness {

	public static SimulationMap M;
	public static double TS;

	public GA2_Fitness(final SimulationMap Map, final double TimeStamp) {
		this.M = Map;
		this.TS = TimeStamp;
	}

	public static double getFitness(final ArrayList<Integer> genes) {
		final double fitness = 0;
		fitness = CreateAndEvaluateTour(M, TS);
		return fitness;
	}

	public static double CreateAndEvaluateTour(final SimulationMap Map, final double TimeStamp,
			final ArrayList<Integer> genes) {
		final Tour T = new Tour("Create From Input", -1, -1, null, null, -1, genes);
		final VoIEvaluation X = new VoIEvaluation();
		T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
				DISTANCE_SCALE, DISTANCE_TYPE));
		this.AllTours.add(T);
		// AllTours.get(i).PrintTour();
		return T.getVoIAccumulatedByTour();
	}

}