package gui;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class GObject implements Serializable
{
	int x;		// (x,y): the coordinate of the object (upper left corner of the image)
	int y;
	
	int speedX;
	int speedY;
	private boolean canMove;
	ImageIcon pic;
	String imageAddress;
	
	public GObject(int x, int y, String imageAddress ){
		this.x = x;
		this.y = y;
		this.speedX = 0;
		this.speedY = 0;
		canMove=false;
		this.imageAddress = imageAddress;
		pic = new ImageIcon(imageAddress);
	}

	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		g.drawImage(pic.getImage(), x+offset_X, y+offset_Y, null);
	}
	
	public String getImageAddress()
	{
	  return imageAddress;
	}
	
	public void setX (int x)
	{
		this.x = x;
	}
	
	public void setY (int y)
	{
		this.y=y;
	}
	
	public int getX ()
	{	
	  return x;
	}
	
	public int getY()
	{
	  return y;
	}
	
	public boolean ifCanMove()
	{
		return canMove;
	}
	
	public void setCanMove(boolean can_or_cannot)
	{
		canMove=can_or_cannot;
	}
	
	public void moveStraight(int destX, int destY)
	{	
		if(y!=destY){	
			if(destY>y)
				speedY = 1;
			else if(destY<y)
				speedY = -1;
			y+=speedY;
		}
		else if(x!=destX){		
			if(destX>x)
				speedX = 1;
			else if(destX<x)
				speedX = -1;
			x+=speedX;
		}
		else
		{
			speedX=0;
			speedY=0;
		}
	}
	
	public void moveStraightXFirst(int destX, int destY){
	  if(x!=destX){		
		  if(destX>x)
			  speedX = 1;
		  else if(destX<x)
			  speedX = -1;
		  x+=speedX;
	  }
	  else if(y!=destY){			
		  if(destY>y)
			  speedY = 1;
		  else if(destY<y)
			  speedY = -1;
		  y+=speedY;
	  }
	  else
	  {
		  speedX=0;
		  speedY=0;
	  }
  }
		
	public void moveTo(int destX, int destY, int speed)
	{		
		int dx=destX-x;
		int dy=destY-y;
		double distance=Math.sqrt(dx*dx+dy*dy);
		if(Math.abs(x-destX)>=2)
			x+=(int)speed*(dx/distance);
		if(Math.abs(y-destY)>=2)
			y+=(int)speed*(dy/distance);
	}
	
	public void moveToLeftOf(GObject destination)		//move to the left of the destination object: for example, partrobot moves to the left of a nest
	{
		moveStraight(destination.getX()-this.getIconWidth(),destination.getY()+(destination.getIconHeight()-this.getIconHeight())/2);
	}
	
	public void moveToRightOf(GObject destination)		//move to the right of the destination object
	{
		moveStraight(destination.getX()+destination.getIconWidth(),destination.getY()+(destination.getIconHeight()-this.getIconHeight())/2);
	}	
	
	public void moveToBottomOf(GObject destination)
	{
		moveStraight(destination.getX()+(destination.getIconWidth()-this.getIconWidth())/2,destination.getY()+destination.getIconHeight());	
	}
	
	public void moveToTopOf(GObject destination)
	{
		moveStraight(destination.getX()+(destination.getIconWidth()-this.getIconWidth())/2,destination.getY()-this.getIconHeight());
	}
	
	public void moveOnLane(int lane_speed)
	{
		if(canMove)
		{
			for(int i=0;i<lane_speed;i++)
			{
				this.x-=1;
			}
		}
	}
	
	
	public int getIconWidth()			//return the width of the imageIcon
	{
		return pic.getIconWidth();
	}
	
	public int getIconHeight()			//return the height of the imageIcon
	{
		return pic.getIconHeight();
	}
}
