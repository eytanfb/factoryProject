package agents;

import interfaces.*;
import mocks.*;
import NonAgent.*;
import java.util.*;

public class NestAgent extends agent.Agent implements Nest
{

	List<Part> parts = new ArrayList<Part>();
	Lane lane;
	Part partType;
	NestEvent event = NestEvent.None;
	NestState state = NestState.Empty;
	Vision vision;
	public int nestID;

	// TEMPORARY//
	public MockNestGUI mockgui;

	// ///////////

	enum NestEvent
	{
		None, PartsRequested
	};

	enum NestState
	{
		Empty, Waiting, Stocked
	};

	public NestAgent()
	{

	}

	/********************
	 * //////Messages///////
	 ********************/

	public void msgPutPart(Part part)
	{
		System.out.println("Nest: Received a part. " + part.partType);
		parts.add(part);
		state = NestState.Stocked;

		vision.msgNestHas(this, part, nestID);

		stateChanged();
	}

	public void msgNeedParts(Part part)
	{
		System.out.println("Nest: Received parts request.");
		this.partType = part;
		event = NestEvent.PartsRequested;
		stateChanged();
	}

	public void msgPartRemoved()
	{
		parts.remove(0);
		if (parts.size() == 0)
		{
			state = NestState.Empty;
		}

		stateChanged();
	}

	/*******************
	 * //////Scheduler/////
	 *******************/

	@Override
	protected boolean pickAndExecuteAnAction()
	{

		// System.out.println("Nest scheduler ran");

		if (state == NestState.Empty && event == NestEvent.PartsRequested)
		{
			// System.out.println("Action selected");
			AskLaneForPart();
			return true;
		}

		return false;
	}

	/******************
	 * //////Actions//////
	 ******************/

	private void AskLaneForPart()
	{
		System.out.println("Nest: Sent parts request to Lane.");
		lane.msgNeedParts(partType);
		state = NestState.Waiting;
	}

	/******************
	 * //////Setters//////
	 ******************/

	public void SetLane(Lane lane)
	{
		this.lane = lane;
	}

	public void TestInit()
	{
		for (int i = 0; i < 8; i++)
		{
			this.parts.add(new Part());
		}
	}

	public void SetVision(Vision vision)
	{
		this.vision = vision;
	}

}
