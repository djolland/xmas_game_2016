package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/3/2017.
 */
public class HealthBar {

    private String heartFull, heartEmpty;
    private Image[] heartBar;
    private int playerHPmax, playerHPcurrent;

    public HealthBar(int playerHPmax, int playerHPcurrent){
        this.heartFull = "assets/other/heart_full.png";
        this.heartEmpty = "assets/other/heart_empty.png";
        this.heartBar = new Image[playerHPmax];
        this.playerHPcurrent = playerHPcurrent;
        this.playerHPmax = playerHPmax;
    }

    public void draw(float xPos, float yPos, int playerHPcurrent) throws SlickException {
        int i = 0;
        for (Image heart : heartBar){
            if (i < playerHPcurrent){
                heart = new Image(heartFull);
            }else {
                heart = new Image(heartEmpty);
            }
            heart.draw(xPos, yPos);
            xPos += heart.getWidth() + heart.getWidth()/4;
            i++;
        }
    }

}
