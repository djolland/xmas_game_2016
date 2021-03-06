package tilemap;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/2/2017.
 */
public class GameTileMap {

    private GameTile[][] gameTiles;
    private float tileHeight, tileWidth;
    private int mapWidth, mapHeight;

    public GameTileMap(int xSize, int ySize, float tileWidth, float tileHeight){
        this.gameTiles = new GameTile[xSize][ySize];
        this.mapWidth = xSize;
        this.mapHeight = ySize;
        for (int xAxis=0; xAxis < xSize; xAxis++) {
            for (int yAxis = 0; yAxis < ySize; yAxis++) {
                gameTiles[xAxis][yAxis] = new GameTile(xAxis, yAxis, tileHeight, tileWidth);
            }
        }
    }

    public GameTile getTile(int xPos, int yPos){return gameTiles[xPos][yPos];}

    public int getWidth(){return mapWidth;}

    public int getHeight(){return mapHeight;}

}
