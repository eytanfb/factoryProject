package gui;

import java.awt.Graphics;
import java.util.ArrayList;

import NonAgent.Kit;
import interfaces.Kittable;

//TODO: I created a new interface GConveyor to make things easier for the Controller ASK ABOUT THIS -- EYTAN
public class GConveyorOut extends GObject implements GKittable, GConveyor
{
	int x, y;
//	ArrayList<GKit> myGKits;
	GKit leavingGKit;
	boolean conveyorMovementOut = false;
	boolean conveyorOutAnimationDone = false;

	public GConveyorOut()
	{
		super(0, 600, "src/resources/pipe_red.png");
		y = 600;
	}

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{

		if (leavingGKit != null) {
			leavingGKit.paintObject(g, offset_X,offset_Y);
		}
		super.paintObject(g, offset_X,offset_Y);


	}


	public GKit giveKit()
	{
		return leavingGKit;
	}

	public int getX()
	{

		return x;
	}

	public int getY()
	{

		return y;
	}

	public void DoMove(Kit k)
	{
//		leavingGKit=k.getGui();
		System.out.println("--------------------GConveyorOut: "+this.getIconWidth()+"----------------------------");
		leavingGKit.setX(this.getIconWidth());
		leavingGKit.setY(600);
		conveyorMovementOut = true;
	}
	
	public void DoMove()
	{
//		addKit(k);
//		conveyorMovementOut = true;
		leavingGKit.setX(this.getIconWidth());
		leavingGKit.setY(600);
		conveyorMovementOut = true;
	}

	public void moveKit()
	{
		if (leavingGKit!= null)
		{
			leavingGKit.moveStraight(this.getX()-leavingGKit.getIconWidth(), this.getY());
			if (leavingGKit.getX() + leavingGKit.getIconWidth() == 0)
			{
				leavingGKit = null;
				conveyorMovementOut = false;
				conveyorOutAnimationDone = true;
			}
		}
	}

	public void addKit(GKit k)
	{
		leavingGKit = k;
	}

}