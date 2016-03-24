package SimpleGA;

public class FitnessCalc {

    static byte[] solution = new byte[7];

    /* Public methods */
    // Set a candidate solution as a byte array
    public static void setSolution(byte[] newSolution) {
        solution = newSolution;
    }

    // To make it easier we can use this method to set our candidate solution 
    // with string of 0s and 1s
    static void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        // Loop through each character of our string and save it in our byte 
        // array
        for (int i = 0; i < newSolution.length(); i++) {
            String character = newSolution.substring(i, i + 1);
            if (character.contains("0") || character.contains("1")) {
                solution[i] = Byte.parseByte(character);
            } else {
                solution[i] = 0;
            }
        }
    }

    // Calculate inidividuals fittness by comparing it to our candidate solution
    static int getFitness(Individual individual) {
        int fitness = 0;
        fitness = binaryToInteger(individual.getGeneString()); 
        return fitness;
    }
    
    // Get optimum fitness
    static int getMaxFitness() {
        int maxFitness = 0;
        maxFitness = binaryToInteger(solution); 
        return maxFitness;
    }
    
    public static int binaryToInteger(byte[] binary) {
        int result = 0;
        int count = 0;
        for (int i = binary.length-1; i >= 0; i--) {
             if (binary[i] == 1) {
            	 result += (int)Math.pow(2, count);
             }
             count++;
        }
        return result;
    }
    
}