package objects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Sound;

import static java.lang.Math.abs;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/25/2016.
 */
public class ChasingCat extends Character{

    private boolean isAlive;
    private boolean isDying;
    private Animation dyingAnim;
    private GameObject targetObject;
    private float targetX, targetY, catSpeed;
    private Sound deathSound;

    public ChasingCat(Animation up, Animation down, Animation left, Animation right, Animation dyingAnim, Sound deathSound,
                      GameObject targetObject) {
        super(up, down, left, right);
        this.dyingAnim = dyingAnim;
        this.dyingAnim.setLooping(false);
        this.deathSound = deathSound;
        this.isAlive = false;
        this.isDying = false;
        this.catSpeed = 0.1f;
        this.targetObject = targetObject;

    }

    public boolean isAlive(){
        return isAlive;
    }

    public boolean isDying(){
        return isDying;
    }

    public void kill(){
        isDying = true;
        this.setAnimation(dyingAnim);
    }

    public void setAlive(){
        this.setAnimation(AnimationDirection.DOWN);
        isAlive = true;
    }

    public void setTargetPosition(float xPos, float yPos){
        targetX = xPos;
        targetY = yPos;
    }

    public void setTargetX(float xPos){
        targetX = xPos;
    }

    public void setTargetY(float yPos){
        targetY = yPos;
    }

    @Override
    public void update(int delta) {
        // If the cat is done dying reset the death animation
        if (isDying() && this.getCurrentAnimation().isStopped()) {
            isDying = false;
            isAlive = false;
            this.dyingAnim.restart();
        }
        else {
            /* Defining Cat Motion - Follows after target*/
            // If cat has reached its target... kill it!
            if (this.isColliding(targetObject)) {
                this.kill();
                deathSound.play();
            } else {
                // When traveling diagonally maintain last animation direction
                if (abs(targetX - this.getX()) + 1 > abs(targetY - this.getY()) &&
                        abs(targetX - this.getX()) - 1 < abs(targetY - this.getY())) {
                    if (abs(targetX - this.getX()) > abs(targetY - this.getY())) {
                        if (targetX > this.getX() + 1) {
                            this.setX(this.getX() + delta * catSpeed);
                        } else {
                            this.setX(this.getX() - delta * catSpeed);
                        }
                    } else {
                        if (targetY > this.getY() + 1) {
                            this.setY(this.getY() + delta * catSpeed);
                        } else {
                            this.setY(this.getY() - delta * catSpeed);
                        }
                    }
                }
                // Left right travel
                else if (abs(targetX - this.getX()) > abs(targetY - this.getY())) {
                    if (targetX > this.getX() + 1) {
                        this.setAnimation(Character.AnimationDirection.RIGHT);
                        this.setX(this.getX() + delta * catSpeed);
                    } else {
                        this.setAnimation(Character.AnimationDirection.LEFT);
                        this.setX(this.getX() - delta * catSpeed);
                    }
                }
                // Up down travel
                else {//if (abs(targetX-cat.getX()) < abs(targetY-yCat)) {
                    if (targetY > this.getY() + 1) {
                        this.setAnimation(Character.AnimationDirection.DOWN);
                        this.setY(this.getY() + delta * catSpeed);
                    } else {
                        this.setAnimation(Character.AnimationDirection.UP);
                        this.setY(this.getY() - delta * catSpeed);
                    }
                }
            }
            super.update(delta);
        }
    }
}
