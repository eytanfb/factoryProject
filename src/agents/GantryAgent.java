package agents;

import interfaces.Feeder;
import interfaces.Gantry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import controllers.GantryController;

import NonAgent.Bin;
import NonAgent.Part;
import agent.Agent;

public class GantryAgent extends Agent implements Gantry
{

	/******* DATA ********/
	public enum feederState
	{
		activeNoAction, notActiveNoAction, needFillPart, needClearPurged
	}
	public String name;
	public class myFeeder
	{
		public Feeder feeder; // can be an agent or a mock
		public feederState state; // current state of the feeder
		public int position; // the feeder number
		public Part currentPart;
		public Part requestedPart;

		public myFeeder(Feeder f, int pos)
		{
			feeder = f;
			currentPart = null;
			requestedPart = null;
			position = pos;
			state = feederState.notActiveNoAction;
		}
	}

	public List<myFeeder> feeders = new ArrayList<myFeeder>();
	public GantryController gui;
	private Semaphore s = new Semaphore(0);

	/******* CONSTRUCTOR ********/

	public GantryAgent(String Name){//GantryGui gui) { 
		this.name = Name;
	}

	/******* MESSAGES ********/

	/**Feeder sends this message to request parts
	 * @param feeder1 feeder sending the message
	 * @param p part needed my the feeder
	 */
	public void msgNeedPart(Feeder feeder1, Part p) 
	{ 
		
		for(myFeeder f: feeders)
		{
			if(f.feeder == feeder1)
			{
				f.requestedPart = p;
				System.out.println("Gantry: Received message need part from feeder " + f.position);

				if(f.state == feederState.notActiveNoAction)
					f.state = feederState.needFillPart;
				else if(f.state == feederState.activeNoAction)
					f.state = feederState.needClearPurged;
			}
		}
		stateChanged();	
	}

	public void msgDoneWithAnim()
	{// Gui
		s.release();
		stateChanged();
	}

	/******* SCHEDULER ********/
	public boolean pickAndExecuteAnAction()
	{
		for (myFeeder f : feeders)
		{
			if (f.state == feederState.needFillPart){
				try {
				fillFeeder(f);
				} catch (InterruptedException e) {e.printStackTrace();}
				return true;
			}
			if (f.state == feederState.needClearPurged){
				try {
				clearPurgedFeeder(f);
				} catch (InterruptedException e) {e.printStackTrace();}
				return true;
			}
		}
		return false;
	}

	/******* ACTIONS 
	 * @throws InterruptedException ********/
	private void fillFeeder(myFeeder f) throws InterruptedException
	{
		System.out.println("Filling feeder " + f.position + " with " + f.requestedPart);
		f.state = feederState.activeNoAction;
		gui.DoPickUpNewBin(f.requestedPart.partType);
		s.acquire();
		gui.DoDeliverBinToFeeder(f.position);
		s.acquire();
		gui.DoDropBin();
		gui.DoPlaceBin(f.position);
		s.acquire();
		f.currentPart = f.requestedPart;
		f.requestedPart = null;
		f.feeder.msgHereIsBin(new Bin(f.currentPart.partType));
		stateChanged();
	}

	private void clearPurgedFeeder(myFeeder f) throws InterruptedException
	{
		System.out.println("Gantry: Removing bin from feeder " + f.position);
		f.state = feederState.activeNoAction;
		gui.DoPickUpPurgedBin(f.position);
		s.acquire();
		gui.DoReleaseBinToGantry(f.position);
		gui.DoDeliverBinToRefill();
		s.acquire();
//		gui.DoDropBin();
//		s.acquire();
		f.currentPart = null;
		f.state = feederState.needFillPart;
		stateChanged();
		System.out.println("Gantry: feeder1 state - " + f.state);
	}
	
	public void setFeeders(List<Feeder> feederList){
		for (int i = 0; i < feederList.size(); i++) { 
			feeders.add(new myFeeder(feederList.get(i), i)); 	
		}
	}

	public void setController(GantryController gantryController)
	{
		gui = gantryController;
	}
}
