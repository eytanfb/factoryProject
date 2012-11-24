package controllers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
	private Socket s;
	private ObjectOutputStream oos;
	
	public ConveyorSystemController()
	{}
	
	public ConveyorSystemController(ConveyorSystem conveyorSystem)
	{
		
		this.conveyorSystem = conveyorSystem;
	}
	
	public void connect()
	{
		try
		{
			s = new Socket("localhost", 63432);
			oos = new ObjectOutputStream(s.getOutputStream());
			System.out.println("ConveyorSystemController Client Ready");
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
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
		//server.doMove(kit, conveyor);
		try
		{
			oos.writeObject("Conveyor_MoveKit");
			oos.reset();
			oos.writeObject(conveyor);
			oos.reset();
			oos.writeObject(kit);
			oos.reset();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
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
