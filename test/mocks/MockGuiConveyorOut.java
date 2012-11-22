package mocks;

import interfaces.ConveyorSystem;

import java.util.Timer;
import java.util.TimerTask;

import NonAgent.Kit;
import gui.GConveyorOut;

public class MockGuiConveyorOut extends GConveyorOut
{
	public EventLog log = new EventLog();
	ConveyorSystem conveyorSystem;
	
	public MockGuiConveyorOut(ConveyorSystem conveyorSystem)
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
