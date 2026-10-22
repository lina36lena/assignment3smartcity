package graph;

/** An edge whose endpoints are zero-based vertex IDs. */
public record Edge(int from, int to, int weight) {
}
