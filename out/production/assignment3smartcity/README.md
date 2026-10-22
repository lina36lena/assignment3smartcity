# Smart City Infrastructure and Route Planning

## First checkpoint: graph and DSU

This version contains the foundation for Assignment 3: a weighted graph, a Disjoint Set Union structure, and a small city connectivity example.

Included in this checkpoint:

- adjacency-list storage for directed and undirected weighted graphs;
- district IDs from `0` to `V-1`, with endpoint validation;
- DSU `find` and `union`, using path compression and union by size;
- connected-component counting for an undirected road network;
- a console example and focused correctness tests.

MST construction, directed connectivity algorithms, shortest paths, and the experimental report are outside this checkpoint's scope.

## Running

Use JDK 17. Both `java` and `javac` must be on PATH, or set `JAVA_HOME` to the JDK directory. No external Java libraries are required. Run commands in the repository root.

PowerShell:

```powershell
./run.ps1 demo
./run.ps1 test
```

If local scripts are blocked, use `powershell -ExecutionPolicy Bypass -File ./run.ps1 test` for that invocation.

Git Bash, Linux or macOS:

```bash
bash run.sh demo
bash run.sh test
```

The scripts compile sources and tests into `build/`, which is ignored by Git.

## Structure

```text
src/
  app/CityPlanner.java
  graph/Edge.java
  graph/WeightedGraph.java
  graph/Metrics.java
  dsu/DisjointSetUnion.java
tests/
  FoundationTests.java
run.ps1
run.sh
README.md
```

## Graph representation

A vertex represents a district, and an edge represents a road. Each edge stores its start district, end district and integer weight. The weight can later be interpreted as a construction cost or travel distance.

`WeightedGraph` stores one logical edge per `addEdge` call. For an undirected road, it adds an adjacency entry at each endpoint, so both districts can access the road. For a directed connection, it adds only the outgoing entry. Isolated vertices are retained even when they have no roads. Returned lists cannot be changed directly by callers.

The graph uses `O(V + E)` space. Adding an edge takes amortized `O(1)` time; iterating over one district's roads takes `O(degree(v))` time. Parallel edges and self-loops are supported by the graph API, though the console example does not need them.

## DSU implementation

Each district starts in its own set. `find` returns the representative of a set and compresses the visited path. `union` finds the two representatives and attaches the smaller tree under the larger tree. If the districts are already in the same set, it returns `false` and leaves the component count unchanged.

Initialization takes `O(V)` time and space. With union by size and path compression, `find` and `union` take `O(alpha(V))` amortized time. The recursive find uses at most `O(log V)` stack space because union by size bounds the tree height before compression. Counters track find calls, union calls, followed parent links and changed parent links.

DSU answers connectivity questions for the undirected example. It does not determine directed reachability or strongly connected components.

## Example

The demo contains six districts and three roads: `0--1`, `1--2`, and `3--4`. District `5` is isolated. Its connected groups are `{0,1,2}`, `{3,4}` and `{5}`.

Expected output:

```text
Districts: 6
Roads: 3
Roads from district 1:
  1 -> 0 (weight 4)
  1 -> 2 (weight 2)
Connected components: 3
All districts connected: false
Districts 0 and 2 connected: true
Districts 0 and 4 connected: false
District 5 has no roads: true
```

## Testing

`FoundationTests` runs without a testing library. It throws on failure, so a failing check causes a nonzero exit status. The checks cover graph storage and direction, isolated vertices, invalid inputs, read-only lists, DSU component counts, repeated unions and the effect of path compression.

Checked with JDK 17: **8 cases and 64 checks passed**.

## Next checkpoints

1. Add Kruskal and priority-queue Prim, and compare their minimum spanning tree weights.
2. Add topological ordering, cycle detection and SCC decomposition for directed graphs.
3. Add Dijkstra with distances, reconstructed paths and unreachable-vertex handling.
4. Run seeded experiments at the required sizes, record five timed repetitions, and complete the README with result tables, plots and discussion.
