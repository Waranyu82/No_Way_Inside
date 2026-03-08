package Client.Client;

import java.io.*;

public class SaveManager {

    private static final String SAVE_FILE = "save.txt";

    public static void save(GamePanel gp) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE));

            writer.println(gp.currentMap);
            writer.println(gp.player.worldX);
            writer.println(gp.player.worldY);
            writer.println(gp.currentLightSize);

            // บันทึก inventory
            writer.println(gp.player.inventory.size());
            for (int i = 0; i < gp.player.inventory.size(); i++) {
                writer.println(gp.player.inventory.get(i).name);
            }

            // บันทึกสถานะ lantern
            for (int m = 0; m < 4; m++) {
                for (int s = 0; s < 10; s++) {
                    writer.println(gp.assetteR.lanternCollected[m][s]);
                }
            }

            // บันทึกสถานะ chest
            for (int i = 0; i < 10; i++) {
                writer.println(gp.assetteR.chestOpened[i]);
            }
            System.out.println("บันทึกสำเร็จ");
            writer.close();

        } catch (IOException e) {
            System.out.println("บันทึกไม่สำเร็จ: " + e.getMessage());
        }
    }

    public static void load(GamePanel gp) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE));

            gp.currentMap = Integer.parseInt(reader.readLine());
            gp.player.worldX = Integer.parseInt(reader.readLine());
            gp.player.worldY = Integer.parseInt(reader.readLine());
            gp.currentLightSize = Integer.parseInt(reader.readLine());

            // โหลด inventory
            int inventorySize = Integer.parseInt(reader.readLine());
            gp.player.inventory.clear();
            for (int i = 0; i < inventorySize; i++) {
                String itemName = reader.readLine();
                switch (itemName) {
                    case "Blood key":
                        gp.player.inventory.add(new Client.object.OBJ_Blood_Key(gp));
                        break;
                    case "Axe":
                        gp.player.inventory.add(new Client.object.OBJ_Axe(gp));
                        break;
                    case "Book":
                        gp.player.inventory.add(new Client.object.OBJ_Book(gp));
                        break;
                }
            }

            // โหลดสถานะ lantern
            for (int m = 0; m < 4; m++) {
                for (int s = 0; s < 10; s++) {
                    gp.assetteR.lanternCollected[m][s] = Boolean.parseBoolean(reader.readLine());
                }
            }

            // โหลดสถานะ chest
            for (int i = 0; i < 10; i++) {
                gp.assetteR.chestOpened[i] = Boolean.parseBoolean(reader.readLine());
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("ไม่พบไฟล์ save: " + e.getMessage());
        }
    }

    public static void deleteSave() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("ลบ save แล้ว!");
        }
    }

    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }
}