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
        if (gp.language) {
            description = "[" + name + "]\nกุญแจปริศนา";
        } else {
            description = "[" + name + "]\nMysterious key.";
        }
        setDialogue();
    }

    public void setDialogue() {
        if (gp.language) {
            dialogues[0] = "ใช้กับประตู";
        } else {
            dialogues[0] = "Used on a door.";
        }

    }

    public void tell() {
        gp.ui.currentDialogue = dialogues[0];
    }
}