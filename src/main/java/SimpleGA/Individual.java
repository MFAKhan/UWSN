package SimpleGA;

public class Individual {

	static int defaultGeneLength = 7;
	private final byte[] genes = new byte[defaultGeneLength];
	// Cache
	private double fitness = 0;
	private int genesConverted = 0;

	// Create a random individual
	public void generateIndividual() {
		for (int i = 0; i < this.size(); i++) {
			final byte gene = (byte) Math.round(Math.random());
			this.genes[i] = gene;
		}
	}

	/* Getters and setters */
	// Use this if you want to create individuals with different gene lengths
	public static void setDefaultGeneLength(final int length) {
		defaultGeneLength = length;
	}

	public byte getGene(final int index) {
		return this.genes[index];
	}

	public byte[] getGeneString() {
		return this.genes;
	}

	public int getGenesConverted() {
		this.genesConverted = FitnessCalc.binaryToInteger(this.genes);
		return this.genesConverted;
	}

	public void setGene(final int index, final byte value) {
		this.genes[index] = value;
		this.fitness = 0;
	}

	/* Public methods */
	public int size() {
		return this.genes.length;
	}

	public double getFitness() {
		if (this.fitness == 0) {
			this.fitness = FitnessCalc.getFitness(this);
		}
		return this.fitness;
	}

	@Override
	public String toString() {
		String geneString = "";
		for (int i = 0; i < this.size(); i++) {
			geneString += this.getGene(i);
		}
		return geneString;
	}
}