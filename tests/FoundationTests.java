package tests;

import dsu.DisjointSetUnion;
import graph.Edge;
import graph.Metrics;
import graph.WeightedGraph;

/** Small checks for the graph and disjoint-set data structures. */
public class FoundationTests {
    private static int cases;
    private static int checks;

    public static void main(String[] args) {
        run("undirected graph and parallel edges", FoundationTests::undirectedGraph);
        run("directed graph and isolated vertices", FoundationTests::directedGraph);
        run("read-only graph lists", FoundationTests::readOnlyLists);
        run("invalid graph input", FoundationTests::invalidGraphInput);
        run("initial disjoint sets", FoundationTests::initialSets);
        run("unions and component counts", FoundationTests::unions);
        run("path compression and counters", FoundationTests::pathCompression);
        run("invalid disjoint-set input", FoundationTests::invalidSetInput);
        System.out.println("Passed " + cases + " cases and " + checks + " checks.");
    }

    private static void undirectedGraph() {
        WeightedGraph graph = new WeightedGraph(5, false);
        graph.addEdge(0, 1, 7);
        graph.addEdge(1, 2, -3);
        graph.addEdge(0, 1, 9);

        equal(5, graph.vertexCount(), "vertex count");
        check(!graph.directed(), "undirected flag");
        equal(3, graph.edgeCount(), "each undirected edge is counted once");
        equal(3, graph.edges().size(), "original edge list size");
        equal(2, graph.neighbors(0).size(), "parallel edges are preserved");
        equal(3, graph.neighbors(1).size(), "reverse adjacency is present");
        check(graph.neighbors(1).contains(new Edge(1, 0, 7)), "reverse edge orientation");
        check(graph.neighbors(2).contains(new Edge(2, 1, -3)), "negative weight is preserved");
        equal(0, graph.neighbors(4).size(), "isolated vertex has no neighbors");
    }

    private static void directedGraph() {
        WeightedGraph graph = new WeightedGraph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(2, 0, 8);

        check(graph.directed(), "directed flag");
        equal(2, graph.edgeCount(), "directed edge count");
        check(graph.neighbors(0).contains(new Edge(0, 1, 5)), "outgoing edge is stored");
        equal(0, graph.neighbors(1).size(), "incoming edge does not become outgoing");
        equal(1, graph.neighbors(2).size(), "second source has one outgoing edge");
        equal(0, graph.neighbors(3).size(), "isolated directed vertex");
    }

    private static void readOnlyLists() {
        WeightedGraph graph = new WeightedGraph(2, false);
        graph.addEdge(0, 1, 4);

        expect(UnsupportedOperationException.class,
                () -> graph.edges().add(new Edge(1, 0, 6)), "cannot append to edge view");
        expect(UnsupportedOperationException.class,
                () -> graph.edges().clear(), "cannot clear edge view");
        expect(UnsupportedOperationException.class,
                () -> graph.neighbors(0).clear(), "cannot change adjacency view");
        equal(1, graph.edgeCount(), "failed list changes leave graph intact");
    }

    private static void invalidGraphInput() {
        expect(IllegalArgumentException.class, () -> new WeightedGraph(0, false), "zero vertices");
        expect(IllegalArgumentException.class, () -> new WeightedGraph(-1, false), "negative vertices");
        WeightedGraph graph = new WeightedGraph(3, false);
        expect(IndexOutOfBoundsException.class, () -> graph.addEdge(-1, 0, 1), "negative source");
        expect(IndexOutOfBoundsException.class, () -> graph.addEdge(0, 3, 1), "destination out of range");
        expect(IndexOutOfBoundsException.class, () -> graph.neighbors(3), "invalid adjacency request");
        equal(0, graph.edgeCount(), "invalid edges do not modify the graph");
    }

