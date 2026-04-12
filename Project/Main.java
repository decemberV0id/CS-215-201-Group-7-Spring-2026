import algorithms.GWO;
import algorithms.YenKShortestPaths;
import graph.Graph;
import graph.Node;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import traffic.TrafficSimulator;

public class Main {

    public static void main(String[] args) {
        runTestCase("SMALL", 10, 15, 10, 25);
        runTestCase("MEDIUM", 25, 50, 15, 40);
        runTestCase("LARGE", 50, 120, 20, 60);
    }

    public static void runTestCase(String label, int numNodes, int extraEdges,
                                   int numWolves, int maxIterations) {

        System.out.println("\n==================================================");
        System.out.println("TEST CASE: " + label);
        System.out.println("Nodes: " + numNodes + " | Extra Edges: " + extraEdges);
        System.out.println("Wolves: " + numWolves + " | Iterations: " + maxIterations);
        System.out.println("==================================================");

        Graph graph = GraphGenerator.generateGraph(numNodes, extraEdges);

        List<Node> nodes = new ArrayList<>(graph.getNodes());
        nodes.sort(Comparator.comparingInt(n -> Integer.parseInt(n.getId().substring(1))));

        Node source = nodes.get(0);
        Node destination = nodes.get(nodes.size() - 1);

        System.out.println("Source: " + source);
        System.out.println("Destination: " + destination);

        YenKShortestPaths yen = new YenKShortestPaths(graph);

        forceGC();
        double yenBeforeMemoryStart = getUsedMemoryMB();
        long yenBeforeStart = System.nanoTime();
        List<YenKShortestPaths.Path> beforePaths = yen.getKShortestPaths(source, destination, 3);
        long yenBeforeEnd = System.nanoTime();
        double yenBeforeMemoryEnd = getUsedMemoryMB();
        double yenBeforeMemoryUsed = Math.max(0, yenBeforeMemoryEnd - yenBeforeMemoryStart);

        System.out.println("\n===== YEN BEFORE TRAFFIC =====");
        if (beforePaths.isEmpty()) {
            System.out.println("No path found.");
        } else {
            for (int i = 0; i < beforePaths.size(); i++) {
                YenKShortestPaths.Path p = beforePaths.get(i);
                System.out.println("Path " + (i + 1) + ": " + p.nodes +
                        " | cost=" + String.format("%.2f", p.cost));
            }
        }
        System.out.printf("Yen runtime before traffic: %.3f ms%n",
                (yenBeforeEnd - yenBeforeStart) / 1_000_000.0);
        System.out.printf("Yen memory before traffic: %.3f MB%n", yenBeforeMemoryUsed);

        GWO gwoBeforeSolver = new GWO(graph, numWolves, maxIterations);

        forceGC();
        double gwoBeforeMemoryStart = getUsedMemoryMB();
        long gwoBeforeStart = System.nanoTime();
        GWO.RouteResult gwoBefore = gwoBeforeSolver.optimize(source, destination);
        long gwoBeforeEnd = System.nanoTime();
        double gwoBeforeMemoryEnd = getUsedMemoryMB();
        double gwoBeforeMemoryUsed = Math.max(0, gwoBeforeMemoryEnd - gwoBeforeMemoryStart);

        System.out.println("\n===== GWO BEFORE TRAFFIC =====");
        System.out.println(gwoBefore);
        System.out.println("Convergence history: " + gwoBeforeSolver.getConvergenceHistory());
        System.out.printf("GWO runtime before traffic: %.3f ms%n",
                (gwoBeforeEnd - gwoBeforeStart) / 1_000_000.0);
        System.out.printf("GWO memory before traffic: %.3f MB%n", gwoBeforeMemoryUsed);

        TrafficSimulator traffic = new TrafficSimulator();
        traffic.applyRandomTraffic(graph);

        forceGC();
        double yenAfterMemoryStart = getUsedMemoryMB();
        long yenAfterStart = System.nanoTime();
        List<YenKShortestPaths.Path> afterPaths = yen.getKShortestPaths(source, destination, 3);
        long yenAfterEnd = System.nanoTime();
        double yenAfterMemoryEnd = getUsedMemoryMB();
        double yenAfterMemoryUsed = Math.max(0, yenAfterMemoryEnd - yenAfterMemoryStart);

        System.out.println("\n===== YEN AFTER TRAFFIC =====");
        if (afterPaths.isEmpty()) {
            System.out.println("No path found.");
        } else {
            for (int i = 0; i < afterPaths.size(); i++) {
                YenKShortestPaths.Path p = afterPaths.get(i);
                System.out.println("Path " + (i + 1) + ": " + p.nodes +
                        " | cost=" + String.format("%.2f", p.cost));
            }
        }
        System.out.printf("Yen runtime after traffic: %.3f ms%n",
                (yenAfterEnd - yenAfterStart) / 1_000_000.0);
        System.out.printf("Yen memory after traffic: %.3f MB%n", yenAfterMemoryUsed);

        GWO gwoAfterSolver = new GWO(graph, numWolves, maxIterations);

        forceGC();
        double gwoAfterMemoryStart = getUsedMemoryMB();
        long gwoAfterStart = System.nanoTime();
        GWO.RouteResult gwoAfter = gwoAfterSolver.optimize(source, destination);
        long gwoAfterEnd = System.nanoTime();
        double gwoAfterMemoryEnd = getUsedMemoryMB();
        double gwoAfterMemoryUsed = Math.max(0, gwoAfterMemoryEnd - gwoAfterMemoryStart);

        System.out.println("\n===== GWO AFTER TRAFFIC =====");
        System.out.println(gwoAfter);
        System.out.println("Convergence history: " + gwoAfterSolver.getConvergenceHistory());
        System.out.printf("GWO runtime after traffic: %.3f ms%n",
                (gwoAfterEnd - gwoAfterStart) / 1_000_000.0);
        System.out.printf("GWO memory after traffic: %.3f MB%n", gwoAfterMemoryUsed);

        System.out.println("\n===== SUMMARY FOR " + label + " =====");

        if (!beforePaths.isEmpty()) {
            System.out.println("Best Yen path before traffic cost: " +
                    String.format("%.2f", beforePaths.get(0).cost));
        }
        System.out.println("Best GWO path before traffic cost: " +
                String.format("%.2f", gwoBefore.cost));

        if (!afterPaths.isEmpty()) {
            System.out.println("Best Yen path after traffic cost: " +
                    String.format("%.2f", afterPaths.get(0).cost));
        }
        System.out.println("Best GWO path after traffic cost: " +
                String.format("%.2f", gwoAfter.cost));

        System.out.printf("Yen memory before traffic: %.3f MB%n", yenBeforeMemoryUsed);
        System.out.printf("GWO memory before traffic: %.3f MB%n", gwoBeforeMemoryUsed);
        System.out.printf("Yen memory after traffic: %.3f MB%n", yenAfterMemoryUsed);
        System.out.printf("GWO memory after traffic: %.3f MB%n", gwoAfterMemoryUsed);
    }

    private static double getUsedMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        long usedBytes = runtime.totalMemory() - runtime.freeMemory();
        return usedBytes / (1024.0 * 1024.0);
    }

    private static void forceGC() {
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
