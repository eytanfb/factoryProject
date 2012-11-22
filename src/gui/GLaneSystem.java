package gui;
import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GLaneSystem
{
	private ArrayList<GLane> lanes;	//all the lanes in a lane system
	private ArrayList<GFeeder> feeders;	//the feeders in each lane unit
	private ImageIcon laneImage=new ImageIcon("src/resources/lane_belt.png");
	private ImageIcon feederImage=new ImageIcon("src/resources/feeder.png");
	private boolean binPurged;
	private ObjectOutputStream oos;
	
	public GLaneSystem(ObjectOutputStream fromServer)//Bony: changing it so that there are pairs of lanes
	{
		lanes=new ArrayList<GLane>();
		feeders = new ArrayList<GFeeder>();
		boolean pairSwitch = true;
		oos=fromServer;
		
		//initialize all the lanes in the system
		for(int i=0;i<8;i++)
		{
			if(pairSwitch)
			{ //places the top lane
				lanes.add(new GLane(250,70+laneImage.getIconHeight()*i+10*i,oos,i)); 
				feeders.add(new GFeeder(250+laneImage.getIconWidth(), 70+laneImage.getIconHeight()*i+10*i)); //only 4 feeders
				pairSwitch = false;
			}
			else
			{ 	//places the bottom lane
				lanes.add(new GLane(250,70+laneImage.getIconHeight()+(laneImage.getIconHeight()+10)*(i-1),oos,i)); 
				pairSwitch = true;
			} 
		}
	}
	
	//run the specific lane
	public void DoRunLane(int lane_index,String part_type, int number_to_feed)
	{
		if(lane_index>=0&&lane_index<=7)	//there is only 8 lanes, so there needs to be 
		{
			feeders.get(lane_index/2).set_parts(number_to_feed);
			lanes.get(lane_index).feed_lane(part_type, number_to_feed, feeders.get(lane_index/2));
		}
		else
		{
			System.err.println("DoRunLane is HORRIBLY WRONG");
			System.exit(1);
		}
	}
	
	//take a part from a specific nest
	public GPart DoTakePart(int lane_index)
	{
		if(!(lane_index>=0&&lane_index<=7))	//there is only 8 lanes, so there needs to be 
		{
			System.err.println("DoTakePart is HORRIBLY WRONG");
			System.exit(1);
		}
		GPart temp=lanes.get(lane_index).take_part();
		return temp;
	}
	
	//place the bin onto the feeder
	public void DoPlaceBin(int feeder_index)
	{
		if(feeder_index>=0&&feeder_index<=3)	//there are only 4 feeders, so there needs to be 
		{
			feeders.get(feeder_index).receiveBin(new GPartBin(feeders.get(feeder_index).getX(),feeders.get(feeder_index).getY()));
		}
		else
		{
			System.err.println("DoPutBin is HORRIBLY WRONG");
			System.exit(1);
		}
	}
	
	//take out the bin and purge it
	public void DoPurgeToBin(int feeder_index)
	{
		if(feeder_index>=0&&feeder_index<=3)	//there are only 4 feeders, so there needs to be 
		{
			feeders.get(feeder_index).dropBin();
			lanes.get(feeder_index*2).clearDiverterParts();
			lanes.get(feeder_index*2+1).clearDiverterParts();
		}
		else
		{
			System.err.println("DoPurgeToBin is HORRIBLY WRONG");
			System.exit(1);
		}
	}
	
	//clean the nest by removing all the stuff in it
	public void DoPurgeNest(int nest_index)
	{
		if(nest_index>=0&&nest_index<=7)	//there are only 4 feeders, so there needs to be 
		{
			lanes.get(nest_index).purgeNest();
		}
		else
		{
			System.err.println("DoPurgeNest is HORRIBLY WRONG");
			System.exit(1);
		}
	}
	
	//paint everything in the entire lane system
	public void paintLaneSystem(Graphics g, int offset_X, int offset_Y)
	{
		for(int i=0;i<lanes.size();i++)
		{
			lanes.get(i).paintLane(g, offset_X,offset_Y);
		}
		for(int j=0;j<feeders.size();j++)
		{
			feeders.get(j).paintFeeder(g, offset_X,offset_Y);
		}
	}
	
	//paint the diverters according to their directions
	public void paintDiverterChoice(Graphics g, boolean top_lane1, boolean top_lane2, boolean top_lane3, boolean top_lane4,int offset_X, int offset_Y){
		for(int j=0;j<feeders.size();j++){
			if(j==0)feeders.get(j).paintButton(g, top_lane1, offset_X,offset_Y);
			else if(j==1)feeders.get(j).paintButton(g, top_lane2, offset_X,offset_Y);
			else if(j==2)feeders.get(j).paintButton(g, top_lane3, offset_X,offset_Y);
			else if(j==3)feeders.get(j).paintButton(g, top_lane4, offset_X,offset_Y);
		}
	}
	
	//update all the coordinates in the lane system
	public void system_update_animation()
	{
		for(int i=0;i<lanes.size();i++)
		{
			lanes.get(i).update_parts();
		}
	}
	
	public ArrayList<GLane> getLanes()
	{
		return lanes;
	}
}
