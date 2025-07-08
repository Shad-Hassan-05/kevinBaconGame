import java.util.*;
import java.util.Queue;

/**
 * Library for graph analysis
 *
 *
 * @author Shad Hassan, Dartmouth CS10, finsihed code provided by Professor Pierson
 */
public class GraphLib {
    /**
     * Takes a random walk from a vertex, up to a given number of steps
     * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
     * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
     * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
     * @param g		graph to walk on
     * @param start	initial vertex (assumed to be in graph)
     * @param steps	max number of steps
     * @return		a list of vertices starting with start, each with an edge to the sequentially next in the list;
     * 			    null if start isn't in graph
     */
    public static <V,E> List<V> randomWalk(Graph<V,E> g, V start, int steps) {
        //count serves as a countdown for steps left to take
        int count = steps;
        //random object
        Random random = new Random();
        //create a list to track a path of vertices
        ArrayList<V> randomWalkPath = new ArrayList<>();

        //if start vertex exists in map start with start vertex
        if(g.hasVertex(start)){
            V currentNode = start;
            randomWalkPath.add(start);

            //loop over a map choosing a random neighbor to visit for each step
            //loop step remaining to take is greater than zero
            while (count > 0){
                int outDeg = g.outDegree(currentNode);

                //if there exist at least one vertex with and edge from current
                //vertex then continue to add at random one of the next vertices
                if (outDeg>0){

                    //choose random int bounded by outDegree and 0
                    int rand = random.nextInt(outDeg);

                    int i = -1;
                    //loop over all neighbors until i = random int then break
                    //leaving random neighbor as currentNode
                    for(V neighbor :g.outNeighbors(currentNode)){
                        i ++;
                        currentNode = neighbor;
                        if(i == rand){
                            break;
                        }

                    }
                    //add neighbor to path
                    randomWalkPath.add(currentNode);
                }
                //decrement steps left to take after every loop of while loop
                count --;
            }
            //return list
            return randomWalkPath;
        }
        //return null if start vertex is not in map.
        return null;
    }

    /**
     * Orders vertices in decreasing order by their in-degree
     * @param g		graph
     * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
     */
    public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
        //create a list of all verticies
        ArrayList<V> sortedInDegreeList = new ArrayList<>();
        for (V vertex : g.vertices()){
            sortedInDegreeList.add(vertex);
        }

        //sort list by each vertex's in degree value.
        sortedInDegreeList.sort((v1,v2) -> g.inDegree(v2) - g.inDegree(v1));

        //return a sorted list
        return sortedInDegreeList;
    }

    //BFS to find shortest path tree for a current center of the universe. Return a path tree as a Graph.
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
        //bfs algorithm
        AdjacencyMapGraph<V, E> returnGraph = new AdjacencyMapGraph<>();
        Queue<V> queue = new LinkedList<>();
        queue.add(source);
        returnGraph.insertVertex(source);
        while(!queue.isEmpty()){
            V u = queue.remove();
            for(V v : g.outNeighbors(u)){
                if(!returnGraph.hasVertex(v)){
                    returnGraph.insertVertex(v);
                    returnGraph.insertDirected(v, u, g.getLabel(u, v));
                    queue.add(v);
                }
            }
        }
        return returnGraph;
    }

    //Given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe.
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        //create path list
        List<V> path = new ArrayList<>();
        V current = v;
        if (tree.outDegree(current) <= 0 ){
            return null;
        }
        while(tree.outDegree(current) > 0){
            path.add(current);
            for(V u: tree.outNeighbors(current)){
                current = u;
                break;
            }
        }
        //add last node to path and reverse the order of the path
        //to get from node to root path.
        path.add(current);
        Collections.reverse(path);
        return path;
    }

    //Given a graph and a subgraph (here shortest path tree),
    //determine which vertices are in the graph but not the subgraph (here, not reached by BFS).
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
        Set<V> missingVerticesSet = new HashSet<>();
        for (V v: graph.vertices()){
            if(!subgraph.hasVertex(v)){
                missingVerticesSet.add(v);
            }
        }
        return missingVerticesSet;

    }

    //Find the average distance-from-root in a shortest path tree. Note: do this without
    //enumerating all the paths! Hint: think tree recursion...
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root){
        if (tree == null || tree.numVertices() == 0) return 0.0;
        double sum = averageHelper( tree, root, 0);
        return sum / (tree.numVertices() - 1.0);
    }

    //helper method recursively totals up all the distances of nodes to root
    public static <V,E> double averageHelper(Graph<V,E> tree, V root, int depth) {
        double sum = depth;
        if(tree.inDegree(root) > 0){
            for (V child : tree.inNeighbors(root)) {
                sum += averageHelper(tree, child, depth + 1);
            }
        }
        return sum;
    }
}