    private static void initialSets() {
        Metrics metrics = new Metrics();
        DisjointSetUnion sets = new DisjointSetUnion(4, metrics);
        equal(4, sets.components(), "each element starts in its own set");
        for (int element = 0; element < 4; element++) {
            equal(element, sets.find(element), "initial representative");
        }
        equal(4, metrics.findCalls, "one counter increment per public find");
        equal(0, metrics.unionCalls, "no unions yet");
        equal(0, metrics.parentTraversals, "initial roots need no traversal");
        equal(0, metrics.parentRewrites, "initial finds change no parent");
        equal(0, new DisjointSetUnion(0, new Metrics()).components(), "empty disjoint set");
    }

    private static void unions() {
        Metrics metrics = new Metrics();
        DisjointSetUnion sets = new DisjointSetUnion(5, metrics);
        check(sets.union(0, 1), "first union merges two sets");
        equal(4, sets.components(), "first union reduces components");
        check(sets.union(2, 3), "second pair can merge");
        check(sets.union(1, 3), "two existing groups can merge");
        equal(2, sets.components(), "four joined elements and one isolated element");
        equal(sets.find(0), sets.find(3), "merged elements share a representative");
        check(sets.find(0) != sets.find(4), "isolated element remains separate");
        check(!sets.union(0, 3), "repeated union does not merge again");
        check(!sets.union(4, 4), "self union does not merge");
        equal(2, sets.components(), "unsuccessful unions leave components unchanged");
        equal(5, metrics.unionCalls, "all valid union attempts are counted");
    }

    private static void pathCompression() {
        Metrics metrics = new Metrics();
        DisjointSetUnion sets = new DisjointSetUnion(4, metrics);
        sets.union(0, 1);
        sets.union(2, 3);
        sets.union(0, 2);
        // Equal sizes select the smaller root: 3 -> 2 -> 0 before find(3).
        equal(3, metrics.unionCalls, "three unions");
        equal(6, metrics.findCalls, "each union performs two finds");
        equal(3, metrics.parentRewrites, "three successful links");

        long traversalsBefore = metrics.parentTraversals;
        long rewritesBefore = metrics.parentRewrites;
        equal(0, sets.find(3), "deep element reaches the root");
        equal(2, metrics.parentTraversals - traversalsBefore, "first find traverses two links");
        equal(1, metrics.parentRewrites - rewritesBefore, "first find compresses one parent");

        traversalsBefore = metrics.parentTraversals;
        rewritesBefore = metrics.parentRewrites;
        equal(0, sets.find(3), "compressed element keeps the same root");
        equal(1, metrics.parentTraversals - traversalsBefore, "next find uses one link");
        equal(0, metrics.parentRewrites - rewritesBefore, "already compressed parent is unchanged");
        equal(8, metrics.findCalls, "find counter includes both explicit calls");
    }

    private static void invalidSetInput() {
        expect(IllegalArgumentException.class,
                () -> new DisjointSetUnion(-1, new Metrics()), "negative set size");
        expect(NullPointerException.class, () -> new DisjointSetUnion(2, null), "missing counters");
        Metrics metrics = new Metrics();
        DisjointSetUnion sets = new DisjointSetUnion(3, metrics);
        expect(IndexOutOfBoundsException.class, () -> sets.find(-1), "negative element");
        expect(IndexOutOfBoundsException.class, () -> sets.find(3), "element out of range");
        expect(IndexOutOfBoundsException.class, () -> sets.union(0, 3), "invalid union endpoint");
        equal(3, sets.components(), "invalid operations preserve components");
        equal(0, metrics.findCalls, "rejected finds are not counted");
        equal(0, metrics.unionCalls, "rejected unions are not counted");
    }

    private static void run(String name, Runnable test) {
        test.run();
        cases++;
        System.out.println("PASS: " + name);
    }

    private static void equal(long expected, long actual, String message) {
        check(expected == actual, message + ": expected " + expected + ", got " + actual);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expect(Class<? extends Throwable> type, Runnable action, String message) {
        checks++;
        try {
            action.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) {
                return;
            }
            throw new AssertionError(message + ": wrong exception " + error, error);
        }
        throw new AssertionError(message + ": expected " + type.getSimpleName());
    }
}
