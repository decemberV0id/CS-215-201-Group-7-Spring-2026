import algorithms.YenKShortestPaths;
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
    }
}
