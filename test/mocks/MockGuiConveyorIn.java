package mocks;

import java.util.Timer;
import java.util.TimerTask;

import interfaces.ConveyorSystem;
import NonAgent.Kit;
import gui.GConveyorIn;
import gui.GKit;
import gui.GKittable;

public class MockGuiConveyorIn extends GConveyorIn
{
	public EventLog log = new EventLog();
	ConveyorSystem conveyorSystem;
	
	public MockGuiConveyorIn(ConveyorSystem conveyorSystem)
	{
		this.conveyorSystem = conveyorSystem;
	}
	
	public void DoMove(Kit kit)
	{
		log.add(new LoggedEvent("DoMove received with " + kit));
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				conveyorSystem.msgAnimDone();
			}
		}, 100);
	}
}
