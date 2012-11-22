package gui;

import java.awt.Graphics;
import java.util.ArrayList;

public class GPartRobot extends GRobot
{
	// PartsRobotState state;
	int step;
	boolean ifArrive;

	/* Animation Variables */
	int frameCounterY;
	int animationOffsetY;
	boolean floatingUp;

	public ArrayList<GPart> partsInArm;

	static String[] pics = { "src/resources/PartsRobot/partsRobot_lakitu0.png",
			"src/resources/PartsRobot/partsRobot_lakitu1.png" };

	/* Hold the value of the current frames passed */
	int animationFrames;

	/* True if in the 2nd frame of the animation */
	boolean animationFrame2;

	public GPartRobot()
	{
		super(100, 400, pics);
		step = -1;
		ifArrive = false;
		initializeArm();

		// Initialize animation variables
		frameCounterY = 0;
		animationOffsetY = 0;
		floatingUp = true;

		animationFrames = 0;
		animationFrame2 = false;
	}

	public GPartRobot(int x, int y)
	{
		super(x, y, pics);
		step = -1;
		ifArrive = false;
		initializeArm();

		// Initialize animation variables
		frameCounterY = 0;
		animationOffsetY = 0;
		floatingUp = true;

		animationFrames = 0;
		animationFrame2 = false;
	}

	public void initializeArm()
	{
		partsInArm = new ArrayList<GPart>();
	}

	public void pickUp(GPart N)
	{
		if (partsInArm.size() < 4)
			partsInArm.add(N);
	}

	public GPart dropDown()
	{
		return partsInArm.remove(partsInArm.size() - 1);
	}

	public int armLocationX(int index) // given the index of a part, generate
										// its x coordinate on the window
	{
		int tempX = 0;
		if (index == 0 || index == 2)
		{
			tempX = this.x + this.getIconWidth() * 3 / 5;
		}
		else if (index == 1 || index == 3)
		{

			tempX = this.x + this.getIconWidth() * 4 / 5;
		}
		return tempX;
	}

	public int armLocationY(int index)// given the index of a part, generate its
										// y coordinate on the window
	{
		int tempY = 0;
		if (index == 0 || index == 1)
		{
			tempY = this.y + this.getIconHeight() / 3;
		}
		else if (index == 2 || index == 3)
		{
			tempY = this.y + this.getIconHeight() / 2;

		}
		return tempY;

	}

	public void moveStraight(int destX, int destY)
	{

		super.moveStraight(destX, destY);

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

	/* Changing the animation floating values for Y position */
	void animateFloatingY()
	{
		// Check the framecounter
		if (frameCounterY <= 30) // if less than 3 stop the update
		{
			frameCounterY++;
			return;
		}

		// Reset the framecounter and y position
		frameCounterY = 0;
		y -= animationOffsetY;

		if (floatingUp)
		{
			if (++animationOffsetY > 5)
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

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		// Change the orientation based on movement direction
		if (speedX < 0)
			flipped = true;
		else if (speedX > 0)
			flipped = false;

		// Check flying positions for animation
		animateFloatingY();

		// Change the animation image
		animate();

		super.paintObject(g, offset_X, offset_Y);

		for (int i = 0; i < partsInArm.size(); i++)
		{
			partsInArm.get(i).setX(this.armLocationX(i));
			partsInArm.get(i).setY(this.armLocationY(i));
			partsInArm.get(i).paintObject(g, offset_X, offset_Y);
		}
	}

	/* Check the frame counter to see if the image needs to be updated */
	void animate()
	{
		// If is the second image, hold for 10 frames
		if (animationFrame2)
		{
			if (animationFrames <= 300)
				animationFrames++;
			else
			{
				animationFrames = 0;
				currentImage = 0;
				animationFrame2 = false;
			}
		}
		// Else hold for 100 frames
		else
		{
			if (animationFrames <= 1200)
				animationFrames++;
			else
			{
				animationFrames = 0;
				currentImage = 1;
				animationFrame2 = true;
			}
		}
	}
}
