package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Adjacency-list graph that preserves insertion order, parallel edges and loops. */
public class WeightedGraph {
    private final int vertexCount;
    private final boolean directed;
    private final List<Edge> edges = new ArrayList<>();
    private final List<Edge> edgeView = Collections.unmodifiableList(edges);
    private final List<List<Edge>> adjacency;

    public WeightedGraph(int vertexCount, boolean directed) {
        if (vertexCount < 1) {
            throw new IllegalArgumentException("A graph must have at least one vertex");
        }
        this.vertexCount = vertexCount;
        this.directed = directed;
        adjacency = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public int vertexCount() {
        return vertexCount;
    }

    public int edgeCount() {
        return edges.size();
    }

    public boolean directed() {
        return directed;
    }

    public void addEdge(int from, int to, int weight) {
        checkVertex(from);
        checkVertex(to);
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adjacency.get(from).add(edge);
        if (!directed) {
            adjacency.get(to).add(new Edge(to, from, weight));
        }
    }

    /** One original edge for each addEdge call, even for an undirected graph. */
    public List<Edge> edges() {
        return edgeView;
    }

    /** Undirected reverse edges are oriented out of the requested vertex. */
    public List<Edge> neighbors(int vertex) {
        checkVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException("Vertex out of range: " + vertex);
        }
    }
}
