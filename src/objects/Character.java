package objects;

import org.newdawn.slick.Animation;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/25/2016.
 */
public class Character extends GameObject{

    public enum AnimationDirection {
        UP, DOWN, LEFT, RIGHT
    }

    private Animation characterSprite, upAnim, downAnim, leftAnim, rightAnim;
    private float xPos, yPos;

    public Character(Animation up, Animation down, Animation left, Animation right){
        upAnim = up;
        downAnim = down;
        leftAnim = left;
        rightAnim = right;
        characterSprite = downAnim;
    }

    public void setAnimation(AnimationDirection anim){
        this.characterSprite = getAnimationDirection(anim);
    }

    public void setAnimation(Animation animation){
        characterSprite = animation;
    }

    public void setPosition(float xpos, float yPos){
        this.xPos = xpos;
        this.yPos = yPos;
    }

    public void setX(float xpos){
        this.xPos = xpos;
    }

    public void setY(float yPos){
        this.yPos = yPos;
    }

    public float getX(){
        return xPos;
    }

    public float getY(){
        return yPos;
    }

    public float getHeight(){
        return characterSprite.getHeight();
    }

    public float getWidth(){return characterSprite.getWidth();}

    public Animation getUpAnimation(){
        return upAnim;
    }

    public Animation getDownAnimation(){
        return downAnim;
    }

    public Animation getLeftAnimation(){
        return leftAnim;
    }

    public Animation getRightAnimation(){
        return rightAnim;
    }

    public Animation getCurrentAnimation(){
        return characterSprite;
    }

    public void update(int delta){
        characterSprite.update(delta);
    }

    public void draw(){
        characterSprite.draw(xPos, yPos);
    }

    protected Animation getAnimationDirection(AnimationDirection direction){
        switch (direction){
            case UP:
                return this.upAnim;
            case DOWN:
                return this.downAnim;
            case LEFT:
                return this.leftAnim;
            case RIGHT:
                return this.rightAnim;
            default:
                return this.downAnim;
        }
    }

}
