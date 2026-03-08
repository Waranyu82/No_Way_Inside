package Client.Entity;

import Client.Client.GamePanel;

public class ghost2 extends ghost {

    public ghost2(GamePanel gp) {
        super(gp);

        name = "Ghost2";
        if (!gp.DEBUG) {
            Speed = 5;
        } else {
            Speed = 0;
        }
    }

    @Override
    public void getImage() {
        // ใส่ path รูปใหม่ของผีตัวนี้
        up1 = setup("/Client/res/ghost/ghost2_w");
        up2 = setup("/Client/res/ghost/ghost2_w");
        down1 = setup("/Client/res/ghost/ghost2_s");
        down2 = setup("/Client/res/ghost/ghost2_s");
        left1 = setup("/Client/res/ghost/ghost2_l");
        left2 = setup("/Client/res/ghost/ghost2_l");
        right1 = setup("/Client/res/ghost/ghost2_r");
        right2 = setup("/Client/res/ghost/ghost2_r");
    }
}