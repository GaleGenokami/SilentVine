

public class TileEntity extends Entity{
	private Game game;
	
	public TileEntity(Game g, String r, int newX, int newY) {
		super(r, newX, newY);
		game = g;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void collidedWith(Entity other) {
		
		
	}

	@Override
	protected int getScreenX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getScreenY() {
		// TODO Auto-generated method stub
		return 0;
	}

}
