package GA_UWSN;

import UWSN_Simulator_1.SimulationMap;

public class GA1_Algorithm {

	/* GA parameters */
	private static final double uniformRate = 0.5;
	private static final double mutationRate = 0.015;
	private static final int tournamentSize = 5;
	private static final boolean elitism = true;

	/* Public methods */

	// Evolve a population
	public static GA1_Population evolvePopulation(final GA1_Population pop, final SimulationMap Map, final int NumNodes,
			final int NumAUVs, final double Speed, final String DistanceType, final double DistanceScale,
			final double TimeStamp) {
		final GA1_Population newPopulation = new GA1_Population(pop.size(), false, NumNodes);

		// Keep our best individual
		if (elitism) {
			newPopulation.saveIndividual(0,
					pop.getFittest(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp));
		}

		// Crossover population
		int elitismOffset;
		if (elitism) {
			elitismOffset = 1;
		} else {
			elitismOffset = 0;
		}
		// Loop over the population size and create new individuals with
		// crossover
		for (int i = elitismOffset; i < pop.size(); i++) {
			final GA1_Individual indiv1 = tournamentSelection(pop, Map, NumNodes, NumAUVs, Speed, DistanceType,
					DistanceScale, TimeStamp);
			final GA1_Individual indiv2 = tournamentSelection(pop, Map, NumNodes, NumAUVs, Speed, DistanceType,
					DistanceScale, TimeStamp);
			final GA1_Individual newIndiv = crossover(indiv1, indiv2);
			newPopulation.saveIndividual(i, newIndiv);
		}

		// Mutate population
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getIndividual(i));
		}

		return newPopulation;
	}

	// Crossover individuals
	private static GA1_Individual crossover(final GA1_Individual indiv1, final GA1_Individual indiv2) {
		final GA1_Individual newSol = new GA1_Individual();
		newSol.generateIndividual(indiv1.genes.length);
		// Loop through genes
		for (int i = 0; i < indiv1.size(); i++) {
			// Crossover
			if (Math.random() <= uniformRate) {
				newSol.setGene(i, indiv1.getGene(i));
			} else {
				newSol.setGene(i, indiv2.getGene(i));
			}
		}
		return newSol;
	}

	// Mutate an individual
	private static void mutate(final GA1_Individual indiv) {
		// Loop through genes
		for (int i = 0; i < indiv.size(); i++) {
			if (Math.random() <= mutationRate) {
				// Create random gene
				final byte gene = (byte) Math.round(Math.random());
				indiv.setGene(i, gene);
			}
		}
	}

	// Select individuals for crossover
	private static GA1_Individual tournamentSelection(final GA1_Population pop, final SimulationMap Map,
			final int NumNodes, final int NumAUVs, final double Speed, final String DistanceType,
			final double DistanceScale, final double TimeStamp) {
		// Create a tournament population
		final GA1_Population tournament = new GA1_Population(tournamentSize, false, NumNodes);
		// For each place in the tournament get a random individual
		for (int i = 0; i < tournamentSize; i++) {
			final int randomId = (int) (Math.random() * pop.size());
			tournament.saveIndividual(i, pop.getIndividual(randomId));
		}
		// Get the fittest
		final GA1_Individual fittest = tournament.getFittest(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale,
				TimeStamp);
		return fittest;
	}
}