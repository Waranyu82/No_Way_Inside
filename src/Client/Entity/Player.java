package Client.Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Client.Client.GamePanel;
import Client.Client.Keyhandler;
import Client.object.OBJ_Axe;
import Client.object.OBJ_Blood_Key;
import Client.object.OBJ_Book;
// import Client.tiles.tile;

public class Player extends Entity {

    private final Keyhandler keyH;

    public final int screenX;
    public final int screenY;

    // Animation frames (8 frames ต่อทิศ)
    private final BufferedImage[] upAnim = new BufferedImage[8];
    public final BufferedImage[] downAnim = new BufferedImage[8];
    private final BufferedImage[] leftAnim = new BufferedImage[8];
    private final BufferedImage[] rightAnim = new BufferedImage[8];

    // Stamina
    public int maxStamina = 1000;
    public int stamina = maxStamina;
    public int staminaDrain = 15;
    public int staminaRegen = 5;
    public boolean exhausted = false;
    private static final int EXHAUST_THRESHOLD = 20;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 65;

    // Chest interaction
    public static final int CHEST_REQUIRED_PRESSES = 5;
    public int chestPressCount = 0;
    public int nearChestIndex = 999;
    private boolean lastEState = false;

    // Door interaction
    public Client.tiles.tile nearDoorTile = null;
    private int tmp = 0;

    // Heart
    private long lastHeartTime = 0;
    private final long heartCooldown = 1250; // ระยะเวลาของเสียงหัวใจ

    public Player(GamePanel gp, Keyhandler keyH) {
        super(gp);
        this.keyH = keyH;

        screenX = gp.ScreenWidth / 2 - gp.tileSize / 2;
        screenY = gp.ScreenHeight / 2 - gp.tileSize / 2;

        solidArea = new Rectangle(
                gp.tileSize / 4,
                gp.tileSize / 2,
                gp.tileSize / 2,
                gp.tileSize / 2);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
        setItem();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 14;
        worldY = gp.tileSize * 9;
        running = 8;
        Speed = 4;
        direction = "down";
        stamina = maxStamina;
        exhausted = false;
    }

    public void setItem() {
    }

