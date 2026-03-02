import java.util.Random;
import java.util.Arrays;

public class GWO {
    private int numWolves;
    private int maxIterations;
    private int dimension;
    private double[] searchSpaceLowerBound;
    private double[] searchSpaceUpperBound;
    private Random rand = new Random();

    private Wolf[] wolfPack;
    private Wolf alphaWolf, betaWolf, deltaWolf;

    public GWO(int numWolves, int maxIterations, int dimension, double[] searchSpaceLowerBound, double[] searchSpaceUpperBound){
        this.numWolves = numWolves;
        this.maxIterations = maxIterations;
        this.dimension = dimension;
        this.searchSpaceLowerBound = searchSpaceLowerBound;
        this.searchSpaceUpperBound = searchSpaceUpperBound;

    }
    public void initializePopulation() {
        wolfPack = new Wolf[numWolves];
        for (int i = 0; i < numWolves; i++) {
            wolfPack[i] = new Wolf(2, i);
            //wolfPack[i].setPosition(searchSpaceLowerBound);position = new double[dimension];
            double[] newPosition = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                newPosition[j] = searchSpaceLowerBound[j] + rand.nextDouble() * 
                                    (searchSpaceUpperBound[j] - searchSpaceLowerBound[j]);; 
                
            }
            wolfPack[i].setPosition(newPosition);
            double newFitness = calculateFitness(wolfPack[i].getPosition()); // Your objective function
            wolfPack[i].setFitness(newFitness);
        }
        sortWolvesByFitness();
    }

    private void sortWolvesByFitness() {
        Arrays.sort(wolfPack, (w1, w2) -> Double.compare(w1.getFitness(), w2.getFitness()));
        alphaWolf = wolfPack[0];
        betaWolf = wolfPack[1];
        deltaWolf = wolfPack[2];
    }
        // Example: Minimizing the simple function f(x) = x^2 (1D example)
    private double calculateFitness(double[] position) {
        // For an n-dimensional problem, calculate the function value
        double fitness = 0;
        for(double val : position) {
            fitness += Math.pow(val, 2);
        }
        return fitness;
    }
    public Wolf optimize() {
        for (int iter = 0; iter < maxIterations; iter++) {
            double a = 2.0 * (1.0 - (double) iter / maxIterations); // Decreases from 2 to 0

            for (int i = 0; i < numWolves; i++) {
                // Calculate new positions for each wolf (including alpha, beta, delta themselves)
                updateWolfPosition(wolfPack[i], a);
            }
        
            // Recalculate fitness for all wolves and sort to find the new alpha, beta, delta
            for (Wolf wolf : wolfPack) {
                double newFitness = calculateFitness(wolf.getPosition());
                wolf.setFitness(newFitness);
            }
            sortWolvesByFitness();
        }
        return alphaWolf; // The best solution found
    }
    private void updateWolfPosition(Wolf wolf, double a) {
        double[] newPosition = new double[dimension];
        for (int j = 0; j < dimension; j++) {
            // Calculate the three potential movements (X1, X2, X3)
            double X1 = calculateMove(wolf.getPosition()[j], alphaWolf.getPosition()[j], a);
            double X2 = calculateMove(wolf.getPosition()[j], betaWolf.getPosition()[j], a);
            double X3 = calculateMove(wolf.getPosition()[j], deltaWolf.getPosition()[j], a);
        
            // Average the positions to find the final new position
            newPosition[j] = (X1 + X2 + X3) / 3.0;
        
            // Apply bounds checking (optional, but recommended)
            newPosition[j] = Math.max(newPosition[j], searchSpaceLowerBound[j]);
            newPosition[j] = Math.min(newPosition[j], searchSpaceUpperBound[j]);
        }
        wolf.setPosition(newPosition);
    }

    private double calculateMove(double currentPos, double leaderPos, double a) {
        double r1 = rand.nextDouble(); // random number [0, 1]
        double r2 = rand.nextDouble(); // random number [0, 1]
        double A = 2.0 * a * r1 - a;   // Coefficient vector A
        double C = 2.0 * r2;           // Coefficient vector C
    
        double D = Math.abs(C * leaderPos - currentPos); // Distance to leader
        double X = leaderPos - A * D;  // New potential position
        return X;
    }
    public static void main(String[] args) {
        long setupTime = System.currentTimeMillis();
        //these are the arguments for initializing the object, as well as the initialization
        int numWolves = 10;
        int maxIterations = 10;
        int testDimension = 2;
        double[] searchSpaceLowerBound = {3.8841,5.1098,12.9015,19.5538,28.9903,31.7764,40.1127};
        double[] searchSpaceUpperBound = {45.8239,56.6710,67.4320,74.1299,81.2345,88.3471,92.0012};
        GWO tester = new GWO(numWolves, maxIterations, testDimension, searchSpaceLowerBound, searchSpaceUpperBound);
        long initTime = System.currentTimeMillis();
        tester.initializePopulation();
        long optimizeTimePre = System.currentTimeMillis();
        Wolf alpha = tester.optimize();
        long optimizeTimePost = System.currentTimeMillis();
        System.out.println("Fittest solution: " + alpha);

        //time calculation
        long elapsedSetupTime = initTime - setupTime;
        long elapsedPopulationTime = optimizeTimePre - initTime;
        long optimizationTime = optimizeTimePost - optimizeTimePre;
        System.out.printf("Setup time: %d ms | Population initialization time: %d ms | Optimization time: %d ms", elapsedSetupTime, elapsedPopulationTime, optimizationTime);
    }


}
