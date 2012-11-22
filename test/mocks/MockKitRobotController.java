package mocks;

import java.util.Timer;
import java.util.TimerTask;

import agents.KitRobotAgent;

import NonAgent.Kit;
import interfaces.IKitRobotController;
import interfaces.KitRobot;
import interfaces.Kittable;

public class MockKitRobotController implements IKitRobotController
{

	public EventLog log = new EventLog();
	
	private KitRobot kitRobot;
	
	public MockKitRobotController(KitRobot kitRobot)
	{
		this.kitRobot = kitRobot;
	}

	public void doAnim(Kittable k1, Kittable k2, Kit kit)
	{
		log.add(new LoggedEvent("doAnim received with kittable " + k1 + " and kittable " + k2 + " kit " + kit));
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				animDone();
			}
		}, 1000);
	}

	public void animDone()
	{
		log.add(new LoggedEvent("animDone received"));
		kitRobot.msgAnimDone();
	}

}
