package Client.object;

import Client.Entity.Entity;
import Client.Client.GamePanel;
import java.awt.Rectangle;

public class OBJ_Clock extends Entity {
    public OBJ_Clock(GamePanel gp) {
        super(gp);
        name = "Clock";
        down1 = setup("/Client/res/object/wood_ck");
        collision = true;
        isBarrier = true;
        drawHeight = 2 * gp.tileSize - 8;
        drawWidth = gp.tileSize - 12;
        solidArea = new Rectangle(0, 0, drawWidth, drawHeight);
    }

}
