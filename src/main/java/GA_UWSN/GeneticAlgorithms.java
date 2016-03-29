package GA_UWSN;

import java.util.ArrayList;

import GA_UWSN.GA1.GA1;
import GA_UWSN.GA2.GA2;
import GA_UWSN.GA3.GA3;
import UWSN_Simulator_1.SimulationMap;

public class GeneticAlgorithms {

	public int OptimizeRurfacingUsingGA1(final SimulationMap Map, final int NumNodes, final int NumAUVs,
			final double Speed, final String DistanceType, final double DistanceScale, final double TimeStamp,
			final boolean Reversal) {
		final GA1 ga1 = new GA1(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp, Reversal);
		return ga1.getBest();
	}

	public ArrayList<Integer> OptimizeRurfacingUsingGA2(final SimulationMap Map, final int NumNodes, final int NumAUVs,
			final double Speed, final String DistanceType, final double DistanceScale, final double TimeStamp,
			final ArrayList<Integer> T) {
		final GA2 ga2 = new GA2(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp, T);
		return ga2.getBest();
	}

	public ArrayList<Integer> OptimizeRurfacingUsingGA3(final SimulationMap Map, final int NumNodes, final int NumAUVs,
			final double Speed, final String DistanceType, final double DistanceScale, final double TimeStamp,
			final ArrayList<Integer> T) {
		final GA3 ga3 = new GA3(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp, T);
		return ga3.getBest();
	}

}
