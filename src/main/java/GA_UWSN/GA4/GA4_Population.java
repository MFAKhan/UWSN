package GA_UWSN.GA4;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;

public class GA4_Population {

	GA4_Individual[] individuals;

	public GA4_Population(final int populationSize, final boolean initialize, final int NumOfNodes,
			final ArrayList<Integer> T) {
		this.individuals = new GA4_Individual[populationSize];
		// Initialize population
		if (initialize) {
			// Loop and create individuals
			for (int i = 0; i < this.size(); i++) {
				final GA4_Individual newIndividual = new GA4_Individual(T);
				this.saveIndividual(i, newIndividual);
			}
		}
	}

	public GA4_Individual getIndividual(final int index) {
		return this.individuals[index];
	}

	public GA4_Individual getFittest(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp) {
		GA4_Individual fittest = this.individuals[0];
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

	public void saveIndividual(final int index, final GA4_Individual indiv) {
		this.individuals[index] = indiv;
	}

}