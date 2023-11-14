/* Game.java
 * Space Invaders Main Program
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Game extends Canvas {


      	private BufferStrategy strategy;   // take advantage of accelerated graphics
        private boolean waitingForKeyPress = true;  // true if game held up until
                                                    // a key is pressed
        private boolean leftPressed = false;  // true if left arrow key currently pressed
        private boolean rightPressed = false; // true if right arrow key currently pressed
        private boolean upPressed = false;  // true if left arrow key currently pressed
        private boolean downPressed = false; // true if right arrow key currently pressed
        private boolean firePressed = false; // true if firing

        private boolean gameRunning = true;
        private ArrayList entities = new ArrayList(); // list of entities
                                                      // in game
        private ArrayList <Entity> alienEntities = new ArrayList(); // list of alien entities
        private ArrayList removeEntities = new ArrayList(); // list of entities
                                                            // to remove this loop
        private Entity ship;  // the ship
        private double moveSpeed = 250; // hor. vel. of ship (px/s)
        private long lastFire = 0; // time last shot fired
        private long firingInterval = 3000; // interval between shots (ms)
        private int alienCount; // # of aliens left on screen

        private String message = ""; // message to display while waiting
                                     // for a key press

        private boolean logicRequiredThisLoop = false; // true if logic
                                                       // needs to be 
                                                       // applied this loop
        
        private double energy = 930;
        private boolean playerVisible = false;
        
        private boolean sonarOn = false;
        private boolean walkOn = false;
        
        private int loopCount = 0;
        private int walkCount = 0;
        
        private int sonarCenterX = 0;
        private int sonarCenterY = 0;

        private int tileSize = 72;
        
        private int screenWidth = 1080;
        private int screenHeight = 1008;
        private int maxScreenCol = 15;
        private int maxScreenRow = 15;
        
        private int maxWorldCol = 200;
        private int maxWorldRow = 100;
        private int worldWidth = tileSize * maxWorldCol;
        private int worldHeight = tileSize * maxWorldRow;
        
        private TileManager tileM = new TileManager(this);
        private double theta = 0;
        
    	/*
    	 * Construct our game and set it running.
    	 */
    	public Game() {
    		// create a frame to contain game
    		JFrame container = new JFrame("Mission Begin.");
    
    		// get hold the content of the frame
    		JPanel panel = (JPanel) container.getContentPane();
    
    		// set up the resolution of the game
    		panel.setPreferredSize(new Dimension(screenWidth,screenHeight));
    		panel.setLayout(null);
    
    		// set up canvas size (this) and add to frame
    		setBounds(0,0,screenWidth,screenHeight);
    		panel.add(this);
    
    		// Tell AWT not to bother repainting canvas since that will
            // be done using graphics acceleration
    		setIgnoreRepaint(true);
    
    		// make the window visible
    		container.pack();
    		container.setResizable(false);
    		container.setVisible(true);
    
    
            // if user closes window, shutdown game and jre
    		container.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				System.exit(0);
    			} // windowClosing
    		});
    
    		// add key listener to this canvas
    		addKeyListener(new KeyInputHandler());
    
    		// request focus so key events are handled by this canvas
    		requestFocus();

    		// create buffer strategy to take advantage of accelerated graphics
    		createBufferStrategy(2);
    		strategy = getBufferStrategy();
    
    		// initialize entities
    		initEntities();
    
    		// start the game
    		gameLoop();
        } // constructor
    
    
        /* initEntities
         * input: none
         * output: none
         * purpose: Initialise the starting state of the ship and alien entities.
         *          Each entity will be added to the array of entities in the game.
    	 */
    	private void initEntities() {
              // create the ship and put in center of screen
              ship = new ShipEntity(this, "sprites/0.png", tileSize * 3, tileSize * 3);
              entities.add(ship);
    
              // create a block of aliens (5x12)
              // create a block of aliens (5x12)
              alienCount = 0;
              for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                  Entity alien = new AlienEntity(this, "sprites/alien.png", 
                      400 + (col * 10 * tileSize),
                      400 + (row * 10 * tileSize), "");
                  entities.add(alien);
                  alienEntities.add(alien);
                  alienCount++;
                } // for
              } // outer for
    	} // initEntities

        /* Notification from a game entity that the logic of the game
         * should be run at the next opportunity 
         */
         public void updateLogic() {
           logicRequiredThisLoop = true;
         } // updateLogic

         /* Remove an entity from the game.  It will no longer be
          * moved or drawn.
          */
         public void removeEntity(Entity entity) {
           removeEntities.add(entity);
         } // removeEntity

         /* Notification that the player has died.
          */
		  
         public void notifyDeath() {
           message = "You DEAD!  Try again?";
           waitingForKeyPress = true;
         } // notifyDeath


         /* Notification that the play has killed all aliens
          */
         public void notifyWin(){
           message = "You kicked some ALIEN BUTT!  You win!";
           waitingForKeyPress = true;
         } // notifyWin

        /* Notification than an alien has been killed
         */
         public void notifyAlienKilled() {
           alienCount--;
           
           if (alienCount == 0) {
             notifyWin();
           } // if
           
           // speed up existing aliens
           for (Entity alien: alienEntities) {
             
            alien.setHorizontalMovement(alien.getHorizontalMovement() * 1.02);
             
           } // for
         } // notifyAlienKilled

        /* Attempt to fire.*/
        public boolean tryToFire() {
          // check that we've waited long enough to fire
          if ((System.currentTimeMillis() - lastFire) < firingInterval){
            return false;
          } // if

          // otherwise add a shot
          lastFire = System.currentTimeMillis();
          return true;
        } // tryToFire

	/*
	 * gameLoop
         * input: none
         * output: none
         * purpose: Main game loop. Runs throughout game play.
         *          Responsible for the following activities:
	 *           - calculates speed of the game loop to update moves
	 *           - moves the game entities
	 *           - draws the screen contents (entities, text)
	 *           - updates game events
	 *           - checks input
	 */
	public void gameLoop() {
          long lastLoopTime = System.currentTimeMillis();

          // keep loop running until game ends
          while (gameRunning) {
            
            // calc. time since last update, will be used to calculate
            // entities movement
            long delta = System.currentTimeMillis() - lastLoopTime;
            lastLoopTime = System.currentTimeMillis();

            // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, worldWidth, worldHeight);
            
            // if spacebar pressed, try to fire
            if (firePressed) {
            	if(tryToFire()) {
            		if(!playerVisible) {
            			playerVisible = true;
            			ship.sprite.setImage((SpriteStore.get()).getSprite("sprites/player.png").getImage());
            		}
            		energy -= 10;
            		sonarCenterX = ship.getX();
                    sonarCenterY = ship.getY();
            		sonarOn = true;
            	}
            } // if

           	tileM.draw(540 - loopCount / 2 - (ship.getX() - sonarCenterX), 500 - loopCount / 2 - (ship.getY() - sonarCenterY), (double)loopCount * 2, (Graphics2D)g);

            g.setColor(Color.WHITE);
           	g.fillRect(40, 40, 25, 940);
            g.setColor(Color.GRAY);
           	g.fillRect(45, 975 - (int)energy, 15, (int)energy);
           	
            if(sonarOn) {
            	loopCount++;
            	g.setColor(Color.WHITE);
            	g.drawOval(540 - loopCount * 2 - (ship.getX() - sonarCenterX), 500 - loopCount * 2 - (ship.getY() - sonarCenterY), loopCount * 4, loopCount * 4);
            	for (Entity alien: alienEntities) {
                    if (alien.inCircle(540 - loopCount / 2 - (ship.getX() - sonarCenterX), 500 - loopCount / 2 - (ship.getY() - sonarCenterY), loopCount * 2)) {
                    	alien.setSprite(("sprites/alienDetected.png"));
                    }
                }
            	if(loopCount * 4 > 500) {
            		sonarOn = false;
            		loopCount = 0;
            		for (Entity alien: alienEntities) {
                        alien.setSprite(("sprites/alien.png"));
                    }
            	}
            }
            
            if(walkOn && walkCount < 20) {
            	g.setColor(Color.WHITE);
            	g.drawOval(510, 474, 60, 60);
            	if(walkCount < 0) {
                	walkCount = 40;
            	}
            }

            walkCount--;
            
            // brute force collisions, compare every entity
            // against every other entity.  If any collisions
            // are detected notify both entities that it has
            // occurred
           for (int i = 0; i < entities.size(); i++) {
             for (int j = i + 1; j < entities.size(); j++) {
                Entity me = (Entity)entities.get(i);
                Entity him = (Entity)entities.get(j);

                if (me.collidesWith(him)) {
                  me.collidedWith(him);
                  him.collidedWith(me);
                } // if
             } // inner for
           } // outer for

            // move each entity
            if (!waitingForKeyPress) {
              for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                entity.move(delta);
              } // for
            } // if
            

            // draw all entities
            for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               entity.draw(g);
            } // for
            
            
            

           // remove dead entities
           entities.removeAll(removeEntities);
           removeEntities.clear();

           /*// run logic if required
           if (logicRequiredThisLoop) {
             for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               entity.doLogic();
             } // for
             logicRequiredThisLoop = false;
           } // if*/

           // if waiting for "any key press", draw message
           if (waitingForKeyPress) {
             g.setColor(Color.white);
             g.drawString(message, (800 - g.getFontMetrics().stringWidth(message))/2, 250);
             g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key"))/2, 300);
           }  // if
           
           
           
           
            // clear graphics and flip buffer
            g.dispose();
            strategy.show();

            // ship should not move without user input
            ship.setHorizontalMovement(0);
            ship.setVerticalMovement(0);
            walkOn = false;

            for (Entity alien: alienEntities) {
            	alien.setHorizontalMovement(0);
            	alien.setVerticalMovement(0);
        		
            	// respond to user moving ship
	            if ((leftPressed) && (!rightPressed)) {
	              ship.setHorizontalMovement(-moveSpeed);
	              walkOn = true;
	              alien.addHorizontalMovement(moveSpeed);
	            } else if ((rightPressed) && (!leftPressed)) {
	              ship.setHorizontalMovement(moveSpeed);
	              walkOn = true;
	              alien.addHorizontalMovement(-moveSpeed);
	            } // else
	            
	            if ((upPressed) && (!downPressed)) {
	                ship.setVerticalMovement(-moveSpeed);
	                walkOn = true;
	                alien.addVerticalMovement(moveSpeed);
	            } else if ((downPressed) && (!upPressed)) {
	                ship.setVerticalMovement(moveSpeed);
	                walkOn = true;
	                alien.addVerticalMovement(-moveSpeed);
	            } // else
            }
            
    		ship.checkCollision(delta);
    		
            
            // move alien (pathfinding)
            for (int i = 0; i < alienEntities.size(); i++) {
            	if (!alienEntities.get(i).collidesWith(ship) || Math.sqrt(Math.pow((ship.getScreenY() - alienEntities.get(i).getScreenY()), 2) + Math.pow((ship.getScreenX() - alienEntities.get(i).getScreenX()), 2)) < 1500) {
	        		try {
	            		theta = Math.abs(Math.atan((ship.getScreenY() - alienEntities.get(i).getScreenY()) / (ship.getScreenX() - alienEntities.get(i).getScreenX())));
	            		alienEntities.get(i).addHorizontalMovement((100 * Math.cos(theta)) * ((ship.getScreenX() - alienEntities.get(i).getScreenX() < 0) ? (-1) : (1)));
	            		alienEntities.get(i).addVerticalMovement((100 * Math.sin(theta)) * ((ship.getScreenY() - alienEntities.get(i).getScreenY() < 0) ? (-1) : (1)));
	        		} catch(Exception e) {
	        			alienEntities.get(i).addVerticalMovement(100 * ((ship.getScreenY() - alienEntities.get(i).getScreenY() < 0) ? (-1) : (1)));
	        		}
            	}
            	try {
            		if (alienEntities.get(i).collidesWith(alienEntities.get(i + 1))) {
	            		if (alienEntities.get(i).getScreenX() + alienEntities.get(i).sprite.getWidth() >= alienEntities.get(i + 1).getScreenX() || alienEntities.get(i).getScreenX() <= alienEntities.get(i + 1).sprite.getWidth() + alienEntities.get(i + 1).getScreenX()) {
	            			alienEntities.get(i).addHorizontalMovement(-(100 * Math.cos(theta)) * ((ship.getScreenX() - alienEntities.get(i).getScreenX() < 0) ? (-1) : (1)));
	            		}
	               	 	if (alienEntities.get(i).getScreenY() + alienEntities.get(i).sprite.getHeight() >= alienEntities.get(i + 1).getScreenY() || alienEntities.get(i).getScreenY() <= alienEntities.get(i + 1).sprite.getHeight() + alienEntities.get(i + 1).getScreenY()) {
	               	 		alienEntities.get(i).addVerticalMovement(-(100 * Math.sin(theta)) * ((ship.getScreenY() - alienEntities.get(i).getScreenY() < 0) ? (-1) : (1)));
	               	 	}
            		}
            	} catch (Exception e){}
            }
            
            // pause
            try { Thread.sleep(10); } catch (Exception e) {}

          } // while

	} // gameLoop
	

        /* startGame
         * input: none
         * output: none
         * purpose: start a fresh game, clear old data
         */
         private void startGame() {
            // clear out any existing entities and initialize a new set
            entities.clear();
            
            initEntities();
            ship.setHorizontalMovement(0);
            ship.setVerticalMovement(0);
            
            // blank out any keyboard settings that might exist
            leftPressed = false;
            rightPressed = false;
            firePressed = false;
         } // startGame


        /* inner class KeyInputHandler
         * handles keyboard input from the user
         */
	private class KeyInputHandler extends KeyAdapter {
                 
                 private int pressCount = 1;  // the number of key presses since
                                              // waiting for 'any' key press

                /* The following methods are required
                 * for any class that extends the abstract
                 * class KeyAdapter.  They handle keyPressed,
                 * keyReleased and keyTyped events.
                 */
		public void keyPressed(KeyEvent e) {

                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if
                  
                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftPressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightPressed = true;
                  } // if
                  
                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_UP) {
                    upPressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    downPressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = true;
                  } // if

		} // keyPressed

		public void keyReleased(KeyEvent e) {
                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if
                  
                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightPressed = false;
                  } // if
                  
                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_UP) {
                    upPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    downPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = false;
                  } // if

		} // keyReleased

 	        public void keyTyped(KeyEvent e) {

                   // if waiting for key press to start game
 	           if (waitingForKeyPress) {
                     if (pressCount == 1) {
                       waitingForKeyPress = false;
                       startGame();
                       pressCount = 0;
                     } else {
                       pressCount++;
                     } // else
                   } // if waitingForKeyPress

                   // if escape is pressed, end game
                   if (e.getKeyChar() == 27) {
                     System.exit(0);
                   } // if escape pressed

		} // keyTyped

	} // class KeyInputHandler


	/**
	 * Main Program
	 */
	public static void main(String [] args) {
        // instantiate this object
		new Game();
	} // main
	
	public void addEntities(Entity entity) {
		entities.add(entity);
	}


	public ArrayList getEntities() {
		return entities;
	}


	public void setEntities(ArrayList entities) {
		this.entities = entities;
	}


	public ArrayList<Entity> getAlienEntities() {
		return alienEntities;
	}


	public void setAlienEntities(ArrayList<Entity> alienEntities) {
		this.alienEntities = alienEntities;
	}


	public double getEnergy() {
		return energy;
	}


	public void setEnergy(double energy) {
		this.energy = energy;
	}


	public boolean isUpPressed() {
		return upPressed;
	}


	public void setUpPressed(boolean upPressed) {
		this.upPressed = upPressed;
	}


	public ArrayList getRemoveEntities() {
		return removeEntities;
	}


	public void setRemoveEntities(ArrayList removeEntities) {
		this.removeEntities = removeEntities;
	}


	public Entity getShip() {
		return ship;
	}


	public void setShip(Entity ship) {
		this.ship = ship;
	}


	public double getMoveSpeed() {
		return moveSpeed;
	}


	public void setMoveSpeed(double moveSpeed) {
		this.moveSpeed = moveSpeed;
	}


	public long getFiringInterval() {
		return firingInterval;
	}


	public void setFiringInterval(long firingInterval) {
		this.firingInterval = firingInterval;
	}


	public int getAlienCount() {
		return alienCount;
	}


	public void setAlienCount(int alienCount) {
		this.alienCount = alienCount;
	}


	public boolean isPlayerVisible() {
		return playerVisible;
	}


	public void setPlayerVisible(boolean playerVisible) {
		this.playerVisible = playerVisible;
	}


	public boolean isWalkOn() {
		return walkOn;
	}


	public void setWalkOn(boolean walkOn) {
		this.walkOn = walkOn;
	}


	public int getWalkCount() {
		return walkCount;
	}


	public void setWalkCount(int walkCount) {
		this.walkCount = walkCount;
	}


	public int getTileSize() {
		return tileSize;
	}


	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}


	public int getMaxScreenCol() {
		return maxScreenCol;
	}


	public void setMaxScreenCol(int maxScreenCol) {
		this.maxScreenCol = maxScreenCol;
	}


	public int getMaxScreenRow() {
		return maxScreenRow;
	}


	public void setMaxScreenRow(int maxScreenRow) {
		this.maxScreenRow = maxScreenRow;
	}


	public int getMaxWorldCol() {
		return maxWorldCol;
	}


	public void setMaxWorldCol(int maxWorldCol) {
		this.maxWorldCol = maxWorldCol;
	}


	public int getMaxWorldRow() {
		return maxWorldRow;
	}


	public void setMaxWorldRow(int maxWorldRow) {
		this.maxWorldRow = maxWorldRow;
	}


	public int getWorldWidth() {
		return worldWidth;
	}


	public void setWorldWidth(int worldWidth) {
		this.worldWidth = worldWidth;
	}


	public int getWorldHeight() {
		return worldHeight;
	}


	public void setWorldHeight(int worldHeight) {
		this.worldHeight = worldHeight;
	}


	public TileManager getTileM() {
		return tileM;
	}


	public void setTileM(TileManager tileM) {
		this.tileM = tileM;
	}


	public int getScreenWidth() {
		return screenWidth;
	}


	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}


	public int getScreenHeight() {
		return screenHeight;
	}


	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}


	/**
	 * @return the theta
	 */
	public double getTheta() {
		return theta;
	}


	/**
	 * @param theta the theta to set
	 */
	public void setTheta(double theta) {
		this.theta = theta;
	}
} // Game
