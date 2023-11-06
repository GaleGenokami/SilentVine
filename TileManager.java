import java.awt.Graphics2D;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.io.BufferedReader;


public class TileManager {
	private Game gp;
	private Tile[] tile;
	private int mapTileNum[][];
	
	public TileManager(Game gp) {
		this.gp = gp;
		
		tile = new Tile[10];
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
		
		getTileImage();
		loadMap();
	}
	
	public void getTileImage() {
		
		try {
			tile[0] = new Tile();
			tile[0].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/grass.png")));
			tile[1] = new Tile();
			tile[1].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/wall.png")));
			tile[2] = new Tile();
			tile[2].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/water.png")));
			tile[3] = new Tile();
			tile[3].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/earth.png")));
			tile[4] = new Tile();
			tile[4].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/tree.png")));
			tile[5] = new Tile();
			tile[5].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/sand.png")));
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
					String numbers[] = line.split(" ");
					
					int num = Integer.parseInt(numbers[col]);
					
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
	
	public void draw(double circleX, double circleY, Graphics2D g2, double r, boolean clear) {
		
		int worldCol = 0;
		int worldRow = 0;
		int tileNum = 0;
		int worldX = 0;
		int worldY = 0;
		int screenX = 0;
		int screenY = 0;
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			if (clear == true) {
				tileNum = 6;
			} else {
				tileNum = mapTileNum[worldCol][worldRow];
			}
			
			worldX = worldCol * gp.tileSize;
			worldY = worldRow * gp.tileSize;
			screenX = worldX - gp.ship.getX() + ((ShipEntity) gp.ship).screenX;
			screenY = worldY - gp.ship.getY() + ((ShipEntity) gp.ship).screenY;
			
			if( worldX + gp.tileSize > ((ShipEntity) gp.ship).x - ((ShipEntity) gp.ship).screenX && 
				worldX - gp.tileSize < ((ShipEntity) gp.ship).x + ((ShipEntity) gp.ship).screenX && 
				worldY + gp.tileSize > ((ShipEntity) gp.ship).y - ((ShipEntity) gp.ship).screenY && 
				worldY - gp.tileSize < ((ShipEntity) gp.ship).y + ((ShipEntity) gp.ship).screenY) {
					
				if (inCircle(circleX, circleY, screenX, screenY, r)) {
					g2.drawImage(tile[tileNum].getImage(), screenX, screenY, gp.tileSize, gp.tileSize, null);
				}
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

	public Tile[] getTile() {
		return tile;
	}

	public void setTile(Tile[] tile) {
		this.tile = tile;
	}

	public int[][] getMapTileNum() {
		return mapTileNum;
	}

	public void setMapTileNum(int[][] mapTileNum) {
		this.mapTileNum = mapTileNum;
	}
}
