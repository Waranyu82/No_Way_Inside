package Client.Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RadialGradientPaint;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Client.tiles.Tilemanager;
// import Client.tiles.tile;

import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;
import Client.Entity.Entity;
import Client.Entity.Player;

public class GamePanel extends JPanel implements Runnable {

    public boolean DEBUG = false;

    // Screen Settings
    final int originalTileSize = 16;
    final int scale = 4;
    public int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int ScreenWidth = tileSize * maxScreenCol;
    public final int ScreenHeight = tileSize * maxScreenRow;
    public final int maxWorldCol = 35;
    public final int maxWorldRow = 25;
    public Tilemanager tileM = new Tilemanager(this);
    public Keyhandler keyH = new Keyhandler(this);
    Thread gameThread;

    // Full Screen
    public int ScreenWidth2 = ScreenWidth;
    public int ScreenHeight2 = ScreenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean fullScreenOn = false;

    // Player
    public Player player = new Player(this, keyH);

    // Entity
    public Entity obj[] = new Entity[20];
    public Entity ghost[] = new Entity[20];
    ArrayList<Entity> entityList = new ArrayList<>();

    // map
    public int currentMap = 1;
    private boolean justWarped = false;
    private int warpCooldown = 0;

    // Collision
    public assetSetter assetteR = new assetSetter(this);
    public CollisionChecker cChecker = new CollisionChecker(this);

    // FPS
    public int FPS = 120;

    // Vision System
    public int currentLightSize = 200;
    BufferedImage darkness;
    Graphics2D g2_dark;

    // Cache สำหรับ RadialGradientPaint (ไม่ต้อง new ทุก frame)
    private RadialGradientPaint cachedPlayerPaint;
    private int cachedPlayerLightSize = -1;
    private int cachedPlayerX = -1, cachedPlayerY = -1;
    private RadialGradientPaint cachedLanternPaint;
    // private static final int LANTERN_LIGHT_SIZE = 150;

    // UI
    public UI ui = new UI(this);

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int optionState = 2;
    public final int dialogueState = 3;
    public final int storyState = 5;
    public int dialogueReturnState;
    public final int gameOverState = 6;
    public final int winState = 7;

    // INVENTORY
    public int gameInventory;
    public final int inventoryState = 4;

    // Sound
    Sound sound = new Sound();
    private long lastScreamTime = 0;
    private final long ScreamCooldown = 2250;

    // ภาษา
    public boolean language = false;

