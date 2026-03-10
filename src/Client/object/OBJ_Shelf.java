package Client.object;

import Client.Client.GamePanel;
import Client.Entity.Entity;
import java.awt.Rectangle;

public class OBJ_Shelf extends Entity {

    public boolean searched = false;
    public boolean getKey = false;
    private int tmp = 0;

    public OBJ_Shelf(GamePanel gp) {
        super(gp);
        name = "Shelf";
        down1 = setup("/Client/res/object/wood_shelf");
        collision = true;
        isBarrier = true;
        drawHeight = 2 * gp.tileSize;
        drawWidth = gp.tileSize;
        solidArea = new Rectangle(0, 0, drawWidth, drawHeight);

        dialogues[0] = "คุณค้นชั้นวาง... ไม่พบอะไรเป็นประโยชน์";
        dialogues[1] = "มีแต่ฝุ่นและกองหนังสือเก่าๆ";
        dialogues[2] = "คุณพบบางอย่างซ่อนอยู่ด้านหลัง!";
    }

    public void searched() {
        if (searched) {
            gp.ui.currentDialogue = "คุณได้ค้นที่นี่แล้ว...";

        } else {
            searched = true;
            int roll = new java.util.Random().nextInt(10);
            if (roll < 5) {
                int x = new java.util.Random().nextInt(2);
                if (tmp == 6)
                    x = 1;
                switch (x) {
                    case 0:
                        gp.ui.currentDialogue = dialogues[1];
                        tmp++;
                        break;
                    case 1:
                        gp.ui.currentDialogue = dialogues[2];
                        getKey = true;
                        tmp = 7;
                        break;
                }
            } else {
                gp.ui.currentDialogue = dialogues[0];
                tmp++;
            }
        }
        gp.dialogueReturnState = gp.playState; // ← กลับ play ไม่ใช่ inventory
        gp.gameState = gp.dialogueState;
    }

    public boolean getKeys() {
        return getKey;
    }
}