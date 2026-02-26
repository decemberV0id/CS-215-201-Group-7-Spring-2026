package traffic;

import graph.*;

import java.util.List;
import java.util.Random;

public class TrafficSimulator {

    private final Random rand = new Random();

    // Apply a fixed multiplier to all edges
    public void applyTraffic(Graph graph, double multiplier) {
        for (Node n : graph.getNodes()) {
            for (Edge e : graph.getEdges(n)) {
                e.setWeight(e.getWeight() * multiplier);
            }
        }
    }

    // Apply random traffic: multiply each edge by 0.5 to 2.0 randomly
    public void applyRandomTraffic(Graph graph) {
        for (Node n : graph.getNodes()) {
            for (Edge e : graph.getEdges(n)) {
                double randomMultiplier = 0.5 + rand.nextDouble() * 1.5; // 0.5 to 2.0
                e.setWeight(e.getWeight() * randomMultiplier);
            }
        }
    }
}
