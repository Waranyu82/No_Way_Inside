package Client.Client;

import Client.object.OBJ_Book;
import Client.object.OBJ_Chest;
import Client.object.OBJ_Clock;
import Client.object.OBJ_Lantern;
import Client.object.OBJ_Shelf;
// import Client.Entity.Entity;
import Client.Entity.ghost;
import Client.Entity.ghost2;

import java.util.Random;

public class assetSetter {

    private final GamePanel gp;
    private final Random rand = new Random();

    // ─── Lantern State ────────────────────────────────────────────
    // เก็บตำแหน่งและสถานะตะเกียงแยกต่อ map และ slot
    private final boolean[][] lanternSpawned = new boolean[4][10]; // เคย spawn แล้วหรือยัง
    public final boolean[][] lanternCollected = new boolean[4][10]; // เก็บแล้วหรือยัง
    private final int[][] lanternSavedX = new int[4][10]; // ตำแหน่ง X ที่ spawn
    private final int[][] lanternSavedY = new int[4][10]; // ตำแหน่ง Y ที่ spawn

    public assetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setobject() {
        setupMap(1);
    }

    public void setupMap(int mapNum) {
        switch (mapNum) {
            case 1:
                setupMap1();
                break;
            case 2:
                setupMap2();
                break;
            case 3:
                setupMap3();
            default:
                System.err.println("No setup defined for map " + mapNum);
        }
    }

    // ─── Map 1 ────
    private void setupMap1() {

        // กล่องสมบัติ
        if (!chestOpened[0]) {
            gp.obj[0] = new OBJ_Chest(gp);
            gp.obj[0].worldX = 33 * gp.tileSize;
            gp.obj[0].worldY = 10 * gp.tileSize;
        }

        gp.obj[4] = new OBJ_Clock(gp);
        gp.obj[4].worldX = 10 * gp.tileSize;
        gp.obj[4].worldY = 1 * gp.tileSize - 15;

        gp.obj[5] = new OBJ_Shelf(gp);
        gp.obj[5].worldX = 6 * gp.tileSize;
        gp.obj[5].worldY = 1 * gp.tileSize - 15;

        gp.obj[7] = new OBJ_Shelf(gp);
        gp.obj[7].worldX = 5 * gp.tileSize;
        gp.obj[7].worldY = 1 * gp.tileSize - 15;

        gp.obj[8] = new OBJ_Shelf(gp);
        gp.obj[8].worldX = 4 * gp.tileSize;
        gp.obj[8].worldY = 1 * gp.tileSize - 15;

        gp.obj[9] = new OBJ_Shelf(gp);
        gp.obj[9].worldX = 3 * gp.tileSize;
        gp.obj[9].worldY = 1 * gp.tileSize - 15;

        gp.obj[10] = new OBJ_Shelf(gp);
        gp.obj[10].worldX = 2 * gp.tileSize;
        gp.obj[10].worldY = 1 * gp.tileSize - 15;

        gp.obj[11] = new OBJ_Shelf(gp);
        gp.obj[11].worldX = 1 * gp.tileSize;
        gp.obj[11].worldY = 1 * gp.tileSize - 15;

        gp.obj[12] = new OBJ_Shelf(gp);
        gp.obj[12].worldX = 7 * gp.tileSize;
        gp.obj[12].worldY = 1 * gp.tileSize - 15;

        // ใส่index เพิ่มจากเดิม ตอนนี้คือ 5 ถ้าซ้ำมันจะเขียนทับ

        // ตะเกียงสุ่มตำแหน่ง
        spawnSingleLantern(3);
        spawnSingleLantern(1);
        spawnSingleLantern(2);
        // // Ghost
        // gp.ghost[0] = new ghost(gp);
        // gp.ghost[0].worldX = gp.tileSize * 10;
        // gp.ghost[0].worldY = gp.tileSize * 15;
    }

