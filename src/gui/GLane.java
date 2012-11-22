package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GLane extends GObject implements ActionListener
{ 
	private ArrayList<GPart> parts_on_lane;	//all the parts on the lane
	private ArrayList<whiteStripe> stripes;	//the stripes on the lane
	private ArrayList<GPart> parts_in_diverter;	//the parts in the diverter that has not been into the lane
	private GNest nest;	
	private ImageIcon laneImage=new ImageIcon("src/resources/lane_belt.png"); 
	private ImageIcon nestImage=new ImageIcon("src/resources/nest.png");
	private int speed;	//the speed of the lane
	private GFeeder feeder;	
	private ObjectOutputStream oos;	//the output stream so that the server would be informed "part fed"
	private Timer dump_interval;	//the time interval for part entering the lane
	private int lane_index;	//the lane index
	
	public GLane(int x,int y, ObjectOutputStream fromServer, int index)
	{
		super(x, y, "src/resources/lane_belt.png");
		lane_index=index;
		oos=fromServer;
		dump_interval=new Timer(1000,this); //this helps move the parts
		stripes=new ArrayList<whiteStripe>();
		parts_in_diverter=new ArrayList<GPart>();
		parts_on_lane=new ArrayList<GPart>();
		speed=2;
		for(int i=0;i<10;i++)
		{
			stripes.add(new whiteStripe(this.getX()+i*20+1,this.getY()+5));	//add stripes that bring the effects of lane moving
		}
		nest=new GNest(x-nestImage.getIconWidth(),y,this); //a nest is created with every GUILane
	}
	
	public ArrayList<whiteStripe> getStripes() {return stripes;}
	
	public ArrayList<GPart> getParts(){return parts_on_lane;}
	
	public ArrayList<GPart> getPartsInDiverter(){return parts_in_diverter;}
	
	public Image getImage(){return laneImage.getImage();}
	
	public int getImageHeight(){return laneImage.getIconHeight();}
	
	public int getImageWidth(){return laneImage.getIconWidth();}
	
	public int getSpeed(){return speed;}
	
	public void setSpeed(int new_speed){speed=new_speed;}
	
	public Timer getTimer(){return dump_interval;}
	
	public void turn_off(){speed=0;}
	
	public void turn_on(){speed=2;}
	
	//paint everything in the lane
	public void paintLane(Graphics g, int offset_X, int offset_Y)
	{
		g.drawImage(this.getImage(),this.getX()+offset_X,this.getY()+offset_Y,null);
		
		nest.paintObject(g, offset_X,offset_Y);
		
		for(int i=0;i<stripes.size();i++)
		{
			g.drawImage(stripes.get(i).getImage(),stripes.get(i).getX()+offset_X,stripes.get(i).getY()+offset_Y,null);
		}
		for(int i=0;i<parts_on_lane.size();i++)
		{
			parts_on_lane.get(i).paintObject(g, offset_X,offset_Y);
		}
	}
	
	public GNest get_nest()
	{
		return nest;
	}
	
	//update the position of all the parts on the lane
	public void update_parts()
	{
		nest.update_parts();
		for(int i=0;i<this.getStripes().size();i++)
		{
			this.getStripes().get(i).move();
		}
		
		for(int i=0;i<this.getParts().size();i++)
		{
			if(this.getParts().get(i).getX()<this.getX()&&!nest.isFull())
			{
				nest.addPart(getParts().get(i));
				parts_on_lane.remove(i); //the part has gone into the nest
			}
			else if(nest.isFull()&&this.getParts().get(i).getX()<=this.getX()+i*this.getParts().get(i).getIconWidth())
			{
				this.getParts().get(i).setCanMove(false);
			}
		}
		
		for(int i=0;i<this.getParts().size();i++)
		{
			this.getParts().get(i).moveOnLane(this.getSpeed());
		}
	}
	
	public boolean ifLaneClean()
	{
		return (nest.ifEmpty()&&parts_on_lane.size()==0&&!feeder.ifFeederOccupied());
	}
	
	public boolean ifLaneEmpty()
	{
		return parts_on_lane.size()==0;
	}
	
	//give the part when one is taken from the nest
	public GPart take_part()
	{
		GPart temp=nest.pushParts();
		for(int i=0;i<this.getParts().size();i++)
		{
			this.getParts().get(i).setCanMove(true);
		}
		return temp;
	}
	
	//remove all the parts in the diverter
	public void clearDiverterParts()
	{
		parts_in_diverter.clear();
	}
	
	//purge the nest and remove all the parts in it
	public void purgeNest()
	{
		nest.purgeNest();
		for(int i=0;i<this.getParts().size();i++)
		{
			this.getParts().get(i).setCanMove(true);
		}
	}
	
	//inform the server that a part is fed
	public void DidFeedPart() 
	{
		try
		{
			if(oos!=null)
			{
				System.out.println("GLane: *@^*($RYUTHWGTKU#YIOY#TYLITHG&Y(*Y*TFEHGIYGIUIGHEUJGEIOUGOEJGHOUGEHUGY");
				oos.writeObject(new String("Nest_PartFed"));
				oos.reset();
				oos.writeObject(new Integer(lane_index));
				oos.reset();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//feed the lane with specific typeof part to a specific feeder with a specific number
	public void feed_lane(String part_type,int number, GFeeder gf)
	{
		for(int i=0;i<number;i++)
		{
			parts_in_diverter.add(new GPart(this.getX()+laneImage.getIconWidth()-15,this.getY()+laneImage.getIconHeight()/2-10,part_type));
			parts_in_diverter.get(parts_in_diverter.size()-1).setCanMove(true);
		}
		feeder=gf;
		dump_interval.start();
	}
	
	public ObjectOutputStream getOos()
	{
		return this.oos;
	}
	
	//inner class for the stripe
	class whiteStripe
	{
		private ImageIcon stripeImage=new ImageIcon("src/resources/white.png");
		private int x_cor;
		private int y_cor;
		
		public whiteStripe(int x, int y)
		{
			x_cor=x;
			y_cor=y;
		}
		
		//move the stripe
		public void move()
		{
			for(int i=0;i<speed;i++)
			{
				x_cor=x_cor-1;
			}
				
			if(x_cor<=GLane.this.getX())
			{
				x_cor=GLane.this.getX()+laneImage.getIconWidth()-5;
			}
		}
		
		public Image getImage()
		{
			return stripeImage.getImage();
		}
		
		public int getX()
		{
			return x_cor;
		}
		
		public int getY()
		{
			return y_cor;
		}
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(parts_in_diverter.size()>0)
		{
			parts_on_lane.add(parts_in_diverter.remove(0));
			feeder.decrement_parts();
		}
	}
}
