package game;

import objects.Character;
import objects.ChasingCat;
import objects.PlayerCharacter;
import objects.Present;
import org.newdawn.slick.*;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/23/2016.
 */
public class XmasGame extends BasicGame {

    private TiledMap xmasMap;

    private Present xmasPresent;
    private ArrayList<ChasingCat> catSwarm;
    /** The collision map indicating which tiles block movement – generated based on tile blocked property */
    private boolean[][] blocked, playerSpawn, catSpawn, goalBlock;
    private static final int SIZE = 64; // Tile size
    private int playerScore;
    private Sound angryCatSound;
    private PlayerCharacter playerCharacter;

    public XmasGame() {
        super("Xmas Game");
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
        playerScore = 0;

        // Defining game map asset
        xmasMap = new TiledMap("assets/maps/xmas_map_64x64.tmx");
        //xmasMap = new TiledMap("assets/maps/xmas_house_map_64x64.tmx");

        // Defining sounds
        angryCatSound = new Sound("assets/sounds/cat_death_sound.wav");

        /* defining other objects */
        Animation visibleAnimation = new Animation(
                new Image[]{new Image("assets/other/xmas_present.png")}, 100, false);
        xmasPresent = new Present(visibleAnimation, visibleAnimation);

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
                                              new Animation(movementRight, duration, false));

        // Setting initial player position to right
        playerCharacter.setAnimation(Character.AnimationDirection.DOWN);

        //Initializing player location
        // TODO: replace this with spawnPlayer map logic
        playerCharacter.setPosition(128f, 128f);

        /* Defining Cat Animations */
        Image[] movementUpCat = {
                new Image("assets/characters/zelda/zelda_up_1.png"),
                new Image("assets/characters/zelda/zelda_up_2.png"),
                new Image("assets/characters/zelda/zelda_up_3.png")};
        Image [] movementDownCat = {
                new Image("assets/characters/zelda/zelda_down_1.png"),
                new Image("assets/characters/zelda/zelda_down_2.png"),
                new Image("assets/characters/zelda/zelda_down_3.png")};
        Image [] movementLeftCat = {
                new Image("assets/characters/zelda/zelda_left_1.png"),
                new Image("assets/characters/zelda/zelda_left_2.png"),
                new Image("assets/characters/zelda/zelda_left_3.png")};
        Image [] movementRightCat = {
                new Image("assets/characters/zelda/zelda_right_1.png"),
                new Image("assets/characters/zelda/zelda_right_2.png"),
                new Image("assets/characters/zelda/zelda_right_3.png")};
        int [] durationCat = {100, 100, 100};

        // Generating cat swarm
        catSwarm = new ArrayList<>();
        int totalCats = 10; // Total number of cats on screen a the same time
        for (int i = 0; i < totalCats; i++){
            catSwarm.add(new ChasingCat(new Animation(movementUpCat, durationCat, false),
                                        new Animation(movementDownCat, durationCat, false),
                                        new Animation(movementLeftCat, durationCat, false),
                                        new Animation(movementRightCat, durationCat, false),
                                        new Animation(new Image[]{new Image("assets/other/explosion_raw_1.png"),
                                                                  new Image("assets/other/explosion_raw_2.png"),
                                                                  new Image("assets/other/explosion_raw_3.png"),
                                                                  new Image("assets/other/explosion_raw_4.png"),
                                                                  new Image("assets/other/explosion_raw_5.png"),
                                                                  new Image("assets/other/explosion_raw_6.png"),
                                                                  new Image("assets/other/explosion_raw_7.png"),
                                                                  new Image("assets/other/explosion_raw_8.png"),
                                                                  new Image("assets/other/explosion_raw_9.png"),
                                                                  new Image("assets/other/explosion_raw_10.png")
                                                                  },
                                                     new int[] {150  ,100,100,100,100,100,100,100,100,100}, false
                                                     )
                                        )
                        );
        }

        // building collision and game maps based on tile properties in the TileD map
        blocked = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        catSpawn = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        playerSpawn = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        goalBlock = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        for (int xAxis=0; xAxis < xmasMap.getWidth(); xAxis++) {
            for (int yAxis=0; yAxis < xmasMap.getHeight(); yAxis++) {
                // Getting spawn points and goal blocks
                int tileID = xmasMap.getTileId(xAxis, yAxis, MapLayers.GAME.getValue());
                String value = xmasMap.getTileProperty(tileID, "playerSpawn", "false");
                if ("true".equals(value)) {
                    playerSpawn[xAxis][yAxis] = true;
                }
                value = xmasMap.getTileProperty(tileID, "catSpawn", "false");
                if ("true".equals(value)) {
                    catSpawn[xAxis][yAxis] = true;
                }
                value = xmasMap.getTileProperty(tileID, "goal", "false");
                if ("true".equals(value)) {
                    goalBlock[xAxis][yAxis] = true;
                }
                // Building collision map
                for (MapLayers layer : MapLayers.values()){
                    tileID = xmasMap.getTileId(xAxis, yAxis, layer.getValue());
                    value = xmasMap.getTileProperty(tileID, "blocked", "false");
                    if ("true".equals(value)) {
                        blocked[xAxis][yAxis] = true;
                    }
                }
            }
        }

    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        Input input = container.getInput();
        spawnCats();
        //explosionAnimation.update(delta);

