package SimpleGA;

public class Population {

	Individual[] individuals;

	/*
	 * Constructors
	 */
	// Create a population
	public Population(final int populationSize, final boolean initialize) {
		this.individuals = new Individual[populationSize];
		// Initialize population
		if (initialize) {
			// Loop and create individuals
			for (int i = 0; i < this.size(); i++) {
				final Individual newIndividual = new Individual();
				newIndividual.generateIndividual();
				this.saveIndividual(i, newIndividual);
			}
		}
	}

	/* Getters */
	public Individual getIndividual(final int index) {
		return this.individuals[index];
	}

	public Individual getFittest() {
		Individual fittest = this.individuals[0];
		// Loop through individuals to find fittest
		for (int i = 0; i < this.size(); i++) {
			if (fittest.getFitness() <= this.getIndividual(i).getFitness()) {
				fittest = this.getIndividual(i);
			}
		}
		return fittest;
	}

	/* Public methods */
	// Get population size
	public int size() {
		return this.individuals.length;
	}

	// Save individual
	public void saveIndividual(final int index, final Individual indiv) {
		this.individuals[index] = indiv;
	}

	public void printPopulation() {
		for (int i = 0; i < this.individuals.length; i++) {
			System.out.print(this.individuals[i].getGenesConverted());
			System.out.print(" - ");
			System.out.print(this.individuals[i].getFitness());
			System.out.print("\n");
		}
		System.out.println();
	}

}