package controllers;

import mocks.MockServer;
import interfaces.ConveyorSystem;
import interfaces.IConveyorSystemController;
import interfaces.Server;
import NonAgent.Conveyor;
import NonAgent.Kit;
import gui.GUIServer;

public class ConveyorSystemController implements IConveyorSystemController
{
	private ConveyorSystem conveyorSystem;
	private Server server;
	
	public ConveyorSystemController()
	{}
	
	public ConveyorSystemController(ConveyorSystem conveyorSystem)
	{
		this.conveyorSystem = conveyorSystem;
	}
	
	public ConveyorSystemController(Server server)
	{
		this.server = server;
	}

	public void setConveyorSystem(ConveyorSystem conveyorSystem)
	{
		this.conveyorSystem = conveyorSystem;
	}
	
	public void doAnim(Conveyor conveyor, Kit kit)
	{
		server.doMove(kit, conveyor);
	}

	public void animDone()
	{
		conveyorSystem.msgAnimDone();
	}

	public void setServer(Server server)
	{
		this.server = server;
	}

}
