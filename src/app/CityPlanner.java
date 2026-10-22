package app;

import dsu.DisjointSetUnion;
import graph.Edge;
import graph.Metrics;
import graph.WeightedGraph;

/** First checkpoint: represent roads and check which districts are connected. */
public final class CityPlanner {
    private CityPlanner() { }

    public static void main(String[] args) {
        WeightedGraph city = new WeightedGraph(6, false);
        city.addEdge(0, 1, 4);
        city.addEdge(1, 2, 2);
        city.addEdge(3, 4, 3);

        System.out.println("Districts: " + city.vertexCount());
        System.out.println("Roads: " + city.edgeCount());
        System.out.println("Roads from district 1:");
        for (Edge road : city.neighbors(1)) {
            System.out.printf("  %d -> %d (weight %d)%n", road.from(), road.to(), road.weight());
        }

        Metrics metrics = new Metrics();
        DisjointSetUnion groups = new DisjointSetUnion(city.vertexCount(), metrics);
        for (Edge road : city.edges()) {
            groups.union(road.from(), road.to());
        }
        System.out.println("Connected components: " + groups.components());
        System.out.println("All districts connected: " + (groups.components() == 1));
        System.out.println("Districts 0 and 2 connected: " + (groups.find(0) == groups.find(2)));
        System.out.println("Districts 0 and 4 connected: " + (groups.find(0) == groups.find(4)));
        System.out.println("District 5 has no roads: " + city.neighbors(5).isEmpty());
    }
}
