package mocks;

import java.util.Timer;
import java.util.TimerTask;

import NonAgent.Kit;
import gui.GKitRobot;
import interfaces.KitRobot;
import interfaces.Kittable;

public class MockGuiKitRobot extends GKitRobot
{
	public EventLog log = new EventLog();
	public KitRobot kitRobot;
	
	
	public MockGuiKitRobot(KitRobot kitRobot)
	{
		this.kitRobot = kitRobot;
	}
	
	public void DoPutKit(Kittable k1, Kittable k2, Kit kit)
	{
		log.add(new LoggedEvent("DoPutKit received with " + kit + k1 + k2));
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				kitRobot.msgAnimDone();
			}
		}, 100);
	}
}
