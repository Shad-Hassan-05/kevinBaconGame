import java.util.*;
import java.util.Set;

/**
 * Bacon Game class and tester to run Bacon Game
 *
 * @author Shad Hassan Winter 2025
 *
 */
public class BaconGame {

    String cou; //Center Of the Universe

    //graph of all actors
    BaconGraph graph;

    //shortest path graph
    Graph<String, Set<String>> bfs;

    //constructor creates a new graph and shortest path graph from the center of the universe to
    //all other actors
    public BaconGame() throws Exception {
        cou = "Kevin Bacon";

        graph = new BaconGraph("PS4/actors.txt", "PS4/movies.txt", "PS4/movie-actors.txt");
        bfs = GraphLib.bfs(graph.actorMovieGraph, cou);
    }

    //check to see if an actor is in graph
    public boolean isValidActor(String actor){
        if(!graph.actorIDtoName.containsValue(actor)){
            System.out.println(actor + " is not in the list of actors");
            return false;
        }
        return true;
    }

    //returns a list of n actors with the highest or lowest connectedness
    public List<String> avgSeperationList(int num){
        Map<String, Double> sepMap = new HashMap<>();

        //fill seperation map
        for(String actorID: graph.actorIDtoName.keySet()){
            String actorName = graph.actorIDtoName.get(actorID);
            bfs = GraphLib.bfs(graph.actorMovieGraph, actorName);

            if (bfs.hasVertex("Kevin Bacon")) {
                sepMap.put(actorName, GraphLib.averageSeparation(bfs, actorName));
            }
        }

        //get shortest path graph from center of universe
        bfs = GraphLib.bfs(graph.actorMovieGraph, cou);
        List<String> sortedList = new ArrayList<>(sepMap.keySet());
        //if input is negative sort lowest to highest
        if (num > 0) {
            sortedList.sort(Comparator.comparingDouble(sepMap::get));
        }
        //else sort highest to lowest
        else {
            sortedList.sort((V1, V2) -> Double.compare(sepMap.get(V2), sepMap.get(V1)));
        }


        //truncate sorted list to on n actors
        List<String> sizedList = new ArrayList<>();
        int size = sortedList.size();
        for (int i = 0; i < Math.abs(num); i++) {

            //makes sure smallest list between
            //sorted list size and num is created
            if(i > size){
                break;
            }

            //copy over ith element of sorted list to ith index
            sizedList.add(sortedList.get(i));
        }

        return sizedList;
    }


    //method takes the degree of all vertices(actors) in graph and reruns a list
    //sorted lowest degree to highest.
    public List<String> lowToHighDegree(int low, int high){
        List<String> temp = new ArrayList<>();
        for(String actorId: graph.actorIDtoName.keySet()){
            String actorName = graph.actorIDtoName.get(actorId);
            int actorDegree = graph.actorMovieGraph.inDegree(actorName);
            if(actorDegree >= low && actorDegree <= high){
                temp.add(actorName);
            }
        }

        //sort the temp list in increasing order
        temp.sort((V1,V2)->Integer.compare(graph.actorMovieGraph.inDegree(V1),
                graph.actorMovieGraph.inDegree(V2)));
        return temp;
    }

    //method returns a list sorted lowest to highest vertices
    //bases degree of separation from the center of the universe
    public List<String> lowHighSortSeparation(int low, int high){

        //get shortest path tree from center of universe
        bfs = GraphLib.bfs(graph.actorMovieGraph, cou);

        //create a new list and add all vertices
        //with path in the shortest path tree in
        //the bounds of low and high
        List<String> temp = new ArrayList<>();
        for (String actor : bfs.vertices()){
            if(GraphLib.getPath(bfs, actor) != null){
                int size = GraphLib.getPath(bfs, actor).size();
                if(size >= low && size <= high){
                    temp.add(actor);
                }
            }
        }

        //shot list using an anonymous function
        temp.sort((V1,V2)->Double.compare(GraphLib.getPath(bfs,V1).size()
                ,GraphLib.getPath(bfs,V2).size()));
        return temp;
    }

