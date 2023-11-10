

public class CollisionChecker {
	Game gp;
	
	public CollisionChecker(Game g) {
		this.gp = g;
	}
	
	public void checkTile(Entity entity, long delta) {
		int entityLeftWorldX = (int) entity.x + entity.solidArea.x;
		int entityRightWorldX = (int) entity.x + entity.solidArea.x + entity.solidArea.width * 2;
		int entityTopWorldY = (int) entity.y + entity.solidArea.y;
		int entityBottomWorldY = (int) entity.y + entity.solidArea.y + entity.solidArea.height * 2;
		
		int entityLeftCol = entityLeftWorldX/gp.tileSize;
		int entityRightCol = entityRightWorldX/gp.tileSize;
		int entityTopRow = entityTopWorldY/gp.tileSize;
		int entityBottomRow = entityBottomWorldY/gp.tileSize;
		
		int tileNum1 = 0;
		int tileNum2 = 0;
		
		
		if(entity.dx < 0) { // left
			entityLeftCol = (int) (entityLeftWorldX + (entity.dx * delta) / 1000) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				((ShipEntity)entity).dx = 0;
				((AlienEntity) gp.getEntities().get(i)).setHorizontalMovement(0);
			}
		}
		
		if(entity.dx > 0) { // right
			entityRightCol = (int) (entityRightWorldX + (entity.dx * delta) / 1000) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				((ShipEntity)entity).dx = 0;
				((AlienEntity) gp.getEntities().get(i)).setHorizontalMovement(0);
			}
		}
	
		if(entity.dy < 0) { // up
			entityTopRow = (int) (entityTopWorldY + (entity.dy * delta) / 1000) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				((ShipEntity)entity).dy = 0;
				((AlienEntity) gp.getEntities().get(i)).setVerticalMovement(0);
			}
		}
		
		if(entity.dy > 0) { // down
			entityBottomRow = (int) (entityBottomWorldY + (entity.dy * delta) / 1000) / gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
				((ShipEntity)entity).dy = 0;
				((AlienEntity) gp.getEntities().get(i)).setVerticalMovement(0);
			}
		}
			
	}
	
}
