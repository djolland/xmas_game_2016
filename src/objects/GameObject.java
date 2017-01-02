package objects;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/28/2016.
 */
public abstract class GameObject {

    // Getters
    public abstract float getX();
    public abstract float getY();
    public abstract float getHeight();
    public abstract float getWidth();
    // Setters
    public abstract void setX(float xPos);
    public abstract void setY(float yPos);
    public abstract void setPosition(float xPos, float yPos);

    public boolean isColliding(GameObject other){
        return (this.getX() < other.getX() + other.getWidth() && this.getX() + this.getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getHeight() && this.getY() + this.getHeight() > other.getY());

    }

}
