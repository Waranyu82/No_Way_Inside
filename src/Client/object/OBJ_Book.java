package Client.object;

import Client.Client.GamePanel;
import Client.Entity.Entity;

public class OBJ_Book extends Entity {

    public OBJ_Book(GamePanel gp) {
        super(gp);
        name = "Book";
        down1 = setup("/Client/res/object/book");
        collision = true;
        description = "";
        description = "[" + name + "]\nมันคือหนังสือ!";
        setDialogue();
    }

    public void setDialogue() {
        dialogues[0] = "กล่องที่ห้องเก็บของนั้น\nหากจะเปิดต้องเคาะก่อนล่ะ..\nสักสี่ห้าทีละมั้ง";

    }

    public void tell() {
        gp.ui.currentDialogue = dialogues[0];
    }
}