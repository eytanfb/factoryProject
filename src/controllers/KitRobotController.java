package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import NonAgent.Kit;
import gui.GKitRobot;
import gui.GKitManager.UpdateChecker;
import interfaces.IKitRobotController;
import interfaces.KitRobot;
import interfaces.Kittable;
import interfaces.Server;

public class KitRobotController implements IKitRobotController
{
	private KitRobot kitRobot;
	private Server server;
	private Socket s;
	private ObjectOutputStream oos;

	public KitRobotController(KitRobot kitRobot, GKitRobot gui)
	{
		this.kitRobot = kitRobot;
	}

	public void connect()
	{
		try
		{
			s = new Socket("localhost", 63432);
			oos = new ObjectOutputStream(s.getOutputStream());
			System.out.println("Client Ready");
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setServer(Server server)
	{
		this.server = server;
	}

	public void doAnim(Kittable origin, Kittable destination, Kit kit)
	{
		try
		{
			oos.writeObject("KitRobot_PutKit");
			oos.reset();
			oos.writeObject(origin);
			oos.reset();
			oos.writeObject(destination);
			oos.reset();
			oos.writeObject(kit);
			oos.reset();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//server.doPutKit(origin, destination, kit);
	}

	public void animDone()
	{
		kitRobot.msgAnimDone();
	}

}
