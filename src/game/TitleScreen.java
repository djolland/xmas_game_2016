package game;

import dao.HighScoreDAO;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 1/13/2017.
 */
public class TitleScreen extends BasicGameState{

    public static final int ID = 0;
    private MainApplication game;

    private static final String titleText = "XMAS GAME";
    private Image titleScreen;
    private Music titleMusic;
    private Animation titleCat, titleCharacter;
    private float titleYPos, titleCharX, titleCharY, titleTextWidth;
    private int blinkTimer;
    private HighScoreDAO scoresDAO = new HighScoreDAO();
    private String[] highScores;
    private UnicodeFont titleFont, scoreFont, instructionFont;
    private Color titleColor;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        game = (MainApplication) stateBasedGame;

        titleScreen = new Image("assets/other/open_title_screen.png");
        titleMusic = new Music("assets/sounds/little_drummer_boy_song.wav");

        blinkTimer = 0;
        titleYPos = gameContainer.getHeight();

        // Defining fonts
        scoreFont = new UnicodeFont("assets/fonts/zig.ttf", 20, false, false);
        titleFont = new UnicodeFont("assets/fonts/arcade.ttf", 120, false, false);
        instructionFont = new UnicodeFont("assets/fonts/kongtext.ttf", 40, false, false);
        titleColor = Color.green;

        scoreFont.addAsciiGlyphs();
        scoreFont.getEffects().add(new ColorEffect());
        scoreFont.loadGlyphs();

        titleFont.addAsciiGlyphs();
        titleFont.getEffects().add(new ColorEffect());
        titleFont.loadGlyphs();

        instructionFont.addAsciiGlyphs();
        instructionFont.getEffects().add(new ColorEffect());
        instructionFont.loadGlyphs();

        titleTextWidth = titleFont.getWidth(titleText);

        // Initializing High Score stuff
        highScores = scoresDAO.loadScores("data/high_scores.txt");
        if (highScores[0] == null){
            scoresDAO.saveScore("data/high_scores.txt", "Dan The Man", 100);
            highScores = scoresDAO.loadScores("data/high_scores.txt");
        }

        // Defining Title Animation Characters
        Image [] movementRight = {
                new Image("assets/characters/nerd_player/nerd_right_2.png"),
                new Image("assets/characters/nerd_player/nerd_right_1.png"),
                new Image("assets/characters/nerd_player/nerd_right_2.png"),
                new Image("assets/characters/nerd_player/nerd_right_3.png")};
        int [] duration = {100, 100, 100, 100};

        titleCharacter = new Animation(movementRight, duration, false);
        titleCharacter.setLooping(true);
        titleCharX = 0;
        titleCharY = gameContainer.getHeight();
        titleCat = new Animation(new Image[] {
                new Image("assets/characters/zelda/zelda_right_1.png"),
                new Image("assets/characters/zelda/zelda_right_2.png"),
                new Image("assets/characters/zelda/zelda_right_3.png")},
                new int[]{100, 100, 100}, false);
        titleCat.setLooping(true);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        titleScreen.draw(0,0);
        titleFont.drawString(gameContainer.getWidth()/2f - titleTextWidth/2, titleYPos, titleText, titleColor);
        titleFont.drawString(gameContainer.getWidth()/2f - titleFont.getWidth("2016")/2,
                titleYPos + titleFont.getHeight(titleText) + 10f, "2016", titleColor);
        if (titleMusic.getPosition() >= 9f){
            // Drawing title text
            instructionFont.drawString(gameContainer.getWidth()/2 - instructionFont.getWidth("Press Enter")/2,
                    gameContainer.getHeight()-128f, "Press Enter", Color.red);
            scoreFont.drawString(
                    gameContainer.getWidth()/2 - scoreFont.getWidth("High Score:")/2, 500,
                    "High Score:", Color.white);
            scoreFont.drawString(
                    gameContainer.getWidth()/2 - scoreFont.getWidth(highScores[0])/2,
                    500 + scoreFont.getHeight("High Score:") + 10f,
                    highScores[0], Color.white);
            // Drawing characters walking across screen
            titleCharacter.draw(titleCharX, titleCharY);
            titleCat.draw(titleCharX - 70f, (titleCharY + titleCharacter.getHeight()) - titleCat.getHeight());
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        if (!titleMusic.playing()){
            titleMusic.loop();
            gameContainer.getInput().clearKeyPressedRecord();
        }
        if (titleMusic.getPosition() < 2){
            gameContainer.getInput().clearKeyPressedRecord();
        }
        if(gameContainer.getInput().isKeyPressed(Input.KEY_RETURN)){
            game.enterState(XmasGame.ID);
        }
        // Defining blinking action for Title text.
        if (blinkTimer < 1000){
            blinkTimer += delta;
        } else {
            blinkTimer = 0;
        }
        if (blinkTimer >= 500){
            titleColor = Color.green;
        } else{
            titleColor = Color.red;
        }
        if (titleYPos >= 64f && titleMusic.getPosition() > 1f){
            titleYPos -= delta * .085f;
        }
        if (titleMusic.getPosition() > 9f){ // Send out the characters
            if (titleCharX >= gameContainer.getWidth() + titleCat.getWidth() + titleCharacter.getWidth() + 80f){
                titleCharX = 0;
                titleCharY = 380;
            }else {
                titleCharX += delta * 0.07f;
                titleCharY = 380;
            }
            titleCharacter.update(delta);
            titleCat.update(delta);
        }
    }

    @Override
    public void enter(GameContainer gameContainer , StateBasedGame sbg) {
        try {
            this.init(gameContainer, game);
        }
        catch (SlickException e){
            e.printStackTrace();
        }
    }

    @Override
    public void leave(GameContainer gameContainer , StateBasedGame sbg) {
        titleMusic.stop();
    }
}
