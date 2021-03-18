import solver.Solver;
import solver.SolverException;
import structures.Pair;
import structures.graph.Graph;
import structures.graph.GraphIO;
import structures.graph.Node;
import structures.matrix.Matrix;
import structures.matrix.MatrixIO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        GraphIO graphIO = new GraphIO();
        MatrixIO matrixIO = new MatrixIO();
        String pref = Paths.get("").toAbsolutePath().toString() + "/src/files/small";
        Pair<Matrix, Map<String, Integer>> p = matrixIO.read(pref + "matrix.txt");
        Graph g = graphIO.read(pref + "graph.txt", p.second);
        Matrix x = p.first;
        Pair<Matrix, Map<String, Integer>> y = matrixIO.read(pref + "ans.txt");

        Solver solver = new Solver();
        try {
            List<Double> ans = solver.solve(g, p.first);
            if (ans.isEmpty()) {
                System.out.println("No solution");
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(pref + "pred" + Solver.TIME_LIMIT + ".txt"))) {
                    int i = 0;
                    double[] est = new double[y.first.getM()];
                    double[] cnt = new double[y.first.getM()];
                    int cnt_pred = 0;
                    for (Node node: g.getNodes()) {
                        int val = 0;
                        if (ans.get(i) > 0.001) {
                            val = 1;
                            cnt_pred++;
                        }
                        writer.write(val + " ");
                        for (int j = 0; j < 4; j++) {
                            if (val == 1 && y.first.get(i, j) != 0) {
                                est[j]++;
                            }
                            if (y.first.get(i, j) != 0) {
                                cnt[j]++;
                            }
                            writer.write(String.format("%d", (int)y.first.get(i, j)) + " ");
                        }
                        writer.write('\n');
                        i++;
                    }

                    for (int j = 0; j < 4; j++) {
                        writer.write(String.format("%f", est[j] / cnt[j]) + " ");
                    }
                    writer.write('\n');
                    writer.write('\n' + cnt_pred + " ");
                    for (int j = 0; j < 4; j++) {
                        writer.write((int)cnt[j] + " ");
                    }
                    /*for (int i = 0; i < x.getM(); i++) {
                        writer.write(ans.get(x.getN() + i).toString() + '\n');
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SolverException e) {
            e.printStackTrace();
        }
    }
}
