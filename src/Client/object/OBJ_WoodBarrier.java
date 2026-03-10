package Client.object;

import java.awt.Rectangle;

import Client.Client.GamePanel;
import Client.Entity.Entity;

public class OBJ_WoodBarrier extends Entity {

    public OBJ_WoodBarrier(GamePanel gp) {
        super(gp);
        name = "Wood Barrier";
        down1 = setup("/Client/res/object/wood_barrier");
        collision = true;
        isBarrier = true;
        drawHeight = 2 * gp.tileSize;
        drawWidth = gp.tileSize;
        solidArea = new Rectangle(0, 0, drawWidth, drawHeight);
        setDialogue();
    }

    public void setDialogue() {
        dialogues[0] = "ไม้กั้นนี้แน่นมาก... ต้องใช้ขวานทำลาย";
    }

    @Override
    public void tell() {
        gp.ui.currentDialogue = dialogues[0];
    }
}