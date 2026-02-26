import graph.Graph;
import graph.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GraphGenerator {

    public static Graph generateGraph(int numNodes, int extraEdges) {
        Graph graph = new Graph();
        Random rand = new Random();

        List<Node> nodes = new ArrayList<>();

        // Create nodes
        for (int i = 0; i < numNodes; i++) {
            Node node = new Node("N" + i);
            graph.addNode(node);
            nodes.add(node);
        }

        // Ensure connectivity (chain structure)
        for (int i = 0; i < numNodes - 1; i++) {
            double weight = 1 + rand.nextInt(20);
            graph.addEdge(nodes.get(i), nodes.get(i + 1), weight);
        }

        // Add extra random edges
        for (int i = 0; i < extraEdges; i++) {
            Node a = nodes.get(rand.nextInt(numNodes));
            Node b = nodes.get(rand.nextInt(numNodes));

            if (!a.equals(b)) {
                double weight = 1 + rand.nextInt(20);
                graph.addEdge(a, b, weight);
            }
        }

        return graph;
    }
}
