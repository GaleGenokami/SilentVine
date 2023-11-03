/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class AlienEntity extends Entity {

  private double moveSpeed = 75; // horizontal speed

  private Game game; // the game in which the alien exists

  /* construct a new alien
   * input: game - the game in which the alien is being created
   *        r - the image representing the alien
   *        x, y - initial location of alien
   */
  public AlienEntity(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    dx = -moveSpeed;  // start off moving left
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
    super.move(delta);
  } // move


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
  
   public boolean inCircle(double x, double y, double r) {
       // temporary variables to set edges for testing
       double testX = x;
       double testY = y;

       // which edge is closest?
       if (x < this.getX()) testX = this.getX(); // test left edge
       else if (x > this.getX() + this.sprite.getWidth()) testX = this.getX() + this.sprite.getWidth(); // right edge
       if (y < this.getY()) testY = this.getY(); // top edge
       else if (y > this.getY() + this.sprite.getHeight()) testY = this.getY() + this.sprite.getHeight(); // bottom edge

       // get distance from closest edges
       double distX = x - testX;
       double distY = y - testY;
       double distance = Math.sqrt((distX * distX) + (distY * distY));

       // if the distance is less than the radius, collision!
       if (distance <= r) {
           return true;
       }
       return false;
   }
   
} // AlienEntity class
