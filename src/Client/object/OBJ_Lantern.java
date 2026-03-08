package Client.object;

import java.awt.Rectangle;

import Client.Client.GamePanel;
import Client.Entity.Entity;

public class OBJ_Lantern extends Entity {

    public OBJ_Lantern(GamePanel gp) {
        super(gp);

        name = "Lantern";
        down1 = setup("/Client/res/object/lantern0");
        collision = true;
        drawHeight = gp.tileSize - 20;
        drawWidth = gp.tileSize - 20;
        solidArea = new Rectangle(0, 0, drawWidth, drawHeight);
        setDialogue();
    }

    public void setDialogue() {
        dialogues[0] = "ระดับความสว่างเพิ่มมากขึ้น!";

    }

    public void tell() {
        gp.ui.currentDialogue = dialogues[0];
    }
}