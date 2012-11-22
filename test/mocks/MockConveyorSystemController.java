package mocks;

import java.util.Timer;
import java.util.TimerTask;

import agents.ConveyorSystemAgent;

import NonAgent.Conveyor;
import NonAgent.Kit;
import interfaces.ConveyorSystem;
import interfaces.IConveyorSystemController;

public class MockConveyorSystemController implements IConveyorSystemController
{
	public EventLog log = new EventLog();
	
	private ConveyorSystem conveyorSystem;
	
	public MockConveyorSystemController(ConveyorSystem conveyorSystem)
	{
		this.conveyorSystem = conveyorSystem;
	}

	public void doAnim(Conveyor conveyor, Kit kit)
	{
		log.add(new LoggedEvent("doAnim received with conveyor " + conveyor + " and kit " + kit));
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				animDone();
			}
		}, 100);
	}

	public void animDone()
	{
		log.add(new LoggedEvent("animDone received"));
		conveyorSystem.msgAnimDone();
	}
	
	public void setConveyorSystem(ConveyorSystem conveyorSystem)
	{
		this.conveyorSystem = conveyorSystem;
	}
}
