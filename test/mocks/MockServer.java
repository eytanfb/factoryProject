package mocks;

import java.util.Timer;
import java.util.TimerTask;

import controllers.ConveyorSystemController;
import controllers.GantryController;
import controllers.KitRobotController;
import NonAgent.Conveyor;
import NonAgent.Kit;
import NonAgent.Part;
import interfaces.Kittable;
import interfaces.Server;

public class MockServer implements Server
{
	public EventLog log = new EventLog();

	private KitRobotController kitRobotController;
	private ConveyorSystemController conveyorSystemController;
	private GantryController gantryController;

	public void doMove(Kit kit, Conveyor conveyor)
	{
		log.add(new LoggedEvent("doMove received with " + kit + " and conveyor " + conveyor));
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				conveyorSystemController.animDone();
			}
		}, 1000);
	}

	public void doPutKit(Kittable origin, Kittable destination, Kit kit)
	{
		log.add(new LoggedEvent("doPutKit received with " + origin + " and " + destination +
				" and " + kit));
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				kitRobotController.animDone();
			}
		}, 1000);
	}

	public void doPickUpFromNest(int nestNumber)
	{
		// TODO Auto-generated method stub
	}

	public void doMoveToNest(int nestNumber)
	{
		// TODO Auto-generated method stub
	}

	public void doMoveToKit(Kit kit)
	{
		// TODO Auto-generated method stub

	}

	public void doDropPartsInKit()
	{
		// TODO Auto-generated method stub

	}

	public void setKitRobotController(KitRobotController kitRobotController)
	{
		this.kitRobotController = kitRobotController;
	}

	public void setConveyorSystemController(ConveyorSystemController conveyorSystemController)
	{
		this.conveyorSystemController = conveyorSystemController;
	}

	public void doShoot(int nestId)
	{
		// TODO Auto-generated method stub

	}

	public void doShootKit()
	{
		// TODO Auto-generated method stub

	}

	// GANTRY

	public void doPickUpNewBin(String partType)
	{
		log.add(new LoggedEvent("doPickUpNewBin received with " + partType));
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				gantryController.animDone();
			}
		}, 100);

	}

	public void doDeliverBinToFeeder(int feederNumber)
	{
		log.add(new LoggedEvent("doDeliverBinToFeeder received with " + feederNumber));
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				gantryController.animDone();
			}
		}, 100);
	}

	public void doDropBin()
	{
		log.add(new LoggedEvent("doDropBin received"));
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				gantryController.animDone();
			}
		}, 100);
	}

	public void doPickUpPurgedBin(int feederNumber)
	{
		log.add(new LoggedEvent("doPickUpPurgedBin received with " + feederNumber));
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				gantryController.animDone();
			}
		}, 100);
	}

	public void doDeliverBinToRefill()
	{
		log.add(new LoggedEvent("doDeliverBinToRefill received"));
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				gantryController.animDone();
			}
		}, 100);
	}

	public void setGantryController(GantryController gantryController)
	{
		this.gantryController = gantryController;
	}

	public void doRunLane(int laneid, Part part, int numparts)
	{
		// This is handled by my MockNestGUI
	}

	public void doPlaceBin(int feederNumber)
	{
		// TODO Auto-generated method stub

	}

	public void doReleaseBinToGantry(int feederNumber)
	{
		// TODO Auto-generated method stub

	}

}
