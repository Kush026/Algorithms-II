import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WordNet {

    private Synset[] synsets;
    private Digraph graph;
    private final Map<String, List<Integer>> nounMap = new TreeMap<>();

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("value in constructor null");

        In in = new In(synsets);
        String[] lines = in.readAllLines();
        this.buildSynsets(lines);
        this.buildGraph(hypernyms, lines.length);

        Topological topological = new Topological(this.graph);
        DirectedCycle directedCycle = new DirectedCycle(this.graph);

        if (!topological.hasOrder() || directedCycle.hasCycle()) throw new IllegalArgumentException();
        in.close();
    }

    private void buildSynsets(String[] lines) {
        this.synsets = new Synset[lines.length];

        for (String s: lines) {
            String[] arr = s.split(",");
            int id = Integer.parseInt(arr[0]);
            String[] set = arr[1].split(" ");
            String gloss = arr[2];

            synsets[id] = new Synset(id, set, gloss);

            for (String noun: set) {
                List<Integer> list = this.nounMap.computeIfAbsent(noun, k -> new ArrayList<>());
                list.add(id);
            }
        }
    }

    private void buildGraph(String filename, int v) {
        In in = new In(filename);
        this.graph = new Digraph(v);

        while (in.hasNextLine()) {
            String[] s = in.readLine().split(",");

            int synsetId = Integer.parseInt(s[0]);

            for (int i = 1; i < s.length; i++) {
                this.graph.addEdge(synsetId, Integer.parseInt(s[i]));
            }
        }

        in.close();
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return this.nounMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("argument value is null");
        return nounMap.get(word) != null;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!this.isNoun(nounA) || !this.isNoun(nounB)) throw new IllegalArgumentException("argument value is null");

        if (nounA.equals(nounB)) return 0;

        List<Integer> l1 = this.nounMap.get(nounA);
        List<Integer> l2 = this.nounMap.get(nounB);
        SAP sap = new SAP(this.graph);
        return sap.length(l1, l2);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!this.isNoun(nounA) || !this.isNoun(nounB)) throw new IllegalArgumentException("argument value is null");

        List<Integer> l1 = this.nounMap.get(nounA);
        List<Integer> l2 = this.nounMap.get(nounB);

        SAP sap = new SAP(this.graph);
        int ind = sap.ancestor(l1, l2);
        return String.join(" ", this.synsets[ind].getSet());
    }

    private class Synset {
        private final int id;
        private final String[] set;
        private final String gloss;

        public Synset(int id, String[] set, String gloss) {
            this.id = id;
            this.set = Arrays.copyOf(set, set.length);
            this.gloss = gloss;
        }

        public int getId() {
            return id;
        }

        public String[] getSet() {
            return set;
        }

        public String getGloss() {
            return gloss;
        }
    }
}