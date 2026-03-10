package Client.Client;

import Client.Entity.Entity;
// import Client.tiles.tile;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// import java.awt.image.BufferedImage;
// import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.util.ArrayList;
// import Client.Entity.Player;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font Agency_FB;

    public int slotCol = 0;
    public int slotRow = 0;
    public int commandNum = 0;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public String currentDialogue = "";
    int subState = 0;

    // Door interaction UI
    public boolean showDoorLockedMessage = false;
    private int doorLockedTimer = 0;
    private static final int DOOR_MSG_DURATION = 120; // frames

    private BufferedImage controlImage;

    // ─── Story / Cutscene ─────────
    private BufferedImage[] storyPages;
    private int storyPageIndex = 0;
    private static final int STORY_PAGE_COUNT = 2; // เปลี่ยนตามจำนวนรูปที่มี
    private BufferedImage gameOverBG;
    private int gameOverCommand = 0; // 0 = New Game, 1 = Back to Menu

    // ─── Win Screen ──────────────────────────────
    private BufferedImage winBG;
    public int winAlphaCounter = 0;
    private static final int WIN_FADE_SPEED = 2; // เร็วขึ้น/ช้าลงได้ที่นี่

    public UI(GamePanel gp) {
        this.gp = gp;
        Agency_FB = new Font("Agency FB", Font.PLAIN, 40);

        try {
            controlImage = ImageIO.read(
                    getClass().getResourceAsStream("/Client/res/ui/control.png"));
        } catch (Exception e) {
            System.err.println("Cannot load control image: " + e.getMessage());
        }

        storyPages = new BufferedImage[STORY_PAGE_COUNT + 1];
        for (int i = 0; i < STORY_PAGE_COUNT; i++) {
            try {
                if (i == STORY_PAGE_COUNT - 1) {
                    storyPages[i] = ImageIO.read(
                            getClass().getResourceAsStream("/Client/res/ui/control.png"));
                } else {
                    storyPages[i] = ImageIO.read(
                            getClass().getResourceAsStream(
                                    "/Client/res/ui/story0" + (i + 1) + ".png"));
                }

            } catch (Exception e) {
                System.err.println("Cannot load story page " + (i + 1) + ": " + e.getMessage());
            }
        }

        // โหลดรูปพื้นหลัง game over
        try {
            gameOverBG = ImageIO.read(
                    getClass().getResourceAsStream("/Client/res/ui/gameover.png"));
        } catch (Exception e) {
            System.err.println("Cannot load gameover image: " + e.getMessage());
        }

        // โหลดรูปพื้นหลัง win (ถ้ามี) ถ้าไม่มีก็ใช้ fallback
        try {
            winBG = ImageIO.read(
                    getClass().getResourceAsStream("/Client/res/ui/win.png"));
        } catch (Exception e) {
            System.err.println("No win.png found, will use fallback.");
        }

    }

    /** กด Enter ข้ามหน้า — หน้าสุดท้ายเข้าเกมเลย */
    public void nextStoryPage() {
        storyPageIndex++;
        if (storyPageIndex > STORY_PAGE_COUNT) {
            storyPageIndex = 0;
            gp.gameState = gp.playState;
        }
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setFont(Agency_FB);
        g2.setColor(Color.white);

        // STORY STATE
        if (gp.gameState == gp.storyState) {
            drawStoryScreen();
        }

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            drawTitleSceen();
        }

        // PLAY STATE
        if (gp.gameState == gp.playState) {
            drawChestInteraction();
            drawDoorInteraction();
        }

        if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen();
        }

        if (gp.gameState == gp.winState) {
            drawWinScreen(g2);
        }

        // PAUSE STATE
        if (gp.gameState == gp.optionState) {
            drawOptionScreen();
        }
        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }

        // IVENTORY
        if (gp.gameState == gp.inventoryState) {
            drawInventory();
        }
    }

    public void drawGameOverScreen() {
        // วาดพื้นหลัง
        if (gameOverBG != null) {
            g2.drawImage(gameOverBG, 0, 0, gp.ScreenWidth, gp.ScreenHeight, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gp.ScreenWidth, gp.ScreenHeight);
        }

        // หัวข้อ GAME OVER
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));
        g2.setColor(new Color(180, 0, 0));
        String title = "GAME OVER";
        int tx = getXforcenterText(title);
        g2.drawString(title, tx, gp.ScreenHeight / 2 - 60);

        // ปุ่ม Back to Menu
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
        g2.setColor(Color.WHITE);
        String backMenu = "Back to Menu";
        tx = getXforcenterText(backMenu);
        g2.drawString(backMenu, tx, gp.ScreenHeight / 2 + 60);
        g2.drawString(">", tx - 30, gp.ScreenHeight / 2 + 60);
    }

    public int getGameOverCommand() {
        return gameOverCommand;
    }

    public void setGameOverCommand(int v) {
        gameOverCommand = v;
    }

    public void drawDoorInteraction() {
        Client.tiles.tile door = gp.player.nearDoorTile;
        if (door == null) {
            showDoorLockedMessage = false;
            doorLockedTimer = 0;
            return;
        }

        int boxW = gp.tileSize * 6;
        int boxH = gp.tileSize;
        int boxX = gp.ScreenWidth / 2 - boxW / 2;
        int boxY = gp.ScreenHeight - gp.tileSize * 2;

        if (door.isUnlocked)
            return; // ปลดล็อกแล้ว เดินผ่านได้เลย
        if (door.isUnlockedL) {
            return;
        }

        if (gp.currentMap == 2) {
            if (showDoorLockedMessage) {
                doorLockedTimer++;
                if (doorLockedTimer > DOOR_MSG_DURATION) {
                    showDoorLockedMessage = false;
                    doorLockedTimer = 0;
                }
                drawSubWindow(boxX, boxY, boxW, boxH);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
                g2.setColor(new Color(255, 80, 80));
                String msg = "";

                msg = "ในห้องมืดเกินไป ฉันไม่กล้าเข้าไป";

                int tx = gp.ScreenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(msg, g2).getWidth() / 2;
                g2.drawString(msg, tx, boxY + boxH / 2 + 8);
            } else {
                drawSubWindow(boxX, boxY, boxW, boxH);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
                g2.setColor(Color.WHITE);
                String msg = "";
                msg = "ต้องมีไฟ : " + gp.currentLightSize + " / 500";
                int tx = gp.ScreenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(msg, g2).getWidth() / 2;
                g2.drawString(msg, tx, boxY + boxH / 2 + 8);
            }
        }

        if (gp.currentMap != 2) {
            if (showDoorLockedMessage) {

                doorLockedTimer++;
                if (doorLockedTimer > DOOR_MSG_DURATION) {
                    showDoorLockedMessage = false;
                    doorLockedTimer = 0;
                }
                drawSubWindow(boxX, boxY, boxW, boxH);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
                g2.setColor(new Color(255, 80, 80));
                String msg = "";
                msg = "ต้องมีกุญแจก่อน!";
                int tx = gp.ScreenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(msg, g2).getWidth() / 2;
                g2.drawString(msg, tx, boxY + boxH / 2 + 8);
            } else {
                drawSubWindow(boxX, boxY, boxW, boxH);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
                g2.setColor(Color.WHITE);
                String prompt = "";
                prompt = "[E] ใช้กุญแจเปิดประตู";
                int tx = gp.ScreenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(prompt, g2).getWidth() / 2;
                g2.drawString(prompt, tx, boxY + boxH / 2 + 8);
            }
        }

    }

    public void drawChestInteraction() {
        if (gp.player.nearChestIndex == 999)
            return;

        Entity obj = gp.obj[gp.player.nearChestIndex];
        if (obj == null)
            return;

        // Shelf
        if (obj.name.equals("Shelf")) {
            Client.object.OBJ_Shelf shelf = (Client.object.OBJ_Shelf) obj;
            int boxW = gp.tileSize * 5;
            int boxH = gp.tileSize;
            int boxX = gp.ScreenWidth / 2 - boxW / 2;
            int boxY = gp.ScreenHeight - gp.tileSize * 2;
            drawSubWindow(boxX, boxY, boxW, boxH);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
            g2.setColor(shelf.searched ? Color.GRAY : Color.WHITE);
            String prompt = "";
            prompt = shelf.searched ? "ค้นหาแล้ว (ไม่มีอะไร)" : "[E] ค้นหาชั้นวางของ";
            int tx = gp.ScreenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(prompt, g2).getWidth() / 2;
            g2.drawString(prompt, tx, boxY + boxH / 2 + 8);
            return;
        }

        // Wood Barrier
        if (obj.name.equals("Wood Barrier")) {
            int boxW = gp.tileSize * 6;
            int boxH = gp.tileSize;
            int boxX = gp.ScreenWidth / 2 - boxW / 2;
            int boxY = gp.ScreenHeight - gp.tileSize * 2;
            drawSubWindow(boxX, boxY, boxW, boxH);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
            boolean hasAxe = false;
            for (int j = 0; j < gp.player.inventory.size(); j++) {
                if (gp.player.inventory.get(j).name.equals("Axe")) {
                    hasAxe = true;
                    break;
                }
            }
            g2.setColor(hasAxe ? Color.WHITE : new Color(255, 80, 80));
            String prompt = "";
            prompt = hasAxe ? "[E] ใช้ Axe ทำลายไม้กั้น" : "[E] ต้องใช้ Axe ก่อน!";
            int tx = gp.ScreenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(prompt, g2).getWidth() / 2;
            g2.drawString(prompt, tx, boxY + boxH / 2 + 8);
            return;
        }

        // Chest
        int current = gp.player.chestPressCount;
        int required = gp.player.CHEST_REQUIRED_PRESSES;

        // แสดง prompt กลางล่างหน้าจอ
        int boxW = gp.tileSize * 5;
        int boxH = gp.tileSize;
        int boxX = gp.ScreenWidth / 2 - boxW / 2;
        int boxY = gp.ScreenHeight - gp.tileSize * 2;
        drawSubWindow(boxX, boxY, boxW, boxH);

        // ข้อความ
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(Color.WHITE);
        String prompt = "";
        prompt = "[E] เปิดกล่อง  " + current + " / " + required;
        int textX = boxX + 20;
        int textY = boxY + boxH / 2 + 8;
        g2.drawString(prompt, textX, textY);

        // progress bar
        int barX = boxX + 20;
        int barY = boxY + boxH - 14;
        int barMaxW = boxW - 40;
        int barFillW = (int) ((current / (double) required) * barMaxW);

        g2.setColor(new Color(80, 80, 80));
        g2.fillRoundRect(barX, barY, barMaxW, 8, 4, 4);
        g2.setColor(new Color(200, 170, 80)); // สีทอง
        g2.fillRoundRect(barX, barY, barFillW, 8, 4, 4);
    }

    public void addMessage(String text) {

        message.add(text);
        messageCounter.add(0);
    }

    public void drawTitleSceen() {

        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.ScreenWidth, gp.ScreenHeight);

        // TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
        String gameTitle = "No Way Inside";

        int x = getXforcenterText(gameTitle); // พิกัดที่จะวาด TITLE
        int y = gp.tileSize * 3;

        // SHADOW
        g2.setColor(Color.gray);
        g2.drawString(gameTitle, x + 5, y + 5);

        // MAIN COLOR
        g2.setColor(Color.white); // SET COLOR TITLE
        g2.drawString(gameTitle, x, y); // DRAW TITLE

        // IMAGE
        x = gp.ScreenWidth / 2 - (gp.tileSize * 2) / 2;
        y += gp.tileSize / 3;
        g2.drawImage(gp.player.downAnim[0], x, y, gp.tileSize * 2, gp.tileSize * 2, null); // วาดรูป PLAYER ตามพิกัด

        // MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

        //
        String text = "NEWGAME";
        x = getXforcenterText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if (commandNum == 0) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "CONTINUE";
        x = getXforcenterText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 1) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        //
        text = "QUIT";
        x = getXforcenterText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 2) {
            g2.drawString(">", x - gp.tileSize, y);
        }

    }

    public void drawSubWindow(int x, int y, int width, int height) {

        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public void drawDialogueScreen() {

        int x = gp.tileSize * 2;
        int y = gp.tileSize * 8;
        int width = gp.ScreenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 3;
        drawSubWindow(x, y, width, height);

        x += gp.tileSize;
        y += gp.tileSize;
        // System.out.println(currentDialogue);
        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawInventory() {
        String textIn = "INVENTORY";
        int x = gp.tileSize * 11;
        int y = gp.tileSize * 8;
        g2.drawString(textIn, x, y);

        // FRAME
        int frameX = gp.tileSize;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 14;
        int frameHeight = gp.tileSize * 6;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // SLOT
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;

        // DRAW PLAY ITEM
        for (int i = 0; i < gp.player.inventory.size(); i++) {

            g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY, null);
            slotX += slotSize;

            if (i == 12 || i == 25 || i == 38 || i == 51) {
                slotX = slotXstart;
                slotY += slotSize;
            }
        }

        // CURSOR
        int cursorX = slotXstart + (slotSize * slotCol);
        int cursorY = slotYstart + (slotSize * slotRow);
        int cursorWidth = gp.tileSize;
        int cursorHeight = gp.tileSize;

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

        // description frame
        int dframeX = frameX;
        int dframeY = frameY + frameHeight + 1;
        int dframeWidth = gp.tileSize * 5;
        int dframeHeight = gp.tileSize * 4;

        // draw description text
        int textX = dframeX + 20;
        int textY = dframeY + gp.tileSize;
        g2.setFont(g2.getFont().deriveFont(20F));

        int itemIndex = getItemIndexOnSlot();

        if (itemIndex < gp.player.inventory.size()) {

            drawSubWindow(dframeX, dframeY, dframeWidth, dframeHeight);

            for (String line : gp.player.inventory.get(itemIndex).description.split("\n")) {

                g2.drawString(line, textX, textY);
                textY += 32;
            }
        }
    }

    public int getItemIndexOnSlot() {
        int itemIndex = slotCol + (slotRow * 13);
        return itemIndex;
    }

    public void drawOptionScreen() {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        // SUBWINDOW
        int frameX = gp.tileSize * 6;
        int frameY = gp.tileSize * 2;
        int frameWidth = gp.tileSize * 5;
        int frameHeight = gp.tileSize * 8;

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch (subState) {
            case 0:
                options_top(frameX, frameY);
                break;
            case 1:
                options_backtoTitleScreen(frameX, frameY);
                break;
            case 2:
                options_control();
                break;
        }
    }

    public void options_top(int frameX, int frameY) {
        int textX;
        int textY;

        // TITLE
        String text = "SETTINGS";
        textX = getXforcenterText(text) + 35;
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        // FULL SCREEN ON-OFF
        textX = frameX + gp.tileSize;
        textY += gp.tileSize;
        g2.drawString("Full screen", textX, textY);

        if (commandNum == 0) {
            g2.drawString(">", textX - 25, textY);

            if (gp.keyH.enterPressed == true) {
                gp.setFullScreen();
                gp.keyH.enterPressed = false;
            }
        }

        // MUSIC CONFIG
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);

        if (commandNum == 1) {
            g2.drawString(">", textX - 25, textY);
        }

        // SOUND EFFECT CONFIG
        textY += gp.tileSize;
        g2.drawString("SE", textX, textY);
        if (commandNum == 2) {
            g2.drawString(">", textX - 25, textY);
        }

        // CONTROL
        textY += gp.tileSize;
        g2.drawString("Control", textX, textY);
        if (commandNum == 3) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed == true) {
                subState = 2;
                gp.keyH.enterPressed = false;
            }
        }

        // END GAME BACK TO TITLE
        textY += (gp.tileSize * 2) - 28;
        g2.drawString("Return To Title", textX, textY);

        if (commandNum == 4) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed == true) {
                subState = 1;
                commandNum = 0;
                gp.keyH.enterPressed = false;
            }
        }

        // Full screen check box
        textX = frameX + gp.tileSize * 4;
        textY = frameY + gp.tileSize + 45;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(textX, textY, 24, 24);

        if (gp.fullScreenOn == true) {
            g2.fillRect(textX, textY, 24, 24);
        }

        // Music check box
        textY += gp.tileSize;
        g2.drawRect(textX - 100, textY, 120, 24);
        int musicVolumeWidth = 24 * gp.sound.musicVolumeScale;
        g2.fillRect(textX - 100, textY, musicVolumeWidth, 24);

        // SE check box
        textY += gp.tileSize;
        g2.drawRect(textX - 100, textY, 120, 24);
        int seVolumeWidth = 24 * gp.sound.seVolumeScale;
        g2.fillRect(textX - 100, textY, seVolumeWidth, 24);
    }

    // Back to title
    public void options_backtoTitleScreen(int frameX, int frameY) {
        int textX = frameX + gp.tileSize - 40;
        int textY = frameY + gp.tileSize * 2;

        String confirmText = "Return to title screen ?";

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(26.5F));
        g2.drawString(confirmText, textX, textY);

        // YES
        String text = "YES";
        textX = getXforcenterText(text) + 30;
        textY += gp.tileSize * 3;
        g2.drawString(text, textX, textY);

        if (commandNum == 0) {
            g2.drawString(">", textX - 25, textY);

            if (gp.keyH.enterPressed == true) {
                subState = 0;
                gp.gameState = gp.titleState;
            }
        }

        // NO
        text = "NO";
        textX = getXforcenterText(text) + 30;
        textY += gp.tileSize;
        g2.drawString(text, textX, textY);

        if (commandNum == 1) {
            g2.drawString(">", textX - 25, textY);

            if (gp.keyH.enterPressed == true) {
                subState = 0;
                commandNum = 2;
            }
        }
    }

    public void options_control() {
        // วาดรูปเต็มจอ
        if (controlImage != null) {
            g2.drawImage(controlImage, 0, 0, gp.ScreenWidth, gp.ScreenHeight, null);
        } else {
            // fallback ถ้าไม่มีรูป
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(28F));
            int tx = gp.tileSize;
            int ty = gp.tileSize * 2;
            g2.drawString("[ W A S D ] - เดิน", tx, ty);
            g2.drawString("[ SHIFT ]  - วิ่ง", tx, ty + 50);
            g2.drawString("[ E ]      - โต้ตอบ", tx, ty + 100);
            g2.drawString("[ I ]      - กระเป๋า", tx, ty + 150);
            g2.drawString("[ ESC ]    - หยุดชั่วคราว", tx, ty + 200);
        }

        // ปุ่ม Back
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(28F));
        String back = "[ ENTER ] Back";
        g2.drawString(back, gp.tileSize, gp.ScreenHeight - gp.tileSize);

        if (gp.keyH.enterPressed) {
            subState = 0;
            commandNum = 3;
            gp.keyH.enterPressed = false;
        }
    }

    public void drawStoryScreen() {
        // พื้นหลังดำ
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.ScreenWidth, gp.ScreenHeight);

        // หน้า hint ข้อความ (หลังภาพ story หมดแล้ว)
        if (storyPageIndex >= STORY_PAGE_COUNT) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
            g2.setColor(Color.WHITE);
            String line3 = "";
            line3 = "มีไฟอยู่รอบๆ เก็บเพื่อเพิ่มแสงสว่าง";
            int x3 = getXforcenterText(line3);
            int cy = gp.ScreenHeight / 2 - 30;
            g2.drawString(line3, x3, cy + 55);

            g2.setColor(new Color(255, 255, 255, 180));
            g2.setFont(g2.getFont().deriveFont(22F));
            String hint = "[ ENTER ] Start Game";
            int hx = gp.ScreenWidth - (int) g2.getFontMetrics().getStringBounds(hint, g2).getWidth() - 20;
            g2.drawString(hint, hx, gp.ScreenHeight - 20);
            return;
        }

        // วาดภาพหน้าปัจจุบัน
        if (storyPages != null && storyPageIndex < storyPages.length
                && storyPages[storyPageIndex] != null) {
            g2.drawImage(storyPages[storyPageIndex],
                    0, 0, gp.ScreenWidth, gp.ScreenHeight, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(30F));
            g2.drawString("Story page " + (storyPageIndex + 1), gp.tileSize, gp.ScreenHeight / 2);
        }

        // hint ล่างขวา
        g2.setColor(new Color(255, 255, 255, 180));
        g2.setFont(g2.getFont().deriveFont(22F));
        String hint = "[ ENTER ] Next";
        int hx = gp.ScreenWidth - (int) g2.getFontMetrics().getStringBounds(hint, g2).getWidth() - 20;
        g2.drawString(hint, hx, gp.ScreenHeight - 20);

        // เลขหน้า
        g2.setColor(new Color(255, 255, 255, 120));
        g2.setFont(g2.getFont().deriveFont(18F));
        String page = (storyPageIndex + 1) + " / " + STORY_PAGE_COUNT;
        g2.drawString(page, 20, gp.ScreenHeight - 20);
    }

    public void drawWinScreen(Graphics2D g2) {
        this.g2 = g2;

        // Fade in counter (หยุดที่ 255)
        if (winAlphaCounter < 255) {
            winAlphaCounter += WIN_FADE_SPEED;
            if (winAlphaCounter > 255)
                winAlphaCounter = 255;
        }
        float alpha = winAlphaCounter / 255f;

        // ─── พื้นหลัง ─────────────────────────────────────
        if (winBG != null) {
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(winBG, 0, 0, gp.ScreenWidth, gp.ScreenHeight, null);
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
        } else {
            // fallback: gradient มืด → น้ำเงินเข้ม
            g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            java.awt.GradientPaint grad = new java.awt.GradientPaint(
                    0, 0, new Color(5, 5, 20),
                    0, gp.ScreenHeight, new Color(10, 30, 60));
            g2.setPaint(grad);
            g2.fillRect(0, 0, gp.ScreenWidth, gp.ScreenHeight);
        }

        // ─── เส้นประดับบนล่าง ─────────────────────────────
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha * 0.6f));
        g2.setColor(new Color(200, 180, 100));
        g2.setStroke(new BasicStroke(2));
        int margin = 30;
        g2.drawLine(margin, margin, gp.ScreenWidth - margin, margin);
        g2.drawLine(margin, gp.ScreenHeight - margin, gp.ScreenWidth - margin, gp.ScreenHeight - margin);
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));

        // ─── YOU ESCAPED ──────────────────────────────────
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 88F));

        // Shadow
        String titleEN = "YOU ESCAPED";
        int tx = getXforcenterText(titleEN);
        g2.setColor(new Color(30, 60, 100, 180));
        g2.drawString(titleEN, tx + 5, gp.ScreenHeight / 2 - 100 + 5);

        // Main text — สีทองอ่อน
        g2.setColor(new Color(230, 210, 120));
        g2.drawString(titleEN, tx, gp.ScreenHeight / 2 - 100);

        // ─── ข้อความภาษาไทย/Eng ──────────────────────────
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        g2.setColor(new Color(200, 200, 200));

        String sub1, sub2;
        sub1 = "คุณรอดจากบ้านหลังนั้นได้";
        sub2 = "แต่ฝันร้ายยังคงอยู่...";

        int subX1 = getXforcenterText(sub1);
        int subX2 = getXforcenterText(sub2);
        g2.drawString(sub1, subX1, gp.ScreenHeight / 2 + 10);

        g2.setColor(new Color(160, 160, 180));
        g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 26F));
        g2.drawString(sub2, subX2, gp.ScreenHeight / 2 + 55);

        // ─── แสงสว่างในมือ ────────────────────────────────
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
        g2.setColor(new Color(180, 160, 90));
        String lightText;
        lightText = "แสงสว่างที่รวบรวมได้: " + gp.currentLightSize;
        int lx = getXforcenterText(lightText);
        g2.drawString(lightText, lx, gp.ScreenHeight / 2 + 110);

        // ─── กด Enter กลับ Title ──────────────────────────
        // กะพริบ blink ตาม winAlphaCounter
        boolean blink = (System.currentTimeMillis() / 600) % 2 == 0;
        if (blink) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26F));
            g2.setColor(new Color(220, 220, 255));
            String back;
            back = "[ ENTER ]  กลับหน้าหลัก";
            int bx = getXforcenterText(back);
            g2.drawString(back, bx, gp.ScreenHeight - 50);
        }

        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
    }

    public int getXforcenterText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.ScreenWidth / 2 - length / 2;
        return x;
    }

}