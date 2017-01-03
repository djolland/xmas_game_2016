package objects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Sound;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/26/2016.
 */
public class Present extends GameObject{

    private Animation currentAnimation;
    private Animation visibleAnimation;
    private Animation collectedAnimation;
    private Sound pickupSound;
    private boolean isVisible, isCollected;

    private float xPos;
    private float yPos;

    public Present(Animation visibleAnimation, Animation collectedAnimation, Sound pickupSound){
        this.visibleAnimation = visibleAnimation;
        this.collectedAnimation = collectedAnimation;
        this.collectedAnimation.setLooping(false);
        this.currentAnimation = this.visibleAnimation;
        this.pickupSound = pickupSound;
        this.isVisible = false;
        this.isCollected = false;
    }

    // Setters

    @Override
    public void setX(float xPos){
        this.xPos = xPos;
    }

    @Override
    public void setY(float yPos){
        this.yPos = yPos;
    }

    @Override
    public void setPosition(float xPos, float yPos){
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void setVisible(){
        this.isVisible = true;
        this.isCollected = false;
        this.currentAnimation = visibleAnimation;
    }

    public void collected(){
        isCollected = true;
        pickupSound.play();
        this.currentAnimation = collectedAnimation;
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
    public float getHeight() {
        return currentAnimation.getHeight();
    }

    @Override
    public float getWidth() {
        return currentAnimation.getWidth();
    }

    public Animation getCurrentAnimation(){
        return this.currentAnimation;
    }

    public boolean isVisible(){
        return isVisible;
    }

    public boolean isCollected(){
        return this.isCollected;
    }

    public void playVisibleAnimation(){
        currentAnimation = visibleAnimation;
    }

    public void playCollectedAnimation(){
        currentAnimation = collectedAnimation;
    }

    public void draw(){
        if (currentAnimation == collectedAnimation) {
            currentAnimation.draw(xPos - 17.5f, yPos);
        }
        else {
            currentAnimation.draw(xPos, yPos);
        }
    }

    public void update(int delta){
        if (this.isCollected && this.getCurrentAnimation().isStopped()) {
            isVisible = false;
            this.collectedAnimation.restart();
        }
        else{
            currentAnimation.update(delta);
        }
    }

}
