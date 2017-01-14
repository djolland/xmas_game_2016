package game;

import dao.HighScoreDAO;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/13/2017.
 */
public class DeathScreen extends BasicGameState {

    public static final int ID = 2;
    private MainApplication game;

    private Image deathScreen;
    private Music deathMusic;
    private HighScoreDAO scoreDAO = new HighScoreDAO();
    private String[] highScores;
    private TextField enterName;
    private UnicodeFont scoreFont;
    private int playerScore;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        game = (MainApplication) stateBasedGame;
        playerScore = game.playerScore;

        deathScreen = new Image("assets/other/death_screen.png");
        deathMusic = new Music("assets/sounds/emmanuel_song.wav");

        // Defining fonts
        scoreFont = new UnicodeFont("assets/fonts/zig.ttf", 20, false, false);

        scoreFont.addAsciiGlyphs();
        scoreFont.getEffects().add(new ColorEffect());
        scoreFont.loadGlyphs();

        // Initializing High Score entry stuff
        highScores = scoreDAO.loadScores("data/high_scores.txt");
        enterName = new TextField(
                gameContainer, gameContainer.getDefaultFont(),
                gameContainer.getWidth(), gameContainer.getHeight(),
                200, 35);
        enterName.setBackgroundColor(Color.black);
        enterName.setBorderColor(Color.black);
        enterName.setTextColor(Color.green);
        enterName.setCursorVisible(true);
        enterName.setFocus(true);
        enterName.setAcceptingInput(true);

        gameContainer.getInput().clearKeyPressedRecord();
        deathMusic.loop();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        // show the death screen
        deathScreen.draw(0,0);
        String scoreString = "You Saved " + playerScore + " Presents";
        float scoreHeight = (gameContainer.getHeight()/4);
        scoreFont.drawString(
                (gameContainer.getWidth()/2) - scoreFont.getWidth(scoreString)/2,
                scoreHeight,
                scoreString, Color.red);
        scoreHeight += scoreFont.getHeight(scoreString) + 10f;
        scoreFont.drawString(
                (gameContainer.getWidth()/2) - scoreFont.getWidth("Before the Cats RUINED Xmas")/2,
                scoreHeight,
                "Before the Cats RUINED Xmas", Color.red);
        scoreHeight += scoreFont.getHeight(scoreString) + 20f;
        scoreFont.drawString(
                (gameContainer.getWidth()/2) - scoreFont.getWidth("Enter Your Name:")/2,
                scoreHeight,
                "Enter Your Name:", Color.white);

        scoreHeight +=  30f;
        // Getting player name for high score
        enterName.setLocation(gameContainer.getWidth()/2 - 100, (int) scoreHeight);
        enterName.render(gameContainer, graphics);

        scoreHeight +=  50f;
        scoreFont.drawString(
                (gameContainer.getWidth()/2) - scoreFont.getWidth("High Score")/2,
                scoreHeight,
                "High Scores:", Color.white);
        scoreHeight += scoreFont.getHeight("HS") + 25f;
        for (String score : highScores){
            if (score != null) {
                scoreFont.drawString(
                        (gameContainer.getWidth() / 2) - scoreFont.getWidth(score) / 2,
                        scoreHeight,
                        score, Color.green);
                scoreHeight += scoreFont.getHeight(score) + 10f;
            }
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        if(deathMusic.getPosition() < 2){
            gameContainer.getInput().clearKeyPressedRecord();
        }
        else if (gameContainer.getInput().isKeyPressed(Input.KEY_RETURN)){
            if (!enterName.getText().equals("")){
                scoreDAO.saveScore("data/high_scores.txt", enterName.getText(), playerScore);
            }
            game.enterState(TitleScreen.ID);
        }
    }

    @Override
    public void leave(GameContainer gameContainer , StateBasedGame sbg) {
        deathMusic.stop();
        gameContainer.getInput().clearKeyPressedRecord();
    }

    @Override
    public void enter(GameContainer gameContainer , StateBasedGame sbg) {
        gameContainer.getInput().clearKeyPressedRecord();
        try {
            this.init(gameContainer, game);
        }
        catch (SlickException e){
            e.printStackTrace();
        }
    }

}
