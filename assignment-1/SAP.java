import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {

        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(this.graph, w);

        int ind = this.random(vPath, wPath, w);

        return ind == -1 ? -1 : vPath.distTo(ind) + wPath.distTo(ind);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {

        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(this.graph, w);

        return this.random(vPath, wPath, w);
    }

    private int random(BreadthFirstDirectedPaths vPath, BreadthFirstDirectedPaths wPath, int w) {
        boolean[] visit = new boolean[this.graph.V()];
        int dis = Integer.MAX_VALUE;
        int ind = -1;

        Queue<Integer> queue = new Queue<>();
        queue.enqueue(w);
        visit[w] = true;

        while (!queue.isEmpty()) {
            int first = queue.dequeue();

            if (vPath.hasPathTo(first)) {
                int newDis = vPath.distTo(first) + wPath.distTo(first);

                if (newDis < dis) {
                    ind = first;
                    dis = newDis;

                    if (dis == 0) break;
                }
            }

            for (int nb: graph.adj(first)) {
                if (visit[nb]) continue;

                queue.enqueue(nb);
                visit[nb] = true;
            }
        }

        return ind;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {

        validation(v);
        validation(w);

        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(this.graph, w);

        int ind = this.random(vPath, wPath, w);
        return ind == -1 ? -1 : vPath.distTo(ind)+wPath.distTo(ind);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validation(v);
        validation(w);
        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(this.graph, w);

        return this.random(vPath, wPath, w);
    }

    private void validation(Iterable<Integer> values) {
        if (values == null) throw new IllegalArgumentException();
        for (Integer v: values) if (v == null) throw new IllegalArgumentException();
    }

    private int random(BreadthFirstDirectedPaths vPath, BreadthFirstDirectedPaths wPath, Iterable<Integer> w) {
        boolean[] visit = new boolean[this.graph.V()];
        int dis = Integer.MAX_VALUE;
        int ind = -1;
        Queue<Integer> queue = new Queue<>();

        for (int value: w) {
            queue.enqueue(value);
            visit[value] = true;
        }

        while (!queue.isEmpty()) {
            int first = queue.dequeue();

            if (vPath.hasPathTo(first)) {
                int newDis = vPath.distTo(first) + wPath.distTo(first);

                if (newDis < dis) {
                    ind = first;
                    dis = newDis;

                    if (dis == 0) break;
                }
            }

            for (int nb: graph.adj(first)) {
                if (!visit[nb]) {
                    queue.enqueue(nb);
                    visit[nb] = true;
                }
            }
        }
        return ind;
    }

    // do unit testing of this class
    public static void main(String[] args) {

        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }

    }
}