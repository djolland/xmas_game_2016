package objects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/25/2016.
 */
public class ChasingCat extends ChasingCharacter{

    private final static int [] durationCat = {100, 100, 100};
    private final static int [] dyingDuration = {150,100,100,100,100,100,100,100,100,100};
    private final static float timeLimitS = 10;
    private float timer;

    public ChasingCat(GameObject targetObject) throws SlickException {
        super(new Animation(new Image[] {
                        new Image("assets/characters/zelda/zelda_up_1.png"),
                        new Image("assets/characters/zelda/zelda_up_2.png"),
                        new Image("assets/characters/zelda/zelda_up_3.png")},
                        durationCat, false),
                new Animation(new Image [] {
                        new Image("assets/characters/zelda/zelda_down_1.png"),
                        new Image("assets/characters/zelda/zelda_down_2.png"),
                        new Image("assets/characters/zelda/zelda_down_3.png")},
                        durationCat, false),
                new Animation(new Image [] {
                        new Image("assets/characters/zelda/zelda_left_1.png"),
                        new Image("assets/characters/zelda/zelda_left_2.png"),
                        new Image("assets/characters/zelda/zelda_left_3.png")},
                        durationCat, false),
                new Animation(new Image [] {
                        new Image("assets/characters/zelda/zelda_right_1.png"),
                        new Image("assets/characters/zelda/zelda_right_2.png"),
                        new Image("assets/characters/zelda/zelda_right_3.png")},
                        durationCat, false),
                new Animation(new Image [] {
                        new Image("assets/other/explosion_raw_1.png"),
                        new Image("assets/other/explosion_raw_2.png"),
                        new Image("assets/other/explosion_raw_3.png"),
                        new Image("assets/other/explosion_raw_4.png"),
                        new Image("assets/other/explosion_raw_5.png"),
                        new Image("assets/other/explosion_raw_6.png"),
                        new Image("assets/other/explosion_raw_7.png"),
                        new Image("assets/other/explosion_raw_8.png"),
                        new Image("assets/other/explosion_raw_9.png"),
                        new Image("assets/other/explosion_raw_10.png")},
                        dyingDuration, false),
                new Sound("assets/sounds/small_explosion_01.wav"), 0.1f, targetObject
        );
        this.timer = 0;
    }

    @Override
    public void kill(){
        super.kill();
        timer = 0;
    }

    @Override
    public void update(int delta) {
        timer += delta / 1000f;
        if (timer >= timeLimitS){
            this.kill();
        }
        super.update(delta);

    }
}
