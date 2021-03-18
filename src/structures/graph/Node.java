package structures.graph;

public class Node {
    private int num;
    public Node(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
    @Override
    public int hashCode() {
        return Integer.valueOf(num).hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }
        Node node = (Node) obj;
        return num == node.getNum();
    }

    @Override
    public String toString() {
        return "N(" + String.valueOf(num) + ")";
    }
}