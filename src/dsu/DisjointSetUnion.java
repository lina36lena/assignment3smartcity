package dsu;

import graph.Metrics;
import java.util.Objects;

/** Union by size with full path compression. */
public class DisjointSetUnion {
    private final int[] parent;
    private final int[] size;
    private final Metrics metrics;
    private int components;

    public DisjointSetUnion(int size, Metrics metrics) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
        this.metrics = Objects.requireNonNull(metrics, "metrics");
        parent = new int[size];
        this.size = new int[size];
        components = size;
        for (int element = 0; element < size; element++) {
            parent[element] = element;
            this.size[element] = 1;
        }
    }

    public int find(int element) {
        checkElement(element);
        metrics.findCalls++;
        return findRoot(element);
    }

    private int findRoot(int element) {
        if (parent[element] == element) {
            return element;
        }
        metrics.parentTraversals++;
        int root = findRoot(parent[element]);
        if (parent[element] != root) {
            parent[element] = root;
            metrics.parentRewrites++;
        }
        return root;
    }

    public boolean union(int first, int second) {
        checkElement(first);
        checkElement(second);
        metrics.unionCalls++;
        int firstRoot = find(first);
        int secondRoot = find(second);
        if (firstRoot == secondRoot) {
            return false;
        }
        if (size[firstRoot] < size[secondRoot]
                || (size[firstRoot] == size[secondRoot] && firstRoot > secondRoot)) {
            int temporary = firstRoot;
            firstRoot = secondRoot;
            secondRoot = temporary;
        }
        parent[secondRoot] = firstRoot;
        metrics.parentRewrites++;
        size[firstRoot] += size[secondRoot];
        components--;
        return true;
    }

    public int components() {
        return components;
    }

    private void checkElement(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException("Element out of range: " + element);
        }
    }
}
