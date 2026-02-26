package algorithms;

import graph.*;
import java.util.*;

public class YenKShortestPaths {

    private final Graph graph;

    public YenKShortestPaths(Graph graph) {
        this.graph = graph;
    }

    public static class Path {
        public final List<Node> nodes;
        public final double cost;

        public Path(List<Node> nodes, double cost) {
            this.nodes = nodes;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return nodes + " | cost=" + cost;
        }
    }

    public List<Path> getKShortestPaths(Node source, Node target, int K) {
        List<Path> result = new ArrayList<>();
        PriorityQueue<Path> pq =
                new PriorityQueue<>(Comparator.comparingDouble(p -> p.cost));

        pq.add(dijkstra(source, target));

        while (!pq.isEmpty() && result.size() < K) {
            Path p = pq.poll();
            result.add(p);
        }
        return result;
    }

    private Path dijkstra(Node start, Node end) {
        Map<Node, Double> dist = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();

        for (Node n : graph.getNodes()) {
            dist.put(n, Double.POSITIVE_INFINITY);
        }
        dist.put(start, 0.0);

        PriorityQueue<Node> pq =
                new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(start);

        while (!pq.isEmpty()) {
            Node curr = pq.poll();
            if (curr.equals(end)) break;

            for (Edge e : graph.getEdges(curr)) {
                double nd = dist.get(curr) + e.getWeight();
                if (nd < dist.get(e.getTo())) {
                    dist.put(e.getTo(), nd);
                    prev.put(e.getTo(), curr);
                    pq.add(e.getTo());
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new Path(path, dist.get(end));
    }
}
