

public class CollisionChecker {
	private Game gp;
	
	public CollisionChecker(Game g) {
		this.gp = g;
	}
	
	public void checkTile(Entity entity, long delta) {
		int entityLeftWorldX = (int) entity.x + entity.solidArea.x + (int) (entity.solidArea.width * 0.4);
		int entityRightWorldX = (int) (entity.x + entity.solidArea.x + entity.solidArea.width * 1.6);
		int entityTopWorldY = (int) entity.y + entity.solidArea.y + (int) (entity.solidArea.width * 0.4);
		int entityBottomWorldY = (int) (entity.y + entity.solidArea.y + entity.solidArea.height * 1.6);
		
		int entityLeftCol = entityLeftWorldX/gp.getTileSize();
		int entityRightCol = entityRightWorldX/gp.getTileSize();
		int entityTopRow = entityTopWorldY/gp.getTileSize();
		int entityBottomRow = entityBottomWorldY/gp.getTileSize();
		
		int tileNum1 = 0;
		int tileNum2 = 0;
		
		
		if(entity.dx < 0) { // left
			entityLeftCol = (int) (entityLeftWorldX + (entity.dx * delta) / 1000) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityLeftCol][entityTopRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityLeftCol][entityBottomRow];
			if(gp.getTileM().getTile()[tileNum1].isCollision() == true || gp.getTileM().getTile()[tileNum2].isCollision() == true) {
				((ShipEntity)entity).dx = 0;
				for (Entity alien: gp.getAlienEntities()) {
					alien.setHorizontalMovement(0);
				}
			}
		}
		
		if(entity.dx > 0) { // right
			entityRightCol = (int) (entityRightWorldX + (entity.dx * delta) / 1000) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityRightCol][entityTopRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityRightCol][entityBottomRow];
			if(gp.getTileM().getTile()[tileNum1].isCollision() == true || gp.getTileM().getTile()[tileNum2].isCollision() == true) {
				((ShipEntity)entity).dx = 0;
				for (Entity alien: gp.getAlienEntities()) {
					alien.setHorizontalMovement(0);
				}
			}
		}
	
		if(entity.dy < 0) { // up
			entityTopRow = (int) (entityTopWorldY + (entity.dy * delta) / 1000) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityLeftCol][entityTopRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityRightCol][entityTopRow];
			if(gp.getTileM().getTile()[tileNum1].isCollision() == true || gp.getTileM().getTile()[tileNum2].isCollision() == true) {
				((ShipEntity)entity).dy = 0;
				for (Entity alien: gp.getAlienEntities()) {
					alien.setVerticalMovement(0);
				}
			}
		}
		
		if(entity.dy > 0) { // down
			entityBottomRow = (int) (entityBottomWorldY + (entity.dy * delta) / 1000) / gp.getTileSize();
			tileNum1 = gp.getTileM().getMapTileNum()[entityLeftCol][entityBottomRow];
			tileNum2 = gp.getTileM().getMapTileNum()[entityRightCol][entityBottomRow];
			if(gp.getTileM().getTile()[tileNum1].isCollision() == true || gp.getTileM().getTile()[tileNum2].isCollision() == true) {
				((ShipEntity)entity).dy = 0;
				for (Entity alien: gp.getAlienEntities()) {
					alien.setVerticalMovement(0);
				}
			}
		}
			
	}
	
}
