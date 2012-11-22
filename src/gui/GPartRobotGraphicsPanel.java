package gui;
import interfaces.IPartRobotController;
import interfaces.IVisionController;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import agents.PartRobotAgent;
import agents.VisionAgent;


public class GPartRobotGraphicsPanel extends JPanel implements ActionListener{

	private int offset_X;
	private int offset_Y;
	Timer timer;
	
	public GPartRobot partRobot;
	public ArrayList <GNest> nest;
	
	GKit kit;
	int lastNestAt;
	int lastKitAt;
	int step;
	
	IVisionController visionController;
	IPartRobotController partRobotController;
	
	public GPartRobotGraphicsPanel(int offset_X, int offset_Y){
		this.offset_X=offset_X;
		this.offset_Y=offset_Y;
		partRobot = new GPartRobot();
		nest = new ArrayList<GNest>();
		kit = new GKit();
		lastNestAt = -1;
		lastKitAt = -1;
		initializeNests();
		timer = new Timer(15,this);
		timer.start();
		step=-1;


		}
	
	public void initializeNests()	
	{
		
		for(int i = 0; i < 8; i++ )
		{
			String imageAddress= "src/resources/enemy"+(i+1)+".png";
			nest.add(new GNest(300,i*80,imageAddress,i,"enemy"+(i+1)+""));
			
		}
		
	}
	
	public void pickUpFromNest(int nestNumber)	//the robot picks up a part from the nest based on the nest number
	{
		
		partRobot.pickUp(nest.get(nestNumber).pushParts());
		
	}
	
	public void dropDownPartsToKit()		//the robot drops down all the parts in its arms to the kit
	{
		
		kit.addAPart(partRobot.dropDown());

		
	}
	
	public void moveToNest(int nestNumber)	//the robot moves to the nest based on the nestNumber
	{
		partRobot.moveToLeftOf(nest.get(nestNumber));
		lastNestAt=nestNumber;
		step=1;
	}
	
	public void moveToKit()					//the robot moves to the kit
	{
		partRobot.moveToRightOf(kit);
		step=2;
	}
	
//	public void doMove(GNest nest)
//	{
//		partRobot.moveToLeftOf(nest);
//
//	}
//	
//	public void doMove(GKit kit)
//	{
//		partRobot.moveToRightOf(kit);
//
//	}
//	
//	public void doPickUp()
//	{
//		partRobot.pickUp(lastNestAt.pushParts());
//	}
//	
//	public void doPutInKit()
//	{
//		lastKitAt.addAPart(partRobot.dropDown());
//	}
	
	public void doShoot(int i)  //shoot a picture on nest i
	{
		nest.get(i).takePicture();
		System.out.println("Panel taking a picture of Nest: "+nest.get(i));
	}
	
	public void actionPerformed(ActionEvent arg0) {
		
	    if(!partRobot.ifArrive()&&step==1)
	    {
	    	moveToNest(lastNestAt);
	    
	    }
	    else if(partRobot.ifArrive()&&step==1)
	    {

	    	partRobotController.animDone();
	    	
	    	step=-1;
	    }
	    else if(!partRobot.ifArrive()&&step==2)
	    {
	    	moveToKit();
	    }
	    else if(partRobot.ifArrive()&&step==2)
	    {
	    	partRobotController.animDone();
	    	step=-1;
	    }
	
		repaint();
	}
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		kit.paintObject(g,offset_X,offset_Y);
		
		for (int i = 0;i<8;i++)
		{
			nest.get(i).paintObject(g,offset_X,offset_Y);
			if(nest.get(i).ifCameraFinished())
				visionController.animDone();
		}
    
		partRobot.paintObject(g,offset_X,offset_Y);
	}

	
	public void setPartRobotController(IPartRobotController partrobot){
		this.partRobotController=partrobot;
	}
	
	public void setVisionController(IVisionController vision){
		this.visionController=vision;
	}
	
	//TEST THE TAKE PICTURE FUNCTION FOR NESTS
	public void picture(int num) {
		nest.get(num).takePicture();
	}
	
}
