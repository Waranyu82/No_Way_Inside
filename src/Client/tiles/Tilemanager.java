package Client.tiles;

import java.awt.Graphics2D;
import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import Client.Client.GamePanel;

public class Tilemanager {
    GamePanel gp;
    public tile[][] tile;
    public int mapTilenumber[][];

    public Tilemanager(GamePanel gp) {
        this.gp = gp;

        tile = new tile[5][50];
        mapTilenumber = new int[gp.maxWorldCol][gp.maxWorldRow];

        // init ทุกช่องเป็น -1 = "ไม่มี tile" (วาดเป็นสีดำ)
        for (int[] col : mapTilenumber)
            java.util.Arrays.fill(col, -1);

        gettileImage();
        loadMap(1);
    }

    public void gettileImage() {

        try {
            tile[0][0] = new tile();
            tile[0][0].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/wall_d.png"));
            tile[0][0].collision = true;

            tile[0][1] = new tile();
            tile[0][1].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/up_wall.png"));
            tile[0][1].collision = true;

            tile[0][2] = new tile();
            tile[0][2].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/down_wall.png"));
            tile[0][2].collision = true;

            tile[0][3] = new tile();
            tile[0][3].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/wall_L.png"));
            tile[0][3].collision = true;

            tile[0][4] = new tile();
            tile[0][4].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/r_wall.png"));
            tile[0][4].collision = true;

            tile[0][5] = new tile();
            tile[0][5].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/conner_left.png"));
            tile[0][5].collision = true;

            tile[0][6] = new tile();
            tile[0][6].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/connerwall.png"));
            tile[0][6].collision = true;

            tile[0][7] = new tile();
            tile[0][7].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/sadow_mid1.png"));
            tile[0][7].collision = false;

            tile[0][8] = new tile();
            tile[0][8].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/sadow_mid2.png"));
            tile[0][8].collision = false;

            tile[0][9] = new tile();
            tile[0][9].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/sadow_mid3.png"));
            tile[0][9].collision = false;

            tile[0][10] = new tile();
            tile[0][10].image = ImageIO
                    .read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/sadow_mid4.png"));
            tile[0][10].collision = false;

            tile[0][11] = new tile();
            tile[0][11].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/wood_m/1.png"));
            tile[0][11].collision = false;

            tile[0][12] = new tile();
            tile[0][12].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/wood_m/2.png"));
            tile[0][12].collision = false;

            tile[0][13] = new tile();
            tile[0][13].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/wood_m/3.png"));
            tile[0][13].collision = false;

            tile[0][14] = new tile();
            tile[0][14].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/WOODNEW/wood_m/4.png"));
            tile[0][14].collision = false;

            tile[0][15] = new tile();
            tile[0][15].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/coner_wall_d.png"));
            tile[0][15].collision = true;

            tile[0][16] = new tile();
            tile[0][16].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/coner_wall_d_R.png"));
            tile[0][16].collision = true;

            // =======================================================================================================

            tile[0][17] = new tile();
            tile[0][17].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up.png"));
            tile[0][17].isWarp = true;
            if (!gp.DEBUG) {
                tile[0][17].requiresKey = true;
            }
            tile[0][17].warpToMap = 2;
            tile[0][17].warpToCol = 32;
            tile[0][17].warpToRow = 9;
            tile[0][17].collision = true; // ป้องกันไม่ให้เดินผ่านประตูล็อกได้

            tile[0][18] = new tile();
            tile[0][18].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up2.png"));
            tile[0][18].isWarp = true;
            if (!gp.DEBUG) {
                tile[0][18].requiresKey = true;
            }
            tile[0][18].warpToMap = 2;
            tile[0][18].warpToCol = 32;
            tile[0][18].warpToRow = 9;

            tile[0][19] = new tile();
            tile[0][19].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up.png"));
            tile[0][19].isWarp = true;
            // tile[0][19].requiresKey = true;
            tile[0][19].warpToMap = 1;
            tile[0][19].warpToCol = 14;
            tile[0][19].warpToRow = 3;
            tile[0][19].collision = true; // ป้องกันไม่ให้เดินผ่านประตูล็อกได้

            tile[0][20] = new tile();
            tile[0][20].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up2.png"));
            tile[0][20].isWarp = true;
            // tile[0][20].requiresKey = true;
            tile[0][20].warpToMap = 1;
            tile[0][20].warpToCol = 14;
            tile[0][20].warpToRow = 3;

            tile[0][21] = new tile();
            tile[0][21].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up.png"));
            tile[0][21].isWarp = true;
            if (!gp.DEBUG) {
                tile[0][21].qust_light = true;
            }
            tile[0][21].warpToMap = 3;
            tile[0][21].warpToCol = 32;
            tile[0][21].warpToRow = 4;
            tile[0][21].collision = true; // ป้องกันไม่ให้เดินผ่านประตูล็อกได้

            tile[0][22] = new tile();
            tile[0][22].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up2.png"));
            tile[0][22].isWarp = true;
            if (!gp.DEBUG) {
                tile[0][22].qust_light = true;
            }
            tile[0][22].warpToMap = 3;
            tile[0][22].warpToCol = 32;
            tile[0][22].warpToRow = 4;

            tile[0][23] = new tile();
            tile[0][23].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up.png"));
            tile[0][23].isWarp = true;
            tile[0][23].warpToMap = 2;
            tile[0][23].warpToCol = 6;
            tile[0][23].warpToRow = 9;
            tile[0][23].collision = true; // ป้องกันไม่ให้เดินผ่านประตูล็อกได้

            tile[0][24] = new tile();
            tile[0][24].image = ImageIO.read(getClass().getResourceAsStream("/Client/res/tiles/door/door_up2.png"));
            tile[0][24].isWarp = true;
            tile[0][24].warpToMap = 2;
            tile[0][24].warpToCol = 6;
            tile[0][24].warpToRow = 9;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(int mapNum) {
        // reset ทุกช่องเป็น -1 ก่อนโหลดแผนที่ใหม่
        for (int[] col : mapTilenumber)
            java.util.Arrays.fill(col, -1);

        String path = String.format("/Client/res/maps/map%02d.txt", mapNum);
        try {
            InputStream iS = getClass().getResourceAsStream(path);
            if (iS == null) {
                System.err.println("Map not found: " + path);
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(iS));
            int col = 0, row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null)
                    break;
                String[] numbers = line.split(" ");
                while (col < gp.maxWorldCol) {
                    mapTilenumber[col][row] = Integer.parseInt(numbers[col]);
                    col++;
                }
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
            System.out.println("Loaded: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** คืน warp tile เฉพาะถ้าปลดล็อกแล้ว */
    public tile getWarpTileAt(int worldX, int worldY) {
        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;

        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow)
            return null;

        int tileNum = mapTilenumber[col][row];
        if (tileNum == -1)
            return null;

        tile t = tile[0][tileNum];
        if (t == null || !t.isWarp)
            return null;

        // ถ้าต้องใช้กุญแจแต่ยังไม่ปลดล็อก → ไม่ warp
        if (t.requiresKey && !t.isUnlocked)
            return null;
        if (t.qust_light && !t.isUnlockedL)
            return null;

        return t;
    }

    /** คืน door tile ใกล้เท้า player (ไม่ว่าล็อกหรือเปล่า) สำหรับแสดง UI */
    public tile getNearDoorTile(int worldX, int worldY) {
        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;

        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow)
            return null;

        int tileNum = mapTilenumber[col][row];
        if (tileNum == -1)
            return null;

        tile t = tile[0][tileNum];
        return (t != null && t.isWarp) ? t : null;
    }

    public void draw(Graphics2D g2) {
        int worldcol = 0, worldrow = 0;

        while (worldcol < gp.maxWorldCol && worldrow < gp.maxWorldRow) {
            int tileNum = mapTilenumber[worldcol][worldrow];
            int worldx = worldcol * gp.tileSize;
            int worldy = worldrow * gp.tileSize;
            int screenX = worldx - gp.player.worldX + gp.player.screenX;
            int screenY = worldy - gp.player.worldY + gp.player.screenY;

            if (worldx + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldx - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldy + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldy - gp.tileSize < gp.player.worldY + gp.player.screenY) {

                if (tileNum != -1 && tile[0][tileNum] != null) {
                    // วาด tile ปกติ
                    g2.drawImage(tile[0][tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
                // tileNum == -1 → ไม่วาดอะไร (พื้นหลังสีดำ)
            }
            worldcol++;
            if (worldcol == gp.maxWorldCol) {
                worldcol = 0;
                worldrow++;
            }
        }
    }
}