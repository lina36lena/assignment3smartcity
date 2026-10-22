package graph;

/** Counts DSU work; later experiments can use these without printing inside find. */
public class Metrics {
    public long findCalls;
    public long unionCalls;
    public long parentTraversals;
    public long parentRewrites;
}
