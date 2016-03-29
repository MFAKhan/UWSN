package GA_UWSN.GA3;

import java.util.ArrayList;
import java.util.Collections;

import UWSN_Simulator_1.SimulationMap;

public class GA3_Seeds {

	public GA3_Seeds() {

	}

	public GA3_Individual[] BestSeeds(final ArrayList<Integer> T, final int NumOfSeeds, final SimulationMap Map,
			final int NumNodes, final int NumAUVs, final double Speed, final String DistanceType,
			final double DistanceScale, final double TimeStamp) {
		final GA3_Individual[] Seeds = new GA3_Individual[NumOfSeeds];
		final ArrayList<GA3_Individual> AllCombinations = new ArrayList<GA3_Individual>();

		for (int i = 0; i < T.size(); i++) {
			final GA3_Individual ToAdd = new GA3_Individual(T);
			final int NumResurfaceStops = i;
			// System.out.println(T.size());
			// System.out.println("Individual " + i);
			// ToAdd.printGenes();
			// Set all transmit visits to none
			for (int j = 1; j < ToAdd.size() - 1; j += 2) {
				ToAdd.setGene(j, -3);
			}
			// ToAdd.printGenes();
			// Set required transmit visits
			if (i != 0) {
				for (int j = 2 * NumResurfaceStops - 1; j < ToAdd.size(); j += 2 * NumResurfaceStops) {
					ToAdd.setGene(j, -2);
				}
			}
			// ToAdd.printGenes();
			ToAdd.getFitness(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp);
			// System.out.println(ToAdd.getFitnessValue());
			AllCombinations.add(ToAdd);
		}
		// Have set fitness before sorting
		Collections.sort(AllCombinations, new GA3_Individual_Comparator());
		for (int i = 0; i < Seeds.length; i++) {
			if (i < AllCombinations.size()) {
				Seeds[i] = AllCombinations.get(i);
			}
		}
		return Seeds;
	}

}