package game;

import objects.*;
import objects.Character;
import org.newdawn.slick.*;
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
    private Present xmasPresent;
    private ArrayList<ChasingCat> catSwarm;
    /** The collision map indicating which tiles block movement – generated based on tile blocked property */
    private boolean[][] goalBlock;
    private GameTileMap gameTiles;
    private static final int SIZE = 64; // Tile size
    private int playerScore;
    private PlayerCharacter playerCharacter;
    private Sound catHissSound;

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

        // Defining music and sounds
        catHissSound = new Sound("assets/sounds/cat_death_sound.wav");
        Music mainMusic = new Music("assets/sounds/gloria_song.wav");
        mainMusic.loop();

        // building collision and game maps based on tile properties in the TileD map
        gameTiles = new GameTileMap(xmasMap.getWidth(), xmasMap.getHeight(), SIZE, SIZE);
        goalBlock = new boolean[xmasMap.getWidth()][xmasMap.getHeight()];
        for (int xAxis=0; xAxis < xmasMap.getWidth(); xAxis++) {
            for (int yAxis=0; yAxis < xmasMap.getHeight(); yAxis++) {
                // Getting spawn points and goal blocks
                int tileID = xmasMap.getTileId(xAxis, yAxis, MapLayers.GAME.getValue());
                // Building map of player spawn points... TODO: Implement player spawn map
                String value = xmasMap.getTileProperty(tileID, "playerSpawn", "false");
                if ("true".equals(value)) {
                    gameTiles.getTile(xAxis, yAxis).setPlayerSpawn(true);
                }
                // Building map of cat spawn points.
                value = xmasMap.getTileProperty(tileID, "catSpawn", "false");
                if ("true".equals(value)) {
                    gameTiles.getTile(xAxis, yAxis).setCatSpawn(true);
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

        //Initializing player location
        // TODO: replace this with spawnPlayer map logic
        playerCharacter.setPosition((xmasMap.getWidth()/2) * SIZE, (xmasMap.getHeight()/2) * SIZE);

        // Generating cat swarm
        catSwarm = new ArrayList<>();
        int totalCats = 10; // Total number of cats on screen a the same time
        for (int i = 0; i < totalCats; i++){
            catSwarm.add(new ChasingCat(playerCharacter));
        }

    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        // Spawning more cats
        spawnCats();

        /* dealing with the presents */
        xmasPresent.update(delta);
        if (playerCharacter.isColliding(xmasPresent) && !xmasPresent.isCollected()){
            xmasPresent.collected();
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
                if (cat.isColliding(playerCharacter) && !cat.isDying()) {
                    if (playerScore > 0) {
                        playerScore--;
                        //catHissSound.play();
                    }
                }
                cat.update(delta);
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
        while (gameTiles.getTile(xPos, yPos).isBlocked()){
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
        for (int xAxis = 0; xAxis < gameTiles.getWidth(); xAxis++) {
            if (catCount < 0){
                break;
            }
            for (int yAxis = 0; yAxis < gameTiles.getHeight(); yAxis++) {
                if (catCount < 0){
                    break;
                }
                if (gameTiles.getTile(xAxis, yAxis).isCatSpawn()) {
                    if (!catSwarm.get(catCount).isAlive()) {
                        catSwarm.get(catCount).setAlive();
                        catHissSound.play();
                        catSwarm.get(catCount).setPosition((xAxis) * SIZE + 32, (yAxis) * SIZE + 32);
                        catCount --;
                    }
                }
            }
        }

    }
}