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
        if (gp.language) {
            description = "[" + name + "]\nมันคือหนังสือ!";
        } else {
            description = "[" + name + "]\nThis is a book!";
        }
        setDialogue();
    }

    public void setDialogue() {
        if (gp.language) {
            dialogues[0] = "กล่องที่ห้องเก็บของนั้น\nหากจะเปิดต้องเคาะก่อนล่ะ..\nสักสี่ห้าทีละมั้ง";
        } else {
            dialogues[0] = "The chest in the storage room…\nIf you want to open it, you have to knock first…\nMaybe four or five times.";
        }

    }

    public void tell() {
        gp.ui.currentDialogue = dialogues[0];
    }
}