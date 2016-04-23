package swarmBots;

import common.MapTile;
import enums.Science;
import enums.Terrain;

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
                    terrainMap[curY][curX] = 1;
                } else if (scanMapTiles[i][j].getTerrain() == Terrain.SAND) {
                    terrainMap[curY][curX] = 2;
                }
                if (scanMapTiles[i][j].getScience() == Science.MINERAL) {
                    scienceMap[curY][curX] = 1;
                }
            }
        }
    }

    @Override
    public String toString(){
        String mapsAsStrings = Arrays.deepToString(terrainMap) + "\n" + Arrays.deepToString(scienceMap);
        return mapsAsStrings;
    }
}
