package gui;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GGantrySystem extends JPanel implements ActionListener{

	GGantryRobot gantryRobot;
	ArrayList <GFeeder> feeders;
	GGantryConveyorIn conveyorIn;
	GGantryConveyorOut conveyorOut;
	int lastFeederAt;
	Timer timer;
	private int offset_X;
	private int offset_Y;
	
	////////// FACTORY VARIABLES //////////
	ArrayList<GKit> kitList;
	ArrayList<GPart> partList;
	GKit chosenKit;	//did not initialize it yet
	
	public boolean doPickUpNewBinDone;
	public boolean doDeliverBinToFeederDone;
	
	public boolean doDropBinDone;
	public boolean doPickUpPurgedBinDone;
	public boolean doDeliverBinToRefillDone;
	
	public boolean robotReadyToPickUp;
	


	
	public GGantrySystem(int offset_X, int offset_Y)
	{
		this.offset_X=offset_X;
		this.offset_Y=offset_Y;
		//gantryRobot= new GGantryRobot(100,800-64);//64 is the gantry robot's height
		gantryRobot= new GGantryRobot(100+offset_X,800-64*2+offset_Y);//64 is the gantry robot's height
		feeders = new ArrayList<GFeeder>();
		conveyorIn = new GGantryConveyorIn(offset_X+175,offset_Y+0);
		conveyorOut = new GGantryConveyorOut(offset_X+175,offset_Y+800-120);//80 is the conveyorOut's height
		
		robotReadyToPickUp = false;
		doPickUpNewBinDone = false;
		doDeliverBinToFeederDone = false;
		doDropBinDone = false;
		doPickUpPurgedBinDone = false;
		doDeliverBinToRefillDone = false;
		
		for(int i=0;i<4;i++)
		{
			feeders.add(new GFeeder(offset_X+0,offset_Y+70+i*170));
		}
		timer = new Timer(2,this);
		timer.start();
		setSize(275,800);
		kitList = new ArrayList<GKit>();
		partList = new ArrayList<GPart>();
		
	}
	
	public void DoPickUpNewBin(String partType)//move to conveyor, conveyor ready , pick up
	{
		System.out.println("test:Do Pick Up New Bin");
		if(gantryRobot.getStep()==-1)
		{
			conveyorIn.addBin(partType);
			moveToConveyorIn();
			conveyorIn.DoMove(conveyorIn.myGBin);
//			System.out.println("Working..");
			gantryRobot.setStep(1);
		}
		else
		{
			System.out.println("GGantryRobot is busy!");
		}
		
	}
	
	public void DoDeliverBinToFeeder(int feederNumber)//(String partName) might be useless.the image name of the part need to be partName.png//The robot moves from the conveyor to the feeder with a bin full of parts.
	{
		System.out.println("test:Do Deliver Bin To Feeder");
		if(gantryRobot.getStep()==-1)
		{
			moveToFeeder(feederNumber);
			gantryRobot.setStep(2);
		}
		else
		{
			System.out.println("GGantryRobot is busy!");
		}
		
		lastFeederAt=feederNumber;
	}
	
	
	
	public void DoDropBin()//This will be used when the robot drops the full bin off at the feeder and when it drops the empty bin at the conveyor
	{
		System.out.println("test:Do Drop Bin");
		if(gantryRobot.getStep()==-1)
		{			
			GPartBin temp= gantryRobot.dropDown();	
			feeders.get(lastFeederAt).receiveBin(temp);
			doDropBinDone = true;
//			System.out.println("PROBLEMS!!!!!!!!");
			//TODO: Heidi needs to send the message to the server saying the DoDropBin is done
			//then send the temp to the server
			//the server should send temp to the panel contains the nest
		}
		
	}
	

	public void DoPickUpPurgedBin(int feederNumber)//move to feeder
	{
		System.out.println("test:Do Pick Up Pureged Bin");
		if(gantryRobot.getStep()==-1)
		{
			System.out.println(feederNumber);
			moveToFeeder(feederNumber);
			gantryRobot.setStep(3);
		}
	}
	
	public void DoDeliverBinToRefill() //move to conveyorOut
	{
		System.out.println("test:Do Deliver Bin To Refill");
		if(gantryRobot.getStep()==-1)
		{
			moveToConveyorOut();
			gantryRobot.setStep(4);
		}
	}
	public void moveToConveyorIn()
	{	
		gantryRobot.moveToBottomOf(conveyorIn.myGBin);
		
	}
	
	public void moveToConveyorOut()
	{	
		gantryRobot.moveToLeftOf(conveyorOut);
		
	}
	public void moveToFeeder(int feederNumber)
	{
		gantryRobot.moveStraight(feeders.get(feederNumber).getX()+feeders.get(feederNumber).getIconWidth(),feeders.get(feederNumber).getY()+(feeders.get(feederNumber).getIconHeight()-gantryRobot.getIconHeight()));
	}
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		conveyorIn.paintObject(g, offset_X,offset_Y);
		conveyorOut.paintObject(g, offset_X,offset_Y);
		gantryRobot.paintObject(g, offset_X,offset_Y);
		for(int i = 0 ; i < feeders.size();i++)
			feeders.get(i).paintFeeder(g, offset_X,offset_Y);
			
		
	}
	public void actionPerformed(ActionEvent ae)	
	{
		if (conveyorIn.conveyorMovementIn)
		{
			conveyorIn.moveBin();
		}
		if (conveyorOut.conveyorMovementOut)
		{
			conveyorOut.moveBin();
			if(!conveyorOut.conveyorMovementOut)
				{
					doDeliverBinToRefillDone=true;			
				}
		}
		
		if(!gantryRobot.ifArrive()&&gantryRobot.getStep()==1)
		{		
			moveToConveyorIn();
		}
		else if(gantryRobot.ifArrive()&&gantryRobot.getStep()==1)
		{

			gantryRobot.pickUp(conveyorIn.giveBin());
			doPickUpNewBinDone = true;
			
			System.out.println("Picked up bin.");
			gantryRobot.setStep(-1);
			
			//TODO:Heidi needs to send message to server indicating DoPickUpNewBin is done
		}
		else if(!gantryRobot.ifArrive()&&gantryRobot.getStep()==2)
		{
			moveToFeeder(lastFeederAt);
		}
		else if(gantryRobot.ifArrive()&&gantryRobot.getStep()==2)
		{
			//feeders.get(lastFeederAt).receiveBin(gantryRobot.dropDown());
			//TODO:Heidi needs to send message to server saying DoDeliverBinToFeeder is done
			doDeliverBinToFeederDone = true;
			gantryRobot.setStep(-1);
			System.out.println("Delivered bin");
		}
		else if(!gantryRobot.ifArrive()&&gantryRobot.getStep()==3)
		{
			moveToFeeder(lastFeederAt);
			gantryRobot.setStep(3);
		}
		else if(gantryRobot.ifArrive()&&gantryRobot.getStep()==3)
		{
			//TODO:Heidi needs to send message to server saying the robot already arrives at the feeder 
			//and the server should send message to the certain feeder and the feeder sends the bin here from server
			//When the robot catch the bin in the run method, send message to server saying DoPickUpPurgedBin is finished,then set step to -1
			System.out.println("Step 3 called");
			robotReadyToPickUp=true;
			gantryRobot.pickUp(feeders.get(lastFeederAt).dropBin());
			doPickUpPurgedBinDone = true;
			//System.out.println("Step 3 called");
			gantryRobot.setStep(-1);
//			System.exit(0);
			
		}
		else if(!gantryRobot.ifArrive()&&gantryRobot.getStep()==4)
		{
			moveToConveyorOut();
		}
		else if(gantryRobot.ifArrive()&&gantryRobot.getStep()==4)
		{

			//conveyorOut.receiveBin((gantryRobot.dropDown()));	
			//TODO:need Kevin to write the above function: receiveBin()
			//When the conveyor moves the bin out of screen send image to server saying DoDeliverBinToRefill is finished
			conveyorOut.addBin(gantryRobot.dropDown());
			conveyorOut.DoMove();
			gantryRobot.setStep(-1);
		}
		repaint();
		
			
	}
	

}