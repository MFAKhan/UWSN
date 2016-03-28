package GA_UWSN.GA3;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;

public class GA3_Population {

	GA3_Individual[] individuals;

	public GA3_Population(final int populationSize, final boolean initialize, final int NumOfNodes,
			final ArrayList<Integer> T) {
		this.individuals = new GA3_Individual[populationSize];
		// Initialize population
		if (initialize) {
			// Loop and create individuals
			for (int i = 0; i < this.size(); i++) {
				final GA3_Individual newIndividual = new GA3_Individual(T);
				this.saveIndividual(i, newIndividual);
			}
		}
	}

	public GA3_Individual getIndividual(final int index) {
		return this.individuals[index];
	}

	public GA3_Individual getFittest(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp) {
		GA3_Individual fittest = this.individuals[0];
		// Loop through individuals to find fittest
		for (int i = 0; i < this.size(); i++) {
			final double fittestFitness = fittest.getFitness(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale,
					TimeStamp);
			final double toCompFitness = this.getIndividual(i).getFitness(Map, NumNodes, NumAUVs, Speed, DistanceType,
					DistanceScale, TimeStamp);
			if (fittestFitness <= toCompFitness) {
				fittest = this.getIndividual(i);
			}
		}
		return fittest;
	}

	public int size() {
		return this.individuals.length;
	}

	public void saveIndividual(final int index, final GA3_Individual indiv) {
		this.individuals[index] = indiv;
	}

}