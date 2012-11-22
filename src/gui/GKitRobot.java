/* 
 * @Authored by Tian(Sky) Lan
 */
package gui;

import interfaces.KitRobot;
import interfaces.Kittable;

import java.awt.Graphics;
import java.util.ArrayList;

import controllers.KitRobotController;

import agents.KitRobotAgent;

import NonAgent.Kit;

enum GAnimationState
{
	_Idle0(0), _Idle1(1), _Idle2(2), _Idle3(3);

	private int value;

	GAnimationState(int v)
	{
		value = v;
	}

	public int getValue()
	{
		return value;
	}
}

public class GKitRobot extends GRobot
{

	int step;
	boolean ifArrive;
	int movingState; // 1 for Moving to Origin, 2 for Moving to Destination, 3
						// for Moving back to original position;
	int originalX, originalY;
	int robotKitOffset;

	boolean robotMovement = false;

	GKit kitInArm;
	GObject myOrigin;
	GObject myDest;
	GKit myKit;
	KitRobotController kitRobotController;

	/* Animation Variables */
	int frameCounterY;
	int frameCounterX;
	int animationOffsetY;
	int animationOffsetX;
	boolean floatingUp;
	boolean floatingRight;

	GKitRobotPropeller propeller;

	static String[] pics = { "src/resources/KitRobot/kitRobot_bowser.png" };

	public GKitRobot()
	{
		// super(75, 350, "src/resources/KitRobot/kitRobot_bowser.png");
		super(75, 350, pics);

		originalX = 75;
		originalY = 350;
		step = -1;
		ifArrive = false;
		movingState = 0;

		// Initialize animation variables
		frameCounterY = 0;
		frameCounterX = 0;
		animationOffsetY = 0;
		animationOffsetX = 0;
		floatingUp = true;
		floatingRight = true;

		propeller = new GKitRobotPropeller(x, y);
	}

	public void pickUp(GKit N)
	{
		kitInArm = N;
		System.out.println("KitInArm: " + kitInArm);
		robotKitOffset = (this.getIconWidth() - kitInArm.getIconWidth()) / 2;
	}

	public GKit dropDown()
	{
		GKit temp;
		temp = kitInArm;
		kitInArm = null;
		System.out.println("_______________________TEMP__________" + temp);
		return temp;
	}

	public int armLocationX()
	{
		return this.getX() + robotKitOffset;
	}

	public int armLocationY()
	{
		return this.getY() - kitInArm.getIconHeight();
	}

	public void moveStraight(int destX, int destY)
	{
		if (y != destY)
		{

			if (destY > y)
				speedY = 1;
			else if (destY < y)
				speedY = -1;

			y += speedY;

		}
		else if (x != destX)
		{

			if (destX > x)
				speedX = 1;
			else if (destX < x)
				speedX = -1;

			x += speedX;

		}
		else
		{
			speedX = 0;
			speedY = 0;
		}

		if (x == destX && y == destY)
			ifArrive = true;
		else
			ifArrive = false;

	}

	boolean ifArrive()
	{
		return ifArrive;
	}

	void setStep(int step)
	{
		this.step = step;
	}

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		// Change the orientation based on movement direction
		if (speedX < 0)
			flipped = true;
		else if (speedX > 0)
			flipped = false;

		// Check flying positions for animation
		animateFloatingY();
		animateFloatingX();

		super.paintObject(g, offset_X, offset_Y);

		if (kitInArm != null)
		{
			kitInArm.setX(this.armLocationX());
			kitInArm.setY(this.armLocationY());
			kitInArm.paintObject(g, offset_X, offset_Y);
		}

