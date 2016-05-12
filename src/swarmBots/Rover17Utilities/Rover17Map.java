package swarmBots.Rover17Utilities;

import common.MapTile;
import enums.Science;
import enums.Terrain;

import java.util.*;

public class Rover17Map {
    private GraphRepresentation graph;
    private int[][] terrainMap;
    private int mapHeight = 500;
    private int mapWidth = 500;

    public Rover17Map(){
        graph = new GraphRepresentation();
        terrainMap = new int[mapHeight][mapWidth];
    }

    public void setTerrainMap(int[][] tMap){
        terrainMap = tMap;
    }

    public int[][] getTerrainMap(){
        return terrainMap;
    }

    public void setGraph(GraphRepresentation graph){
        this.graph = graph;
    }
    public GraphRepresentation getGraph(){
        return graph;
    }


    // method to update int array map
    public void updateMap(int x, int y, MapTile[][] scanMapTiles) throws IndexOutOfBoundsException{
        //iterate through the rover's vision
        int curX, curY;
        for (int i=0;i<scanMapTiles.length;i++) {
            for (int j=0;j<scanMapTiles.length;j++) {
                //the actual coordinates we are updating on the main map
                curX = x-5+i;
                curY = y-5+j;

                //Create Node with coordinates
                Node tempNode = new Node(new Coordinates(curX, curY));

                if (curX < 0 || curY < 0){
                    //no need to create nodes at negative spots
                    continue;
                }
                else if (scanMapTiles[i][j].getTerrain() == Terrain.NONE ||
                        scanMapTiles[i][j].getTerrain() == Terrain.ROCK ||
                        scanMapTiles[i][j].getTerrain() == Terrain.SAND) {
                    tempNode.setPassable(false);
                    if (scanMapTiles[i][j].getTerrain() == Terrain.NONE){
                        tempNode.setTerrain("NONE");
                        terrainMap[curX][curY] = -1;
                    } else if (scanMapTiles[i][j].getTerrain() == Terrain.ROCK){
                        tempNode.setTerrain("ROCK");
                        terrainMap[curX][curY] = 2;
                    } else {
                        tempNode.setTerrain("SAND");
                        terrainMap[curX][curY] = 2;
                    }
                    graph.addNode(tempNode);
                    System.out.println(graph);
                } else{
                    terrainMap[curX][curY] = 1;
                    graph.addNode(tempNode);
                    if (i != 0 && curX != 0) {
                        Coordinates temp = new Coordinates(curX-1, curY);
                        if (graph.getNodes().get(graph.getNodes().indexOf(new Node(temp))).getPassable()) {
                            Edge fromCurrent = new Edge(tempNode, new Node(temp), 1);
                            Edge toCurrent = new Edge(new Node(temp), tempNode, 1);
                            graph.addEdge(fromCurrent);
                            graph.addEdge(toCurrent);
                        }
                    }
                    if (j != 0 && curY != 0) {
                        Coordinates temp = new Coordinates(curX, curY-1);
                        if (graph.getNodes().get(graph.getNodes().indexOf(new Node(temp))).getPassable()) {
                            graph.addEdge(new Edge(tempNode, new Node(temp), 1));
                            graph.addEdge(new Edge(new Node(temp), tempNode, 1));
                        }
                    }
                }
                if (scanMapTiles[i][j].getScience() == Science.MINERAL) {
                    graph.addSciences(tempNode);
                }
            }
        }
    }

