package gui;

import java.awt.Graphics;

public class GGantryConveyorIn extends GObject 
{
	GPartBin myGBin;

	boolean finalDest = false;
	boolean firstTime = true;
	boolean conveyorMovementIn = false;

	public GGantryConveyorIn(int x, int y)
	{
		super(x, y, "src/resources/GGantryConveyorIn.png");
	}

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		super.paintObject(g, offset_X,offset_Y);
		if (myGBin != null)
		{
			myGBin.paintObject(g, offset_X,offset_Y);
		}
	}

	public void addBin(String myPart)
	{
		myGBin = new GPartBin(myPart);
		myGBin.setX(322);
		myGBin.setY(0);
	}

	public GPartBin giveBin()
	{
		return myGBin;
	}

	public int getX()
	{

		return x;
	}

	public int getY()
	{

		return y;
	}

//	public void DoMove(GPartBin k)
//	{
//		System.out.println("Do Move called");
//		myGBin = k.getGui();
//		myGBin.setX(this.getX());
//		myGBin.setY(this.getY());
//		conveyorMovementIn = true;
//	}
	
	public void DoMove(GPartBin k) {
		System.out.println("Do Move called");
		myGBin = k;
		myGBin.setX(this.getX());
		myGBin.setY(this.getY());
		conveyorMovementIn = true;
		
	}

	public void moveBin()
	{

		myGBin.moveStraight(this.getX(), this.getY()+this.getIconHeight());
		if (myGBin.getY() == this.getY()+this.getIconHeight())

		{
			conveyorMovementIn = false;
		}
	}
	
	//TODO: We need to make a function so that we can move each bin across the screen using the turtle idea we were talking about. Will figure this out at next group meeting.
}