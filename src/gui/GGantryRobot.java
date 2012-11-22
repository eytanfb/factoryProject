package gui;

import java.awt.Graphics;

public class GGantryRobot extends GRobot {

	GPartBin binInGantry;
	int step;//1 = dipickupnewbin, 2 = doDeliverBinToFeeder, 3 = doPickUp, 4 = doPickUpPurgedBin, 5= doPickUpBinToRefill
	boolean ifArrive;
	
	/* Animation Variables */
  int frameCounterY;
  int animationOffsetY;
  boolean floatingUp;
	
	public GGantryRobot (int x, int y)
	{			
			super(x,y,"src/resources/gantryRobot_boo.png");
			step=-1;
			ifArrive=false;
			binInGantry = null;
			
			// Initialize animation variables
			frameCounterY = 0;
	    animationOffsetY = 0;
	    floatingUp = true;
	    
	    // Start this robot facing the left
	    flipped = true;
	}
	
	public void pickUp(GPartBin bin)
	{
		if(binInGantry==null)
			binInGantry=bin;
	}
	
	public GPartBin dropDown()
	{
		GPartBin temp = binInGantry;
		if(binInGantry ==null)
			System.out.println("GGantryRobot: drop Down a null bin!");
		binInGantry=null;
		return temp;
	}
	
	public void moveStraight(int destX, int destY)
	{
		
		super.moveStraight(destX, destY);		
		
		if(x==destX && y == destY)
			ifArrive=true;
		else
			ifArrive = false;
			
	}
	
	public boolean ifArrive()
	{
		return ifArrive;
	}
	public int getStep()
	{
		return step;
	}
	
	public void setStep(int step)
	{
		this.step=step;
	}
	
	public int updateBinX()
	{
		int x = this.x;
		return x;
	}
	
	public int updateBinY()
	{
		int y = this.y-this.getIconHeight();
		return y;
	}
	
	/* Changing the animation floating values for Y position */
  void animateFloatingY()
  {
    // Check the framecounter
    if (frameCounterY <= 20)  // if less than 3 stop the update
    {
      frameCounterY++;
      return;
    }
    
    // Reset the framecounter and y position
    frameCounterY = 0;
    y -= animationOffsetY;
    
    if (floatingUp)
    {
      if(++animationOffsetY > 7)
        floatingUp = false;
    }
    else
    {
      if(--animationOffsetY <= 0)
        floatingUp = true;
    }
    
    // Change the position to the new offset
    y += animationOffsetY;
  }
	
	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
	  // Change the orientation based on movement direction
	  if (speedX < 0) flipped = true;
	  else if (speedX > 0) flipped = false;
	  
	  // Check flying positions for animation
    animateFloatingY();
	  
		super.paintObject(g, offset_X,offset_Y);
		if(binInGantry!=null)
		{
			binInGantry.setX(updateBinX());
			binInGantry.setY(updateBinY());
			binInGantry.paintObject(g, offset_X,offset_Y);
		}

		
	}
	
}
