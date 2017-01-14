package tilemap;

/**
 * @author Daniel J. Holland
 * @version 1.0
 * Created on 12/25/2016.
 */
public enum MapLayers {
    BACKGROUND(0), FOREGROUND(1), GAME(2);

    private final int layer_num;
    MapLayers(int layer_num) { this.layer_num = layer_num; }
    public int getValue() { return layer_num; }
}
