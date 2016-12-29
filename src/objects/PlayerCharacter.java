package objects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Input;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/26/2016.
 */
public class PlayerCharacter extends Character{

    //private Input input;

    public PlayerCharacter(Animation up, Animation down, Animation left, Animation right){//, Input input) {
        super(up, down, left, right);
        //this.input = input;
    }
    /*
    @Override
    public void update(int delta){
        // Defining Player Motion
        float playerSpeed = 0.3f; // Higher values = faster player speed
        if (input.isKeyDown(Input.KEY_UP)) {
            this.setAnimation(Character.AnimationDirection.UP);
            if (!isBlocked(this.getX(), this.getY() - delta * playerSpeed)) {
                this.update(delta);
                // The lower the delta the slowest the sprite will animate.
                this.setY(this.getY() - delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_DOWN)) {
            this.setAnimation(Character.AnimationDirection.DOWN);
            if (!isBlocked(this.getX(), this.getY()+ SIZE + delta * playerSpeed)) {
                this.update(delta);
                this.setY(this.getY() + delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_LEFT)) {
            this.setAnimation(Character.AnimationDirection.LEFT);
            if (!isBlocked(this.getX() - delta * playerSpeed, this.getY())) {
                this.update(delta);
                this.setX(this.getX() - delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_RIGHT)) {
            this.setAnimation(Character.AnimationDirection.RIGHT);
            if (!isBlocked(playerCharacter.getX() + SIZE + delta * playerSpeed, this.getY())) {
                this.update(delta);
                this.setX(this.getX() + delta * playerSpeed);
            }
        }
    }
    */
}
