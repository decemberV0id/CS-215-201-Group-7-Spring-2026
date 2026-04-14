package visualization;

import graph.*;
import algorithms.YenKShortestPaths.Path;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import java.util.List;

public class YenGraphViewer {

    public static void show(Graph graph, List<Path> yenPaths) {
        System.setProperty("org.graphstream.ui", "swing");

        Graph gsGraph = new SingleGraph("Yen K-Shortest Paths");

        // Styling
        gsGraph.setAttribute("ui.stylesheet", """
            node {
                fill-color: lightblue;
                size: 20px;
                text-size: 14;
            }
            edge {
                fill-color: #cccccc;
                size: 2px;
            }
        """);

        // Add nodes
        for (Node n : graph.getNodes()) {
            org.graphstream.graph.Node gsn =
                    gsGraph.addNode(n.getId());
            gsn.setAttribute("ui.label", n.getId());
        }

        // Add edges
        for (Node from : graph.getNodes()) {
            for (Edge e : graph.getEdges(from)) {
                String edgeId = from.getId() + "-" + e.getTo().getId();
                if (gsGraph.getEdge(edgeId) == null) {
                    gsGraph.addEdge(edgeId, from.getId(), e.getTo().getId(), true)
                           .setAttribute("ui.label", e.getWeight());
                }
            }
        }

        String[] colors = { "red", "green", "orange", "purple", "blue" };

        // Draw Yen paths
        for (int i = 0; i < yenPaths.size(); i++) {
            List<Node> nodes = yenPaths.get(i).nodes;
            String color = colors[i % colors.length];

            for (int j = 0; j < nodes.size() - 1; j++) {
                String edgeId = nodes.get(j).getId() + "-" +
                                nodes.get(j + 1).getId();

                Edge gsEdge = gsGraph.getEdge(edgeId);
                if (gsEdge != null) {
                    gsEdge.setAttribute("ui.style",
                        "fill-color: " + color + "; size: 4px;");
                }
            }
        }

        gsGraph.display();
    }
}
