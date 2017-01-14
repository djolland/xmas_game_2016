package tilemap;

import objects.GameObject;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/2/2017.
 */
public class GameTile extends GameObject {

    private int xPos, yPos;
    private float tileHeight, tileWidth;
    private boolean isBlocked, isCatSpawn, isPresentSpawn, isTarget;

    public GameTile(int xPos, int yPos, float tileHeight, float tileWidth){
        this.xPos = xPos;
        this.yPos = yPos;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.isBlocked = false;
        this.isCatSpawn = false;
        this.isPresentSpawn = false;
        this.isTarget = false;
    }

    public GameTile(int xPos, int yPos, float tileHeight, float tileWidth, boolean isBlocked){
        this.xPos = xPos;
        this.yPos = yPos;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.isBlocked = isBlocked;
        this.isCatSpawn = false;
        this.isPresentSpawn = false;
        this.isTarget = false;
    }

    // Getters

    @Override
    public float getX(){return xPos * tileWidth;}

    @Override
    public float getY(){return yPos * tileHeight;}

    @Override
    public float getHeight(){return tileHeight;}

    @Override
    public float getWidth(){return tileWidth;}

    public boolean isBlocked(){return this.isBlocked;}

    public boolean isCatSpawn(){return isCatSpawn;}

    public boolean isPresentSpawn(){return isPresentSpawn;}

    public boolean isTarget(){return isTarget;}

    // Setters

    @Override
    public void setX(float xPos){this.xPos = (int)(xPos / tileWidth);}

    @Override
    public void setY(float yPos){this.yPos = (int)(yPos / tileHeight);}

    @Override
    public void setPosition(float xPos, float yPos) {
        this.xPos = (int)(xPos / tileWidth);
        this.yPos = (int)(yPos / tileHeight);
    }

    public void setBlocked(boolean blocked){this.isBlocked = blocked;}

    public void setCatSpawn(boolean catSpawn){this.isCatSpawn = catSpawn;}

    public void setPresentSpawn(boolean presentSpawn){this.isPresentSpawn = presentSpawn;}

    public void setTarget(boolean target){this.isTarget = target;}

}
