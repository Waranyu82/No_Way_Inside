package Client.Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyhandler implements KeyListener {
    GamePanel gp;

    public boolean upPreesed, downPressed, leftPressed, rightPressed, shiftPressed;
    public boolean enterPressed;
    public boolean ePressed; // ใช้ interact กับ object

    private long lastStepTime = 0;
    private final long stepCooldown = 400; // หน่วง 200 ms สำหรับเสียงตอนเดินนะ ถ้ามันเสียงดูแปลกๆมาปรับตรงนี้ได้

    // Constructor
    public Keyhandler(GamePanel gp) {
        this.gp = gp;
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            titleState(code);
        }

        // PLAY STATE
        else if (gp.gameState == gp.playState) {

            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.optionState;
            } else {
                playState(code);
            }
        }

        else if (gp.gameState == gp.gameOverState) {
            if (code == KeyEvent.VK_ENTER) {
                SaveManager.deleteSave();
                gp.gameState = gp.titleState;
            }
        }

        // WIN STATE
        else if (gp.gameState == gp.winState) {
            if (code == KeyEvent.VK_ENTER) {
                gp.ui.winAlphaCounter = 0; // reset fade สำหรับครั้งหน้า
                gp.gameState = gp.titleState;
            }
        }

        // INVENTORY
        else if (gp.gameState == gp.inventoryState) {
            Inventory(code);
        }

        // STORY STATE
        else if (gp.gameState == gp.storyState) {
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                gp.ui.nextStoryPage();
            }
        }

        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.dialogueReturnState; // ← กลับไป state ที่กำหนด
            }
        }

        // OPTION STATE
        else if (gp.gameState == gp.optionState || code == KeyEvent.VK_ESCAPE) {
            optionState(code);
        }
    }

    // TITLE STATE
    public void titleState(int code) {
        if (code == KeyEvent.VK_1) {
            if (!gp.language) {
                gp.language = true;
            } else {
                gp.language = false;
            }
        }

        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = 2;
            }
            gp.playSE(1);
        }

        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 2) {
                gp.ui.commandNum = 0;
            }
            gp.playSE(1);
        }
        // PRESS ENTER TO PLAY GAME
        if (code == KeyEvent.VK_ENTER) {

            if (gp.ui.commandNum == 0) {
                // NEW GAME — reset ทุกอย่างก่อน
                SaveManager.deleteSave();

                gp.currentMap = 1;
                gp.currentLightSize = 200;
                gp.player.setDefaultValues();
                gp.player.inventory.clear();
                gp.player.inventory.add(new Client.object.OBJ_Book(gp));
                // reset สถานะ object ทั้งหมด
                gp.assetteR = new assetSetter(gp);
                // โหลด map ใหม่
                gp.tileM.loadMap(1);
                gp.obj = new Client.Entity.Entity[20];
                gp.ghost = new Client.Entity.Entity[20];
                gp.assetteR.setupMap(1);

                gp.gameState = gp.storyState;
            }

            if (gp.ui.commandNum == 1) {
                if (SaveManager.saveExists()) {
                    SaveManager.load(gp);
                    gp.tileM.loadMap(gp.currentMap);
                    gp.obj = new Client.Entity.Entity[20];
                    gp.ghost = new Client.Entity.Entity[20];
                    gp.assetteR.setupMap(gp.currentMap);
                    gp.gameState = gp.playState;
                } else {
                    gp.gameState = gp.storyState;
                }
            }
            if (gp.ui.commandNum == 2) {
                System.exit(0);
            }
        }

    }

    // PLAY STATE
    public void playState(int code) {
        if (code == KeyEvent.VK_0) {
            if (!gp.DEBUG) {
                gp.DEBUG = true;
            } else {
                gp.DEBUG = false;
            }
        }

        if (code == KeyEvent.VK_W) {
            upPreesed = true;

            long now = System.currentTimeMillis();

            if (now - lastStepTime > stepCooldown) {
                gp.playSE(4);
                lastStepTime = now;
            }
        }

        if (code == KeyEvent.VK_S) {
            downPressed = true;
            long now = System.currentTimeMillis();

            if (now - lastStepTime > stepCooldown) {
                gp.playSE(4);
                lastStepTime = now;
            }
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
            long now = System.currentTimeMillis();

            if (now - lastStepTime > stepCooldown) {
                gp.playSE(4);
                lastStepTime = now;
            }
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
            long now = System.currentTimeMillis();

            if (now - lastStepTime > stepCooldown) {
                gp.playSE(4);
                lastStepTime = now;
            }
        }
        if (code == KeyEvent.VK_I) {
            gp.gameState = gp.inventoryState;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = true;
            long now = System.currentTimeMillis();

            if (now - lastStepTime > stepCooldown) {
                gp.playSE(4);
                lastStepTime = now;
            }
        }
        if (code == KeyEvent.VK_E) {
            ePressed = true;
        }
    }

    public void Inventory(int code) {
        if (code == KeyEvent.VK_I) {
            gp.gameState = gp.playState;
        }
        if (code == KeyEvent.VK_W) {
            if (gp.ui.slotRow != 0) {
                gp.ui.slotRow--;
            }
        }
        if (code == KeyEvent.VK_S) {
            if (gp.ui.slotRow != 4) {
                gp.ui.slotRow++;
            }
        }
        if (code == KeyEvent.VK_A) {
            if (gp.ui.slotCol != 0) {
                gp.ui.slotCol--;
            }
        }
        if (code == KeyEvent.VK_D) {
            if (gp.ui.slotCol != 12) {
                gp.ui.slotCol++;
            }
        }
        // กด Enter เพื่อใช้ item ที่เลือกอยู่
        if (code == KeyEvent.VK_ENTER) {
            int itemIndex = gp.ui.getItemIndexOnSlot();
            if (itemIndex < gp.player.inventory.size()) {
                gp.player.useItem(itemIndex);
            }
        }
    }

    // OPTIONS
    public void optionState(int code) {
        if (code == KeyEvent.VK_1) {
            if (!gp.language) {
                gp.language = true;
            } else {
                gp.language = false;
            }
        }

        if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = gp.playState;
            gp.ui.commandNum = 0;
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        int maxCommandNum = 0;

        switch (gp.ui.subState) {
            case 0:
                maxCommandNum = 4;
                break;
            case 1:
                maxCommandNum = 1;
                break;
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            gp.ui.commandNum--;

            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = maxCommandNum;
            }
        }

        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            gp.ui.commandNum++;

            if (gp.ui.commandNum > maxCommandNum) {
                gp.ui.commandNum = 0;
            }
        }

        // Sound control
        if (code == KeyEvent.VK_A) {// Decrease

            if (gp.ui.subState == 0) {
                // Music
                if (gp.ui.commandNum == 1 && gp.sound.musicVolumeScale > 0) {
                    gp.sound.musicVolumeScale--;
                    gp.sound.checkMusicVolume();
                    gp.playSE(1);
                }

                // SE
                if (gp.ui.commandNum == 2 && gp.sound.seVolumeScale > 0) {
                    gp.sound.seVolumeScale--;
                    gp.sound.checkSeVolume();
                    gp.playSE(1);
                }
            }
        }

        if (code == KeyEvent.VK_D) {// Increase
            if (gp.ui.subState == 0) {
                if (gp.ui.commandNum == 1 && gp.sound.musicVolumeScale < 5) {
                    gp.sound.musicVolumeScale++;
                    gp.sound.checkMusicVolume();
                    gp.playSE(1);
                }
            }

            // SE
            if (gp.ui.commandNum == 2 && gp.sound.seVolumeScale < 5) {
                gp.sound.seVolumeScale++;
                gp.sound.checkSeVolume();
                gp.playSE(1);
            }
        }

    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPreesed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = false;
        }
        if (code == KeyEvent.VK_E) {
            ePressed = false;
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}