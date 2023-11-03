import java.awt.Graphics2D;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.io.BufferedReader;


public class TileManager {
	Game gp;
	Tile[] tile;
	int mapTileNum[][];
	
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
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("/sprites/grass.png"));
			
			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("/sprites/wall.png"));
			tile[1].collision = true;
			
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("/sprites/water.png"));
			tile[2].collision = true;
			
			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getResourceAsStream("/sprites/earth.png"));
			
			tile[4] = new Tile();
			tile[4].image = ImageIO.read(getClass().getResourceAsStream("/sprites/tree.png"));
			tile[4].collision = true;
			
			tile[5] = new Tile();
			tile[5].image = ImageIO.read(getClass().getResourceAsStream("/sprites/sand.png"));
			
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
	
	public void draw(Graphics2D g2) {
		
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
			
			
			if( worldX + gp.tileSize > ((ShipEntity) gp.ship).x - ((ShipEntity) gp.ship).screenX && 
				worldX - gp.tileSize < ((ShipEntity) gp.ship).x + ((ShipEntity) gp.ship).screenX && 
				worldY + gp.tileSize > ((ShipEntity) gp.ship).y - ((ShipEntity) gp.ship).screenY && 
				worldY - gp.tileSize < ((ShipEntity) gp.ship).y + ((ShipEntity) gp.ship).screenY) {
				
				g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
			}
			
			worldCol++;
			
			if(worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
			
		}
	
	}
}