        /* dealing with the presents */
        xmasPresent.update(delta);
        if (playerCharacter.isColliding(xmasPresent)){
            xmasPresent.setInvisible();
            playerScore += 1;
        }
        if (!xmasPresent.isVisible()){
            spawnPresent();
        }

        /* Defining Player Motion */
        float playerSpeed = 0.3f; // Higher values = faster player speed
        if (input.isKeyDown(Input.KEY_UP)) {
            playerCharacter.setAnimation(Character.AnimationDirection.UP);
            if (!isBlocked(playerCharacter.getX(), playerCharacter.getY() - delta * playerSpeed)) {
                playerCharacter.update(delta);
                // The lower the delta the slowest the sprite will animate.
                playerCharacter.setY(playerCharacter.getY() - delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_DOWN)) {
            playerCharacter.setAnimation(Character.AnimationDirection.DOWN);
            if (!isBlocked(playerCharacter.getX(), playerCharacter.getY()+ SIZE + delta * playerSpeed)) {
                playerCharacter.update(delta);
                playerCharacter.setY(playerCharacter.getY() + delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_LEFT)) {
            playerCharacter.setAnimation(Character.AnimationDirection.LEFT);
            if (!isBlocked(playerCharacter.getX() - delta * playerSpeed, playerCharacter.getY())) {
                playerCharacter.update(delta);
                playerCharacter.setX(playerCharacter.getX() - delta * playerSpeed);
            }
        }
        else if (input.isKeyDown(Input.KEY_RIGHT)) {
            playerCharacter.setAnimation(Character.AnimationDirection.RIGHT);
            if (!isBlocked(playerCharacter.getX() + SIZE + delta * playerSpeed, playerCharacter.getY())) {
                playerCharacter.update(delta);
                playerCharacter.setX(playerCharacter.getX() + delta * playerSpeed);
            }
        }

        /* Updating CAT SWARM */
        for (ChasingCat cat : catSwarm){
            if (cat.isDying()){
                cat.update(delta);
            }
            else if (cat.isAlive()){
                chasingCatUpdate(cat, playerCharacter, delta);
            }
        }

    }

    public void render(GameContainer container, Graphics g) throws SlickException {
        // Rendering the ground layer (last parameter is layer index)
        xmasMap.render(0, 0, MapLayers.BACKGROUND.getValue());
        g.drawString("PRESENTS COLLECTED: " + playerScore, 32, 668);
        xmasPresent.draw();
        // Rendering Characters
        for (ChasingCat cat : catSwarm){
            if (cat.isAlive()){
                cat.draw();
            }
        }
        playerCharacter.draw();

        //Rendering foliage layer over-top of Characters
        xmasMap.render(0,0, MapLayers.FOREGROUND.getValue());
    }

    private boolean isBlocked(float x, float y)
    {
        int xBlock = (int)x / SIZE;
        int yBlock = (int)y / SIZE;
        return blocked[xBlock][yBlock];
    }

    private void spawnPresent(){
        //generate random location until non-blocked location found
        Random rand = new Random();

        int xPos = rand.nextInt(xmasMap.getWidth());
        int yPos = rand.nextInt(xmasMap.getHeight());
        while (blocked[xPos][yPos]){
            xPos = rand.nextInt(xmasMap.getWidth());
            yPos = rand.nextInt(xmasMap.getHeight());
        }
        xmasPresent.setPosition(xPos * SIZE, yPos * SIZE);
        xmasPresent.setVisible();
    }

    private void spawnCats(){
        // Spawning Cats
        int catCount =  -1;
        for (ChasingCat cat : catSwarm){
            if (!cat.isAlive()){
                catCount ++;
            }
        }
        for (int xAxis = 0; xAxis < catSpawn[0].length ; xAxis++) {
            if (catCount < 0){
                break;
            }
            for (int yAxis = 0; yAxis < catSpawn.length; yAxis++) {
                if (catCount < 0){
                    break;
                }
                if (catSpawn[xAxis][yAxis]) {
                    if (!catSwarm.get(catCount).isAlive()) {
                        catSwarm.get(catCount).setAlive();
                        catSwarm.get(catCount).setPosition((xAxis) * SIZE + 32, (yAxis) * SIZE + 32);
                        catCount --;
                    }
                }
            }
        }

    }

    private void chasingCatUpdate(ChasingCat cat, Character target, int delta){
        float targetX = target.getX();
        float targetY = target.getY();
        /* Defining Cat Motion - Follows after target*/
        float catSpeed = 0.1f;
        // If cat has reached its target... kill it!
        if (cat.isColliding(target)){
            cat.kill();
            angryCatSound.play();
            if (playerScore > 0){
                playerScore --;
            }
        }
        else {
            cat.update(delta);
            // When traveling diagonally maintain last animation direction
            if (abs(targetX - cat.getX()) + 1 > abs(targetY - cat.getY()) &&
                    abs(targetX - cat.getX()) - 1 < abs(targetY - cat.getY())) {
                if (abs(targetX - cat.getX()) > abs(targetY - cat.getY())) {
                    if (targetX > cat.getX() + 1) {
                        cat.setX(cat.getX() + delta * catSpeed);
                    } else {
                        cat.setX(cat.getX() - delta * catSpeed);
                    }
                }
                else {
                    if (targetY > cat.getY() + 1) {
                        cat.setY(cat.getY() + delta * catSpeed);
                    } else {
                        cat.setY(cat.getY() - delta * catSpeed);
                    }
                }
            }
            // Left right travel
            else if (abs(targetX - cat.getX()) > abs(targetY - cat.getY())) {
                if (targetX > cat.getX() + 1) {
                    cat.setAnimation(Character.AnimationDirection.RIGHT);
                    cat.setX(cat.getX() + delta * catSpeed);
                }
                else {
                    cat.setAnimation(Character.AnimationDirection.LEFT);
                    cat.setX(cat.getX() - delta * catSpeed);
                }
            }
            // Up down travel
            else {//if (abs(targetX-cat.getX()) < abs(targetY-yCat)) {
                if (targetY > cat.getY() + 1) {
                    cat.setAnimation(Character.AnimationDirection.DOWN);
                    cat.setY(cat.getY() + delta * catSpeed);
                }
                else {
                    cat.setAnimation(Character.AnimationDirection.UP);
                    cat.setY(cat.getY() - delta * catSpeed);
                }
            }
        }
    }

    private void chasingCatUpdate(ChasingCat cat, float targetX, float targetY, int delta){
        /* Defining Cat Motion - Follows after target*/
        float catSpeed = 0.1f;
        // If cat has reached its target... kill it!
        if ((targetX + SIZE > cat.getX() && targetX - 1 < cat.getX()) &&
            (targetY + SIZE > cat.getY() && targetY - 1 < cat.getY()) ){
            cat.kill();
            angryCatSound.play();
            if (playerScore > 0){
                playerScore --;
            }
        }
        else {
            cat.update(delta);
            // When traveling diagonally maintain last animation direction
            if (abs(targetX - cat.getX()) + 1 > abs(targetY - cat.getY()) &&
                    abs(targetX - cat.getX()) - 1 < abs(targetY - cat.getY())) {
                if (abs(targetX - cat.getX()) > abs(targetY - cat.getY())) {
                    if (targetX > cat.getX() + 1) {
                        cat.setX(cat.getX() + delta * catSpeed);
                    } else {
                        cat.setX(cat.getX() - delta * catSpeed);
                    }
                }
                else {
                    if (targetY > cat.getY() + 1) {
                        cat.setY(cat.getY() + delta * catSpeed);
                    } else {
                        cat.setY(cat.getY() - delta * catSpeed);
                    }
                }
            }
            // Left right travel
            else if (abs(targetX - cat.getX()) > abs(targetY - cat.getY())) {
                if (targetX > cat.getX() + 1) {
                    cat.setAnimation(Character.AnimationDirection.RIGHT);
                    cat.setX(cat.getX() + delta * catSpeed);
                }
                else {
                    cat.setAnimation(Character.AnimationDirection.LEFT);
                    cat.setX(cat.getX() - delta * catSpeed);
                }
            }
            // Up down travel
            else {//if (abs(targetX-cat.getX()) < abs(targetY-yCat)) {
                if (targetY > cat.getY() + 1) {
                    cat.setAnimation(Character.AnimationDirection.DOWN);
                    cat.setY(cat.getY() + delta * catSpeed);
                }
                else {
                    cat.setAnimation(Character.AnimationDirection.UP);
                    cat.setY(cat.getY() - delta * catSpeed);
                }
            }
        }
    }

}