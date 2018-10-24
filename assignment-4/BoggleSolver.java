import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BoggleSolver {

    private Trie dic;
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dic = new Trie();
        for (String word: dictionary) {
            dic.insert(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int width = board.cols();
        int height = board.rows();

        boolean[][] status = new boolean[height][width];
        Set<String> output = new HashSet<String>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                backtrack(i, j, status, output, "", dic.root, board);
            }
        }
        return output;
    }

    private void backtrack(int row, int col, boolean[][] status, Set<String> words, String prefix, Trie.Node node, BoggleBoard board) {
        status[row][col] = true;
        char c = board.getLetter(row, col);
        Trie.Node newNode = dic.getChild(node, c);

        if (c == 'Q' && newNode != null) newNode = dic.getChild(newNode, 'U');

        if (newNode == null) {
            status[row][col] = false;
            return;
        }

        String newPrefix = c == 'Q' ? prefix+"QU" : prefix+c;

        if (newPrefix.length() > 2 && dic.contains(newPrefix)) words.add(newPrefix);

        List<int[]> neighbours = getNeighbour(row, col, status, board.rows(), board.cols());

        for (int[] coordinates: neighbours) {
            backtrack(coordinates[0], coordinates[1], status, words, newPrefix, newNode, board);
        }

        status[row][col] = false;
    }

    // get valid unvisited neighbour of given cell
    private List<int[]> getNeighbour(int row, int col, boolean[][] status, int height, int width) {
        List<int[]> out = new LinkedList<int[]>();

        if (col+1 < width && !status[row][col+1])
            out.add(new int[] {row, col+1});

        if (col-1 >= 0 && !status[row][col-1])
            out.add(new int[] {row, col-1});

        if (row-1 >= 0 && !status[row-1][col])
            out.add(new int[] {row-1, col});

        if (row+1 < height && !status[row+1][col])
            out.add(new int[] {row+1, col});

        if (row-1 >= 0 && col-1 >= 0 && !status[row-1][col-1])
            out.add(new int[] {row-1, col-1});

        if (row+1 < height && col+1 < width && !status[row+1][col+1])
            out.add(new int[] {row+1, col+1});

        if (row+1 < height && col-1 >= 0 && !status[row+1][col-1])
            out.add(new int[] {row+1, col-1});

        if (row-1 >= 0 && col+1 < width && !status[row-1][col+1])
            out.add(new int[] {row-1, col+1});

        return out;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException("Null value passed");


        if (!dic.contains(word)) return 0;
        int length = word.length();

        if (length < 3) return 0;
        else if (length < 5) return 1;
        else if (length == 5) return 2;
        else if (length == 6) return 3;
        else if (length == 7) return 5;
        else return 11;
    }

    private static class Trie {

        private static final int R = 26;
        private static final char NORMALIZE = 'A';
        private Node root;

        public Trie() {
            root = new Node();
        }

        public void insert(String word) {
            Node current = root;
            int i = 0;
            while (i < word.length() && current.children[index(word.charAt(i))] != null) {
                current = current.children[index(word.charAt(i))];
                i++;
            }

            while (i < word.length()) {
                current.children[index(word.charAt(i))] = new Node();
                current = current.children[index(word.charAt(i))];
                i++;
            }

            current.word = true;
        }

        public boolean contains(String word) {
            Node current = root;

            for (char c: word.toCharArray()) {
                Node temp = current.children[index(c)];
                if (temp == null) return false;
                current = temp;
            }
            return current.word;
        }

        public Node getChild(Node node, char a) {
            return node.children[index(a)];
        }

        private int index(char a) {
            return a-NORMALIZE;
        }

        private static class Node {

            private boolean word;
            private Node[] children;

            Node() {
                this.word = false;
                this.children = new Node[R];
            }
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
