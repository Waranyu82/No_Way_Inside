package Client.object;

import Client.Client.GamePanel;
import Client.Entity.Entity;
import java.awt.Rectangle;

public class OBJ_Chest extends Entity {

    public OBJ_Chest(GamePanel gp) {
        super(gp);
        name = "Chest";
        down1 = setup("/Client/res/object/chest");
        collision = true;
        drawHeight = gp.tileSize - 15;
        drawWidth = gp.tileSize - 15;
        solidArea = new Rectangle(0, 0, drawWidth, drawHeight);
        description = "";
        if (gp.language) {
            description = "[" + name + "]\nกล่องที่เต็มไปด้วยสมบัติ!";
        } else {
            description = "[" + name + "]\nA chest filled with treasures.";
        }
    }
}