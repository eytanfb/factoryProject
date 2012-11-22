package gui;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class GNest extends GObject
{
	private final int NEST_CAPACITY=4;		
	private final int part_width=25;
	private final int part_height=25;
	ArrayList<GPart> parts_in_nest;
	private ImageIcon camera_flash=new ImageIcon("src/resources/camera_flash.png");
	private int index;
	int pictureFrameCount;			//a counter to control the time of flashing of camera
	boolean cameraFinished;
	private GLane laneReference;
	
	public GNest()
	{
		super(0,0, "src/resources/nest.png" );
		parts_in_nest = new ArrayList<GPart> ();
		createTempNest("src/resources/enemy1.png",1,"mushroom");
		pictureFrameCount=0;
		cameraFinished=false;
		laneReference=null;
	}

	public GNest(int x,int y)
	{
		super(x,y, "src/resources/nest.png" );
		parts_in_nest = new ArrayList<GPart> ();
		//createTempNest("src/resources/enemy1.png",1,"mushroom");
		pictureFrameCount = 0;
		cameraFinished=false;
		laneReference=null;
	}
	
	public GNest(int x, int y, GLane parentLane)
	{
		super(x,y, "src/resources/nest.png" );
		parts_in_nest = new ArrayList<GPart> ();
		//createTempNest("src/resources/enemy1.png",1,"mushroom");
		pictureFrameCount = 0;
		cameraFinished=false;
		laneReference=parentLane;
	}
	
	//if the flash of the camera has gone
	public boolean ifCameraFinished()
	{
		return cameraFinished;
	}
	
	public GNest(int x, int y, String partImageAddress, int partTypeNumber,String partName)
	{
		super(x, y, "src/resources/nest.png");
		parts_in_nest = new ArrayList<GPart> ();	
		createTempNest(partImageAddress,partTypeNumber,partName);
		pictureFrameCount = 0;
		laneReference=null;
		}
	
	//add a part to the nest
	public void addPart(GPart part_to_add)
	{
		if(parts_in_nest.size()!=NEST_CAPACITY)	//if the nest is not full
		{
				parts_in_nest.add(part_to_add);
		}
		else
		{
			System.out.println("The Lane-Nest system goes wrong");
		}
	}
	
	//purge the nest by removing all its parts
	public void purgeNest()
	{
		parts_in_nest.clear();
	}
	
	//push a part into the robot and remove that part pushed
	public GPart pushParts()		//reomove a part from the nest and return it
	{
		if(parts_in_nest.size()!=0)
		{
			GPart temp=parts_in_nest.remove(0);
			for(int i=0;i<parts_in_nest.size();i++)
			{
				parts_in_nest.get(i).setInNestSocket(false);
			}
			return temp;
		}
		else
		{
			System.err.println("The process of taking parts from nest is compromised. File: GNest");
			return null;
		}
	}
	
	public boolean isFull()
	{
		return parts_in_nest.size()==NEST_CAPACITY;
	}
	
	
	//for the robot to craete a temp nest
	public void createTempNest(String partImageAddress,int partTypeNumber,String partName)
	{
	
		for(int i = 0; i<4; i++)
		{
			if(i==0)
			{
				parts_in_nest.add(new GPart(this.getX()+2,this.getY()+2,partImageAddress,1,partName));
			}
			if(i==1)
			{
				parts_in_nest.add(new GPart(this.getX()+super.getIconWidth()-parts_in_nest.get(0).getIconWidth()-2,this.getY()+2,partImageAddress,1,partName));
			}
			if(i==2)
			{
				parts_in_nest.add(new GPart(this.getX()+2,this.getY()+super.getIconHeight()-parts_in_nest.get(0).getIconHeight()-2,partImageAddress,1,partName));
			}
			if(i==3)
			{
				parts_in_nest.add(new GPart(this.getX()+super.getIconWidth()-parts_in_nest.get(0).getIconWidth()-2,this.getY()+super.getIconHeight()-parts_in_nest.get(0).getIconHeight()-2,partImageAddress,1,partName));
			}
		}
	}
	
	//take the picture and make the flash last for 10 frames
	public void takePicture() 
	{			
		pictureFrameCount = 10;
	}
	
	//paint eveything in the nest
	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
		super.paintObject(g, offset_X,offset_Y);
		for(int i=0;i<parts_in_nest.size();i++)
		{
			parts_in_nest.get(i).paintObject(g, offset_X,offset_Y);
		}
		
		if (pictureFrameCount > 0) 
		{
			g.drawImage(camera_flash.getImage(), x+offset_X, y+offset_Y, null);
			pictureFrameCount--;
			if(pictureFrameCount==0)
				cameraFinished=true;
			else
				cameraFinished=false;
		}
		else
			cameraFinished=false;
	}
	
	//update the position of the parts in the nest
	public void update_parts()
	{
		for(int i=0;i<parts_in_nest.size();i++)
		{
			if(i==0&&!parts_in_nest.get(i).ifInNestSocket())
			{
				parts_in_nest.get(i).moveTo(this.getX()+2, this.getY()+2, 2);
				
				if(Math.abs(parts_in_nest.get(i).getX()-(this.getX()+2))<=2&&Math.abs(parts_in_nest.get(i).getY()-(this.getY()+2))<=2)
				{
					parts_in_nest.get(i).setInNestSocket(true);
					laneReference.DidFeedPart();
					try
					{
						if(laneReference.getOos()!=null)
						{
							laneReference.getOos().writeObject(parts_in_nest.get(i).getImageAddress());
							laneReference.getOos().reset();
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(i==1&&!parts_in_nest.get(i).ifInNestSocket())
			{
				parts_in_nest.get(i).moveTo(this.getX()+super.getIconWidth()-parts_in_nest.get(i).getIconWidth()-2, this.getY()+2, 2);
				if(Math.abs(parts_in_nest.get(i).getX()-(this.getX()+super.getIconWidth()-parts_in_nest.get(i).getIconWidth()-2))<=2&&Math.abs(parts_in_nest.get(i).getY()-(this.getY()+2))<=2)
				{
					parts_in_nest.get(i).setInNestSocket(true);
					laneReference.DidFeedPart();
					System.out.println("\n\n\n\n\n\n\n\n\nGNest FunctionL"+parts_in_nest.get(i).getImageAddress()+"\n\n\n\n\n\n\n\n\n\n");
					try
					{
						if(laneReference.getOos()!=null)
						{
							laneReference.getOos().writeObject(parts_in_nest.get(i).getImageAddress());
							laneReference.getOos().reset();
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(i==2&&!parts_in_nest.get(i).ifInNestSocket())
			{
				parts_in_nest.get(i).moveTo(this.getX()+2,this.getY()+super.getIconHeight()-parts_in_nest.get(i).getIconHeight()-2, 2);
				if(Math.abs(parts_in_nest.get(i).getX()-(this.getX()+2))<=2&&Math.abs(parts_in_nest.get(i).getY()-(this.getY()+super.getIconHeight()-parts_in_nest.get(i).getIconHeight()-2))<=2)
				{
					parts_in_nest.get(i).setInNestSocket(true);
					laneReference.DidFeedPart();
					System.out.println("\n\n\n\n\n\n\n\n\nGNest FunctionL"+parts_in_nest.get(i).getImageAddress()+"\n\n\n\n\n\n\n\n\n\n");
					try
					{
						if(laneReference.getOos()!=null)
						{
							laneReference.getOos().writeObject(parts_in_nest.get(i).getImageAddress());
							laneReference.getOos().reset();
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(i==3&&!parts_in_nest.get(i).ifInNestSocket())
			{
				parts_in_nest.get(i).moveTo(this.getX()+super.getIconWidth()-parts_in_nest.get(i).getIconWidth()-2, this.getY()+super.getIconHeight()-parts_in_nest.get(i).getIconHeight()-2, 2);
				if(Math.abs(parts_in_nest.get(i).getX()-(this.getX()+super.getIconWidth()-parts_in_nest.get(i).getIconWidth()-2))<=2&&Math.abs(parts_in_nest.get(i).getY()-(this.getY()+super.getIconHeight()-parts_in_nest.get(i).getIconHeight()-2))<=2)
				{
					parts_in_nest.get(i).setInNestSocket(true);
					laneReference.DidFeedPart();
					try
					{
						if(laneReference.getOos()!=null)
						{
							laneReference.getOos().writeObject(parts_in_nest.get(i).getImageAddress());
							laneReference.getOos().reset();
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	//for the robot, since we have separate screens
	public void addPartToNest(String partType)
	{
		if(parts_in_nest.size()==0)
		{
			parts_in_nest.add(new GPart(this.getX()+2, this.getY()+2,partType));
		}
		else if(parts_in_nest.size()==1)
		{
			parts_in_nest.add(new GPart(this.getX()+super.getIconWidth()-part_width-2, this.getY()+2,partType));
		}
		else if(parts_in_nest.size()==2)
		{
			parts_in_nest.add(new GPart(this.getX()+2,this.getY()+super.getIconHeight()-part_height-2,partType));
		}
		else if(parts_in_nest.size()==3)
		{
			parts_in_nest.add(new GPart(this.getX()+super.getIconWidth()-part_width-2, this.getY()+super.getIconHeight()-part_height-2,partType));
		}
		else
		{
			System.out.println("Something is wrong with the function addPartToNest "+parts_in_nest.size());
		}
	}
	
	public boolean ifEmpty()
	{
		return parts_in_nest.size()==0;
	}
}
