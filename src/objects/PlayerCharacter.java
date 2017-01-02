package objects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Input;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/26/2016.
 */
public class PlayerCharacter extends Character{

    private Input input;
    private boolean[][] blocked;
    private float playerSpeed;

    public PlayerCharacter(Animation up, Animation down, Animation left, Animation right,
                           Input input, boolean[][] blocked) {
        super(up, down, left, right);
        this.input = input;
        this.blocked = blocked;
        this.playerSpeed = 0.3f; // Higher values = faster player speed
    }

    public void setPlayerSpeed(float playerSpeed){
        this.playerSpeed = playerSpeed;
    }

    @Override
    public void update(int delta){
        // Defining Player Motion
        if (input.isKeyDown(Input.KEY_UP)) {
            this.setAnimation(Character.AnimationDirection.UP);
            if (!isBlocked(this.getX(), this.getY() - delta * playerSpeed)) {
                super.update(delta);
                // The lower the delta the slowest the sprite will animate.
                this.setY(this.getY() - delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_DOWN)) {
            this.setAnimation(Character.AnimationDirection.DOWN);
            if (!isBlocked(this.getX(), this.getY()+ this.getHeight() + delta * playerSpeed)) {
                super.update(delta);
                this.setY(this.getY() + delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_LEFT)) {
            this.setAnimation(Character.AnimationDirection.LEFT);
            if (!isBlocked(this.getX() - delta * playerSpeed, this.getY())) {
                super.update(delta);
                this.setX(this.getX() - delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_RIGHT)) {
            this.setAnimation(Character.AnimationDirection.RIGHT);
            if (!isBlocked(this.getX() + this.getWidth() + delta * playerSpeed, this.getY())) {
                super.update(delta);
                this.setX(this.getX() + delta * playerSpeed);
            }
        }
    }

    private boolean isBlocked(float x, float y) {
        int xBlock = (int)x / 64;
        int yBlock = (int)y / 64;
        return blocked[xBlock][yBlock];
    }

}
