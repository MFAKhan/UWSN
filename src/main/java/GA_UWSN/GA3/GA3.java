package GA_UWSN.GA3;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;

public class GA3 {

	public GA3_Individual Best;
	public double fitness;

	public GA3(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp, final ArrayList<Integer> T) {
		GA3_Population myPop = new GA3_Population(100, true, NumNodes, T);
		int generationCount = 0;
		while (generationCount < 100) {
			generationCount++;
			myPop = GA3_Algorithm.evolvePopulation(myPop, Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale,
					TimeStamp, T);
		}
		this.Best = myPop.getFittest(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp);
		this.fitness = this.Best.getFitness(Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale, TimeStamp);

		// System.out.println(myPop.getFittest() + " = " +
		// myPop.getFittest().getFitness());
		/*
		 * // Set a candidate solution GA1_Fitness.setSolution(0);
		 *
		 * // Create an initial population GA1_Population myPop = new
		 * GA1_Population(10, true);
		 *
		 * // Evolve our population until we reach an optimum solution int
		 * generationCount = 0; while (generationCount < 50) {
		 * generationCount++; // myPop.printPopulation(); System.out.println(
		 * "Generation: " + generationCount + " Gene: " +
		 * myPop.getFittest().getGenesConverted() + " Fittest: " +
		 * myPop.getFittest().getFitness()); myPop =
		 * GA1_Algorithm.evolvePopulation(myPop); } System.out.println(
		 * "Solution found!"); System.out.println("Generation: " +
		 * generationCount); System.out.println("Genes:"); //
		 * System.out.println(myPop.getFittest() + " = " + //
		 * myPop.getFittest().getGenesConverted());
		 */
	}

	public ArrayList<Integer> getBest() {
		final ArrayList<Integer> TourToReturn = new ArrayList<Integer>();
		for (int i = 0; i < this.Best.getGenes().size(); i++) {
			if (this.Best.getGene(i) != -3) {
				TourToReturn.add(this.Best.getGene(i));
			}
		}
		return this.Best.getGenes();
	}

}
