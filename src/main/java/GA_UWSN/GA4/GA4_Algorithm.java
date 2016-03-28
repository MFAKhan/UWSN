package GA_UWSN.GA4;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;

public class GA4_Algorithm {

	/* GA parameters */
	private static final double uniformRate = 0.5;
	private static final double mutationRate = 0.015;
	private static final int tournamentSize = 5;
	private static final boolean elitism = true;

	/* Public methods */

	// Evolve a population
	public static GA4_Population evolvePopulation(final GA4_Population pop, final SimulationMap Map, final int NumNodes,
			final int NumAUVs, final double Speed, final String DistanceType, final double DistanceScale,
			final double TimeStamp, final ArrayList<Integer> T) {
		final GA4_Population newPopulation = new GA4_Population(pop.size(), false, NumNodes, T);

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
			final GA4_Individual indiv1 = tournamentSelection(pop, Map, NumNodes, NumAUVs, Speed, DistanceType,
					DistanceScale, TimeStamp, T);
			final GA4_Individual indiv2 = tournamentSelection(pop, Map, NumNodes, NumAUVs, Speed, DistanceType,
					DistanceScale, TimeStamp, T);
			final GA4_Individual newIndiv = crossover(indiv1, indiv2, T);
			newPopulation.saveIndividual(i, newIndiv);
		}

		// Mutate population
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getIndividual(i));
		}

		return newPopulation;
	}

	// Crossover individuals
	private static GA4_Individual crossover(final GA4_Individual indiv1, final GA4_Individual indiv2,
			final ArrayList<Integer> T) {
		final GA4_Individual newSol = new GA4_Individual(T);
		// Loop through genes
		for (int i = 0; i < indiv1.size(); i++) {
			// Crossover
			if (Math.random() <= uniformRate) {
				newSol.setGene(i, indiv1.getGene(i));
			} else {
				newSol.setGene(i, indiv2.getGene(i));
			}
		}
		// System.out.println("X-Over");
		// newSol.printGenes();
		return newSol;
	}

	// Mutate an individual
	private static void mutate(final GA4_Individual indiv) {
		// Loop through genes
		for (int i = 0; i < indiv.size(); i++) {
			if (Math.random() <= mutationRate && (i != indiv.size() - 1)
					&& (indiv.getGene(i) == -2 || indiv.getGene(i) == -3)) {
				// Create random gene
				final byte gene = (byte) Math.round(Math.random());
				if (gene == 0) {
					indiv.setGene(i, -2);
				} else if (gene == 1) {
					indiv.setGene(i, -3);
				}
			}
		}
		// System.out.println("Mutate");
		// indiv.printGenes();
	}

	// Select individuals for crossover
	private static GA4_Individual tournamentSelection(final GA4_Population pop, final SimulationMap Map,
			final int NumNodes, final int NumAUVs, final double Speed, final String DistanceType,
			final double DistanceScale, final double TimeStamp, final ArrayList<Integer> T) {
		// Create a tournament population
		final GA4_Population tournament = new GA4_Population(tournamentSize, false, NumNodes, T);
		// For each place in the tournament get a random individual
		for (int i = 0; i < tournamentSize; i++) {
			final int randomId = (int) (Math.random() * pop.size());
			tournament.saveIndividual(i, pop.getIndividual(randomId));
		}
		// Get the fittest
		final GA4_Individual fittest = tournament.getFittest(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale,
				TimeStamp);
		return fittest;
	}
}