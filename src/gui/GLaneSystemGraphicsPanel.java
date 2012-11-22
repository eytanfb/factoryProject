package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GLaneSystemGraphicsPanel extends JPanel implements ActionListener
{
	private int offset_X;
	private int offset_Y;
	private GLaneSystem system; //calls LaneSystem.java
	private Timer timer;	//the timer for the frame rate
	private boolean topLane1, topLane2, topLane3, topLane4;	//the direction of the diverters
	private GLaneSystemManager manager;
	public GLaneSystemGraphicsPanel(GLaneSystemManager info_center,int offset_X, int offset_Y)
	{
		this.offset_X=offset_X;
		this.offset_Y=offset_Y;
		this.manager=info_center;
		if(this.manager!=null)
			system=new GLaneSystem(manager.get_oos());
		else
			system=new GLaneSystem(null);
		topLane1 = topLane2 = topLane3 = topLane4 = true;
		timer=new Timer(1000/24,this);
		timer.start();
	}
	
	//paint everything in the panel
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		system.paintLaneSystem(g,offset_X,offset_Y);
		system.paintDiverterChoice(g, topLane1, topLane2, topLane3, topLane4,offset_X,offset_Y);
	}
	
	public void DoDiverter(int gfeeder, boolean direction){
		//int gfeeder indicates which feeder you want to select [0-3]
		//boolean direction determines whether you feed the top or bottom [true = diverter at top | false = diverter at bottom]
		
		if(gfeeder == 0){
			if(direction){topLane1 = true;}
			else{topLane1 = false;}
		}else if(gfeeder == 1){
			if(direction){topLane2 = true;}
			else{topLane2 = false;}
		}else if(gfeeder == 2){
			if(direction){topLane3 = true;}
			else{topLane3 = false;}
		}else if(gfeeder == 3){
			if(direction){topLane4 = true;}
			else{topLane4 = false;}
		}
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource()==timer)
		{
			system.system_update_animation();	//let the lane system to update all the coordinate of everything in it
		}
		repaint();
	}
	
	//used for changing the speed at which the animation goes
	public void frameRateChanged(int fps)
	{
		int delay=1000/fps;
		timer.setDelay(delay);
		for(int i=0;i<system.getLanes().size();i++)
		{
			system.getLanes().get(i).getTimer().setDelay(delay*24);
		}
	}
	
	//feed a specific lane
	public void feedLane(int laneNumber,String part_type,int number)
	{
		if(laneNumber<0||laneNumber>7){
			System.err.println("Feed Lane Function is down!");
		}
		else
			system.DoRunLane(laneNumber, part_type, number);
	}
	
	//take a part from a specific nest
	public GPart takePart(int laneNumber)
	{
		GPart temp=system.DoTakePart(laneNumber);
		
		if(laneNumber<0||laneNumber>7)
		{
			System.err.println("Take part Function is down!");
			System.exit(0);
		}
		return temp;
	}
	
	public GLaneSystem getSystem()
	{
		return system;
	}
	
	public boolean getDiverterChoice(int lane_unit){
		if(lane_unit == 1){return topLane1;}
		else if(lane_unit == 2){return topLane2;}
		else if(lane_unit == 3){return topLane3;}
		else{return topLane4;}
	}
}
