package GA_UWSN.GA2;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;

public class GA2_Population {

	GA2_Individual[] individuals;

	public GA2_Population(final int populationSize, final boolean initialize, final int NumOfNodes,
			final ArrayList<Integer> T) {
		this.individuals = new GA2_Individual[populationSize];
		// Initialize population
		if (initialize) {
			// Loop and create individuals
			for (int i = 0; i < this.size(); i++) {
				final GA2_Individual newIndividual = new GA2_Individual(T);
				this.saveIndividual(i, newIndividual);
			}
		}
	}

	public GA2_Individual getIndividual(final int index) {
		return this.individuals[index];
	}

	public GA2_Individual getFittest(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp) {
		GA2_Individual fittest = this.individuals[0];
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

	public void saveIndividual(final int index, final GA2_Individual indiv) {
		this.individuals[index] = indiv;
	}

}