    // ─── Map 2 ────
    private void setupMap2() {
        // ตะเกียงสุ่มตำแหน่ง
        spawnSingleLantern(0);

        // Ghost บน map2
        gp.ghost[0] = new ghost(gp);
        gp.ghost[0].worldX = gp.tileSize * 15;
        gp.ghost[0].worldY = gp.tileSize * 11;

        // gp.obj[6] = new OBJ_Shelf(gp);
        // gp.obj[6].worldX = 15 * gp.tileSize;
        // gp.obj[6].worldY = 5 * gp.tileSize - 15;

    }

    private void setupMap3() {
        gp.ghost[1] = new ghost2(gp);
        gp.ghost[1].worldX = gp.tileSize * 15;
        gp.ghost[1].worldY = gp.tileSize * 11;

        int[][] barrierPositions = {
                { 13, 13 },
                { 12, 13 },
        };
        spawnBarriers(barrierPositions, 3);

        gp.obj[13] = new OBJ_Book(gp);
        gp.obj[13].worldX = 32 * gp.tileSize;
        gp.obj[13].worldY = 8 * gp.tileSize;

        // ───── ทางออก (วางไว้ฝั่งตรงข้ามจาก barriers) ─────
        gp.obj[14] = new Client.object.OBJ_Exit(gp);
        gp.obj[14].worldX = 12 * gp.tileSize;
        gp.obj[14].worldY = 17 * gp.tileSize;
    }

    public void spawnBarriers(int[][] positions, int startIndex) {
        for (int i = 0; i < positions.length; i++) {
            int objIdx = startIndex + i;
            if (objIdx >= gp.obj.length) {
                System.err.println("obj[] เต็ม เพิ่มขนาด obj array ใน GamePanel");
                break;
            }
            gp.obj[objIdx] = new Client.object.OBJ_WoodBarrier(gp);
            gp.obj[objIdx].worldX = positions[i][0] * gp.tileSize;
            gp.obj[objIdx].worldY = positions[i][1] * gp.tileSize;
        }
    }

    public void setGhost() {
        // ใช้ setupMap แทนแล้ว เก็บไว้เพื่อ backward compat
    }

    public void spawnSingleLantern(int index) {
        int mapNum = gp.currentMap;

        // เก็บแล้วไม่ spawn อีก
        if (lanternCollected[mapNum][index]) {
            System.out.println("Lantern slot " + index + " on map " + mapNum + " already collected.");
            return;
        }

        if (lanternSpawned[mapNum][index]) {
            gp.obj[index] = new OBJ_Lantern(gp);
            gp.obj[index].worldX = lanternSavedX[mapNum][index];
            gp.obj[index].worldY = lanternSavedY[mapNum][index];
            System.out.println("Lantern restored at map " + mapNum + " slot " + index);
            return;
        }

        final int MAX_TRIES = 500;
        for (int tries = 0; tries < MAX_TRIES; tries++) {
            int col = rand.nextInt(gp.maxWorldCol);
            int row = rand.nextInt(gp.maxWorldRow);
            int tileNum = gp.tileM.mapTilenumber[col][row];
            if (tileNum == -1)
                continue;
            if (!gp.tileM.tile[0][tileNum].collision) {
                gp.obj[index] = new OBJ_Lantern(gp);
                gp.obj[index].worldX = col * gp.tileSize;
                gp.obj[index].worldY = row * gp.tileSize;
                lanternSpawned[mapNum][index] = true;
                lanternSavedX[mapNum][index] = gp.obj[index].worldX;
                lanternSavedY[mapNum][index] = gp.obj[index].worldY;
                System.out.println("Lantern spawned at: " + col + ", " + row);
                return;
            }
        }
        System.err.println("Could not place Lantern after " + MAX_TRIES + " tries.");
    }

    // ─── Chest State ────
    public final boolean[] chestOpened = new boolean[10];

    // เรียกตอน player เปิด chest
    public void onChestOpened(int index) {
        if (index >= 0 && index < chestOpened.length)
            chestOpened[index] = true;
    }

    // เรียกตอน player เก็บตะเกียง
    public void onLanternCollected(int mapNum, int index) {
        if (mapNum >= 0 && mapNum < lanternCollected.length)
            lanternCollected[mapNum][index] = true;
    }
}