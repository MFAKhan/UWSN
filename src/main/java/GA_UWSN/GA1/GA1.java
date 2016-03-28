package GA_UWSN.GA1;

import UWSN_Simulator_1.SimulationMap;

public class GA1 {

	public GA1_Individual Best;
	public double fitness;

	public GA1(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp) {
		GA1_Fitness.setSolution(0, NumNodes);
		GA1_Population myPop = new GA1_Population(30, true, NumNodes);
		int generationCount = 0;
		while (generationCount < 50) {
			generationCount++;
			myPop = GA1_Algorithm.evolvePopulation(myPop, Map, NumNodes, NumAUVs, Speed, DistanceType, DistanceScale,
					TimeStamp);
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

	public int getBest() {
		int result = 0;
		int count = 0;
		for (int i = this.Best.genes.length - 1; i >= 0; i--) {
			if (this.Best.genes[i] == 1) {
				result += (int) Math.pow(2, count);
			}
			count++;
		}
		return result;
	}

}
