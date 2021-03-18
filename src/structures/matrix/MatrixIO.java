package structures.matrix;

import structures.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static structures.graph.GraphIO.extractName;
import static structures.graph.GraphIO.getId;

public class MatrixIO {
    public Pair<Matrix, Map<String, Integer>> read(String file) throws IOException {
        Pair<Matrix, Map<String, Integer>> res = new Pair<>();
        Scanner scanner = new Scanner(new File(file));
        scanner.useLocale(Locale.US);
        Map<String, Integer> map = new HashMap<>();
        List<List<Double>> data = new ArrayList<>();
        while (scanner.hasNext()) {
            int row = getId(map, scanner.next());
            List<Double> rowData = new ArrayList<>();
            while (scanner.hasNextDouble()) {
                rowData.add(scanner.nextDouble());
            }
            data.add(rowData);
        }
        res.second = map;
        int n = map.size();
        if (n == 0) {
            throw new RuntimeException("Empty matrix");
        }
        int m = data.get(0).size();
        double[][] matrix = new double[n][m];
        for (int i = 0; i < n; ++i) for (int j = 0; j < m; ++j) matrix[i][j] = data.get(i).get(j);

        res.first = new Matrix(n, m, matrix);
        return res;
    }
}
