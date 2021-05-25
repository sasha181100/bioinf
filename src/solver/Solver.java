package solver;

import ilog.concert.IloNumExpr;
import structures.matrix.Matrix;
import structures.graph.*;
import structures.*;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.*;
import java.util.stream.IntStream;

public class Solver {
    public static final double EPS = 0.01;
    public static int TIME_LIMIT = 300;
    private IloCplex cplex;
    private Map<Edge, Pair<IloNumVar, IloNumVar>> x;
    private Map<Node, IloNumVar> d;
    private Map<Node, IloNumVar> r;
    private Map<Node, IloNumVar> a;
    private Map<Integer, IloNumVar> p;
    private Map<Integer, IloNumVar> t;
    private Map<Integer, IloNumVar> y;
    private Graph graph;
    private Matrix mtx;
    private int n, m;
    private double[] upper_bounds;

    public List<Double> solve(Graph graph, Matrix mtx) throws SolverException {
        try {
            cplex = new IloCplex();
            this.graph = graph;
            this.mtx = mtx;
            n = mtx.getN();
            m = mtx.getM();
            initVariables();
            addConstraints();
            addObjective();
            cplex.setParam(IloCplex.Param.TimeLimit, TIME_LIMIT);
            boolean solFound = cplex.solve();
            System.out.println(cplex.getSolnPoolNsolns());
            cplex.populate();
            cplex.writeSolutions("solutions.xml");
            if (solFound) {
                return getResult();
            }
            return Collections.emptyList();
        } catch (IloException e) {
            throw new SolverException(e.getMessage());
        } finally {
            cplex.end();
        }
    }
    private void initVariables() throws IloException {
        p = new LinkedHashMap<>();
        a = new LinkedHashMap<>();
        d = new LinkedHashMap<>();
        x = new LinkedHashMap<>();
        t = new LinkedHashMap<>();
        r = new LinkedHashMap<>();
        y = new LinkedHashMap<>();
        upper_bounds = new double[mtx.getM()];
        for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                upper_bounds[i] = Math.max(upper_bounds[i], Math.abs(mtx.get(j, i)));
            }
            y.put(i, cplex.boolVar("y" + (i + 1)));
        }
        for (Node node : graph.getNodes()) {
            String nodeName = Integer.toString(node.getNum() + 1);
            d.put(node, cplex.numVar(0, n - 1, "d" + nodeName));
            a.put(node, cplex.numVar(0, 1, "a" + nodeName));
            r.put(node, cplex.boolVar("r" + nodeName));
        }
        for (int i = 0; i < m; i++) {
            p.put(i, cplex.numVar(-upper_bounds[i], upper_bounds[i], "p" + (i + 1)));
            t.put(i, cplex.numVar(0, upper_bounds[i], "t" + (i + 1)));
        }
        for (Edge edge : graph.getEdges()) {
            Node from = edge.getFrom();
            Node to = edge.getTo();
            String edgeName = (from.getNum() + 1) + "_" + (to.getNum() + 1);
            IloNumVar in = cplex.boolVar("x_" + edgeName + "_direct");
            IloNumVar out = cplex.boolVar("x_" + edgeName + "_back");
            x.put(edge, new Pair<>(in, out));
        }
    }

    private void addObjective() throws IloException {
        IloNumExpr[] squares = new IloNumExpr[n];
        int i = 0;
        for (Node node : graph.getNodes()) {
            squares[i] = cplex.prod(a.get(node), a.get(node));
            i++;
        }
        cplex.addMinimize(cplex.sum(squares));
    }

    private void addConstraints() throws IloException {
        arborescenceConstraints();
        aPartConstraints();
        //optimizationExpressionConstraints();
        //addMyConstraints();
    }
    private void addMyConstraints() throws IloException {
        for (int i = 0; i < m; i++) {
            cplex.addEq(p.get(i), -i - 2);
        }
    }
    private void arborescenceConstraints() throws IloException {
        //(1)
        cplex.addEq(cplex.sum(graph.getNodes().stream().map(x -> r.get(x)).toArray(IloNumVar[]::new)), 1);
        //(3)
        for (Node node : graph.getNodes()) {
            Set<Edge> edges = graph.edgesOf(node);
            if (edges == null) continue;
            IloNumVar[] xSum = new IloNumVar[edges.size() + 1];
            int i = 0;
            for (Edge edge: edges) {
                xSum[i++] = getX(edge, node);
            }
            xSum[xSum.length - 1] = r.get(node);
            cplex.addEq(cplex.sum(xSum), 1);
        }
        //(4)
        IloNumVar xSum[] = new IloNumVar[graph.getEdges().size()];
        for (Edge edge : graph.getEdges()) {
            cplex.addLe(cplex.sum(x.get(edge).first, x.get(edge).second), 1);
        }
        //(10), (12)-(13)
        for (Edge edge: graph.getEdges()) {
            Node v = edge.getFrom();
            Node u = edge.getTo();
            //(10)
            cplex.addGe(cplex.diff(cplex.sum(n, a.get(v)), a.get(u)), cplex.prod(n, x.get(edge).first));
            cplex.addGe(cplex.diff(cplex.sum(n, a.get(u)), a.get(v)), cplex.prod(n, x.get(edge).second));
            //(12)
            cplex.addGe(cplex.diff(cplex.sum(n, d.get(u)), d.get(v)), cplex.prod(n + 1, x.get(edge).first));
            cplex.addGe(cplex.diff(cplex.sum(n, d.get(v)), d.get(u)), cplex.prod(n + 1, x.get(edge).second));
            //(13)
            cplex.addGe(cplex.diff(cplex.sum(n, d.get(u)), d.get(v)), cplex.prod(n - 1, x.get(edge).first));
            cplex.addGe(cplex.diff(cplex.sum(n, d.get(v)), d.get(u)), cplex.prod(n - 1, x.get(edge).second));
        }

        //(11)
        for (Node node: graph.getNodes()) {
            cplex.addLe(cplex.sum(d.get(node), cplex.prod(n, r.get(node))), n);
        }
    }

    private IloNumVar getX(Edge e, Node to) {
        if (e.getTo().equals(to)) {
            return x.get(e).first;
        } else {
            return x.get(e).second;
        }
    }

    private void aPartConstraints() throws IloException {
        //(8)
        IloNumExpr[] sum = new IloNumExpr[n];
        for (int i = 0; i < m; i++) {
            IloNumVar[] vars = new IloNumVar[n];
            double[] vals = new double[n];
            for (Node node : graph.getNodes()) {
                int num = node.getNum();
                vals[num] = mtx.get(num, i);
                vars[num] = a.get(node);
            }
            cplex.addEq(cplex.scalProd(vals, vars), p.get(i));
        }
        /*for (int i = 0; i < m; i++) {
            cplex.addGe(t.get(i), p.get(i));
            cplex.addGe(t.get(i), cplex.diff(0, p.get(i)));
            cplex.addLe(t.get(i), cplex.sum(p.get(i), cplex.prod(2 * upper_bounds[i], y.get(i))));
            cplex.addLe(t.get(i), cplex.sum(cplex.diff(0, p.get(i)), cplex.prod(2 * upper_bounds[i], cplex.diff(1, y.get(i)))));
        }*/
        IloNumExpr[] l1normP = new IloNumExpr[m];
        for (int i = 0; i < m; i++) {
            l1normP[i] = cplex.abs(p.get(i));
        }
        cplex.addEq(cplex.sum(l1normP), 1);
        //(9)
    }

    private List<Double> getResult() throws IloException {
        List<Double> result = new ArrayList<>();
        for (Node node: graph.getNodes()) {
            result.add(cplex.getValue(a.get(node)));
        }
        System.out.println(cplex.getObjValue());
        /*for (int i = 0; i < m; i++) {
            result.add(cplex.getValue(p.get(i)));
        }*/
        return result;
    }
}
