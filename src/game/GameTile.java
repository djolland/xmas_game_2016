package game;

import objects.GameObject;

/**
 * @author Daniel J. Holland
 * @version 1.0
 *          Created on 1/2/2017.
 */
public class GameTile extends GameObject {

    private int xPos, yPos;
    private float tileHeight, tileWidth;
    private boolean isBlocked, isCatSpawn, isPlayerSpawn;

    public GameTile(int xPos, int yPos, float tileHeight, float tileWidth){
        this.xPos = xPos;
        this.yPos = yPos;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.isBlocked = false;
        this.isCatSpawn = false;
        this.isPlayerSpawn = false;
    }

    public GameTile(int xPos, int yPos, float tileHeight, float tileWidth, boolean isBlocked){
        this.xPos = xPos;
        this.yPos = yPos;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.isBlocked = isBlocked;
        this.isCatSpawn = false;
        this.isPlayerSpawn = false;
    }

    // Getters

    @Override
    public float getX(){
        return xPos;
    }

    @Override
    public float getY(){
        return yPos;
    }

    @Override
    public float getHeight(){
        return tileHeight;
    }

    @Override
    public float getWidth(){return tileWidth;}

    public boolean isBlocked(){return this.isBlocked;}

    public boolean isCatSpawn(){return isCatSpawn;}

    public boolean isPlayerSpawn(){return isPlayerSpawn;}

    // Setters

    @Override
    public void setX(float xPos){
        this.xPos = (int)(xPos % tileWidth);
    }

    @Override
    public void setY(float yPos){
        this.yPos = (int)(yPos % tileHeight);
    }

    @Override
    public void setPosition(float xPos, float yPos) {
        this.xPos = (int)(xPos % tileWidth);
        this.yPos = (int)(yPos % tileHeight);
    }

    public void setBlocked(boolean blocked){this.isBlocked = blocked;}

    public void setCatSpawn(boolean catSpawn){this.isCatSpawn = catSpawn;}

    public void setPlayerSpawn(boolean playerSpawn){this.isPlayerSpawn = playerSpawn;}
}
