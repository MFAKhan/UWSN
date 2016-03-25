package UWSN_ResurfacingGA;

import SimpleGA.Algorithm;
import SimpleGA.FitnessCalc;
import SimpleGA.Population;

public class GAforResurfacing {

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(final String[] args) {

		// Set a candidate solution
		FitnessCalc.setSolution(0);

		// Create an initial population
		Population myPop = new Population(10, true);

		// Evolve our population until we reach an optimum solution
		int generationCount = 0;
		while (generationCount < 25) {
			generationCount++;
			//myPop.printPopulation();
			System.out.println("Generation: " + generationCount + " Gene: " + myPop.getFittest().getGenesConverted()
					+ " Fittest: " + myPop.getFittest().getFitness());
			myPop = Algorithm.evolvePopulation(myPop);
		}
		System.out.println("Solution found!");
		System.out.println("Generation: " + generationCount);
		System.out.println("Genes:");
		//System.out.println(myPop.getFittest() + " = " + myPop.getFittest().getGenesConverted());

	}

}