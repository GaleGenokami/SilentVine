/* Entity.java
 * An entity is any object that appears in the game.
 * It is responsible for resolving collisions and movement.
 */
 
 import java.awt.*;
 
 public abstract class Entity {

    // Java Note: the visibility modifier "protected"
    // allows the variable to be seen by this class,
    // any classes in the same package, and any subclasses
    // "private" - this class only
    // "public" - any class can see it
    
    protected double x;   // current x location
    protected double y;   // current y location
    protected Sprite sprite; // this entity's sprite
    protected double dx; // horizontal speed (px/s)  + -> right
    protected double dy; // vertical speed (px/s) + -> down
    private Rectangle me = new Rectangle(); // bounding rectangle of
                                            // this entity
    private Rectangle him = new Rectangle(); // bounding rect. of other
                                             // entities
    public Rectangle solidArea;
	CollisionChecker cChecker;
                                             
    /* Constructor
     * input: reference to the image for this entity,
     *        initial x and y location to be drawn at
     */
     public Entity(String r, int newX, int newY) {
       x = newX;
       y = newY;
       sprite = (SpriteStore.get()).getSprite(r);
       
     } // constructor

     /* move
      * input: delta - the amount of time passed in ms
      * output: none
      * purpose: after a certain amout of time has passed,
      *          update the location
      */
     public void move(long delta) {
       // update location of entity based on move speeds
       x += (delta * dx) / 1000;
       y += (delta * dy) / 1000;
     } // move

     // get and set velocities
     public void setHorizontalMovement(double newDX) {
       dx = newDX;
     } // setHorizontalMovement

     public void setVerticalMovement(double newDY) {
       dy = newDY;
     } // setVerticalMovement
     
  // get and set velocities
     public void addHorizontalMovement(double newDX) {
       dx += newDX;
     } // setHorizontalMovement

     public void addVerticalMovement(double newDY) {
       dy += newDY;
     } // setVerticalMovement

     public double getHorizontalMovement() {
       return dx;
     } // getHorizontalMovement

     public double getVerticalMovement() {
       return dy;
     } // getVerticalMovement

     // get position
     public int getX() {
       return (int) x;
     } // getX

     public int getY() {
       return (int) y;
     } // getY

    /*
     * Draw this entity to the graphics object provided at (x,y)
     */
     public void draw (Graphics g) {
       sprite.draw(g,(int)x,(int)y);
     }  // draw
     
    /* Do the logic associated with this entity.  This method
     * will be called periodically based on game events.
     */
     public void doLogic() {}
     
     /* collidesWith
      * input: the other entity to check collision against
      * output: true if entities collide
      * purpose: check if this entity collides with the other.
      */
     public boolean collidesWith(Entity other) {
     	if (this instanceof AlienEntity || this instanceof ShipEntity) {
     		me.setBounds(this.getScreenX(), this.getScreenY(), sprite.getWidth(), sprite.getHeight());
     	} else {
     		me.setBounds((int)x, (int)y, sprite.getWidth(), sprite.getHeight());
     	}
     	
     	if (other instanceof ShipEntity || other instanceof AlienEntity) {
     		him.setBounds(other.getScreenX(), other.getScreenY(), 
                     other.sprite.getWidth(), other.sprite.getHeight());
     		
     	} else {
     		him.setBounds(other.getX(), other.getY(), 
                     other.sprite.getWidth(), other.sprite.getHeight());
     	}
     	
        return me.intersects(him);
      } // collidesWith
      
 	protected abstract int getScreenX();
 	
 	protected abstract int getScreenY();

 	public boolean collidesWith(double alienX, double alienY, int tileX, int tileY, Game g) {
     	me.setBounds((int)alienX, (int)alienY, sprite.getWidth(), sprite.getHeight());
     	him.setBounds(tileX, tileY, 
                      g.getTileSize(), g.getTileSize());
        return me.intersects(him);
      } // collidesWith
     
     /* collidedWith
      * input: the entity with which this has collided
      * purpose: notification that this entity collided with another
      * Note: abstract methods must be implemented by any class
      *       that extends this class
      */
      public abstract void collidedWith(Entity other);
      
      public boolean inCircle(double x, double y, double r) {
          // temporary variables to set edges for testing
          double testX = x;
          double testY = y;

          // which edge is closest?
          if (x < this.getScreenX()) testX = this.getScreenX(); // test left edge
          else if (x > this.getScreenX() + this.sprite.getWidth()) testX = this.getScreenX() + this.sprite.getWidth(); // right edge
          if (y < this.getScreenY()) testY = this.getScreenY(); // top edge
          else if (y > this.getScreenY() + this.sprite.getHeight()) testY = this.getScreenY() + this.sprite.getHeight(); // bottom edge

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

      public void setSprite(String r) {
     	 sprite = (SpriteStore.get()).getSprite(r);
      }

	public void checkCollision(long delta) {
		// TODO Auto-generated method stub
		
	}

 } // Entity class