package swarmBots.Rover17Utilities;

import common.MapTile;
import enums.Science;
import enums.Terrain;

import java.util.ArrayList;
import java.util.Arrays;

public class Rover17Map {
    private GraphRepresentation graph;
    private int[][] terrainMap;
    private int[][] scienceMap;
    private int mapHeight = 500;
    private int mapWidth = 500;

    public Rover17Map(){
        graph = new GraphRepresentation();
        terrainMap = new int[mapHeight][mapWidth];
        scienceMap = new int[mapHeight][mapWidth];
    }

    public void setTerrainMap(int[][] tMap){
        terrainMap = tMap;
    }

    public void setScienceMap(int[][] sMap){
        scienceMap = sMap;
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

    public int[][] getScienceMap(){
        return scienceMap;
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

                //Create Node
                Node tempNode = new Node(new Coordinates(curX, curY));


                //Need to check if its out of the bounds of the map array
                //TODO: improve this check
                if (curX < 0 || curY < 0 || curX > mapWidth || curY > mapHeight){
                    tempNode.setPassable(false);
                    continue;
                }
                if (scanMapTiles[i][j].getTerrain() == Terrain.NONE ||
                        scanMapTiles[i][j].getTerrain() == Terrain.ROCK ||
                        scanMapTiles[i][j].getTerrain() == Terrain.SAND) {
                    tempNode.setPassable(false);
                } else{
                    if (j != 0) {
                        Coordinates temp = new Coordinates(j-1, i);
                        if (graph.getNodes().get(graph.getNodes().indexOf(new Node(temp))).getPassable()) {
                            Edge fromCurrent = new Edge(tempNode, new Node(temp), 1);
                            Edge toCurrent = new Edge(new Node(temp), tempNode, 1);
                            graph.addEdge(fromCurrent);
                            graph.addEdge(toCurrent);
                        }
                    }
//                    if (i != 0) {
//                        Coordinates temp = new Coordinates(j, i-1);
//                        if (graph.getNodes().get(graph.getNodes().indexOf(new Node(temp))).getPassable()) {
//                            Node upNode = ipCoordinate.get(new IntPair(j, i-1));
//                            graph.addEdge(new Edge(tempNode, upNode, 1));
//                            graph.addEdge(new Edge(upNode, tempNode, 1));
//                        }
//                    }
                }
                if (scanMapTiles[i][j].getScience() == Science.MINERAL) {
                    graph.addSciences(tempNode);
                }
                graph.addNode(tempNode);
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
        String mapsAsStrings = Arrays.deepToString(terrainMap) + "\n" + Arrays.deepToString(scienceMap);
        return mapsAsStrings;
    }
}
