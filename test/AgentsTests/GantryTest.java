package AgentsTests;

import interfaces.Feeder;
import NonAgent.Part;

import java.util.ArrayList;
import java.util.List;

import mocks.MockFeeder;
import agents.GantryAgent;
import agents.GantryAgent.feederState;
import junit.framework.TestCase;

public class GantryTest extends TestCase {
	public GantryAgent gantry;
	public MockFeeder feeder;
	public List <Feeder> feeders = new ArrayList <Feeder>();
	public Part p1 = new Part("p1");
	public Part p2 = new Part("p2");
	
	
	public void testActiveFeederRefill(){
		feeder = new MockFeeder("MockFeeder");
		feeders.add(feeder);
		gantry = new GantryAgent("TestGantry");
		gantry.setFeeders(feeders);
		
		//tests to make sure everything was constructed properly
		assertTrue(gantry.feeders.size() == 1);
		assertTrue(gantry.name == "TestGantry");
		assertTrue(gantry.feeders.get(0).feeder == feeder);
		assertTrue(gantry.feeders.get(0).state == feederState.notActiveNoAction);
		assertTrue(gantry.feeders.get(0).currentPart == null);
		assertTrue(gantry.feeders.get(0).requestedPart == null);

		
		//send the first message to the Gantry
		gantry.msgNeedPart(feeder, p1);
		//test the message handling	
		assertTrue(gantry.feeders.get(0).state == feederState.needFillPart);
		assertTrue(gantry.feeders.get(0).currentPart == null);
		assertTrue(gantry.feeders.get(0).requestedPart == p1);
		assertTrue(feeder.log.size() == 0);
		assertTrue(gantry.feeders.get(0).feeder != null);
		
		//call the scheduler
		gantry.pickAndExecuteAnAction();
		assertTrue(gantry.feeders.get(0).state == feederState.activeNoAction);
		assertTrue(gantry.feeders.get(0).currentPart == p1);
		assertTrue(gantry.feeders.get(0).requestedPart == null);
		assertTrue(feeder.log.size() == 1);
		assertTrue(feeder.log.containsString("Received msgHereIsBin of p1"));
		
		
	}
	
	public void testInactiveFeederRefill(){
		feeder = new MockFeeder("MockFeeder");
		feeders.add(feeder);
		gantry = new GantryAgent("TestGantry");
		gantry.setFeeders(feeders);
		
		//tests to make sure everything was constructed properly
		assertTrue(gantry.feeders.size() == 1);
		assertTrue(gantry.name == "TestGantry");
		assertTrue(gantry.feeders.get(0).feeder == feeder);
		assertTrue(gantry.feeders.get(0).state == feederState.notActiveNoAction);
		assertTrue(gantry.feeders.get(0).currentPart == null);
		assertTrue(gantry.feeders.get(0).requestedPart == null);

		
		//send the first message to the Gantry
		gantry.msgNeedPart(feeder, p1);
		//test the message handling	
		assertTrue(gantry.feeders.get(0).state == feederState.needFillPart);
		assertTrue(gantry.feeders.get(0).currentPart == null);
		assertTrue(gantry.feeders.get(0).requestedPart == p1);
		assertTrue(feeder.log.size() == 0);
		assertTrue(gantry.feeders.get(0).feeder != null);
		
		//call the scheduler
		gantry.pickAndExecuteAnAction();
		assertTrue(gantry.feeders.get(0).state == feederState.activeNoAction);
		assertTrue(gantry.feeders.get(0).currentPart == p1);
		assertTrue(gantry.feeders.get(0).requestedPart == null);
		assertTrue(feeder.log.size() == 1);
		assertTrue(feeder.log.containsString("Received msgHereIsBin of p1"));
		
		//send the second message to the Gantry
		gantry.msgNeedPart(feeder, p2);
		//test the message handling
		assertTrue(gantry.feeders.get(0).state == feederState.needClearPurged);
		assertTrue(gantry.feeders.get(0).currentPart == p1);
		assertTrue(gantry.feeders.get(0).requestedPart == p2);
		assertTrue(feeder.log.size() == 1);
		assertTrue(gantry.feeders.get(0).feeder != null);
		
		//call the scheduler
		gantry.pickAndExecuteAnAction();
		assertTrue(gantry.feeders.get(0).state == feederState.needFillPart);
		assertTrue(gantry.feeders.get(0).currentPart == null);
		assertTrue(gantry.feeders.get(0).requestedPart == p2);
		assertTrue(feeder.log.size() == 1);
		
		//call the scheduler again
		gantry.pickAndExecuteAnAction();
		assertTrue(gantry.feeders.get(0).state == feederState.activeNoAction);
		assertTrue(gantry.feeders.get(0).currentPart == p2);
		assertTrue(gantry.feeders.get(0).requestedPart == null);
		assertTrue(feeder.log.size() == 2);
		assertTrue(feeder.log.containsString("Received msgHereIsBin of p2"));
		
		
	}
	public GantryTest() {
		// TODO Auto-generated constructor stub
	}

	public GantryTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

}
