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
    private ArrayList<GameObject> targetList;
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
        targetList = new ArrayList<>();
        gameTiles = new GameTileMap(xmasMap.getWidth(), xmasMap.getHeight(), SIZE, SIZE);
        for (int xAxis=0; xAxis < xmasMap.getWidth(); xAxis++) {
            for (int yAxis=0; yAxis < xmasMap.getHeight(); yAxis++) {
                // Getting spawn points and goal blocks
                int tileID = xmasMap.getTileId(xAxis, yAxis, MapLayers.GAME.getValue());
                // Building map of player spawn points... TODO: Implement player spawn map
                String value = xmasMap.getTileProperty(tileID, "presentSpawn", "false");
                if ("true".equals(value)) {
                    gameTiles.getTile(xAxis, yAxis).setPresentSpawn(true);
                }
                // Building map of cat spawn points.
                value = xmasMap.getTileProperty(tileID, "catSpawn", "false");
                if ("true".equals(value)) {
                    gameTiles.getTile(xAxis, yAxis).setCatSpawn(true);
                }
                // Building goal map.... TODO: Implement goal map.
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

        //Initializing player location
        // TODO: replace this with spawnPlayer map logic
        playerCharacter.setPosition((xmasMap.getWidth()/2) * SIZE, (xmasMap.getHeight()/2) * SIZE);

        targetList.add(playerCharacter);

        // Generating cat swarm
        catSwarm = new ArrayList<>();
        int totalCats = 5; // Total number of cats on screen a the same time
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
                    cat.kill();
                    if (playerScore > 0) {
                        playerScore--;
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