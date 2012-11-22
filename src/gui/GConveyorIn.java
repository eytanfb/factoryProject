package gui;

import java.awt.Graphics;

import NonAgent.Kit;
import interfaces.ConveyorSystem;
import interfaces.Kittable;

// TODO: I created a new interface GConveyor to make things easier for the Controller ASK ABOUT THIS -- EYTAN
public class GConveyorIn extends GObject implements GKittable, GConveyor
{
	GKit myGKit;
	ConveyorSystem conveyorSystem;
	boolean finalDest = false;
	boolean firstTime = true;
	boolean conveyorMovementIn = false;
	boolean conveyorInAnimationDone = false;

	public GConveyorIn()
	{
		super(0, 100, "src/resources/pipe_red.png");
	}

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		if (myGKit != null)
		{
			myGKit.paintObject(g, offset_X,offset_Y);
//			System.out.println("ConveyorIn.GKit:" + myGKit.x+ " " + myGKit.y);
		}
		super.paintObject(g, offset_X,offset_Y);
	}


	public GKit giveKit()
	{
		System.out.println(" ------------------- " + myGKit + " ------------------- ");
		return myGKit;
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
		System.out.println("Do Move called");
		myGKit = k.getGui();
		myGKit.x = -75;
		myGKit.y = this.getY();
		if (myGKit != null) {
//			System.out.println("!!!!!!NULL MYGKIT!!!!!!");
			System.out.println(" ------------------- " + k + " ------------------- ");
			System.out.println(" ------------------- " + myGKit + " ------------------- ");
		}
		conveyorMovementIn = true;
	}
	

	public void moveKit()
	{
//		System.out.println(" ------------------- " + myGKit + " ------------------- in moveKit ");
		myGKit.moveStraight(this.getX()+this.getIconWidth(), this.getY());
		if (myGKit.getX() == this.getX()+this.getIconWidth())
		{
			conveyorMovementIn = false;
			conveyorInAnimationDone = true;
			//conveyorSystem.msgAnimDone();
		}
	}

	public void addKit(GKit k)
	{
		// TODO Auto-generated method stub

	}

	public void setConveyorSystem(ConveyorSystem conveyorSystem)
	{
		this.conveyorSystem = conveyorSystem;
	}

}