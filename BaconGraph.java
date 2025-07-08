import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Bacon Graph constructs actors and movies graph from
 * inputted files, used in bacon game
 *
 * @author Shad Hassan, Dartmouth CS10, Winter 2025
 *
 */
public class BaconGraph {

    //initialize all the Maps and Graphs used
    public Map<String, String> actorIDtoName;
    public Map<String, String> movieIDtoName;
    public Map<String, List<String>> movieActors;
    public Graph<String, Set<String>> actorMovieGraph;

    //constructor takes in file names for actor files,
    //movie files, and movie actor files and creates all
    //map and final graph with helpers
    public BaconGraph(String actorFile, String movieFile, String movieActor) throws Exception {

        //all helpers throw exceptions so method needs to a try catch to catch any
        //exceptions thrown
        try {
            actorIDtoName = new HashMap<>();
            movieIDtoName = new HashMap<>();
            movieActors = new HashMap<>();
            actorMovieGraph = new AdjacencyMapGraph<>();
            fileToMap(actorFile, actorIDtoName);
            fileToMap(movieFile, movieIDtoName);
            fileToMoveActorMap(movieActor, movieActors);
            mapToGraph(movieActors, actorMovieGraph);

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    //helper method for constructor that takes in a file name and fills a map
    //with file contents
    public void fileToMap (String fileName, Map<String, String> map) throws Exception {

        //read file
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String line;
        try {
            //go line by line in file while there are still lines in a file
            //process each line and fill the map
            while ((line = input.readLine()) != null){
                String[] lineList = line.split("\\|");
                String id = lineList[0];
                String name = lineList[1];
                map.put(id, name);
            }
        }
        //throw an exeption if one is caught by reading inputed file
        catch (Exception e) {
            throw new Exception(e);
        }

        //close buffered reader
        input.close();
    }

    //helper method to fill a movie-actor map, has more precautions when a construction map
    public void fileToMoveActorMap(String fileName, Map<String, List<String>> map) throws Exception{

        //open buffered reader on an inputed file
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String line;
        try {
            //read every line while line is not null
            //process every line
            while((line = input.readLine()) != null){
                String[] lineList = line.split("\\|");
                String movieID = lineList[0];
                String actorID = lineList[1];
                if(!map.containsKey(movieID)){
                    map.put(movieID, new ArrayList<>());
                }
                map.get(movieID).add(actorID);
            }
        }
        //throw an exeption if one is caught by reading inputed file
        catch (Exception e) {
            throw new Exception(e);
        }

        //close buffered reader
        input.close();
    }

    //helper method that takes in a map of movies and its actors and creates a graph will all connections
    public void mapToGraph(Map<String, List<String>> movieActorsMap, Graph<String, Set<String>> graph){

        //add every actor in file to graph as a vertex
        for(String actorID: actorIDtoName.keySet()){
            graph.insertVertex(actorIDtoName.get(actorID));
        }

        //for every movie loop over all its actors and connect them to each other
        //creating an edge (set) if its first connection between the two.
        for(String movieID: movieActorsMap.keySet()){
            String movieName = movieIDtoName.get(movieID);
            List<String> listOfActor = movieActorsMap.get(movieID);
            for(int i = 0; i < listOfActor.size(); i ++){
                for(int j = i + 1; j < listOfActor.size(); j++){
                    String actor1 = actorIDtoName.get(listOfActor.get(i));
                    String actor2 = actorIDtoName.get(listOfActor.get(j));
                    if(!graph.hasEdge(actor2, actor1)){
                        graph.insertUndirected(actor1, actor2, new HashSet<String>());
                    }
                    graph.getLabel(actor1,actor2).add(movieName);
                }
            }
        }
    }

    //main tests the class
    public static void main(String[] args) throws Exception {
        BaconGraph test = new BaconGraph("PS4/actorsTest.txt","PS4/moviesTest.txt","PS4/movie-actorsTest.txt");
        //BaconGraph test = new BaconGraph("PSet4/actors.txt","PSet4/movies.txt","PSet4/movie-actors.txt");

        System.out.println(test.movieIDtoName);
        System.out.println(test.actorIDtoName);
        System.out.println(test.actorMovieGraph);
        String center = "Kevin Bacon";
        Graph<String,Set<String>> bfs = GraphLib.bfs(test.actorMovieGraph,center);
        System.out.println("\nlets bfs\n");
        System.out.println(bfs);
        String actor = "Charlie";
        List<String> path = GraphLib.getPath(bfs,actor);
        System.out.println("\nlets find path\n");
        System.out.println(path+"\n");
        for(int i = 0; i<path.size()-1;i++){
            System.out.println(path.get(i) + " was in " + test.actorMovieGraph.getLabel(path.get(i),path.get(i+1)) + " with "+path.get(i+1));
        }
        //System.out.println("\nmissing:"+BaconGraphLib.missingVertices(test.actorMovie,bfs)+"\n");
        System.out.println(GraphLib.averageSeparation(bfs,center));

    }

}

