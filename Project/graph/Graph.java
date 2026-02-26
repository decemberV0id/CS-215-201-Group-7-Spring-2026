package graph;

import java.util.*;

public class Graph {
    private final Map<Node, List<Edge>> adj = new HashMap<>();

    public void addNode(Node node) {
        adj.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Node from, Node to, double weight) {
        adj.get(from).add(new Edge(from, to, weight));
    }

    public List<Edge> getEdges(Node node) {
        return adj.getOrDefault(node, List.of());
    }

    public List<Node> getNodes() {
    return new ArrayList<>(adj.keySet());
    }
}
