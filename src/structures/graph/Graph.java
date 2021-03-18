package structures.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {
    private Set<Edge> edges;
    private Set<Node> nodes;
    private Map<Node, Set<Edge>> g;
    public Graph(Set<Edge> edges, Set<Node> nodes, Map<Node, Set<Edge>> g) {
        this.edges = edges;
        this.nodes = nodes;
        this.g = g;
    }

    public Set<Edge> getEdges() {
        return edges;
    }
    public Set<Edge> edgesOf(Node v) {
        return g.get(v);
    }
    public Set<Node> getNodes() {
        return nodes;
    }
}
