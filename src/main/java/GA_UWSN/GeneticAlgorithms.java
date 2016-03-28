package GA_UWSN;

import UWSN_Simulator_1.SimulationMap;

public class GeneticAlgorithms {

	public int OptimizeRurfacingUsingGA1(final SimulationMap Map, final int NumNodes, final int NumAUVs,
			final double Speed, final String DistanceType, final double DistanceScale, final double TimeStamp) {
		final GA1 ga1 = new GA1(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp);
		return ga1.getBest();
	}

	// private void OptimizeRurfacingUsingGA2(final
	// ArrayList<ArrayList<Integer>> Tour) {
	// TODO Auto-generated method stub

	// }

}
