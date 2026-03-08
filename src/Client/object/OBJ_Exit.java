package Client.object;

import Client.Client.GamePanel;
import Client.Entity.Entity;
import java.awt.Rectangle;

public class OBJ_Exit extends Entity {

    public OBJ_Exit(GamePanel gp) {
        super(gp);
        name = "Exit";
        collision = false;
        isBarrier = false;

        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = gp.tileSize;
        solidArea.height = gp.tileSize;

        down1 = setup("/Client/res/object/Exit");
        image = down1;
        drawHeight = gp.tileSize;
        drawWidth = 2 * gp.tileSize;
        solidArea = new Rectangle(drawWidth, drawHeight);
        if (gp.language) {
            description = "ทางออก... ในที่สุดก็เจอแล้ว";
        } else {
            description = "The exit... you finally found it.";
        }
    }

}
