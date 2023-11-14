import java.awt.Graphics;
import java.awt.Rectangle;

/* ShipEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class ShipEntity extends Entity {

  private Game game; // the game in which the ship exists

  protected int screenX;   // current camera x location
  protected int screenY;   // current camera y location
  
  
  CollisionChecker cChecker;

  /* construct the player's ship
   * input: game - the game in which the ship is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the ship
   *        x, y - initial location of ship
   */
  public ShipEntity(Game g, String r, int newX, int newY) {
	    super(r, newX, newY);  // calls the constructor in Entity
	    game = g;
	    cChecker = new CollisionChecker(game);
	    
	    screenX = game.getScreenWidth()/2 - game.getTileSize()/2;
	    screenY = game.getScreenHeight()/2 - game.getTileSize()/2;
	    
	    solidArea = new Rectangle(0, 0, game.getTileSize()/2, game.getTileSize()/2);
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move ship 
   */
  public void move (long delta){
	    // stop at left side of screen
	    if ((dx < 0) && (x < 10)) {
	      return;
	    } // if
	    // stop at right side of screen
	    if ((dx > 0) && (x > game.getWorldWidth() - 50)) {
	      return;
	    } // if
	    
	    if ((dy < 0) && (y < 10)) {
	        return;
	    } // if
	      // stop at right side of screen
	    if ((dy > 0) && (y > game.getWorldHeight() - 50)) {
	        return;
	    } // if
	    
        

        super.move(delta);  // calls the move method in Entity
  } // move
  
  public void checkCollision(long delta) {
	  
	cChecker.checkTile(this, delta);
		
  }
  
  public void draw(Graphics g) {
	  sprite.draw(g, screenX,screenY);
  }
  
  
  /* collidedWith
   * input: other - the entity with which the ship has collided
   * purpose: notification that the player's ship has collided
   *          with something
   */
   public void collidedWith(Entity other) {
     if (other instanceof AlienEntity) {
    	
        game.setEnergy(game.getEnergy() - 0.4);
        if (game.getEnergy() <= 0) {
        	game.notifyDeath();
        }
     } // if
   } // collidedWith    

   @Override
   protected int getScreenX() {
   	// TODO Auto-generated method stub
   	return screenX;
   }

   @Override
   protected int getScreenY() {
   	// TODO Auto-generated method stub
   	return screenY;
   }

} // ShipEntity class