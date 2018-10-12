import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BaseballElimination {

    private Map<String, Integer> teamToId;
    private Team[] teams;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        int n = Integer.parseInt(in.readLine());
        teamToId = new HashMap<>();
        teams = new Team[n];

        for (int i = 0; i < n; i++) {
            String[] line = in.readLine().trim().split("\\s+");

            String name = line[0];
            int wins = Integer.parseInt(line[1]);
            int losses = Integer.parseInt(line[2]);
            int remainingGames = Integer.parseInt(line[3]);
            int[] remainingGamesArr = new int[n];

            for (int j = 0; j < n; j++) {
                remainingGamesArr[j] = Integer.parseInt(line[4+j]);
            }

            Team team = new Team(name, wins, losses, remainingGames, remainingGamesArr);
            teamToId.put(name, i);
            teams[i] = team;
        }
    }

    public int numberOfTeams() {
        return this.teams.length;
    }

    public Iterable<String> teams() {
        return teamToId.keySet();
    }

    public int wins(String team) {
        validate(team);
        return teams[teamToId.get(team)].wins;
    }

    public int losses(String team) {
        validate(team);
        return teams[teamToId.get(team)].losses;
    }

    public int remaining(String team) {
        validate(team);
        return teams[teamToId.get(team)].remainingGames;
    }

    public int against(String team1, String team2) {
        validate(team1);
        validate(team2);
        return teams[teamToId.get(team1)].remains[teamToId.get(team2)];
    }

    public boolean isEliminated(String team) {
        validate(team);
        Optional<List<String>> trivialOut = trivialCase(team);
        if (trivialOut.isPresent()) return true;
        List<String> output = nonTrivialCase(team);
        return output != null;
    }

    public Iterable<String> certificateOfElimination(String team) {
        validate(team);
        Optional<List<String>> trivialOut = trivialCase(team);
        if (trivialOut.isPresent()) return trivialOut.get();
        return nonTrivialCase(team);
    }

    private void validate(String team) {
        if (teamToId.get(team) == null) throw new IllegalArgumentException("Not found: "+team);
    }

    private Optional<List<String>> trivialCase(String team) {
        int maxWin = this.wins(team)+this.remaining(team);

        for (String other: this.teams()) {
            if (!other.equals(team) && this.wins(other) > maxWin) return Optional.of(Arrays.asList(other));
        }

        return Optional.empty();
    }

    private List<String> nonTrivialCase(String outcast) {
        List<String> filteredTeams = new LinkedList<>();

        for (String team: this.teams()) {
            if (!team.equals(outcast)) {
                filteredTeams.add(team);
            }
        }

        FlowNetwork network = buildNetwork(filteredTeams, outcast);
        FordFulkerson fordFulkerson = new FordFulkerson(network, network.V()-2, network.V()-1);

        List<String> array = new ArrayList<>();
        int nC2 = filteredTeams.size()*(filteredTeams.size()-1)/2;
        for (int i = 0; i < filteredTeams.size(); i++) {
            if (fordFulkerson.inCut(nC2+i)) array.add(filteredTeams.get(i));
        }

        return array.isEmpty() ? null : array;
    }


    private FlowNetwork buildNetwork(List<String> filteredTeams, String outcast) {

        int n = filteredTeams.size();
        int nC2 = n*(n-1)/2;
        int v = nC2+2+n;
        int t = v-1;
        int s = v-2;
        int maxWinOfOutcast = this.wins(outcast)+this.remaining(outcast);

        FlowNetwork network = new FlowNetwork(v);
        int start = 0;

        for (int i = 0; i < n; i++) {
            FlowEdge edge = new FlowEdge(nC2+i, t, maxWinOfOutcast-this.wins(filteredTeams.get(i)));
            network.addEdge(edge);
            for (int j = i+1; j < n; j++) {
                FlowEdge edge1 = new FlowEdge(start, nC2+i, Double.POSITIVE_INFINITY);
                FlowEdge edge2 = new FlowEdge(start, nC2+j, Double.POSITIVE_INFINITY);
                FlowEdge edge3 = new FlowEdge(s, start, this.against(filteredTeams.get(i), filteredTeams.get(j)));
                network.addEdge(edge1);
                network.addEdge(edge2);
                network.addEdge(edge3);
                start++;
            }
        }

        return network;
    }

    private static class Team {
        private String name;
        private int wins;
        private int losses;
        private int remainingGames;
        private int[] remains;

        public Team(String name, int wins, int losses, int remainingGames, int[] remains) {
            this.name = name;
            this.wins = wins;
            this.losses = losses;
            this.remainingGames = remainingGames;
            this.remains = remains;
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        System.out.println(division.numberOfTeams());
//        for (String team : division.teams()) {
//            if (division.isEliminated(team)) {
//                StdOut.print(team + " is eliminated by the subset R = { ");
//                for (String t : division.certificateOfElimination(team)) {
//                    StdOut.print(t + " ");
//                }
//                StdOut.println("}");
//            }
//            else {
//                StdOut.println(team + " is not eliminated");
//            }
//        }
    }
}