    private void getPlayerImage() {
        try {
            BufferedImage sheet = ImageIO.read(
                    getClass().getResourceAsStream("/Client/res/player/walk.png"));
            int fw = 64, fh = 64;
            for (int i = 0; i < 8; i++) {
                upAnim[i] = sheet.getSubimage(i * fw, 0 * fh, fw, fh);
                leftAnim[i] = sheet.getSubimage(i * fw, 1 * fh, fw, fh);
                downAnim[i] = sheet.getSubimage(i * fw, 2 * fh, fw, fh);
                rightAnim[i] = sheet.getSubimage(i * fw, 3 * fh, fw, fh);
            }
            System.out.println("Player sprite loaded.");
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading player sprite: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        boolean moving = keyH.upPreesed || keyH.downPressed
                || keyH.leftPressed || keyH.rightPressed;

        updateStamina(moving);

        boolean canRun = !exhausted && stamina > 0;
        boolean wantRun = keyH.shiftPressed && moving && canRun;
        int speed = wantRun ? running : Speed;
        if (gp.DEBUG) {
            speed = 15;
        }

        int dx = 0, dy = 0;

        if (keyH.upPreesed) {
            dy -= speed;
            direction = "up";
        }
        if (keyH.downPressed) {
            dy += speed;
            direction = "down";
        }
        if (keyH.leftPressed) {
            dx -= speed;
            direction = "left";
        }
        if (keyH.rightPressed) {
            dx += speed;
            direction = "right";
        }

        if (moving) {
            // แยกแกน Y
            worldY += dy;
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, false); // เช็ค object collision แกน Y
            if (collisionOn)
                worldY -= dy;

            // แยกแกน X
            worldX += dx;
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, false); // เช็ค object collision แกน X
            if (collisionOn)
                worldX -= dx;

            // Animate
            if (++spriteCounter > 7) {
                spriteNum = (spriteNum % 8) + 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
        }

        int objIndex = gp.cChecker.checkObject(this, true);
        pickUpObject(objIndex);

        // เช็ก door ใกล้ๆ
        int feetX = worldX + solidArea.x + solidArea.width / 2;
        int feetY = worldY + solidArea.y + solidArea.height;
        nearDoorTile = gp.tileM.getNearDoorTile(feetX, feetY);

        // กด E ใกล้ประตู
        boolean eNowDoor = keyH.ePressed;

        // ประตูที่ใช้กุญแจ (map 1 → map 2)
        if (nearDoorTile != null && nearDoorTile.requiresKey && !nearDoorTile.isUnlocked) {
            if (eNowDoor && !lastEState) {
                boolean hasKey = false;
                int keyIdx = -1;
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).name.equals("Blood key")) {
                        hasKey = true;
                        keyIdx = i;
                        break;
                    }
                }
                if (hasKey) {
                    inventory.remove(keyIdx);
                    nearDoorTile.isUnlocked = true;
                    gp.playSE(5);
                    System.out.println("Door unlocked!");
                } else {
                    gp.ui.showDoorLockedMessage = true;
                }
            }
        }

        if (nearDoorTile != null && nearDoorTile.qust_light && !nearDoorTile.isUnlockedL) {
            if (gp.currentLightSize >= 500) {
                nearDoorTile.isUnlockedL = true;
                gp.playSE(5);
                System.out.println("Door unlocked by light!");
            } else {
                gp.ui.showDoorLockedMessage = true;
            }
        }

        if (!eNowDoor)
            lastEState = false;

        long now = System.currentTimeMillis();
        boolean ghostClose = false;

        for (int i = 0; i < gp.ghost.length; i++) {

            if (gp.ghost[i] != null) { // เช็คว่าผีอยู่ใกล้ไหม ถ้าใกล้ให้มันเล่นเสียงหัวใจ
                int diffX = gp.ghost[i].worldX - worldX;
                int diffY = gp.ghost[i].worldY - worldY;
                double distance = Math.sqrt(diffX * diffX + diffY * diffY);
                int dangerRange = 250; // ปรับระยะได้ตรงนี้เลย

                if (distance <= dangerRange) {
                    ghostClose = true;
                    break;
                }
            }
        }

        if (ghostClose && now - lastHeartTime > heartCooldown) {
            gp.playSE(2);
            lastHeartTime = now;
        }
    }

    private void updateStamina(boolean moving) {
        if (stamina <= 0)
            exhausted = true;
        if (stamina >= EXHAUST_THRESHOLD)
            exhausted = false;

        boolean wantRun = keyH.shiftPressed && moving;
        boolean canRun = !exhausted && stamina > 0;

        if (wantRun && canRun) {
            stamina = Math.max(0, stamina - staminaDrain);
        } else if (!keyH.shiftPressed) {
            stamina = Math.min(maxStamina, stamina + staminaRegen);
        }
    }

    public void pickUpObject(int i) {
        if (i == 999 || gp.obj[i] == null) {
            if (nearChestIndex != 999) {
                nearChestIndex = 999;
                chestPressCount = 0;
            }
            return;
        }

        switch (gp.obj[i].name) {
            case "Book":
                inventory.add(new OBJ_Book(gp));
                gp.obj[i] = null;
                break;

            case "Exit":
                // ผู้เล่นแตะทางออก → ชนะ!
                gp.warpToMap(99, 0, 0);
                break;

            case "Lantern":
                gp.playSE(3);
                gp.currentLightSize += 100;
                gp.obj[i] = null;
                gp.assetteR.onLanternCollected(gp.currentMap, i); // บันทึกว่าเก็บแล้ว
                System.out.println("Light Up!" + gp.currentLightSize);
                break;

            case "Chest":
                // ถ้าเปลี่ยน chest ที่ยืนอยู่ → reset counter
                if (nearChestIndex != i) {
                    nearChestIndex = i;
                    chestPressCount = 0;
                }

                // detect rising edge ของ E (กดใหม่แต่ละครั้ง)
                boolean eNow = keyH.ePressed;
                if (eNow && !lastEState) {
                    chestPressCount++;
                    gp.playSE(1); // เสียง feedback แต่ละครั้ง
                    System.out.println("Chest E press: " + chestPressCount + "/" + CHEST_REQUIRED_PRESSES);

                    if (chestPressCount >= CHEST_REQUIRED_PRESSES) {
                        gp.obj[i] = null;
                        nearChestIndex = 999;
                        chestPressCount = 0;
                        gp.assetteR.onChestOpened(i); // บันทึกว่าเปิดแล้ว
                        inventory.add(new OBJ_Axe(gp));
                    }
                }
                lastEState = eNow;
                break;

            case "Wood Barrier":
                // ต้องมี Axe ถึงจะทำลายได้
                if (nearChestIndex != i) {
                    nearChestIndex = i;
                    chestPressCount = 0;
                }
                boolean eNowBarrier = keyH.ePressed;
                if (eNowBarrier && !lastEState) {
                    boolean hasAxe = false;
                    // int axeIdx = -1;
                    for (int j = 0; j < inventory.size(); j++) {
                        if (inventory.get(j).name.equals("Axe")) {
                            hasAxe = true;
                            // axeIdx = j;
                            break;
                        }
                    }
                    if (hasAxe) {
                        // inventory.remove(axeIdx);
                        gp.obj[i] = null;
                        nearChestIndex = 999;
                        gp.playSE(1);
                        System.out.println("Wood Barrier destroyed!");
                    } else {
                        gp.obj[i].tell();
                        gp.dialogueReturnState = gp.playState;
                        gp.gameState = gp.dialogueState;
                    }
                }
                lastEState = eNowBarrier;
                break;

            case "Shelf":

                boolean eNowShelf = keyH.ePressed;
                if (eNowShelf && !lastEState) {
                    ((Client.object.OBJ_Shelf) gp.obj[i]).searched();
                    if (((Client.object.OBJ_Shelf) gp.obj[i]).getKeys() || tmp == 6) {
                        inventory.add(new OBJ_Blood_Key(gp));
                    }
                    tmp++;
                }
                lastEState = eNowShelf;
                break;

            default:
                break;
        }
    }

    public void useItem(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= inventory.size())
            return;

        String itemName = inventory.get(itemIndex).name;

        switch (itemName) {

            case "Book":
                inventory.get(itemIndex).tell();
                gp.dialogueReturnState = gp.inventoryState;
                gp.gameState = gp.dialogueState;
                break;

            case "Axe":
                gp.playSE(1);
                // inventory.remove(itemIndex);
                System.out.println("Axe used!");
                gp.dialogueReturnState = gp.inventoryState;
                gp.gameState = gp.playState;
                break;

            case "Blood key":
                gp.playSE(5);
                gp.dialogueReturnState = gp.inventoryState;
                gp.gameState = gp.dialogueState;
                break;

            default:
                if (gp.language) {
                    gp.ui.currentDialogue = "[" + itemName + "] ยังไม่สามารถใช้ได้ตอนนี้";
                } else {
                    gp.ui.currentDialogue = "[" + itemName + "] Cannot be used right now.";
                }
                gp.gameState = gp.dialogueState;
                break;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage[] anim;
        switch (direction) {
            case "up":
                anim = upAnim;
                break;
            case "left":
                anim = leftAnim;
                break;
            case "right":
                anim = rightAnim;
                break;
            default:
                anim = downAnim;
                break;
        }

        BufferedImage img = anim[spriteNum - 1];
        if (img != null) {
            g2.drawImage(img, screenX, screenY, gp.tileSize, gp.tileSize, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
        }

        // Debug hitbox (ลบออกได้เมื่อ ship)
        if (gp.DEBUG) {
            g2.setColor(Color.RED);
            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y,
                    solidArea.width, solidArea.height);
        }
    }
}