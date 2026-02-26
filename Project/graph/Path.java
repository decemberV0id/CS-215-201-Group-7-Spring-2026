package graph;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private final List<Node> nodes;

    public Path() {
        nodes = new ArrayList<>();
    }

    public Path(List<Node> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}
