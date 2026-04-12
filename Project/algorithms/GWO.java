package algorithms;

import graph.*;
import java.util.*;

public class GWO {

    private final Graph graph;
    private final int numWolves;
    private final int maxIterations;
    private final int dimension;
    private final Random rand = new Random();

    private final List<Node> allNodes;
    private final Map<Node, Integer> nodeIndexMap;

    private Wolf[] wolfPack;
    private Wolf alphaWolf, betaWolf, deltaWolf;

    private final List<Double> convergenceHistory = new ArrayList<>();

    public GWO(Graph graph, int numWolves, int maxIterations) {
        this.graph = graph;
        this.numWolves = numWolves;
        this.maxIterations = maxIterations;

        this.allNodes = new ArrayList<>(graph.getNodes());
        this.allNodes.sort(Comparator.comparing(Node::getId)); // stable order

        this.dimension = allNodes.size();
        this.nodeIndexMap = new HashMap<>();

        for (int i = 0; i < allNodes.size(); i++) {
            nodeIndexMap.put(allNodes.get(i), i);
        }
    }

    public static class RouteResult {
        public final List<Node> path;
        public final double cost;
        public final double fitness;
        public final boolean reachedDestination;

        public RouteResult(List<Node> path, double cost, double fitness, boolean reachedDestination) {
            this.path = new ArrayList<>(path);
            this.cost = cost;
            this.fitness = fitness;
            this.reachedDestination = reachedDestination;
        }

        @Override
        public String toString() {
            return "path=" + path +
                   " | cost=" + String.format("%.2f", cost) +
                   " | fitness=" + String.format("%.2f", fitness) +
                   " | reachedDestination=" + reachedDestination;
        }
    }

    private static class DecodedPath {
        List<Node> path;
        double cost;
        boolean reachedDestination;

        DecodedPath(List<Node> path, double cost, boolean reachedDestination) {
            this.path = path;
            this.cost = cost;
            this.reachedDestination = reachedDestination;
        }
    }

    public List<Double> getConvergenceHistory() {
        return convergenceHistory;
    }

    public RouteResult optimize(Node source, Node destination) {
        initializePopulation(source, destination);
        convergenceHistory.clear();
        convergenceHistory.add(alphaWolf.getFitness());

        for (int iter = 0; iter < maxIterations; iter++) {
            double a = 2.0 * (1.0 - (double) iter / maxIterations);

            for (int i = 0; i < numWolves; i++) {
                updateWolfPosition(wolfPack[i], a);
            }

            for (Wolf wolf : wolfPack) {
                double fitness = calculateFitness(wolf.getPosition(), source, destination);
                wolf.setFitness(fitness);
            }

            sortWolvesByFitness();
            convergenceHistory.add(alphaWolf.getFitness());
        }

        DecodedPath bestPath = decodePath(alphaWolf.getPosition(), source, destination);
        return new RouteResult(
                bestPath.path,
                bestPath.cost,
                alphaWolf.getFitness(),
                bestPath.reachedDestination
        );
    }

    private void initializePopulation(Node source, Node destination) {
        wolfPack = new Wolf[numWolves];

        for (int i = 0; i < numWolves; i++) {
            wolfPack[i] = new Wolf(dimension, i);

            double[] newPosition = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                newPosition[j] = rand.nextDouble(); // 0 to 1
            }

            wolfPack[i].setPosition(newPosition);
            double fitness = calculateFitness(newPosition, source, destination);
            wolfPack[i].setFitness(fitness);
        }

        sortWolvesByFitness();
    }

    private void sortWolvesByFitness() {
        Arrays.sort(wolfPack, Comparator.comparingDouble(Wolf::getFitness));
        alphaWolf = wolfPack[0];
        betaWolf = wolfPack[1];
        deltaWolf = wolfPack[2];
    }

    private double calculateFitness(double[] position, Node source, Node destination) {
        DecodedPath decoded = decodePath(position, source, destination);

        if (!decoded.reachedDestination) {
            return 1_000_000 + decoded.cost;
        }

        return decoded.cost;
    }

    private DecodedPath decodePath(double[] position, Node source, Node destination) {
        List<Node> path = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        Node current = source;
        path.add(current);
        visited.add(current);

        double totalCost = 0.0;
        int maxSteps = allNodes.size();

        for (int step = 0; step < maxSteps; step++) {
            if (current.equals(destination)) {
                return new DecodedPath(path, totalCost, true);
            }

            List<Edge> edges = graph.getEdges(current);
            if (edges.isEmpty()) {
                return new DecodedPath(path, totalCost + 1000, false);
            }

            Edge bestEdge = null;
            double bestScore = Double.POSITIVE_INFINITY;

            for (Edge edge : edges) {
                Node next = edge.getTo();
                int idx = nodeIndexMap.get(next);

                double nodePreference = position[idx];
                double revisitPenalty = visited.contains(next) ? 1000.0 : 0.0;

                // lower score is better
                double score = edge.getWeight() + (5.0 * nodePreference) + revisitPenalty;

                if (score < bestScore) {
                    bestScore = score;
                    bestEdge = edge;
                }
            }

            if (bestEdge == null) {
                return new DecodedPath(path, totalCost + 1000, false);
            }

            current = bestEdge.getTo();
            totalCost += bestEdge.getWeight();
            path.add(current);

            if (visited.contains(current)) {
                totalCost += 100.0; // extra loop penalty
            }

            visited.add(current);
        }

        return new DecodedPath(path, totalCost + 1000, false);
    }

    private void updateWolfPosition(Wolf wolf, double a) {
        double[] newPosition = new double[dimension];

        for (int j = 0; j < dimension; j++) {
            double x1 = calculateMove(wolf.getPosition()[j], alphaWolf.getPosition()[j], a);
            double x2 = calculateMove(wolf.getPosition()[j], betaWolf.getPosition()[j], a);
            double x3 = calculateMove(wolf.getPosition()[j], deltaWolf.getPosition()[j], a);

            newPosition[j] = (x1 + x2 + x3) / 3.0;

            // keep values between 0 and 1
            if (newPosition[j] < 0.0) newPosition[j] = 0.0;
            if (newPosition[j] > 1.0) newPosition[j] = 1.0;
        }

        wolf.setPosition(newPosition);
    }

    private double calculateMove(double currentPos, double leaderPos, double a) {
        double r1 = rand.nextDouble();
        double r2 = rand.nextDouble();

        double A = 2.0 * a * r1 - a;
        double C = 2.0 * r2;

        double D = Math.abs(C * leaderPos - currentPos);
        return leaderPos - A * D;
    }
}
