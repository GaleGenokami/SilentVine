

public class TileEntity extends Entity{
	private Game game;
	
	public TileEntity(Game g, String r, int newX, int newY) {
		super(r, newX, newY);
		game = g;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void collidedWith(Entity other) {
		if (other instanceof AlienEntity) {
			
	    } // if
		
	}

}
