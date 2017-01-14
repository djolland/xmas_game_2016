package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/13/2017.
 */
public class MainApplication extends StateBasedGame {

    // Application properties
    public static final int WIDTH = 640;
    public static final int HEIGHT = 704;
    public static final int FPS = 120;
    public static final String GAMENAME = "XMas Game - 2016";

    private int playerScore = 0;

    public MainApplication(String appName){
        super(appName);
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        // First state added will be the one that is loaded first when game is launched
        this.addState(new TitleScreen());
        this.addState(new XmasGame());
        this.addState(new DeathScreen());
    }

    public static void main(String[] args){
        try {
            AppGameContainer app = new AppGameContainer(new MainApplication(GAMENAME));
            app.setDisplayMode(WIDTH, HEIGHT, false);
            app.setTargetFrameRate(FPS);
            app.setShowFPS(false);
            app.start();
        }
        catch (SlickException e){
            e.printStackTrace();
        }
    }

    public void setPlayerScore(int score){
        playerScore = score;
    }

    public int getPlayerScore(){
        return playerScore;
    }

}
