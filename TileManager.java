import java.awt.Graphics2D;

import javax.imageio.ImageIO;

public class TileManager {
	Game gp;
	Tile[] tile;
	int mapTileNum[][];
	
	public TileManager(Game gp) {
		this.gp = gp;
		
		tile = new Tile[10];
		
		getTileImage();
	}
	
	public void getTileImage() {
		
		try {
			tile[0] = new Tile();
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("/sprites/grass.png"));
			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("/sprites/wall.png"));
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("/sprites/water.png"));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g2) {
		
		int col = 0;
		int row = 0;
		int x = 0;
		int y = 0;
		
		while(col < 20 && row < 20) {
			g2.drawImage(tile[0].image, x, y, gp.tileSize, gp.tileSize, null);
			col++;
			x += gp.tileSize;
			
			if(col == 20) {
				col = 0;
				x = 0;
				row++;
				y += gp.tileSize;
			}
			
		}
	
	}
}