		// Paint the propeller at the bottom center of the image
		propeller.paint(g, x + images[currentImage].getWidth() / 2,
				y + images[currentImage].getHeight(), offset_X, offset_Y);
	}

	/* Changing the animation floating values for Y position */
	void animateFloatingY()
	{
		// Check the framecounter
		if (frameCounterY <= 8) // if less than 3 stop the update
		{
			frameCounterY++;
			return;
		}

		// Reset the framecounter and y position
		frameCounterY = 0;
		y -= animationOffsetY;

		if (floatingUp)
		{
			if (++animationOffsetY > 10)
				floatingUp = false;
		}
		else
		{
			if (--animationOffsetY <= 0)
				floatingUp = true;
		}

		// Change the position to the new offset
		y += animationOffsetY;
	}

	/* Changing the animation floating values for X position */
	void animateFloatingX()
	{
		// Check the framecounter
		if (frameCounterX <= 20) // if less than 3 stop the update
		{
			frameCounterX++;
			return;
		}

		// Reset the framecounter and x position
		frameCounterX = 0;
		x -= animationOffsetX;

		if (floatingRight)
		{
			if (++animationOffsetX > 3)
				floatingRight = false;
		}
		else
		{
			if (--animationOffsetX <= 0)
				floatingRight = true;
		}

		// Change the position to the new offset
		x += animationOffsetX;
	}

	/*
	 * public void DoPutKit(Kittable origin, Kittable dest, Kit k) {
	 * moveStraight(origin.getX(), origin.getY()); // How to use Kittable as
	 * conveyor or kittable pickup(origin.giveKit()); // needs to add this
	 * function in conveyor and kittingstand moveStraight(dest.getX(),
	 * dest.getY()); dest.addKit(dropDown()); // needs to add this function in
	 * conveyor and kittingstand }
	 */

	/*
	 * public void DoPutKit(Kittable origin, Kittable dest, Kit k) {
	 * moveStraight(origin.getX(), origin.getY()); // How to use Kittable as
	 * conveyor or kittable pickup(origin.giveKit()); // needs to add this
	 * function in conveyor and kittingstand moveStraight(dest.getX(),
	 * dest.getY()); dest.addKit(dropDown()); // needs to add this function in
	 * conveyor and kittingstand }
	 */

	public void setMovingState(int n)
	{

		movingState = n;
	}

	public void DoPutKit(Kittable origin, Kittable dest, Kit k)
	{
		myOrigin = origin.getGui();
		System.out.println("==============" + dest.getGui() + "==============");

		myDest = dest.getGui();
		// myKit = k.getGui();
		movingState = 1;
	}

	// Below is testing without the agents
	public void DoPutKit(GKittable origin, GKittable dest, GKit k)
	{
		myOrigin = (GObject) origin;
		myDest = (GObject) dest;
		// myKit = k;
		movingState = 1;
	}

	public void paintMovement()
	{
		if (movingState == 1)
		{ // state1: moving from home to origin
			if (myOrigin instanceof GConveyorIn)
			{
				moveStraight(myOrigin.getX() + myOrigin.getIconWidth() - robotKitOffset,
						myOrigin.getY() + myOrigin.getIconHeight());
			}
			else
			{
				moveStraight(myOrigin.getX() - this.getIconWidth() + robotKitOffset,
						myOrigin.getY() + myOrigin.getIconHeight());
			}
			// System.out.println("I am moving to origin");

			if (ifArrive)
			{
				System.out.println("Picked Up");
				System.out.println(myOrigin);
				pickUp(((GKittable) myOrigin).giveKit());
				movingState = 2;
			}
		}

		if (movingState == 2)
		{ // state2: moving from origin to destination
			if (myDest instanceof GKittingStand)
			{
				moveStraight(myDest.getX() - this.getIconWidth() + robotKitOffset, myDest.getY() +
						myDest.getIconHeight());
			}
			else
			{
				moveStraight(myDest.getX() + myDest.getIconWidth() - robotKitOffset, myDest.getY() +
						myDest.getIconHeight());
			}
			if (ifArrive)
			{
				System.out.println("dropped");
				((GKittable) myDest).addKit(dropDown());
				System.out.println("-----------------------" + (GKittable) myDest);
				movingState = 3;
			}
		}

		if (movingState == 3)
		{ // state3: moving from destination to home
			moveStraight(originalX, originalY);

			if (ifArrive)
			{
				movingState = 4;
				// kitRobotController.animDone();
			}

		}

	}

	public void setController(KitRobotController krController)
	{
		this.kitRobotController = krController;
	}
}
