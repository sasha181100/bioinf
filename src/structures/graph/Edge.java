package structures.graph;

public class Edge {
    private Node from;
    private Node to;
    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }
    @Override
    public int hashCode() {
        return from.hashCode() ^ to.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge edge = (Edge) obj;
        return (from.equals(edge.getFrom()) && to.equals(edge.getTo())) || (from.equals(edge.getTo()) && to.equals(edge.getFrom()));
    }
    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }
}
