package SimpleGA;

public class GA {

    public static void main(String[] args) {

        // Set a candidate solution
        FitnessCalc.setSolution("0000000");

        // Create an initial population
        Population myPop = new Population(10, true);
        
        // Evolve our population until we reach an optimum solution
        int generationCount = 0;
        while(generationCount < 25) {
            generationCount++;
            System.out.println("Generation: " + generationCount + " Fittest: " + myPop.getFittest().getFitness());
            myPop = Algorithm.evolvePopulation(myPop);
        }
        System.out.println("Solution found!");
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myPop.getFittest());
        
    }
}