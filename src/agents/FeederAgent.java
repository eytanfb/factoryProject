package agents;

import interfaces.*;
import NonAgent.*;
import java.util.*;

import agents.LaneAgent.LaneEvent;

public class FeederAgent extends agent.Agent implements Feeder
{

	public Bin bin;
	public Lane top, bottom;
	public Gantry gantry;
	public List<LaneObject> lanes = new ArrayList<LaneObject>();

	public enum LaneState
	{
		Idle, PartsRequested, WaitingForBin, BinArrived, Feeding
	};

	public class LaneObject
	{

		public Part partType;
		public LaneState laneState = LaneState.Idle;
		public Lane lane;

		public LaneObject(Part parttype, LaneState laneState, Lane lane)
		{
			this.partType = parttype;
			this.laneState = laneState;
			this.lane = lane;
		}

		public LaneObject(Lane lane)
		{
			this.lane = lane;
			this.laneState = LaneState.Idle;
		}
	}

	public FeederAgent(Gantry g)
	{
		this.gantry = g;
		this.bin = null;

	}

	/********************
	 * //////Messages///////
	 ********************/

	public void msgHereIsBin(Bin bin)
	{
		System.out.println("Feeder: Received bin from gantry.");
		this.bin = bin;
		for (int i = 0; i < lanes.size(); i++)
		{
			if (lanes.get(i).laneState == LaneState.WaitingForBin)
			{
				lanes.get(i).laneState = LaneState.BinArrived;
			}
		}
		stateChanged();
	}

	public void msgNeedParts(Lane lane, Part part)
	{
		System.out.println("Feeder: Received parts request from lane.");
		for (int i = 0; i < lanes.size(); i++)
		{
			if (lanes.get(i).lane == lane)
			{
				lanes.get(i).laneState = LaneState.PartsRequested;
				lanes.get(i).partType = part;
			}
		}
		stateChanged();
	}

	public void msgDoneFeeding(Lane lane)
	{
		System.out.println("Feeder: Was notified that the lane has completed feeding.");
		for (int i = 0; i < lanes.size(); i++)
		{
			if (lanes.get(i).lane == lane)
			{
				lanes.get(i).laneState = LaneState.Idle;
			}
		}
		stateChanged();
	}

	/*******************
	 * //////Scheduler/////
	 *******************/

	public boolean pickAndExecuteAnAction()
	{

		for (int i = 0; i < lanes.size(); i++)
		{
			int q = 0;
			if (i == 0)
			{
				q = 1;
			}
			if (i == 1)
			{
				q = 0;
			}

			// Only one lane gets to request from the Gantry
			if (lanes.get(i).laneState == LaneState.PartsRequested &&
					(lanes.get(q).laneState == LaneState.Idle || lanes.get(q).laneState == LaneState.PartsRequested))
			{

				RequestParts(lanes.get(i));
				return true;
			}

		}

		for (int i = 0; i < lanes.size(); i++)
		{
			if (lanes.get(i).laneState == LaneState.BinArrived)
			{
				FeedParts(lanes.get(i));
				lanes.get(i).laneState = LaneState.Feeding;
				return true;
			}
		}

		return false;
	}

	/******************
	 * //////Actions//////
	 ******************/

	private void RequestParts(LaneObject lane)
	{

		System.out.println("Feeder: Requested parts from gantry.");

		gantry.msgNeedPart(this, lane.partType);
		lane.laneState = LaneState.WaitingForBin;

		// HACKY TEMPORARY THING FOR V0:
		// this.msgHereIsBin(new Bin());

	}

	private void FeedParts(LaneObject lane)
	{
		System.out.println("Feeder: Feeding parts to lane.");

		List<Part> partlist = new ArrayList<Part>();
		for (int i = 0; i < 10; i++)
		{
			Part newpart = new Part();
			newpart.partType = lane.partType.partType;
//			print("newpart type " + newpart.partType);
			partlist.add(newpart);
		}

		if (lane.lane == top)
		{
			top.msgHereAreParts(partlist);
		}
		else if (lane.lane == bottom)
		{
			bottom.msgHereAreParts(partlist);
		}

	}

	/******************
	 * //////Setters//////
	 ******************/

	public void SetTopLane(Lane lane)
	{
		this.top = lane;
		this.lanes.add(new LaneObject(lane));
	}

	public void SetBottomLane(Lane lane)
	{
		this.bottom = lane;
		this.lanes.add(new LaneObject(lane));
	}

}
