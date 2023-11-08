import java.awt.Graphics;

/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class AlienEntity extends Entity {

  private double moveSpeed = 0; // horizontal speed

  private Game game; // the game in which the alien exists
  
  private String alienType = "";
  protected double screenX;   // current camera x location
  protected double screenY;   // current camera y location
  
  /* construct a new alien
   * input: game - the game in which the alien is being created
   *        r - the image representing the alien
   *        x, y - initial location of alien
   */
  public AlienEntity(Game g, String r, int newX, int newY, String t) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    dx = -moveSpeed;  // start off moving left
    alienType = t;
    screenX = newX;
    screenY = newY;
    
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move alien
   */
  public void move (long delta){
    // if we reach left side of screen and are moving left
    // request logic update
    if ((dx < 0) && (x < 10)) {
      game.updateLogic();   // logic deals with moving entities
                            // in other direction and down screen
    } // if

    // if we reach right side of screen and are moving right
    // request logic update
    if ((dx > 0) && (x > 750)) {
      game.updateLogic();
    } // if
    
    // proceed with normal move
 // update location of entity based on move speeds
    screenX += (delta * dx) / 1000;
    screenY += (delta * dy) / 1000;
  } // move
  
  public void draw(Graphics g) {
	  sprite.draw(g, (int)screenX, (int)screenY);
  }

  /* doLogic
   * Updates the game logic related to the aliens,
   * ie. move it down the screen and change direction
   */
  public void doLogic() {
    // swap horizontal direction and move down screen 10 pixels
    dx *= -1;
    y += 10;

    // if bottom of screen reached, player dies
    if (y > 570) {
      game.notifyDeath();
    } // if
  } // doLogic


  /* collidedWith
   * input: other - the entity with which the alien has collided
   * purpose: notification that the alien has collided
   *          with something
   */
   public void collidedWith(Entity other) {
     // collisions with aliens are handled in ShotEntity and ShipEntity
   } // collidedWith

	/**
	 * @return the alienType
	 */
	public String getAlienType() {
		return alienType;
	}
	
	/**
	 * @param alienType the alienType to set
	 */
	public void setAlienType(String alienType) {
		this.alienType = alienType;
	}

	/**
	 * @return the screenX
	 */
	public int getScreenX() {
		return (int) screenX;
	}

	/**
	 * @param screenX the screenX to set
	 */
	public void setScreenX(int screenX) {
		this.screenX = screenX;
	}

	/**
	 * @return the screenY
	 */
	public int getScreenY() {
		return (int) screenY;
	}

	/**
	 * @param screenY the screenY to set
	 */
	public void setScreenY(int screenY) {
		this.screenY = screenY;
	}
   
} // AlienEntity class
