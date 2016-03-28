package GA_UWSN;

import java.util.ArrayList;

public class GA2_Individual {

	private final ArrayList<Integer> genes = new ArrayList<Integer>();
	private double fitness = 0;

	public GA2_Individual(final ArrayList<Integer> T) {
		for (int i = 0; i < T.size(); i++) {
			this.genes.add(T.get(i));
			if (Math.random() < 0.5) {
				this.genes.add(-2);
			} else {
				this.genes.add(-3);
			}
		}
		this.setFitness();
	}

	public ArrayList<Integer> getGenes() {
		return this.genes;
	}

	public String getGeneString() {
		String geneString = "";
		for (int i = 0; i < this.genes.size(); i++) {
			geneString += Integer.toString(this.getGene(i));
		}
		return geneString;
	}

	public void setFitness() {
		final GA2_Fitness value = new GA2_Fitness();
		this.fitness = value.getFitness(this.genes);
	}

	public double getFitness() {
		return this.fitness;
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

}