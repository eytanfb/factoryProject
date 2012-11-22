package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import NonAgent.Part;

public class GKit extends GObject implements Serializable
{

	int kitNumber;
	String kitName;
	int numberOfParts;
	ArrayList<GPart> partsInKit;
	ArrayList<Part> partsKit;
	private ImageIcon camera_flash = new ImageIcon("src/resources/camera_flash.png");

	int pictureFrameCount; // a counter to control the time of flashing of
							// camera
	boolean cameraFinished;

	public GKit()
	{
		super(10, 400, "src/resources/kit.png");
		partsInKit = new ArrayList<GPart>();
		partsKit = new ArrayList<Part>();
		numberOfParts = 8;
		kitNumber = 0;
		kitName = "DefaultKit";
		pictureFrameCount = 0;
		cameraFinished = false;
	}

	public GKit(int x, int y, int numberOfParts, int number, String name)
	{
		super(x, y, "src/resources/kit.png");
		partsInKit = new ArrayList<GPart>();
		this.numberOfParts = numberOfParts;
		kitNumber = number;
		kitName = name;
	}

	public void setPartsList()
	{
		for (int i = 0; i < partsInKit.size(); i++)
		{
			partsKit.add(new Part(partsInKit.get(i).typeName));
			// System.out.println(partsInKit.get(i).typeName);
		}
	}

	public void addAPart(GPart N)
	{
		int index = partsInKit.size();
		partsInKit.add(N);
		N.setX(generatePartPositionX(index));
		N.setY(generatePartPositionY(index));

	}

	int generatePartPositionX(int partIndex) // calculate the x coordinate of a
												// part in the kit based on its
												// index
	{
		int tempX = 0;

		if (partIndex == 0 || partIndex == 3 || partIndex == 6)
		{
			tempX = x;

		}
		else if (partIndex == 1 || partIndex == 4 || partIndex == 7)
		{
			tempX = x + getIconWidth() / 3;

		}
		else if (partIndex == 2 || partIndex == 5 || partIndex == 8)
		{
			tempX = x + getIconWidth() * 2 / 3;

		}
		else
		{
			System.out.println("The X partIndex in function generatePartPosition is illegal");
		}

		return tempX;

	}

	int generatePartPositionY(int partIndex) // calculate the y coordinate of a
												// part in the kit based on its
												// index
	{
		int tempY = 0;

		if (partIndex == 0 || partIndex == 1 || partIndex == 2)
		{
			tempY = y;

		}
		else if (partIndex == 3 || partIndex == 4 || partIndex == 5)
		{
			tempY = y + getIconHeight() / 3;
		}
		else if (partIndex == 6 || partIndex == 7 || partIndex == 8)
		{
			tempY = y + getIconHeight() * 2 / 3;

		}
		else
		{
			System.out.println("The Y partIndex in function generatePartPosition is illegal");
		}

		return tempY;

	}

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		super.paintObject(g, offset_X, offset_Y);
		for (int i = 0; i < partsInKit.size(); i++)
		{
			partsInKit.get(i).paintObject(g, offset_X, offset_Y);
			partsInKit.get(i).setX(generatePartPositionX(i));
			partsInKit.get(i).setY(generatePartPositionY(i));
		}
		if (pictureFrameCount > 0)
		{
			g.drawImage(camera_flash.getImage(), x, y, null);
			pictureFrameCount--;
			if (pictureFrameCount == 0)
				cameraFinished = true;
			else
				cameraFinished = false;
		}
		else
			cameraFinished = false;
	}

	public void takePicture()
	{
		pictureFrameCount = 10;
	}

}
