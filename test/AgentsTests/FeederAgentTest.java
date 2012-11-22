package AgentsTests;

import junit.framework.TestCase;
import mocks.MockGantry;
import mocks.MockLane;
import NonAgent.Bin;
import NonAgent.Part;
import agents.FeederAgent;
import agents.FeederAgent.LaneState;


//Written by Stephanie Aceves5
public class FeederAgentTest extends TestCase {
	
	FeederAgent feeder;
	MockGantry gantry;
	MockLane lane1;
	MockLane lane2;
	Part p1;
	Bin bin;
	
	public void setUp(){
		gantry = new MockGantry();
		feeder = new FeederAgent(gantry);
		p1 = new Part("p1");
		bin = new Bin(p1.partType);
		lane1 = new MockLane();
		lane2 = new MockLane();
		
	}
	
	public void testSetTopLane(){
		assertTrue(feeder.lanes.isEmpty());
		
		feeder.SetTopLane(lane1);
		
		assertTrue(feeder.lanes.size() == 1);
		assertTrue(feeder.lanes.get(0).lane == lane1);
		assertTrue(feeder.top == lane1);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Idle);
	}

	public void testSetBottomLane(){
		assertTrue(feeder.lanes.isEmpty());
		
		feeder.SetBottomLane(lane2);
		
		assertTrue(feeder.lanes.size() == 1);
		assertTrue(feeder.lanes.get(0).lane == lane2);
		assertTrue(feeder.bottom == lane2);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Idle);

	}

	
	public void testMsgHereIsBinNoLaneWaiting() {
		feeder.SetTopLane(lane1);
		feeder.SetBottomLane(lane2);
		assertTrue(feeder.bin == null);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Idle);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);

		feeder.msgHereIsBin(bin);
		
		//handle a bin when neither of the lanes are ready
		assertTrue(feeder.bin == bin);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Idle);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);
		
		
		feeder.lanes.get(0).laneState = LaneState.WaitingForBin;
		feeder.msgHereIsBin(bin);
		
		//handle a bin when one lane is prepared
		assertTrue(feeder.bin == bin);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.BinArrived);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);

		feeder.pickAndExecuteAnAction();
		
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Feeding);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);

		assertTrue(lane1.log.size() == 1);
		assertTrue(lane2.log.size() == 0);
		assertTrue(lane1.log.containsString("Received msgHereAreParts"));	
		
		
		//There's no need to test a situation where both lanes are waiting for a bin
		//because the message reception logic handles this
		
		
		/*feeder.lanes.get(0).laneState = LaneState.WaitingForBin;
		feeder.lanes.get(1).laneState = LaneState.WaitingForBin;

		feeder.msgHereIsBin(bin);
		
		//handle a bin when one lane is prepared
		assertTrue(feeder.bin == bin);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.BinArrived);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.WaitingForBin);

		feeder.pickAndExecuteAnAction();
		
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Feeding);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.WaitingForBin);

		assertTrue(lane1.log.size() == 2);
		assertTrue(lane2.log.size() == 0);
		assertTrue(lane1.log.containsString("Received msgHereAreParts"));*/	
	}
	
	public void testMsgNeedParts(){
		feeder.SetTopLane(lane1);
		feeder.SetBottomLane(lane2);
		assertTrue(feeder.bin == null);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Idle);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);

		feeder.msgNeedParts(lane1, p1);
		
		assertTrue(feeder.lanes.get(0).laneState == LaneState.PartsRequested);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);
		assertTrue(feeder.lanes.get(0).partType == p1);
		assertTrue(feeder.lanes.get(1).partType == null);

		feeder.pickAndExecuteAnAction();
		
		assertTrue(gantry.log.size() == 1);
		assertTrue(gantry.log.containsString("Received msgNeedParts of p1"));
		assertTrue(feeder.lanes.get(0).laneState == LaneState.WaitingForBin);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);		
		
	}
	
	public void testMsgDoneFeeding(){
		
		feeder.SetTopLane(lane1);
		feeder.SetBottomLane(lane2);
		
		feeder.lanes.get(0).laneState = LaneState.Feeding;
		
		assertTrue(feeder.bin == null);
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Feeding);
		assertTrue(feeder.lanes.get(1).laneState == LaneState.Idle);

		feeder.msgDoneFeeding(lane1);
		
		assertTrue(feeder.lanes.get(0).laneState == LaneState.Idle);

	}

}
