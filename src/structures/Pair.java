package structures;

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
    public Pair() {
        first = null;
        second = null;
    }
    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) obj;
        return first.equals(p.first) && second.equals(p.second);
    }
}