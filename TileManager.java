import java.awt.Graphics2D;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.io.BufferedReader;


public class TileManager {
	private Game gp;
	private Tile[] tile;
	private int mapTileNum[][];
	private boolean mapVisibility[][];
	
	public TileManager(Game gp) {
		this.gp = gp;
		
		tile = new Tile[10];
		mapTileNum = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
		mapVisibility = new boolean[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
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
			tile[0].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/0.png")));
			
			tile[1] = new Tile();
			tile[1].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/1.png")));
			tile[1].setCollision(true);
			
			tile[2] = new Tile();
			tile[2].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/2.png")));
			tile[2].setCollision(true);
			
			tile[3] = new Tile();
			tile[3].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/3.png")));
			tile[3].setCollision(true);
			
			tile[4] = new Tile();
			tile[4].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/4.png")));
			tile[4].setCollision(true);
			
			tile[5] = new Tile();
			tile[5].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/5.png")));
			tile[5].setCollision(true);
			
			tile[6] = new Tile();
			tile[6].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/6.png")));
			tile[6].setCollision(true);
			
			tile[7] = new Tile();
			tile[7].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/7.png")));
			tile[7].setCollision(true);
			
			tile[8] = new Tile();
			tile[8].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/8.png")));
			tile[8].setCollision(true);
			
			tile[9] = new Tile();
			tile[9].setImage(ImageIO.read(getClass().getResourceAsStream("/sprites/9.png")));
			tile[9].setCollision(true);
			
			
			
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
			
			while(col < gp.getMaxWorldCol() && row < gp.getMaxWorldRow()) {
				String line = br.readLine();
				
				while(col < gp.getMaxWorldCol()) {
					
					int num = line.charAt(col) - '0';
					
					mapTileNum[col][row] = num;
					col++;
				}
				
				if(col == gp.getMaxWorldCol()) {
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
		
		
		while(worldCol < gp.getMaxWorldCol() && worldRow < gp.getMaxWorldRow()) {
			tileNum = mapTileNum[worldCol][worldRow];
			worldX = worldCol * gp.getTileSize();
			worldY = worldRow * gp.getTileSize();
			screenX = worldX - gp.getShip().getX() + ((ShipEntity) gp.getShip()).screenX;
			screenY = worldY - gp.getShip().getY() + ((ShipEntity) gp.getShip()).screenY;
			
			if(inCircle(circleX, circleY, screenX, screenY, r)) {
				mapVisibility[worldCol][worldRow] = true;
			}
			
			if( worldX + gp.getTileSize() > ((ShipEntity) gp.getShip()).x - ((ShipEntity) gp.getShip()).screenX && 
				worldX - gp.getTileSize() < ((ShipEntity) gp.getShip()).x + ((ShipEntity) gp.getShip()).screenX && 
				worldY + gp.getTileSize() > ((ShipEntity) gp.getShip()).y - ((ShipEntity) gp.getShip()).screenY && 
				worldY - gp.getTileSize() < ((ShipEntity) gp.getShip()).y + ((ShipEntity) gp.getShip()).screenY && 
				mapVisibility[worldCol][worldRow]) {
				
				g2.drawImage(tile[tileNum].getImage(), screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
			}
			
			worldCol++;
			
			if(worldCol == gp.getMaxWorldCol()) {
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
        else if (x > circleX + gp.getTileSize()) testX = circleX + gp.getTileSize(); // right edge
        if (y < circleY) testY = circleY; // top edge
        else if (y > circleY + gp.getTileSize()) testY = circleY + gp.getTileSize(); // bottom edge

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

	public Game getGp() {
		return gp;
	}

	public void setGp(Game gp) {
		this.gp = gp;
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

	public boolean[][] getMapVisibility() {
		return mapVisibility;
	}

	public void setMapVisibility(boolean[][] mapVisibility) {
		this.mapVisibility = mapVisibility;
	}
}
