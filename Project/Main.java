import algorithms.YenKShortestPaths;
import algorithms.GWO;
import graph.Wolf;
import graph.Graph;
import graph.Node;
import java.util.List;
import traffic.TrafficSimulator;

public class Main {

    public static void main(String[] args) {

        // ===== Generate Controlled Test Graph =====
        // 20 nodes, 40 extra random edges
        Graph graph = GraphGenerator.generateGraph(20, 40);

        // Pick start and end nodes
        Node source = graph.getNodes().get(0);
        Node destination = graph.getNodes().get(19);

        // ===== Run Yen Before Traffic =====
        YenKShortestPaths yen = new YenKShortestPaths(graph);

        System.out.println("===== BEFORE TRAFFIC =====");
        List<YenKShortestPaths.Path> beforePaths = yen.getKShortestPaths(source, destination, 3);

        for (YenKShortestPaths.Path p : beforePaths) {
            System.out.println(p.nodes + " | cost=" + String.format("%.2f", p.cost));
        }

        // ===== Apply Random Traffic =====
        TrafficSimulator traffic = new TrafficSimulator();
        traffic.applyRandomTraffic(graph);

        // ===== Run Yen After Traffic =====
        System.out.println("\n===== AFTER TRAFFIC =====");
        List<YenKShortestPaths.Path> afterPaths = yen.getKShortestPaths(source, destination, 3);

        for (YenKShortestPaths.Path p : afterPaths) {
            System.out.println(p.nodes + " | cost=" + String.format("%.2f", p.cost));
        }
        //Starting Gray Wolf Optimization
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
