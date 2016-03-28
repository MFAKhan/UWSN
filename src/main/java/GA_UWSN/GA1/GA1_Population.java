package GA_UWSN.GA1;

import UWSN_Simulator_1.SimulationMap;

public class GA1_Population {

	GA1_Individual[] individuals;

	public GA1_Population(final int populationSize, final boolean initialize, final int NumOfNodes) {
		final int genesLength = ((int) (Math.log(NumOfNodes) / Math.log(2)) + 1);
		this.individuals = new GA1_Individual[populationSize];
		// Initialize population
		if (initialize) {
			// Loop and create individuals
			for (int i = 0; i < this.size(); i++) {
				final GA1_Individual newIndividual = new GA1_Individual();
				newIndividual.generateIndividual(genesLength);
				this.saveIndividual(i, newIndividual);
			}
		}
	}

	public GA1_Individual getIndividual(final int index) {
		return this.individuals[index];
	}

	public GA1_Individual getFittest(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp) {
		GA1_Individual fittest = this.individuals[0];
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

	public void saveIndividual(final int index, final GA1_Individual indiv) {
		this.individuals[index] = indiv;
	}

}