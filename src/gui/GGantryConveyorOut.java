package gui;

import java.awt.Graphics;
import java.util.ArrayList;

public class GGantryConveyorOut extends GObject 
{
	boolean conveyorMovementOut = false;
	GPartBin myGBin;

	public GGantryConveyorOut(int x, int y)
	{
		super(x, y, "src/resources/pipe_green.png");
	}

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		super.paintObject(g, offset_X,offset_Y);
		if (myGBin != null)
		{
			myGBin.paintObject(g, offset_X,offset_Y);
		}
	}

	public void addBin(GPartBin bin)
	{
		myGBin = bin;
	}

	public GPartBin giveBin()
	{
		return myGBin;
	}



//	public void DoMove(GPartBin k)
//	{
//		myGBins.add(k.getGui());
//		myGBins.get(myGBins.size() - 1).setX(17);
//		myGBins.get(myGBins.size() - 1).setY(566 - 100 * (myGBins.size() - 1));
//		conveyorMovementOut = true;
//	}
	
	public void DoMove()
	{
		if(myGBin != null)
		{	
			conveyorMovementOut = true;
		}
	}

	public void moveBin()
	{
		if (myGBin != null)
		{
			myGBin.moveStraightXFirst(this.getX(),800);
			if (myGBin.getY() >=800)
			{
				myGBin = null;
				conveyorMovementOut = false;
				
			}
		}
	}
}