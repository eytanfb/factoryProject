package agents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
//import Part3.*;
import java.util.concurrent.Semaphore;

import gui.GUIServer;
import interfaces.*;
import NonAgent.*;
import javax.swing.*;
import javax.swing.Timer;

public class LaneAgent extends agent.Agent implements Lane
{

//	Part3_GUI gui;
	Feeder feeder;
	Nest nest;
	Part partType;
	Part part;
	List<Part> parts = new ArrayList<Part>();
	Semaphore partSem = new Semaphore(0); // Used in sending parts to the Nest
	LaneState state = LaneState.Idle;
	LaneEvent event = LaneEvent.None;
	Server server;
	public int laneID;
	

	int FeedingTimerDelay = 11; // in seconds

	Timer t = new Timer(FeedingTimerDelay * 1000, new ActionListener()
	{

		public void actionPerformed(ActionEvent arg0)
		{
			FeedingTimedOut();
		}
	});

	enum LaneState
	{
		Idle, WaitingForParts, Feeding
	};

	enum LaneEvent
	{
		None, PartsRequested, PartsReceived
	};

	public LaneAgent()
	{
//		gui = Part3_GUI.GetGUI();
	}

	/********************
	 * //////Messages///////
	 ********************/

	public void msgHereAreParts(List<Part> parts)
	{
		System.out.println("Lane: Feeding parts from the feeder.");
		this.parts = parts;
		event = LaneEvent.PartsReceived;
		stateChanged();
	}

	public void msgNeedParts(Part part)
	{
		System.out.println("Lane: Received parts request from Nest.");
		this.part = part;
		event = LaneEvent.PartsRequested;
		stateChanged();
	}

	public void msgPartPutInNest()
	{
		GivePart();
	}

	/*******************
	 * //////Scheduler/////
	 *******************/

	@Override
	protected boolean pickAndExecuteAnAction()
	{

		if (state == LaneState.Idle && event == LaneEvent.PartsRequested)
		{
			AskFeederForParts();
			return true;
		}

		if (state == LaneState.WaitingForParts && event == LaneEvent.PartsReceived)
		{
			state = LaneState.Feeding;
			FeedToNest();
			return true;
		}

		return false;
	}

	/******************
	 * //////Actions//////
	 ******************/

	private void AskFeederForParts()
	{
		System.out.println("Lane: Asked feeder for parts.");
		feeder.msgNeedParts(this, part);
		state = LaneState.WaitingForParts;
	}

	private void FeedToNest()
	{
		
		//REAL VERSION:
		server.doRunLane(new Integer(laneID), parts.get(0), parts.size());
		
		//TEMP FOR TESTING:
		//((NestAgent) nest).mockgui.runStuff(parts.size());
		
		
//		gui.DoRunLane(this, part, parts.size());

		/*
		 * for(int i=0;i<parts.size();i++) { try { partSem.acquire(); } catch
		 * (InterruptedException e) { e.printStackTrace(); }
		 * 
		 * nest.msgPutPart(parts.get(0)); parts.remove(0); }
		 * 
		 * feeder.msgDoneFeeding(this); state = LaneState.Idle; event =
		 * LaneEvent.None;
		 */

		// make timer that calls FeedingTimedOut

		t.start();

	}

	private void GivePart()
	{
		nest.msgPutPart(parts.get(0));
		parts.remove(0);
	}

	private void FeedingTimedOut()
	{

		feeder.msgDoneFeeding(this);
		state = LaneState.Idle;
		event = LaneEvent.None;

		t.restart();
		t.stop();

	}

	/******************
	 * //////Setters//////
	 ******************/

	public void SetFeeder(Feeder feeder)
	{
		this.feeder = feeder;
	}

	public void SetNest(Nest nest)
	{
		this.nest = nest;
	}

	public void SetServer(Server server)
	{
		this.server = server;
	}

}
