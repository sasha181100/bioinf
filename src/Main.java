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
        String end = "";
        int tl = 300;
        final double MOD_CONST = 0.001;
        if (args.length > 1) {
            end = args[0];
            tl = Integer.parseInt(args[1]);
        } else {
            if (args.length == 1) {
                tl = Integer.parseInt(args[0]);
                end = "";
            } else {
                System.out.println("Illegal arguments");
            }
        }
        Solver.TIME_LIMIT = tl;
        String pref = Paths.get("").toAbsolutePath().toString() + "/src/files/" + end;
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
                    int cnt_pred = 0;
                    for (Node node: g.getNodes()) {
                        int val = 0;
                        if (ans.get(i) > MOD_CONST) {
                            val = 1;
                            cnt_pred++;
                        }
                        writer.write(val + " " + ans.get(i) + '\n');
                        i++;
                    }


                    writer.write('\n');
                    writer.write('\n' + "Total size of module: " + cnt_pred + " ");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SolverException e) {
            e.printStackTrace();
        }
    }
}
