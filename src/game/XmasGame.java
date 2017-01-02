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
        // Initializing score
        playerScore = 0;

        // Defining game map asset
        xmasMap = new TiledMap("assets/maps/xmas_map_64x64.tmx");

        // building collision and game maps based on tile properties in the TileD map
        blocked = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        catSpawn = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        playerSpawn = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        goalBlock = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        for (int xAxis=0; xAxis < xmasMap.getWidth(); xAxis++) {
            for (int yAxis=0; yAxis < xmasMap.getHeight(); yAxis++) {
                // Getting spawn points and goal blocks
                int tileID = xmasMap.getTileId(xAxis, yAxis, MapLayers.GAME.getValue());
                // Building map of player spawn points... TODO: Implement player spawn map
                String value = xmasMap.getTileProperty(tileID, "playerSpawn", "false");
                if ("true".equals(value)) {
                    playerSpawn[xAxis][yAxis] = true;
                }
                // Building map of cat spawn points.
                value = xmasMap.getTileProperty(tileID, "catSpawn", "false");
                if ("true".equals(value)) {
                    catSpawn[xAxis][yAxis] = true;
                }
                // Building goal map.... TODO: Implement goal map.
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
                                              new Animation(movementRight, duration, false),
                                              container.getInput(), blocked);

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
                                        ),
                                        new Sound("assets/sounds/cat_death_sound.wav"), playerCharacter
                            )
            );
        }

    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        // Spawning more cats
        spawnCats();

        /* dealing with the presents */
        xmasPresent.update(delta);
        if (playerCharacter.isColliding(xmasPresent)){
            xmasPresent.setInvisible();
            playerScore += 1;
        }
        if (!xmasPresent.isVisible()) {
            spawnPresent();
        }

        /* updating player character */
        playerCharacter.update(delta);

        /* Updating CAT SWARM */
        for (ChasingCat cat : catSwarm) {
            if (cat.isAlive()) {
                cat.update(delta);
                if (cat.isColliding(playerCharacter)) {
                    if (playerScore > 0) {
                        playerScore--;
                    }
                }
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
}