package gui;

import java.awt.Graphics;

public class GKitRobotPropeller extends GRobot {

  static String[] pics = { "src/resources/KitRobot/propellers/propeller0.png",
    "src/resources/KitRobot/propellers/propeller1.png",
    "src/resources/KitRobot/propellers/propeller2.png",
    "src/resources/KitRobot/propellers/propeller3.png" };
  
  /* Hold the value of the current frames passed */
  int animationFrames;
  
  public GKitRobotPropeller(int x, int y) {
    super(x, y, pics);
    
    // Initialize the animation and force the image to update
    animationFrames = 100;
    animate();  //after animation the animationFrames will be 0
  }

  public void paint(Graphics g, int posX, int posY, int offX, int offY)
  {
    // Update position
    x = posX;
    y = posY;
    
    //Update animation
    animate();
    
    // Paint object based on offset
    super.paintObject(g, offX, offY);
  }
  
  /* Update the current image to create animation and update position */
  void animate()
  {
    // If the image hasn't been up for 10 frames, increment it
    if (animationFrames <= 15) {
      animationFrames++;
    } 
    // Else set the duration to 0, and update the current image
    else {
      animationFrames = 0;
      currentImage++;
      
      // If it has cycled through every image go to the first image
      if (currentImage >= pics.length)
        currentImage = 0;
    }
    
    // Update the position
    x -= images[currentImage].getWidth() / 2;
  }
}