    public GamePanel() {
        this.setPreferredSize(new DimensionUIResource(ScreenWidth, ScreenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

    }

    public void setupgame() {
        assetteR.setobject();
        assetteR.setGhost();
        gameState = titleState;
        setFullScreen(); // เปิด fullscreen ตั้งแต่เริ่ม
        // gameState = winState;

        darkness = new BufferedImage(ScreenWidth, ScreenHeight, BufferedImage.TYPE_INT_ARGB);
        g2_dark = (Graphics2D) darkness.getGraphics();

        // Full Screen
        tempScreen = new BufferedImage(ScreenWidth, ScreenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) tempScreen.getGraphics();

        // sound
        playmusic(0);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                SaveManager.save(GamePanel.this);
            }
        }));

    }

    // ─── Map Warp ──
    public void warpToMap(int mapNum, int spawnCol, int spawnRow) {
        // mapNum 99 = เงื่อนไขชนะ
        if (mapNum == 99) {
            SaveManager.deleteSave();
            gameState = winState;
            return;
        }

        currentMap = mapNum;
        playSE(5);

        tileM.loadMap(mapNum);

        player.worldX = spawnCol * tileSize;
        player.worldY = spawnRow * tileSize;
        player.direction = "down";

        obj = new Entity[20];
        ghost = new Entity[20];
        assetteR.setupMap(mapNum);

        warpCooldown = 120;
    }

    public void setFullScreen() {
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice();

        Main.window.dispose();
        if (!fullScreenOn) {
            Main.window.setUndecorated(true);
            gd.setFullScreenWindow(Main.window);
            fullScreenOn = true;
        } else {
            Main.window.setUndecorated(false);
            gd.setFullScreenWindow(null);
            Main.window.pack();
            Main.window.setLocationRelativeTo(null);
            fullScreenOn = false;
        }
        Main.window.setVisible(true);
        ScreenWidth2 = Main.window.getWidth();
        ScreenHeight2 = Main.window.getHeight();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1000000000 / FPS; // 120 FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        if (darkness == null) {
            setupgame();
        }

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                drawToTempScreen();
                drawToScreen();
                delta--;
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            player.update();
            checkWarpTile();
            // Update Ghost
            for (int i = 0; i < ghost.length; i++) {
                if (ghost[i] != null) {
                    ghost[i].update();

                    if (checkPlayerHit(ghost[i])) {
                        System.out.println("โดนผีหลอก!"); // เช็กใน Console
                        // player.setDefaultValues(); // รีเซ็ตตำแหน่งผู้เล่น (ตาย)
                        SaveManager.deleteSave(); // ลบ save เมื่อตาย
                        gameState = gameOverState;
                        long now = System.currentTimeMillis();

                        if (now - lastScreamTime > ScreamCooldown) {
                            playSE(6);
                            lastScreamTime = now;
                        }
                        // ghost.setDefaultValues(); // รีเซ็ตตำแหน่งผี (ตาย)
                    }
                }
            }
        }
    }

    private void checkWarpTile() {
        // นับ cooldown ลง
        if (warpCooldown > 0) {
            warpCooldown--;
            return;
        }

        int feetX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
        int feetY = player.worldY + player.solidArea.y + player.solidArea.height;

        Client.tiles.tile warpTile = tileM.getWarpTileAt(feetX, feetY);
        if (warpTile != null) {
            warpToMap(warpTile.warpToMap, warpTile.warpToCol, warpTile.warpToRow);
            warpCooldown = 120; // หยุด warp 120 frame หลังจาก warp แล้ว
        }
    }

    public void drawToTempScreen() {

        if (gameState == titleState) {
            ui.draw(g2);
        } else if (gameState == winState) {
            ui.drawWinScreen(g2);
        } else {
            // Tile
            tileM.draw(g2);

            // Add Entities to List
            entityList.add(player);
            for (int i = 0; i < ghost.length; i++) {
                if (ghost[i] != null)
                    entityList.add(ghost[i]);
            }
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null)
                    entityList.add(obj[i]);
            }

            // Sort
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    return Integer.compare(e1.worldY, e2.worldY);
                }
            });

            // Draw Entities
            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).draw(g2);
            }
            entityList.clear();

            // Vision & UI
            drawDarkness(g2);

            g2.setColor(java.awt.Color.GRAY);
            g2.fillRect(20, 20, 200, 20);

            int barWidth = (int) (player.stamina / (double) player.maxStamina * 200);
            g2.setColor(java.awt.Color.GREEN);
            g2.fillRect(20, 20, barWidth, 20);

            g2.setColor(java.awt.Color.BLACK);
            g2.drawRect(20, 20, 200, 20);

            ui.draw(g2);
        }
    }

    public void drawToScreen() {
        // BufferStrategy: วิธีเดียวที่ไม่ flicker บน Mac + Windows
        java.awt.image.BufferStrategy bs = Main.window.getBufferStrategy();
        if (bs == null) {
            Main.window.createBufferStrategy(3);
            return;
        }

        double srcRatio = (double) ScreenWidth / ScreenHeight;
        double dstRatio = (double) ScreenWidth2 / ScreenHeight2;

        int drawW, drawH, drawX, drawY;
        if (dstRatio >= srcRatio) {
            drawH = ScreenHeight2;
            drawW = (int) (ScreenHeight2 * srcRatio);
        } else {
            drawW = ScreenWidth2;
            drawH = (int) (ScreenWidth2 / srcRatio);
        }
        drawX = (ScreenWidth2 - drawW) / 2;
        drawY = (ScreenHeight2 - drawH) / 2;

        do {
            do {
                Graphics g = bs.getDrawGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, ScreenWidth2, ScreenHeight2);
                g.drawImage(tempScreen, drawX, drawY, drawW, drawH, null);
                g.dispose();
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());

        java.awt.Toolkit.getDefaultToolkit().sync();
    }

    public void drawDarkness(Graphics2D g2) {
        if (darkness == null)
            return;

        g2_dark.setComposite(AlphaComposite.Src);
        g2_dark.setColor(new Color(0, 0, 0));
        g2_dark.fillRect(0, 0, ScreenWidth, ScreenHeight);
        g2_dark.setComposite(AlphaComposite.DstOut);

        if (player != null) {
            drawLightCircle(player.screenX + (tileSize / 2), player.screenY + (tileSize / 2), currentLightSize);
        }

        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null && obj[i].name != null && obj[i].name.equals("Lantern")) {
                int screenX = obj[i].worldX - player.worldX + player.screenX;
                int screenY = obj[i].worldY - player.worldY + player.screenY;
                drawLightCircle(screenX + (tileSize / 2), screenY + (tileSize / 2), 50);
            }
        }
        g2_dark.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(darkness, 0, 0, null);
    }

    public void drawLightCircle(int x, int y, int size) {
        RadialGradientPaint gPaint;
        boolean isPlayerLight = (size == currentLightSize);
        if (isPlayerLight) {
            if (cachedPlayerPaint == null
                    || cachedPlayerLightSize != size
                    || cachedPlayerX != x
                    || cachedPlayerY != y) {
                cachedPlayerPaint = new RadialGradientPaint(x, y, size,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { Color.BLACK, new Color(0, 0, 0, 0) });
                cachedPlayerLightSize = size;
                cachedPlayerX = x;
                cachedPlayerY = y;
            }
            gPaint = cachedPlayerPaint;
        } else {
            if (cachedLanternPaint == null
                    || (int) cachedLanternPaint.getCenterPoint().getX() != x
                    || (int) cachedLanternPaint.getCenterPoint().getY() != y) {
                cachedLanternPaint = new RadialGradientPaint(x, y, size,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { Color.BLACK, new Color(0, 0, 0, 0) });
            }
            gPaint = cachedLanternPaint;
        }
        g2_dark.setPaint(gPaint);
        g2_dark.fillOval(x - size, y - size, size * 2, size * 2);
    }

    public boolean checkPlayerHit(Entity ghost) {
        boolean hit = false;

        // หาตำแหน่ง Hitbox ของ Player (พิกัดจริงในโลก)
        int playerLeftX = player.worldX + player.solidArea.x;
        int playerRightX = player.worldX + player.solidArea.x + player.solidArea.width;
        int playerTopY = player.worldY + player.solidArea.y;
        int playerBottomY = player.worldY + player.solidArea.y + player.solidArea.height;

        // หาตำแหน่ง Hitbox ของ Ghost
        int ghostLeftX = ghost.worldX + ghost.solidArea.x;
        int ghostRightX = ghost.worldX + ghost.solidArea.x + ghost.solidArea.width;
        int ghostTopY = ghost.worldY + ghost.solidArea.y;
        int ghostBottomY = ghost.worldY + ghost.solidArea.y + ghost.solidArea.height;

        // เช็กว่าสี่เหลี่ยม 2 อันนี้ซ้อนทับกันหรือไม่
        if (playerLeftX < ghostRightX &&
                playerRightX > ghostLeftX &&
                playerTopY < ghostBottomY &&
                playerBottomY > ghostTopY) {
            hit = true;
        }

        return hit;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
    }

    public void playmusic(int i) {
        sound.setFile(i);
        sound.play();
        sound.loop();
    }

    public void stopMusic() {
        sound.stop();
    }

    public void playSE(int i) {
        sound.playSE(i);

    }
}