    //x and y are starting coordinates on map
    public String makeDecision(int x, int y, ArrayList<String> possibleMoves){
        String decision = "nothing";
        int discoveredNodes = 0;
        int startingX = x-5;
        int endingX = x+5;
        int startingY = y-5;
        int endingY = y+5;
        int moves = 1;
        while (decision.equals("nothing")){
            for (String direction : possibleMoves) {
                int tempCounter = 0;
                switch (direction) {
                    case "S":
                        if (startingY+moves > mapHeight) {
                            continue;
                        } else {
                            for (int i = 0; i < 11; i++) {
                                if (startingX + i < 0 || startingX + i > mapWidth)
                                    continue;
                                else if (getTerrainMap()[endingY+moves][startingX + i] == 0) {
                                    tempCounter++;
                                }
                            }
                            if (tempCounter > discoveredNodes) {
                                discoveredNodes = tempCounter;
                                decision = "S";
                            }
                        }
                        break;
                    case "W":
                        if (startingX-moves < 0) {
                            continue;
                        } else {
                            for (int i = 0; i < 11; i++) {
                                if (startingY + i < 0 || startingY + i > mapHeight)
                                    continue;
                                else if (getTerrainMap()[startingY+i][startingX-moves] == 0) {
                                    tempCounter++;
                                }
                            }
                            if (tempCounter > discoveredNodes) {
                                discoveredNodes = tempCounter;
                                decision = "W";
                            }
                        }
                        break;
                    case "E":
                        if (endingX+moves > mapWidth) {
                            continue;
                        } else {
                            for (int i = 0; i < 11; i++) {
                                if (startingY + i < 0 || startingY + i > mapHeight)
                                    continue;
                                else if (getTerrainMap()[startingY+i][endingX+moves] == 0) {
                                    tempCounter++;
                                }
                            }
                            if (tempCounter > discoveredNodes) {
                                discoveredNodes = tempCounter;
                                decision = "E";
                            }
                        }
                        break;
                    case "N":
                        if (startingY-moves < 0) {
                            continue;
                        } else {
                            for (int i = 0; i < 11; i++) {
                                if (startingX + i < 0 || startingX + i > mapWidth)
                                    continue;
                                else if (getTerrainMap()[startingY-moves][startingX + i] == 0) {
                                    tempCounter++;
                                }
                            }
                            if (tempCounter > discoveredNodes) {
                                discoveredNodes = tempCounter;
                                decision = "N";
                            }
                        }
                        break;
                }
            }
            moves++;
        }

        return decision;
    }



    @Override
    public String toString(){
        return graph.toString();
    }


    /*
    ASTAR MOVED HERE
     */
    public LinkedList<Edge> search(GraphRepresentation graph, Node source, Node dist) {
        Map<Node, Node> parents = new HashMap<>();
        Map<Node, Integer> distances = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        LinkedList<Edge> result = new LinkedList<>();

        PriorityQueue<Node> queue = new PriorityQueue<>(1, new Comparator<Node>() {
            public int compare(Node x, Node y){
                double distX = distances.get(x) + heuristic(x, dist);
                double distY = distances.get(y) + heuristic(y, dist);
                if (distX > distY)
                    return 1;
                else if (distY > distX)
                    return -1;
                else {
                    if(x.getCoord().getY() < y.getCoord().getY())
                        return -1;
                    if (x.getCoord().getX() > y.getCoord().getX() &&
                            Math.abs(x.getCoord().getY()-y.getCoord().getY()) == 1)
                        return 1;
                    else
                        return -1;
                }

            }
        });

        distances.put(source, 0);
        parents.put(source, null);
        queue.add(source);

        while (!queue.isEmpty()) {
            Node temp = queue.poll();

            if (temp.equals(dist)) {
                break;
            }

            for(Node node:graph.neighbors(temp)) {
                if (!visited.contains(node)) {
                    parents.put(node, temp);
                    distances.put(node, distances.get(temp) + 1);
                    visited.add(node);
                    queue.add(node);
                }
                else {
                    int newDistance = distances.get(temp) + 1;
                    if (distances.get(node) > newDistance) {
                        distances.remove(node);
                        parents.remove(node);
                        distances.put(node, newDistance);
                        parents.put(node, temp);
                        queue.add(node);
                    }
                }

            }
        }

        Node currentNode = dist;

        while (!currentNode.equals(source)) {
            Node parent = parents.get(currentNode);

            if (parent != null) {
                result.add(new Edge(parent, currentNode, graph.distance(parent, currentNode)));
            }

            currentNode = parents.get(currentNode);
        }

        Collections.reverse(result);

        return result;
    }


    public double heuristic(Node current, Node end){
        Coordinates curXY = current.getCoord();
        Coordinates endXY = end.getCoord();
        double doubleX = Math.pow((double)(curXY.getX() - endXY.getX()),2);
        double doubleY = Math.pow((double)(curXY.getY() - endXY.getY()),2);
        return Math.sqrt(doubleX + doubleY);
    }

    public ArrayList<String> getMoves(LinkedList<Edge> edges){
        ArrayList<String> moves = new ArrayList();
        for (int i = 0; i<edges.size(); i++) {
            Node from = edges.get(i).getFrom();
            Node to = edges.get(i).getTo();
            if (from.getCoord().getX() < to.getCoord().getX())
                moves.add("E");
            else if (from.getCoord().getX() > to.getCoord().getX())
                moves.add("W");
            else if (from.getCoord().getY() < to.getCoord().getY())
                moves.add("S");
            else
                moves.add("N");
        }
        return moves;

    }

}
