package swarmBots;

import common.MapTile;
import enums.Science;
import enums.Terrain;

import java.util.ArrayList;
import java.util.Arrays;

public class Rover17Map {
    private int[][] terrainMap;
    private int[][] scienceMap;

    public Rover17Map(){
        terrainMap = new int[500][500];
        scienceMap = new int[500][500];
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

                //Need to check if its out of the bounds of the map array
                //TODO: improve this check
                if (curX < 0 || curY < 0 || curX > 500 || curY > 500){
                    continue;
                }
                if (scanMapTiles[i][j].getTerrain() == Terrain.NONE) {
                    terrainMap[curY][curX] = -1;
                } else if (scanMapTiles[i][j].getTerrain() == Terrain.ROCK) {
                    terrainMap[curY][curX] = 2;
                } else if (scanMapTiles[i][j].getTerrain() == Terrain.SAND) {
                    terrainMap[curY][curX] = 3;
                } else {
                    terrainMap[curY][curX] = 1;
                }
                if (scanMapTiles[i][j].getScience() == Science.MINERAL) {
                    scienceMap[curY][curX] = 1;
                }
            }
        }
    }

    //x and y are starting coordinates on map
    public String makeDecision(int x, int y, ArrayList<String> possibleMoves){
        String decision = "";
        int discoveredNodes = 0;
        int startingX, startingY;
        for (String direction : possibleMoves){
            int tempCounter = 0;
            switch (direction){
                case "W":
                    startingX = x-6;
                    if (startingX < 0){
                        continue;
                    } else {
                        for(int i=0; i<11; i++){
                            if (getTerrainMap()[y-5+i][startingX] == 0){
                                tempCounter++;
                            }
                        }
                        if (tempCounter > discoveredNodes){
                            discoveredNodes = tempCounter;
                            decision = "W";
                        }
                    }
                case "E":
                    startingX = x+6;
                    if (startingX > 500){
                        continue;
                    } else {
                        for(int i=0; i<11; i++){
                            if (getTerrainMap()[y-5+i][startingX] == 0){
                                tempCounter++;
                            }
                        }
                        if (tempCounter > discoveredNodes){
                            discoveredNodes = tempCounter;
                            decision = "E";
                        }
                    }
                case "N":
                    startingY = y-6;
                    if (startingY < 0){
                        continue;
                    } else {
                        for(int i=0; i<11; i++){
                            if (getTerrainMap()[startingY][x-5+i] == 0){
                                tempCounter++;
                            }
                        }
                        if (tempCounter > discoveredNodes){
                            discoveredNodes = tempCounter;
                            decision = "N";
                        }
                    }
                case "S":
                    startingY = y+6;
                    if (startingY > 500){
                        continue;
                    } else {
                        for(int i=0; i<11; i++){
                            if (getTerrainMap()[startingY][x-5+i] == 0){
                                tempCounter++;
                            }
                        }
                        if (tempCounter > discoveredNodes){
                            discoveredNodes = tempCounter;
                            decision = "S";
                        }
                    }
            }
        }

        return decision;
    }

    @Override
    public String toString(){
        String mapsAsStrings = Arrays.deepToString(terrainMap) + "\n" + Arrays.deepToString(scienceMap);
        return mapsAsStrings;
    }
}
