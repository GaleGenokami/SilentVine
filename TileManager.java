import java.awt.Graphics2D;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.io.BufferedReader;


public class TileManager {
	Game gp;
	Tile[] tile;
	int mapTileNum[][];
	boolean mapVisibility[][];
	
	public TileManager(Game gp) {
		this.gp = gp;
		
		tile = new Tile[10];
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
		mapVisibility = new boolean[gp.maxWorldCol][gp.maxWorldRow];
		for(int i = 0; i < mapVisibility.length; i++) {
			for(int j = 0; j < mapVisibility[i].length; j++) {
				mapVisibility[i][j] = false;
			}
		}
		
		getTileImage();
		loadMap();
	}
	
	public void getTileImage() {
		
		try {
			tile[0] = new Tile();
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("/sprites/0.png"));
			
			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("/sprites/1.png"));
			tile[1].collision = true;
			
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("/sprites/2.png"));
			tile[2].collision = true;
			
			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getResourceAsStream("/sprites/3.png"));
			tile[3].collision = true;
			
			tile[4] = new Tile();
			tile[4].image = ImageIO.read(getClass().getResourceAsStream("/sprites/4.png"));
			tile[4].collision = true;
			
			tile[5] = new Tile();
			tile[5].image = ImageIO.read(getClass().getResourceAsStream("/sprites/5.png"));
			tile[5].collision = true;
			
			tile[6] = new Tile();
			tile[6].image = ImageIO.read(getClass().getResourceAsStream("/sprites/6.png"));
			tile[6].collision = true;
			
			tile[7] = new Tile();
			tile[7].image = ImageIO.read(getClass().getResourceAsStream("/sprites/7.png"));
			tile[7].collision = true;
			
			tile[8] = new Tile();
			tile[8].image = ImageIO.read(getClass().getResourceAsStream("/sprites/8.png"));
			tile[8].collision = true;
			
			tile[9] = new Tile();
			tile[9].image = ImageIO.read(getClass().getResourceAsStream("/sprites/9.png"));
			tile[9].collision = true;
			
			
			
		}catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public void loadMap() {
		try {
			InputStream is = getClass ().getResourceAsStream("/maps/map.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				String line = br.readLine();
				
				while(col < gp.maxWorldCol) {
					
					int num = line.charAt(col) - '0';
					
					mapTileNum[col][row] = num;
					col++;
				}
				
				if(col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			
			br.close();
			
		}catch(Exception E){
			 System.out.println(E);
		}
	}
	
	public void draw(double circleX, double circleY, double r, Graphics2D g2) {
		
		int worldCol = 0;
		int worldRow = 0;
		int tileNum = 0;
		int worldX = 0;
		int worldY = 0;
		int screenX = 0;
		int screenY = 0;
		
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			tileNum = mapTileNum[worldCol][worldRow];
			worldX = worldCol * gp.tileSize;
			worldY = worldRow * gp.tileSize;
			screenX = worldX - gp.ship.getX() + ((ShipEntity) gp.ship).screenX;
			screenY = worldY - gp.ship.getY() + ((ShipEntity) gp.ship).screenY;
			
			if(inCircle(circleX, circleY, screenX, screenY, r)) {
				mapVisibility[worldCol][worldRow] = true;
			}
			
			if( worldX + gp.tileSize > ((ShipEntity) gp.ship).x - ((ShipEntity) gp.ship).screenX && 
				worldX - gp.tileSize < ((ShipEntity) gp.ship).x + ((ShipEntity) gp.ship).screenX && 
				worldY + gp.tileSize > ((ShipEntity) gp.ship).y - ((ShipEntity) gp.ship).screenY && 
				worldY - gp.tileSize < ((ShipEntity) gp.ship).y + ((ShipEntity) gp.ship).screenY && 
				mapVisibility[worldCol][worldRow]) {
				
				g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
			}
			
			worldCol++;
			
			if(worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
			
		}
	
	}
	
	private boolean inCircle(double circleX, double circleY, double x, double y, double r) {
        // temporary variables to set edges for testing
        double testX = x;
        double testY = y;

        // which edge is closest?
        if (x < circleX) testX = circleX; // test left edge
        else if (x > circleX + gp.tileSize) testX = circleX + gp.tileSize; // right edge
        if (y < circleY) testY = circleY; // top edge
        else if (y > circleY + gp.tileSize) testY = circleY + gp.tileSize; // bottom edge

        // get distance from closest edges
        double distX = x - testX;
        double distY = y - testY;
        double distance = Math.sqrt((distX * distX) + (distY * distY));

        // if the distance is less than the radius, collision!
        if (distance < r) {
            return true;
        }
        return false;
    }
}
