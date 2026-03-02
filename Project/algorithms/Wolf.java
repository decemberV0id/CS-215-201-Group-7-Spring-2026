public class Wolf {
    private double[] position; // The wolf's position in the search space
    private double fitness;    // The objective function value
    private int ID; //just to ID individuals
    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    public int getID() {
        return ID;
    }
    public Wolf(int dimension, int ID) {
        this.position = new double[dimension];
        this.fitness = Double.MAX_VALUE; // Or Double.MIN_VALUE for maximization
        this.ID = ID;
    }
    public String toString(){


        String outputString1 = "ID: ";
        outputString1 = outputString1 + this.ID + " | ";
        String outputString2 = String.format("Fitness Value: %.2f | Position: (%.2f, %.2f)", this.fitness, this.position[0], this.position[1]);
        String outputString = outputString1 + outputString2;
        return outputString;
    }
}
