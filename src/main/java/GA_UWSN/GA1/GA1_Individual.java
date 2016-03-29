package GA_UWSN.GA1;

import UWSN_Simulator_1.SimulationMap;

public class GA1_Individual {

	public byte[] genes = {};
	// Cache
	private double fitness = -1;

	// Create a random individual
	public void generateIndividual(final int GeneLength) {
		this.genes = new byte[GeneLength];
		for (int i = 0; i < this.size(); i++) {
			final byte gene = (byte) Math.round(Math.random());
			this.genes[i] = gene;
		}
	}

	/* Getters and setters */
	// Use this if you want to create individuals with different gene lengths

	public byte getGene(final int index) {
		return this.genes[index];
	}

	public void setGene(final int index, final byte value) {
		this.genes[index] = value;
		this.fitness = 0;
	}

	public int size() {
		return this.genes.length;
	}

	public double getFitness(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp, final boolean Reversal) {
		if (this.fitness == -1) {
			this.fitness = GA1_Fitness.getFitness(this.genes, Map, NumNodes, NumAUVs, Speed, DistanceType,
					DistanceScale, TimeStamp, Reversal);
		}
		return this.fitness;
	}

}