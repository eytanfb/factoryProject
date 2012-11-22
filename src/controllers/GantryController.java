package controllers;

import interfaces.Gantry;
import interfaces.IGantryController;
import interfaces.Server;

public class GantryController implements IGantryController
{
	private Gantry gantry;
	private Server server;

	public GantryController(Gantry gantry)
	{
		this.gantry = gantry;
	}

	public void setServer(Server server)
	{
		System.out.println("server set " + server );
		this.server = server;
	}

	public void DoPickUpNewBin(String partType)
	{
		System.out.println("server " + server);
		this.server.doPickUpNewBin(partType);
	}

	public void DoDeliverBinToFeeder(int feederNumber)
	{
		server.doDeliverBinToFeeder(feederNumber);
	}

	public void DoDropBin()
	{
		server.doDropBin();
	}

	public void DoPickUpPurgedBin(int feederNumber)
	{
		server.doPickUpPurgedBin(feederNumber);
	}

	public void DoReleaseBinToGantry(int feederNumber)
	{
		server.doReleaseBinToGantry(feederNumber);
	}

	public void DoDeliverBinToRefill()
	{
		server.doDeliverBinToRefill();
	}

	public void animDone()
	{
		gantry.msgDoneWithAnim();
	}

	public void DoPlaceBin(int feederNumber) {
		server.doPlaceBin(feederNumber);
	}

}