    public void scanCommand(){
        /*
        c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
d <low> <high>: list actors sorted by degree, with degree between low and high
i: list actors with infinite separation from the current center
p <name>: find path from <name> to current center of the universe
s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high
u <name>: make <name> the center of the universe
q: quit game
         */
        System.out.println("Commands for Bacon Game:");
        System.out.println("\tc <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                "\td <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                "\ti: list actors with infinite separation from the current center\n" +
                "\tp <name>: find path from <name> to current center of the universe\n" +
                "\ts <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                "\tu <name>: make <name> the center of the universe\n" +
                "\tq: quit game\n");
        Scanner scanner = new Scanner(System.in);


        //take in the input from keyboard
        String[] command;
        boolean q = false;
        while (!q) {

            //prompt user to make an input
            System.out.print("Enter a command: ");
            String rawScan = scanner.nextLine();

            //split input
            command = rawScan.split(":");

            //if what is inputed is only one character long
            //check what command it is
            if(command.length==1){

                //q quits the game, ends the while loop
                if (command[0].equals( "q")) {
                    q=true;
                    System.out.println("\n goodbye.");
                }

                //prints out missing actors by calling graph lib function
                else if (command[0].equals( "i")) {
                    System.out.println("\n The actors with infinite separation are: " +
                            GraphLib.missingVertices(graph.actorMovieGraph ,bfs) + "\n");
                }

                //if the single input doesn't match any of the okay inputs return error
                else{
                    System.out.println("\n" + rawScan + " is an invalid input.\n");
                }
            }
            //repeat same process for inputs with two characters
            else if(command.length==2){

                if (command[0].equals( "c")) {
                    System.out.println("\n" + avgSeperationList(Integer.parseInt(command[1])) + "\n");
                }
                else if (command[0].equals( "p")) {
                    if(isValidActor(command[1])) {
                        List<String> path = GraphLib.getPath(bfs, command[1]);
                        System.out.println("\n"+ command[1] + "is" +(path.size()-1) +
                                " step(s) to get from " + command[1] + ":");
                        for (int i = 0; i < path.size() - 1; i++) {
                            System.out.println("\t" + path.get(i) + " was in " +
                                    graph.actorMovieGraph.getLabel(path.get(i), path.get(i + 1)) + " with " + path.get(i + 1));
                        }
                        System.out.println("\n");
                    }

                }
                else if (command[0].equals( "u")) {

                    if(isValidActor(command[1])) {
                        cou = command[1];
                        bfs = GraphLib.bfs(graph.actorMovieGraph, cou);
                        System.out.println("\nThe center of universe has been updated to: " + command[1] + "\n");
                    }

                }
                else{
                    System.out.println("\n" + rawScan + " is not a valid input.\n");
                }
            }

            //repeat the same process for 3 inputted characters
            else if(command.length==3){
                if (command[0].equals( "d")) {
                    System.out.println("\n" + lowToHighDegree(Integer.parseInt(command[1]),Integer.parseInt(command[2]))+"\n");
                }
                else if (command[0].equals( "s")) {
                    System.out.println("\n actors sorted by non-infinite separation" +
                                    " from the current center, with separation between low and high. \n");
                    System.out.println("\n"+lowHighSortSeparation(Integer.parseInt(command[1]),Integer.parseInt(command[2]))+"\n");
                }else{
                    System.out.println("\n" + rawScan + " is not a valid input.\n");
                }
            }

            //input matches none of the above cases then return error message
            else{
                System.out.println("\n" + rawScan + " is not a valid input.\n");
            }

        }
        // Close the scanner
        scanner.close();
    }

    //main to run game
    public static void main(String[] args) throws Exception {
        BaconGame test = new BaconGame();
        test.scanCommand();
    }
}









