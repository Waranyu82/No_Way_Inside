package Client.Client;

import Client.Entity.Entity;
import java.awt.Rectangle;

public class CollisionChecker {

    private final GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    // ─── Tile Collision ─────
    public void checkTile(Entity entity) {
        // บีบ Hitbox เล็กน้อยเพื่อแก้กำแพงอากาศและลดการเดินติดมุมกำแพง
        int left = entity.worldX + entity.solidArea.x + 2;
        int right = entity.worldX + entity.solidArea.x + entity.solidArea.width - 3;
        int top = entity.worldY + entity.solidArea.y + 2;
        int bottom = entity.worldY + entity.solidArea.y + entity.solidArea.height - 3;

        int sp = entity.Speed; // ใช้พยากรณ์จุดที่กำลังจะเดินไปถึง

        switch (entity.direction) {
            case "up":
                checkTwoCols(entity,
                        left / gp.tileSize, right / gp.tileSize,
                        (top - sp) / gp.tileSize, (top - sp) / gp.tileSize);
                break;
            case "down":
                checkTwoCols(entity,
                        left / gp.tileSize, right / gp.tileSize,
                        (bottom + sp) / gp.tileSize, (bottom + sp) / gp.tileSize);
                break;
            case "left":
                checkTwoCols(entity,
                        (left - sp) / gp.tileSize, (left - sp) / gp.tileSize,
                        top / gp.tileSize, bottom / gp.tileSize);
                break;
            case "right":
                checkTwoCols(entity,
                        (right + sp) / gp.tileSize, (right + sp) / gp.tileSize,
                        top / gp.tileSize, bottom / gp.tileSize);
                break;
        }
    }

    // ตรวจสอบ 2 ไทล์พร้อมกัน
    private void checkTwoCols(Entity entity, int col1, int col2, int row1, int row2) {
        if (isSolidAt(col1, row1) || isSolidAt(col2, row2)) {
            entity.collisionOn = true;
        } else if (!isValid(col1, row1) || !isValid(col2, row2)) {
            entity.collisionOn = true; // ออกนอกแผนที่
        }
    }

    private boolean isSolidAt(int col, int row) {
        if (!isValid(col, row))
            return true;
        int tileNum = gp.tileM.mapTilenumber[col][row];
        if (tileNum == -1)
            return true; // นอก map = solid boundary
        return gp.tileM.tile[0][tileNum].collision;
    }

    private boolean isValid(int col, int row) {
        return (col >= 0 && row >= 0)
                && col < gp.tileM.mapTilenumber.length
                && row < gp.tileM.mapTilenumber[0].length;
    }

    // ─── Object Collision ────
    public int checkObject(Entity entity, boolean isPlayer) {
        int hitIndex = 999;

        Rectangle entityRect = getProjectedRect(entity);

        for (int i = 0; i < gp.obj.length; i++) {
            Entity obj = gp.obj[i];
            if (obj == null)
                continue;

            Rectangle objRect = getWorldRect(obj);

            if (entityRect.intersects(objRect)) {
                if (obj.collision && obj.isBarrier)
                    entity.collisionOn = true;

                if (isPlayer)
                    hitIndex = i;
            }
        }
        return hitIndex;
    }

    // ─── Entity Collision ─────
    public int checkEntity(Entity entity, Entity[] targets) {
        int hitIndex = 999;
        Rectangle entityRect = getProjectedRect(entity);

        for (int i = 0; i < targets.length; i++) {
            Entity t = targets[i];
            if (t == null || t == entity)
                continue;

            if (entityRect.intersects(getWorldRect(t))) {
                entity.collisionOn = true;
                hitIndex = i;
            }
        }
        return hitIndex;
    }

    // ─── Player Collision (for ghost/enemies) ────
    public boolean checkPlayer(Entity entity) {
        Rectangle entityRect = getProjectedRect(entity);
        Rectangle playerRect = getWorldRect(gp.player);

        if (entityRect.intersects(playerRect)) {
            entity.collisionOn = true;
            return true;
        }
        return false;
    }

    private Rectangle getProjectedRect(Entity e) {
        int x = e.worldX + e.solidArea.x;
        int y = e.worldY + e.solidArea.y;
        switch (e.direction) {
            case "up":
                y -= e.Speed;
                break;
            case "down":
                y += e.Speed;
                break;
            case "left":
                x -= e.Speed;
                break;
            case "right":
                x += e.Speed;
                break;
        }
        return new Rectangle(x, y, e.solidArea.width, e.solidArea.height);
    }

    private Rectangle getWorldRect(Entity e) {
        return new Rectangle(
                e.worldX + e.solidArea.x,
                e.worldY + e.solidArea.y,
                e.solidArea.width,
                e.solidArea.height);
    }
}