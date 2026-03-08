package Client.Entity;

import Client.Client.GamePanel;
import java.util.Random;

public class ghost extends Entity {

    private int lastWorldX, lastWorldY;
    private int stuckCounter = 0;
    private static final int STUCK_THRESHOLD = 20;

    private int chaseUpdateCounter = 0;
    private static final int CHASE_UPDATE_RATE = 20;
    private static final int ROAM_UPDATE_RATE = 80;

    private final Random random = new Random();

    public void setDefaultValues() {
        worldX = gp.tileSize * 3;
        worldY = gp.tileSize * 18;
        direction = "down";
    }

    public ghost(GamePanel gp) {
        super(gp);

        name = "Ghost";
        if (!gp.DEBUG) {
            Speed = 2;
        } else {
            Speed = 0;
        }
        maxLife = 4;
        life = maxLife;

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/Client/res/ghost/ghostnew_r");
        up2 = setup("/Client/res/ghost/ghostnew_l");
        down1 = setup("/Client/res/ghost/ghostnew_r");
        down2 = setup("/Client/res/ghost/ghostnew_l");
        left1 = setup("/Client/res/ghost/ghostnew_l");
        left2 = setup("/Client/res/ghost/ghostnew_l");
        right1 = setup("/Client/res/ghost/ghostnew_r");
        right2 = setup("/Client/res/ghost/ghostnew_r");
    }

    @Override
    public void update() {
        setAction();

        collisionOn = false;
        gp.cChecker.checkTile(this); // ยังชนกำแพงปกติ
        // gp.cChecker.checkObject(this, false); // ยังชน object ปกติ
        gp.cChecker.checkEntity(this, gp.ghost); // ชนผีด้วยกัน

        if (!collisionOn) {
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
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

    }

    @Override
    public void setAction() {

        if (worldX == lastWorldX && worldY == lastWorldY) {
            stuckCounter++;
        } else {
            stuckCounter = 0;
        }
        lastWorldX = worldX;
        lastWorldY = worldY;

        if (stuckCounter >= STUCK_THRESHOLD) {
            pickEscapeDirection();
            stuckCounter = 0;
            chaseUpdateCounter = 0;
            return;
        }

        int diffX = gp.player.worldX - worldX;
        int diffY = gp.player.worldY - worldY;
        int distance = Math.abs(diffX) + Math.abs(diffY);
        int detectionRange = gp.tileSize * 10;

        chaseUpdateCounter++;

        boolean isChasing = distance <= detectionRange;
        int updateRate = isChasing ? CHASE_UPDATE_RATE : ROAM_UPDATE_RATE;

        if (chaseUpdateCounter >= updateRate) {
            chaseUpdateCounter = 0;

            if (isChasing) {
                // เดินเข้าหา Player โดยเลือกแกนที่ห่างกว่า
                if (Math.abs(diffX) >= Math.abs(diffY)) {
                    direction = (diffX > 0) ? "right" : "left";
                } else {
                    direction = (diffY > 0) ? "down" : "up";
                }
            } else {
                // เดินมั่ว
                pickRandomDirection();
            }
        }

        if (chaseUpdateCounter >= updateRate) {
            chaseUpdateCounter = 0;

        }

    }

    // เลือกทิศที่ไม่ใช่ทิศปัจจุบัน (หนีจากกำแพง)
    private void pickEscapeDirection() {
        String[] options = { "up", "down", "left", "right" };
        // สลับลำดับแบบสุ่มก่อน
        for (int i = options.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            String tmp = options[i];
            options[i] = options[j];
            options[j] = tmp;
        }
        // เลือกอันแรกที่ไม่ใช่ทิศปัจจุบัน
        for (String d : options) {
            if (!d.equals(direction)) {
                direction = d;
                return;
            }
        }
        direction = options[0];
    }

    private void pickRandomDirection() {
        int i = random.nextInt(4);
        switch (i) {
            case 0:
                direction = "up";
                break;
            case 1:
                direction = "down";
                break;
            case 2:
                direction = "left";
                break;
            default:
                direction = "right";
                break;
        }
    }
}