package UWSN_ResurfacingGA;

public class Main {

	public static void main(final String[] args) {
		final GAforResurfacing resurface = new GAforResurfacing();
		for (int i = 0; i < resurface.getSamplesPerTour(); i++) {
			resurface.run();
		}
		resurface.analyzeExperiment();
		resurface.printAverages();
	}

}
