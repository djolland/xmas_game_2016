package objects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Sound;

import static java.lang.Math.abs;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/2/2017.
 */
public class ChasingCharacter extends Character{

    private boolean isAlive;
    private boolean isDying;
    private Animation dyingAnim;
    private Sound deathSound;
    private float characterSpeed;
    private GameObject targetObject;

    public ChasingCharacter(Animation up, Animation down, Animation left, Animation right, Animation dyingAnim,
                            Sound deathSound, float characterSpeed, GameObject targetObject) {
        super(up, down, left, right);
        this.dyingAnim = dyingAnim;
        this.dyingAnim.setLooping(false);
        this.deathSound = deathSound;
        this.isAlive = false;
        this.isDying = false;
        this.characterSpeed = characterSpeed;
        this.targetObject = targetObject;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public boolean isDying(){
        return isDying;
    }

    private float getTargetX(){return targetObject.getX() + (targetObject.getWidth()/2);}

    private float getTargetY(){return targetObject.getY() + (targetObject.getHeight()/2);}

    public void kill(){
        isDying = true;
        this.setAnimation(dyingAnim);
    }

    public void setAlive(){
        this.setAnimation(AnimationDirection.DOWN);
        isAlive = true;
    }

    public void setTargetObject(GameObject targetObject){
        this.targetObject = targetObject;
    }

    public void setCharacterSpeed(float characterSpeed){
        this.characterSpeed = characterSpeed;
    }

    @Override
    public void update(int delta) {
        // If the character is done dying reset the death animation and make it not alive
        if (this.isDying() && this.getCurrentAnimation() == dyingAnim && this.getCurrentAnimation().isStopped()) {
            isDying = false;
            isAlive = false;
            this.dyingAnim.restart();
        }
        else if (this.isDying){
            super.update(delta);
        }
        else {
            super.update(delta);
            /* Defining Character Motion - Follows after target*/
            // If character has reached its target... kill it!
            if (this.isColliding(targetObject)) {
                this.kill();
                deathSound.play();
            } else {
                // When traveling diagonally maintain last animation direction
                if (abs(getTargetX() - this.getX()) + 1 > abs(getTargetY() - this.getY()) &&
                        abs(getTargetX() - this.getX()) - 1 < abs(getTargetY() - this.getY()))
                {
                    if (abs(getTargetX() - this.getX()) > abs(getTargetY() - this.getY()))
                    {
                        if (getTargetX() > this.getX()) {
                            this.setX(this.getX() + delta * characterSpeed);
                        } else {
                            this.setX(this.getX() - delta * characterSpeed);
                        }
                    } else {
                        if (getTargetY() > this.getY()) {
                            this.setY(this.getY() + delta * characterSpeed);
                        } else {
                            this.setY(this.getY() - delta * characterSpeed);
                        }
                    }
                }
                // Left right travel
                else if (abs(getTargetX() - this.getX()) > abs(getTargetY() - this.getY())) {
                    if (getTargetX() > this.getX() + 1) {
                        this.setAnimation(AnimationDirection.RIGHT);
                        this.setX(this.getX() + delta * characterSpeed);
                    } else {
                        this.setAnimation(AnimationDirection.LEFT);
                        this.setX(this.getX() - delta * characterSpeed);
                    }
                }
                // Up down travel
                else {
                    if (getTargetY() > this.getY() + 1) {
                        this.setAnimation(AnimationDirection.DOWN);
                        this.setY(this.getY() + delta * characterSpeed);
                    } else {
                        this.setAnimation(AnimationDirection.UP);
                        this.setY(this.getY() - delta * characterSpeed);
                    }
                }
            }
        }
    }
}
