package Client.tiles;

import java.awt.image.BufferedImage;

public class tile {

    public BufferedImage image;
    public boolean collision = false;

    // Warp data
    public boolean isWarp = false;
    public int warpToMap = 0;
    public int warpToCol = 0;
    public int warpToRow = 0;

    // Key requirement
    public boolean requiresKey = false; // ต้องมีกุญแจก่อนถึงจะ warp ได้
    public boolean isUnlocked = false; // ถูกปลดล็อกแล้วหรือยัง
    public boolean isUnlockedL = false; // ถูกปลดล็อกแล้วหรือยัง
    public boolean qust_light = false;
    public boolean EndGame = false;
}