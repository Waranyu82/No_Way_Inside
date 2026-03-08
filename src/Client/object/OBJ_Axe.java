package Client.object;

import Client.Client.GamePanel;
import Client.Entity.Entity;

public class OBJ_Axe extends Entity {

    public OBJ_Axe(GamePanel gp) {
        super(gp);
        name = "Axe";
        down1 = setup("/Client/res/object/axe");
        collision = true;
        description = "";
        if (gp.language) {
            description = "[" + name + "]\nใช้สำหรับกำจัดสิ่งกีดขวาง";
        } else {
            description = "[" + name + "]\nUsed to remove obstacles.";
        }
    }

    public void setDialogue() {
        dialogues[0] = "Can't use this!!!";

    }

    public void tell() {
        gp.ui.currentDialogue = dialogues[0];
    }
}