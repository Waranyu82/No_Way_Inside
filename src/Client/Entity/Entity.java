package Client.Entity;

import Client.Client.GamePanel;
import Client.Client.UtilityTool;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;

public class Entity {

    public GamePanel gp;
    public int worldX, worldY;
    public int Speed, running;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction = "down";
    public String name;

    public int spriteCounter = 0;
    public int spriteNum = 1;
    protected String dialogues[] = new String[20];
    public String description = "";
    public int dialogIndex = 0;

    // Hitbox
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    // สถานะ
    public int maxLife;
    public int life;
    public int actionLookCounter = 0;
    public boolean collision = false;
    public boolean isBarrier = false;
    public BufferedImage image;

    public int drawWidth = -1;
    public int drawHeight = -1;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    // Override
    public void setAction() {
    }

    // Override
    public void tell() {
    }

    public void resetPosition() {
    }

    public void update() {
        setAction();
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkEntity(this, gp.ghost);
        gp.cChecker.checkPlayer(this);

        if (collisionOn == false) {
            switch (direction) {
                case "up":
                    worldY -= Speed;
                    break;
                case "down":
                    worldY += Speed;
                    break;
                case "left":
                    worldX -= Speed;
                    break;
                case "right":
                    worldX += Speed;
                    break;
            }
        }

        spriteCounter++;
        if (spriteCounter > 12) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage img = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            switch (direction) {
                case "up":
                    if (spriteNum == 1)
                        img = up1;
                    if (spriteNum == 2)
                        img = up2;
                    break;
                case "down":
                    if (spriteNum == 1)
                        img = down1;
                    if (spriteNum == 2)
                        img = down2;
                    break;
                case "left":
                    if (spriteNum == 1)
                        img = left1;
                    if (spriteNum == 2)
                        img = left2;
                    break;
                case "right":
                    if (spriteNum == 1)
                        img = right1;
                    if (spriteNum == 2)
                        img = right2;
                    break;
            }
            if (img == null) {
                img = image;
            }

            int dw = drawWidth > 0 ? drawWidth : gp.tileSize;
            int dh = drawHeight > 0 ? drawHeight : gp.tileSize;

            g2.drawImage(img, screenX, screenY, dw, dh, null);
            if (gp.DEBUG) {
                g2.setColor(Color.RED);
                g2.drawRect(screenX + solidArea.x, screenY + solidArea.y,
                        solidArea.width, solidArea.height);
            }
        }
    }

    public BufferedImage setup(String imagePath) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage scaledImage = null;
        try {
            scaledImage = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            scaledImage = uTool.scaleImage(scaledImage, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledImage;
    }
}