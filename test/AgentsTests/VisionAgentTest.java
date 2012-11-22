package AgentsTests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import interfaces.KitRobot;
import interfaces.Nest;
import interfaces.PartRobot;
import interfaces.Vision;

import org.junit.Before;
import org.junit.Test;

import mocks.MockPartRobot;
import mocks.MockKitRobot;

import NonAgent.Kit;
import NonAgent.Kit.KitState;
import NonAgent.Part;
import agents.KitRobotAgent;
import agents.NestAgent;
import agents.VisionAgent;

public class VisionAgentTest extends BasicTest{
	
		
		
		@Test
		public void testMsgTellCameraInspect()
		{
			// Create a WaiterAgent
			VisionAgent vision = new VisionAgent("Camera1",null);

			// Create a MockPartsRobot
			MockPartRobot partRobot = new MockPartRobot("PartRobot1");
			MockKitRobot kitRobot = new MockKitRobot("KitRobot1");
			Part p= new Part("TestPart");
			List<Part> parts = new ArrayList<Part>();
			parts.add(p);
			Kit kit = new Kit("Config1");
			vision.setKitRobot(kitRobot);
			vision.setPartRobot(partRobot);
			vision.msgTellCameraInspect(kit);

			// This will check that you're not messaging the waiter in the
			// cashier's message reception.
			assertEquals(
					"Mock KitRobot should have an empty event log before the Vision's scheduler is called. Instead, the mock kitrobot's event log reads: "
							+ kitRobot.log.toString(), 0, kitRobot.log.size());

			// Call the vision's scheduler
			vision.pickAndExecuteAnAction();

			// Now, make asserts to make sure that the scheduler did what it was
			// supposed to.

			assertTrue(
					"Mock KitRobot should have received message msgKitIsGood with kit. Event log: "
							+ kitRobot.log.toString(), kitRobot.log
							.containsString("msgKitIsGood recevied with kit"));
			assertEquals(
					"Only 1 message should have been sent to the kit robot. Event log: "
							+ kitRobot.log.toString(), 1, kitRobot.log.size());
			
		}

		@Test
		public void testMsgNestHas()
		{
			// Create a WaiterAgent
			VisionAgent vision = new VisionAgent("Camera1",null);

			// Create a MockPartsRobot
			MockPartRobot partRobot = new MockPartRobot("PartRobot1");
			MockKitRobot kitRobot = new MockKitRobot("KitRobot1");
			Part p= new Part("TestPart");
			List<Part> parts = new ArrayList<Part>();
			parts.add(p);
			Kit kit = new Kit("Config1");
			vision.setKitRobot(kitRobot);
			vision.setPartRobot(partRobot);
			Nest nest = new NestAgent();
			vision.msgNestHas(nest, p,0);

			// This will check that you're not messaging the waiter in the
			// cashier's message reception.
			assertEquals(
					"Mock PartRobot should have an empty event log before the Vision's scheduler is called. Instead, the mock kitrobot's event log reads: "
							+ partRobot.log.toString(), 0, partRobot.log.size());

			// Call the vision's scheduler
			vision.pickAndExecuteAnAction();

			// Now, make asserts to make sure that the scheduler did what it was
			// supposed to.
			//partRobot.msgPickUpParts(part,p.owner);
			assertTrue(
					"Mock PartRobot should have received message to go to confirmPass with bill. Event log: "
							+ partRobot.log.toString(), partRobot.log
							.containsString("Received message msgPickUpParts for part "+p.toString()+" for Nest "+nest+"."));
			assertEquals(
					"Only 1 message should have been sent to the kit robot. Event log: "
							+ partRobot.log.toString(), 1, partRobot.log.size());
			
		}

}

