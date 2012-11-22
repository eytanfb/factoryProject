package controllers;

import NonAgent.Kit;
import gui.GKitRobot;
import interfaces.IKitRobotController;
import interfaces.KitRobot;
import interfaces.Kittable;
import interfaces.Server;

public class KitRobotController implements IKitRobotController
{
	private KitRobot kitRobot;
	private Server server;

	public KitRobotController(KitRobot kitRobot, GKitRobot gui)
	{
		this.kitRobot = kitRobot;
	}
	
	public void setServer(Server server)
	{
		this.server = server;
	}

	public void doAnim(Kittable origin, Kittable destination, Kit kit)
	{
		server.doPutKit(origin, destination, kit);
	}

	public void animDone()
	{
		kitRobot.msgAnimDone();
	}

}
