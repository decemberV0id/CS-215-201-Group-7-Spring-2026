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
            this.nodes = new ArrayList<>(nodes);
            this.cost = cost;
        }

        @Override
        public String toString() {
            return nodes + " | cost=" + String.format("%.2f", cost);
        }
    }

    private static class EdgeKey {
        Node from;
        Node to;

        EdgeKey(Node from, Node to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EdgeKey)) return false;
            EdgeKey other = (EdgeKey) o;
            return from.equals(other.from) && to.equals(other.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    public List<Path> getKShortestPaths(Node source, Node target, int K) {
        List<Path> shortestPaths = new ArrayList<>();
        PriorityQueue<Path> candidates =
                new PriorityQueue<>(Comparator.comparingDouble(p -> p.cost));
        Set<String> seen = new HashSet<>();

        Path firstPath = dijkstra(source, target, new HashSet<>(), new HashSet<>());
        if (firstPath == null) {
            return shortestPaths;
        }

        shortestPaths.add(firstPath);
        seen.add(pathSignature(firstPath.nodes));

        for (int k = 1; k < K; k++) {
            Path previousPath = shortestPaths.get(k - 1);

            for (int i = 0; i < previousPath.nodes.size() - 1; i++) {
                Node spurNode = previousPath.nodes.get(i);
                List<Node> rootPath = new ArrayList<>(previousPath.nodes.subList(0, i + 1));

                Set<Node> bannedNodes = new HashSet<>();
                for (int r = 0; r < rootPath.size() - 1; r++) {
                    bannedNodes.add(rootPath.get(r));
                }

                Set<EdgeKey> bannedEdges = new HashSet<>();
                for (Path p : shortestPaths) {
                    if (p.nodes.size() > i && samePrefix(p.nodes, rootPath)) {
                        bannedEdges.add(new EdgeKey(p.nodes.get(i), p.nodes.get(i + 1)));
                    }
                }

                Path spurPath = dijkstra(spurNode, target, bannedNodes, bannedEdges);

                if (spurPath != null) {
                    List<Node> totalPathNodes = new ArrayList<>(rootPath);
                    totalPathNodes.addAll(spurPath.nodes.subList(1, spurPath.nodes.size()));

                    String sig = pathSignature(totalPathNodes);
                    if (!seen.contains(sig)) {
                        double totalCost = calculatePathCost(totalPathNodes);
                        Path candidate = new Path(totalPathNodes, totalCost);
                        candidates.add(candidate);
                        seen.add(sig);
                    }
                }
            }

            if (candidates.isEmpty()) {
                break;
            }

            shortestPaths.add(candidates.poll());
        }

        return shortestPaths;
    }

    private boolean samePrefix(List<Node> path, List<Node> prefix) {
        if (path.size() < prefix.size()) return false;
        for (int i = 0; i < prefix.size(); i++) {
            if (!path.get(i).equals(prefix.get(i))) {
                return false;
            }
        }
        return true;
    }

    private String pathSignature(List<Node> nodes) {
        StringBuilder sb = new StringBuilder();
        for (Node n : nodes) {
            sb.append(n.getId()).append("->");
        }
        return sb.toString();
    }

    private double calculatePathCost(List<Node> nodes) {
        double cost = 0.0;

        for (int i = 0; i < nodes.size() - 1; i++) {
            Node from = nodes.get(i);
            Node to = nodes.get(i + 1);

            boolean found = false;
            for (Edge e : graph.getEdges(from)) {
                if (e.getTo().equals(to)) {
                    cost += e.getWeight();
                    found = true;
                    break;
                }
            }

            if (!found) {
                return Double.POSITIVE_INFINITY;
            }
        }

        return cost;
    }

    private Path dijkstra(Node start, Node end, Set<Node> bannedNodes, Set<EdgeKey> bannedEdges) {
        if (bannedNodes.contains(start) || bannedNodes.contains(end)) {
            return null;
        }

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

            if (curr.equals(end)) {
                break;
            }

            for (Edge e : graph.getEdges(curr)) {
                Node next = e.getTo();

                if (bannedNodes.contains(next)) continue;
                if (bannedEdges.contains(new EdgeKey(curr, next))) continue;

                double newDist = dist.get(curr) + e.getWeight();
                if (newDist < dist.get(next)) {
                    dist.put(next, newDist);
                    prev.put(next, curr);
                    pq.add(next);
                }
            }
        }

        if (dist.get(end) == Double.POSITIVE_INFINITY) {
            return null;
        }

        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new Path(path, dist.get(end));
    }
}
