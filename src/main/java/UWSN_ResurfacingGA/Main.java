package UWSN_ResurfacingGA;

/**
 * This is the main file that is used by the GAResurface
 * 
 * @author Sehar Butt
 *
 */
public class Main {
    int randomVariable = 0 ;
    public static void main(final String[] args) {
        final GAforResurfacing resurface = new GAforResurfacing();
        for (int i = 0; i < GAforResurfacing.getSamplesPerTour(); i++) {
            resurface.run();
        }
        resurface.analyzeExperiment();
        resurface.printAverages();
    }

}
