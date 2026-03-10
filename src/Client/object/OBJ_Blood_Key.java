package Client.object;

import Client.Client.GamePanel;
import Client.Entity.Entity;

public class OBJ_Blood_Key extends Entity {

    public OBJ_Blood_Key(GamePanel gp) {
        super(gp);
        name = "Blood key";
        down1 = setup("/Client/res/object/blood_key");
        collision = true;
        description = "";
        description = "[" + name + "]\nกุญแจปริศนา";
        setDialogue();
    }

    public void setDialogue() {
        dialogues[0] = "ใช้กับประตู";

    }

    public void tell() {
        gp.ui.currentDialogue = dialogues[0];
    }
}