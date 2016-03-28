package GA_UWSN.GA2;

import java.util.ArrayList;

import UWSN_Simulator_1.SimulationMap;

public class GA2_Individual {

	private final ArrayList<Integer> genes = new ArrayList<Integer>();
	private double fitness = 0;

	public GA2_Individual(final ArrayList<Integer> T) {

		// Add good seeds

		for (int i = 0; i < T.size(); i++) {
			this.genes.add(T.get(i));
			if ((Math.random() < 0.5) || (i == (T.size() - 1))) {
				// -2 reserved for visiting hotspot
				this.genes.add(-2);
			} else {
				// -3 reserved for not visiting hotspot
				this.genes.add(-3);
			}
		}
		// this.printGenes();
	}

	public ArrayList<Integer> getGenes() {
		return this.genes;
	}

	public void setGene(final int index, final int value) {
		this.genes.set(index, value);
	}

	public int getGene(final int index) {
		return this.genes.get(index);
	}

	/* Public methods */
	public int size() {
		return this.genes.size();
	}

	public double getFitness(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp) {
		if (this.fitness == -1) {
			this.fitness = GA2_Fitness.getFitness(this.genes, Map, NumNodes, NumAUVs, Speed, DistanceType,
					DistanceScale, TimeStamp);
		}
		return this.fitness;
	}

	public void printGenes() {
		for (int i = 0; i < this.genes.size(); i++) {
			System.out.print(this.genes.get(i) + " ");
		}
		System.out.println();
	}
}