import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordNet;
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    public String outcast(String[] nouns) {

        if (nouns == null) throw new IllegalArgumentException();

        String output = nouns[0];
        int dis = Integer.MIN_VALUE;

        for (String n: nouns) {
            int newDist = 0;
            for (String noun: nouns) {
                newDist += wordNet.distance(n, noun);
            }

            if (newDist > dis) {
                dis = newDist;
                output = n;
            }
        }
        return output;
    }


    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}