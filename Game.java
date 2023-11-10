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
        private ArrayList removeEntities = new ArrayList(); // list of entities
                                                            // to remove this loop
        public Entity ship;  // the ship
        private double moveSpeed = 250; // hor. vel. of ship (px/s)
        private long lastFire = 0; // time last shot fired
        private long firingInterval = 3000; // interval between shots (ms)
        private int alienCount; // # of aliens left on screen

        private String message = ""; // message to display while waiting
                                     // for a key press

        private boolean logicRequiredThisLoop = false; // true if logic
                                                       // needs to be 
                                                       // applied this loop
        
        private int energy = 930;
        private boolean playerVisible = false;
        
        private boolean sonarOn = false;
        private boolean walkOn = false;
        
        private int loopCount = 0;
        private int walkCount = 0;
        
        private int sonarCenterX = 0;
        private int sonarCenterY = 0;

        public int tileSize = 72;
        
        public int screenWidth = 1080;
        public int screenHeight = 1008;
        public int maxScreenCol = 15;
        public int maxScreenRow = 15;
        
        public int maxWorldCol = 200;
        public int maxWorldRow = 100;
        public int worldWidth = tileSize * maxWorldCol;
        public int worldHeight = tileSize * maxWorldRow;
        
        TileManager tileM = new TileManager(this);
        double theta = 0;
        
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
                for (int col = 0; col < 1; col++) {
                  Entity alien = new AlienEntity(this, "sprites/alien.gif", 
                      400 + (col * 10 * tileSize),
                      400 + (row * 10 * tileSize), "");
                  entities.add(alien);
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
           for (int i=0; i < entities.size(); i++) {
             Entity entity = (Entity) entities.get(i);
             if (entity instanceof AlienEntity) {
               // speed up by 2%
               entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
             } // if
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
            			ship.sprite.image = (SpriteStore.get()).getSprite("sprites/player.png").image;
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
           	g.fillRect(45, 975 - energy, 15, energy);
           	
            if(sonarOn) {
            	loopCount++;
            	g.setColor(Color.WHITE);
            	g.drawOval(540 - loopCount * 2 - (ship.getX() - sonarCenterX), 500 - loopCount * 2 - (ship.getY() - sonarCenterY), loopCount * 4, loopCount * 4);
            	for (int i = 0; i < entities.size(); i++) {
                    if (entities.get(i) instanceof AlienEntity) {
                        if (((AlienEntity) entities.get(i)).inCircle(540 - loopCount / 2 - (ship.getX() - sonarCenterX), 500 - loopCount / 2 - (ship.getY() - sonarCenterY), loopCount * 2)) {
                            ((Entity) entities.get(i)).setSprite(("sprites/alienDetected.gif"));
                        }
                    }
                }
            	if(loopCount * 4 > 500) {
            		sonarOn = false;
            		loopCount = 0;
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

            for (int i = 0; i < entities.size(); i++) {
            	if (entities.get(i) instanceof AlienEntity) {
            		((Entity) entities.get(i)).setHorizontalMovement(0);
		            ((Entity) entities.get(i)).setVerticalMovement(0);
            		
	            	// respond to user moving ship
		            if ((leftPressed) && (!rightPressed)) {
		              ship.setHorizontalMovement(-moveSpeed);
		              walkOn = true;
		              ((Entity) entities.get(i)).addHorizontalMovement(moveSpeed);
		            } else if ((rightPressed) && (!leftPressed)) {
		              ship.setHorizontalMovement(moveSpeed);
		              walkOn = true;
		              ((Entity) entities.get(i)).addHorizontalMovement(-moveSpeed);
		            } // else
		            
		            if ((upPressed) && (!downPressed)) {
		                ship.setVerticalMovement(-moveSpeed);
		                walkOn = true;
		                ((Entity) entities.get(i)).addVerticalMovement(moveSpeed);
		            } else if ((downPressed) && (!upPressed)) {
		                ship.setVerticalMovement(moveSpeed);
		                walkOn = true;
		                ((Entity) entities.get(i)).addVerticalMovement(-moveSpeed);
		            } // else
		         
		            
            	}
            }
            
    		ship.checkCollision(delta);
    		
            
            /*// move alien (pathfinding)
            for (int i = 0; i < entities.size(); i++) {
            	if (entities.get(i) instanceof AlienEntity) {
            		try {
	            		theta = Math.abs(Math.atan((ship.getScreenY() - ((Entity) entities.get(i)).getScreenY()) / (ship.getScreenX() - ((Entity) entities.get(i)).getScreenX())));
	            		((Entity) entities.get(i)).addHorizontalMovement((100 * Math.cos(theta)) * ((ship.getScreenX() - ((Entity) entities.get(i)).getScreenX() < 0) ? (-1) : (1)));
	            		((Entity) entities.get(i)).addVerticalMovement((100 * Math.sin(theta)) * ((ship.getScreenY() - ((Entity) entities.get(i)).getScreenY() < 0) ? (-1) : (1)));
	            		System.out.println((100 * Math.cos(theta)) * ((ship.getScreenX() - ((Entity) entities.get(i)).getScreenX() < 0) ? (-1) : (1)));
	                    System.out.println((100 * Math.sin(theta)) * ((ship.getScreenY() - ((Entity) entities.get(i)).getScreenY() < 0) ? (-1) : (1)));
            		} catch(Exception e) {
            			((Entity) entities.get(i)).addVerticalMovement(100 * ((ship.getScreenY() - ((Entity) entities.get(i)).getScreenY() < 0) ? (-1) : (1)));
            		}
            	}
            }*/
            
            

            

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
} // Game
