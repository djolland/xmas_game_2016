package game;

import dao.HighScoreDAO;
import objects.*;
import objects.Character;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/23/2016.
 */
public class XmasGame extends BasicGame {

    private TiledMap xmasMap;
    private static final int SIZE = 64; // Tile size
    private GameTileMap gameTiles;
    private Image deathScreen, titleScreen;
    private PlayerCharacter playerCharacter;
    private int playerScore;
    private int playerHPmax, playerHPcurrent;
    private Present xmasPresent;
    private ArrayList<ChasingCat> catSwarm;
    private ArrayList<GameObject> targetList;
    private Sound catHissSound;
    private boolean playerAlive, gameStart;
    private HealthBar healthBar;
    private Music mainMusic, deathMusic, titleMusic;
    private Sound santaTaunt;
    private Animation titleCat, titleCharacter;
    private UnicodeFont scoreFont, titleFont, instructionFont;
    private Color titleColor;
    private int blinkTimer;
    private float titleYPos, titleCharX, titleCharY;
    private final static HighScoreDAO scoreDAO = new HighScoreDAO();
    private String[] highScores;
    private TextField enterName;

    public XmasGame() {
        super("Xmas Game - 2016");
    }

    public static void main(String[] arguments) {
        try {
            AppGameContainer app = new AppGameContainer(new XmasGame());
            app.setDisplayMode(640, 704, false);
            app.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        // Defining game map asset
        xmasMap = new TiledMap("assets/maps/xmas_map_64x64.tmx");
        deathScreen = new Image("assets/other/death_screen.png");
        titleScreen = new Image("assets/other/open_title_screen.png");

        // Defining music and sounds
        catHissSound = new Sound("assets/sounds/cat_death_sound.wav");
        mainMusic = new Music("assets/sounds/gloria_song.wav");
        deathMusic = new Music("assets/sounds/emmanuel_song.wav");
        titleMusic = new Music("assets/sounds/little_drummer_boy_song.wav");
        santaTaunt = new Sound("assets/sounds/santa_ho.wav");

        // Initializing player data
        playerScore = 0;
        playerHPmax = 5;
        playerHPcurrent = playerHPmax;
        healthBar = new HealthBar(playerHPmax, playerHPcurrent);
        playerAlive = true;
        gameStart = false;

        // Initializing High Score stuff
        highScores = scoreDAO.loadScores("data/high_scores.txt");
        if (highScores[0] == null){
            scoreDAO.saveScore("data/high_scores.txt", "Dan The Man", 100);
            highScores = scoreDAO.loadScores("data/high_scores.txt");
        }

        enterName = new TextField(
                container, container.getDefaultFont(),
                container.getWidth(), container.getHeight(),
                200, 35);
        enterName.setBackgroundColor(Color.black);
        enterName.setBorderColor(Color.black);
        enterName.setTextColor(Color.green);
        enterName.setFocus(true);
        enterName.setAcceptingInput(true);

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

        blinkTimer = 0;
        titleYPos = container.getHeight();

        // building collision and game maps based on tile properties in the TileD map
        targetList = new ArrayList<>();
        gameTiles = new GameTileMap(xmasMap.getWidth(), xmasMap.getHeight(), SIZE, SIZE);
        for (int xAxis=0; xAxis < xmasMap.getWidth(); xAxis++) {
            for (int yAxis=0; yAxis < xmasMap.getHeight(); yAxis++) {
                // Getting spawn points and goal blocks
                int tileID = xmasMap.getTileId(xAxis, yAxis, MapLayers.GAME.getValue());
                // Building map of present spawn points
                String value = xmasMap.getTileProperty(tileID, "presentSpawn", "false");
                if ("true".equals(value)) {
                    gameTiles.getTile(xAxis, yAxis).setPresentSpawn(true);
                }
                // Building map of cat spawn points.
                value = xmasMap.getTileProperty(tileID, "catSpawn", "false");
                if ("true".equals(value)) {
                    gameTiles.getTile(xAxis, yAxis).setCatSpawn(true);
                }
                // Building goal map
                value = xmasMap.getTileProperty(tileID, "goal", "false");
                if ("true".equals(value)) {
                    gameTiles.getTile(xAxis, yAxis).setTarget(true);
                    targetList.add(gameTiles.getTile(xAxis,yAxis));
                }
                // Building collision map
                for (MapLayers layer : MapLayers.values()){
                    tileID = xmasMap.getTileId(xAxis, yAxis, layer.getValue());
                    value = xmasMap.getTileProperty(tileID, "blocked", "false");
                    if ("true".equals(value)) {
                        gameTiles.getTile(xAxis, yAxis).setBlocked(true);
                    }
                }
            }
        }

        /* defining other objects */
        xmasPresent = new Present();

        /* Defining player animations */
        Image[] movementUp = {
                new Image("assets/characters/nerd_player/nerd_up_2.png"),
                new Image("assets/characters/nerd_player/nerd_up_1.png"),
                new Image("assets/characters/nerd_player/nerd_up_2.png"),
                new Image("assets/characters/nerd_player/nerd_up_3.png")};
        Image [] movementDown = {
                new Image("assets/characters/nerd_player/nerd_down_2.png"),
                new Image("assets/characters/nerd_player/nerd_down_1.png"),
                new Image("assets/characters/nerd_player/nerd_down_2.png"),
                new Image("assets/characters/nerd_player/nerd_down_3.png")};
        Image [] movementLeft = {
                new Image("assets/characters/nerd_player/nerd_left_2.png"),
                new Image("assets/characters/nerd_player/nerd_left_1.png"),
                new Image("assets/characters/nerd_player/nerd_left_2.png"),
                new Image("assets/characters/nerd_player/nerd_left_3.png")};
        Image [] movementRight = {
                new Image("assets/characters/nerd_player/nerd_right_2.png"),
                new Image("assets/characters/nerd_player/nerd_right_1.png"),
                new Image("assets/characters/nerd_player/nerd_right_2.png"),
                new Image("assets/characters/nerd_player/nerd_right_3.png")};
        int [] duration = {100, 100, 100, 100};

        playerCharacter = new PlayerCharacter(new Animation(movementUp, duration, false),
                                              new Animation(movementDown, duration, false),
                                              new Animation(movementLeft, duration, false),
                                              new Animation(movementRight, duration, false),
                                              container.getInput(), gameTiles);

        // Setting initial player position to right
        playerCharacter.setAnimation(Character.AnimationDirection.DOWN);

        //Initializing player location at center of map
        playerCharacter.setPosition((xmasMap.getWidth()/2) * SIZE, (xmasMap.getHeight()/2) * SIZE);

        targetList.add(playerCharacter);

        // Title Animation Characters
        titleCharacter = new Animation(movementRight, duration, false);
        titleCharacter.setLooping(true);
        titleCharX = 0;
        titleCharY = container.getHeight();
        titleCat = new Animation(new Image [] {
                new Image("assets/characters/zelda/zelda_right_1.png"),
                new Image("assets/characters/zelda/zelda_right_2.png"),
                new Image("assets/characters/zelda/zelda_right_3.png")},
                new int[]{100, 100, 100}, false);
        titleCat.setLooping(true);

        // Generating cat swarm
        catSwarm = new ArrayList<>();
        int initialCatCount = 1; // Total number of cats that appear on screen a the same time
        for (int i = 0; i < initialCatCount; i++){
            catSwarm.add(new ChasingCat(playerCharacter));
        }

    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        if (playerAlive && gameStart) { // We are in the main game loop
            if (titleMusic.playing()){
                titleMusic.stop();
                mainMusic.loop();
            }
            // Spawning more cats
            spawnCats();

            /* dealing with the presents */
            xmasPresent.update(delta);
            if (playerCharacter.isColliding(xmasPresent) && !xmasPresent.isCollected()) {
                xmasPresent.collected();
                playerScore += 1;
                // Increase difficulty as more presents are collected.
                if (playerScore == 5) {
                    catSwarm.add(new ChasingCat(playerCharacter));
                    catSwarm.add(new ChasingCat(playerCharacter));
                } else if (playerScore == 15) {
                    catSwarm.add(new ChasingCat(playerCharacter));
                } else if (playerScore == 25) {
                    catSwarm.add(new ChasingCat(playerCharacter));
                    catSwarm.add(new ChasingCat(playerCharacter));
                } else if (playerScore == 40) {
                    catSwarm.add(new ChasingCat(playerCharacter));
                } else if (playerScore == 50) {
                    // Reward for collecting 50 presents!
                    playerHPcurrent = playerHPmax;
                }else if (playerScore == 75){
                    playerHPcurrent = playerHPmax;
                    catSwarm.add(new ChasingCat(playerCharacter));
                    catSwarm.add(new ChasingCat(playerCharacter));
                }  if (playerScore == 100) {
                    // Impossible mode!
                    for (ChasingCat cat : catSwarm) {
                        cat.setCharacterSpeed(0.25f);
                    }
                }
            }
            if (!xmasPresent.isVisible()) {
                spawnPresent();
            }

            /* updating player character */
            playerCharacter.update(delta);

             /* Updating CAT SWARM */
            for (ChasingCat cat : catSwarm) {
                if (cat.isAlive()) {
                    if (cat.isColliding(playerCharacter) && !cat.isDying()) {
                        cat.kill();
                        if (playerHPcurrent >= 0) {
                            playerHPcurrent--;
                        }
                    }
                    cat.update(delta);
                }
            }

            // Checking if player should be dead
            if (playerHPcurrent <= 0) {
                playerAlive = false;
            }
        }
        else if (!gameStart){ // We are on the Starting Title Screen
            if (!titleMusic.playing()){
                titleMusic.loop();
                container.getInput().clearKeyPressedRecord();
            }
            if (titleMusic.getPosition() < 2){
                container.getInput().clearKeyPressedRecord();
            }
            if(container.getInput().isKeyPressed(Input.KEY_RETURN)){
                gameStart = true;
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
                if (titleCharX >= container.getWidth() + titleCat.getWidth() + titleCharacter.getWidth() + 80f){
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
        else{ // We are on the Death Screen
            if (mainMusic.playing()){
                mainMusic.stop();
                santaTaunt.play();
                container.getInput().clearKeyPressedRecord();
            }
            else if (!mainMusic.playing() && !santaTaunt.playing() && !deathMusic.playing()){
                deathMusic.loop();
                container.getInput().clearKeyPressedRecord();
            }
            else if(deathMusic.getPosition() < 2){
                container.getInput().clearKeyPressedRecord();
            } else if (container.getInput().isKeyPressed(Input.KEY_RETURN)){
                if (enterName.getText() != ""){
                    scoreDAO.saveScore("data/high_scores.txt", enterName.getText(), playerScore);
                }
                this.init(container);
            }
        }
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
        // Rendering the ground layer (last parameter is layer index)
        xmasMap.render(0, 0, MapLayers.BACKGROUND.getValue());
        xmasPresent.draw();
        // Rendering Characters
        for (ChasingCat cat : catSwarm){
            if (cat.isAlive()){
                cat.draw();
            }
        }
        playerCharacter.draw();

        // Rendering foliage layer over-top of Characters
        xmasMap.render(0,0, MapLayers.FOREGROUND.getValue());

        // Rendering player score and health
        scoreFont.drawString(260, 665, "PRESENTS COLLECTED:" + playerScore, Color.white);
        healthBar.draw(32, 660, playerHPcurrent);

        // show the death screen
        if (!playerAlive && deathMusic.getPosition() >  1){
            deathScreen.draw(0,0);
            String scoreString = "You Saved " + playerScore + " Presents";
            float scoreHeight = (xmasMap.getHeight()/4) * SIZE;
            scoreFont.drawString(
                    (xmasMap.getWidth()/2) * SIZE - scoreFont.getWidth(scoreString)/2,
                    scoreHeight,
                    scoreString, Color.red);
            scoreHeight += scoreFont.getHeight(scoreString) + 10f;
            scoreFont.drawString(
                    (xmasMap.getWidth()/2) * SIZE - scoreFont.getWidth("Before the Cats RUINED Xmas")/2,
                    scoreHeight,
                    "Before the Cats RUINED Xmas", Color.red);
            scoreHeight += scoreFont.getHeight(scoreString) + 20f;
            scoreFont.drawString(
                    (xmasMap.getWidth()/2) * SIZE - scoreFont.getWidth("Enter Your Name:")/2,
                    scoreHeight,
                    "Enter Your Name:", Color.white);

            scoreHeight +=  30f;
            // Getting player name for high score
            enterName.setLocation(xmasMap.getWidth()/2 *64 - 100, (int) scoreHeight);
            enterName.render(container, g);
            enterName.setFocus(true);
            enterName.setAcceptingInput(true);

            scoreHeight +=  50f;
            scoreFont.drawString(
                    (xmasMap.getWidth()/2) * SIZE - scoreFont.getWidth("High Score")/2,
                    scoreHeight,
                    "High Scores:", Color.white);
            scoreHeight += scoreFont.getHeight("HS") + 25f;
            for (String score : highScores){
                if (score != null) {
                    scoreFont.drawString(
                            (xmasMap.getWidth() / 2) * SIZE - scoreFont.getWidth(score) / 2,
                            scoreHeight,
                            score, Color.green);
                    scoreHeight += scoreFont.getHeight(score) + 10f;
                }
            }
        }
        // Show Title Screen
        if (!gameStart){
            String titleText = "XMAS GAME";
            float titleWidth = titleFont.getWidth(titleText);
            titleScreen.draw(0,0);
            titleFont.drawString(container.getWidth()/2 - titleWidth/2,titleYPos,titleText, titleColor);
            titleFont.drawString(container.getWidth()/2 - titleFont.getWidth("2016")/2,
                    titleYPos + titleFont.getHeight(titleText) + 10f, "2016", titleColor);
            if (titleMusic.getPosition() >= 9f){
                // Drawing title text
                instructionFont.drawString(container.getWidth()/2 - instructionFont.getWidth("Press Enter")/2,
                        container.getHeight()-128f, "Press Enter", Color.red);
                scoreFont.drawString(
                        container.getWidth()/2 - scoreFont.getWidth("High Score:")/2, 500,
                        "High Score:", Color.white);
                scoreFont.drawString(
                        container.getWidth()/2 - scoreFont.getWidth(highScores[0])/2,
                        500 + scoreFont.getHeight("High Score:") + 10f,
                        highScores[0], Color.white);
                // Drawing characters walking across screen
                titleCharacter.draw(titleCharX, titleCharY);
                titleCat.draw(titleCharX - 70f, (titleCharY + titleCharacter.getHeight()) - titleCat.getHeight());
            }
        }

    }

    private void spawnPresent(){
        //generate random location until non-blocked location found
        Random rand = new Random();

        int xPos = rand.nextInt(xmasMap.getWidth()-1);
        int yPos = rand.nextInt(xmasMap.getHeight()-1);
        // Continue to generate a random spawn point if current tile is not a present spawn point or
        // the generated spawn point is within 2 tiles of the last present location.
        while (!gameTiles.getTile(xPos, yPos).isPresentSpawn() ||
                ((xPos < (int) xmasPresent.getX()/SIZE + 2 && xPos > (int) xmasPresent.getX()/SIZE - 2) &&
                (yPos < (int) xmasPresent.getY()/SIZE + 2 && yPos > (int) xmasPresent.getY()/SIZE - 2))){
            xPos = rand.nextInt(xmasMap.getWidth()-1);
            yPos = rand.nextInt(xmasMap.getHeight()-1);
        }
        xmasPresent.setPosition((xPos * SIZE + SIZE/2),
                                (yPos * SIZE + SIZE/2));
        xmasPresent.setVisible();
    }

    private void spawnCats(){
        // Spawning Cats
        Random rand = new Random();
        boolean[][] spawnedList = new boolean[gameTiles.getWidth()][gameTiles.getHeight()];
        for (ChasingCat cat : catSwarm){
            int xPos = rand.nextInt(gameTiles.getWidth() - 1);
            int yPos = rand.nextInt(gameTiles.getHeight() - 1);
            //generate random location until unused cat spawn location found
            if (!cat.isAlive()) {
                while (!gameTiles.getTile(xPos, yPos).isCatSpawn()){// && spawnedList[xPos][yPos]) {
                    xPos = rand.nextInt(gameTiles.getWidth() - 1);
                    yPos = rand.nextInt(gameTiles.getHeight() - 1);
                }
                cat.setPosition(xPos * SIZE + SIZE/2, yPos * SIZE + SIZE/2);
                cat.setAlive();
                GameObject catTarget = targetList.get(rand.nextInt(targetList.size()));
                cat.setTargetObject(catTarget);
                // If the spawned cat is targeting the player - play the cat his sound!
                if (catTarget == playerCharacter) {
                    catHissSound.play(); // this thing is really annoying, just like the real thing!
                }
                spawnedList[xPos][yPos] = true;
            }
        }
    }
}