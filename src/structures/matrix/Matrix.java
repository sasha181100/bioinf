package structures.matrix;

public class Matrix {
    private final int M;             // number of rows
    private final int N;             // number of columns
    private final double[][] data;

    public Matrix(int N, int M, double[][] data) {
        this.M = M;
        this.N = N;
        this.data = data;
    }

    public double get(int i, int j) {
        return data[i][j];
    }

    public int getM() {
        return M;
    }

    public int getN() {
        return N;
    }
}
