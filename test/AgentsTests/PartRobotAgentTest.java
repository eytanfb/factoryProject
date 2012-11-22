package AgentsTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import interfaces.Nest;
import interfaces.PartRobot;

import mocks.MockKitRobot;
import mocks.MockNest;
import mocks.MockPartRobot;
import mocks.MockVision;

import org.junit.Before;
import org.junit.Test;

import NonAgent.Kit;
import NonAgent.Part;
import NonAgent.Kit.KitState;
import agents.KitRobotAgent;
import agents.PartRobotAgent;
import agents.VisionAgent;

public class PartRobotAgentTest extends BasicTest
{
	List<Nest> nests = new ArrayList<Nest>();

	@Before
	public void setUp()
	{
		for (int i = 0; i < 8; i++)
		{
			nests.add(new MockNest("Nest" + i));
		}
	}

	@Test
	public void testMsgGiveConfig()
	{
		// Create a PartRobot
		PartRobotAgent partRobot = new PartRobotAgent("PartRobot1", null);
		
		// Create a MockPartsRobot
		MockKitRobot kitRobot = new MockKitRobot("KitRobot1");
		Part p = new Part("TestPart1");
		List<Part> parts = new ArrayList<Part>();
		Part p2 = new Part("TestPart2");
		Part p3 = new Part("TestPart3");
		parts.add(p);
		parts.add(p2);
		parts.add(p3);
		Kit kit = new Kit("Config1");

		partRobot.setKitRobot(kitRobot);
		MockVision vision = new MockVision("Vision1");
		partRobot.setVision(vision);
		partRobot.setNests(nests);
		// public void msgGiveConfig(List<Part> config, String name) {
		// kitrobot.msgNeedEmptyKit(c.config, c.name); should be called

		partRobot.msgGiveConfig(parts, "config1",1);
		// This will check that you're not messaging the kitrobot in the
		// part robots's message reception.
		assertEquals(
				"Mock KitRobot should have an empty event log before the Vision's scheduler is called. Instead, the mock kitrobot's event log reads: " +
						kitRobot.log.toString(), 0, kitRobot.log.size());

		// Call the vision's scheduler
		partRobot.pickAndExecuteAnAction();

		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.

		assertTrue(
				"Mock KitRobot should have received msgNeedEmptyKit with config, and the config name. Event log: " +
						kitRobot.log.toString(),
				kitRobot.log.containsString("msgNeedEmptyKit recevied with name config1."));
		assertEquals("Only 1 message should have been sent to the kit robot. Event log: " +
				kitRobot.log.toString(), 1, kitRobot.log.size());

	}

	@Test
	public void msgPickUpParts()
	{
		// Create a partRobot agent
		PartRobotAgent partRobot = new PartRobotAgent("PartRobot1", null);
		

		// Create a MockPartsRobot
		MockKitRobot kitRobot = new MockKitRobot("KitRobot1");
		Part p = new Part("TestPart1");
		List<Part> parts = new ArrayList<Part>();
		Part p2 = new Part("TestPart2");
		Part p3 = new Part("TestPart3");
		parts.add(p);
		parts.add(p2);
		parts.add(p3);
		parts.add(p3);
		Kit kit = new Kit("Config1");

		partRobot.setKitRobot(kitRobot);
		partRobot.setController(null);
		MockVision vision = new MockVision("Vision1");
		partRobot.setVision(vision);
		partRobot.setNests(nests);
		partRobot.msgGiveConfig(parts, "Config1",1);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgHereIsEmptyKit(kit);
		partRobot.pickAndExecuteAnAction();
		assertEquals(
				"Mock kitRobot should have one event log before the PartRobot's scheduler is called. Instead, the mock kitrobot's event log reads: " +
						kitRobot.log.toString(), 1, kitRobot.log.size());

		partRobot.msgPickUpParts(p, nests.get(0),0);
		partRobot.pickAndExecuteAnAction();
		assertEquals(
				"Mock kitRobot should STILL have one event log before the PartRobot's scheduler is called" +
						"because it needs 4 parts to do an actions. Instead, the mock kitrobot's event log reads: " +
						kitRobot.log.toString(), 1, kitRobot.log.size());

		partRobot.msgPickUpParts(p2, nests.get(0),0);
		partRobot.pickAndExecuteAnAction();

		assertEquals(
				"Mock kitRobot should STILL one event log before the PartRobot's scheduler is called" +
						"because it needs 4 parts to do an actions. Instead, the mock kitrobot's event log reads: " +
						kitRobot.log.toString(), 1, kitRobot.log.size());

		partRobot.msgPickUpParts(p3, nests.get(0),0);
		partRobot.pickAndExecuteAnAction();

		partRobot.msgPickUpParts(p3, nests.get(0),0);
		partRobot.pickAndExecuteAnAction();
		partRobot.pickAndExecuteAnAction();
		partRobot.pickAndExecuteAnAction();
		// assertEquals(
		// "Mock kitRobot should have received the kit to inspect"
		// + kitRobot.log.toString(),
		// kitRobot.log.containsString("msgKitReadyForInspection recevied with kit: "
		// + kit));
		assertTrue(kitRobot.log
				.containsString("msgKitReadyForInspection recevied with kit: " + kit));
	}

	@Test
	public void testMsg8Parts()
	{
		// Create a partRobot Agent
		PartRobotAgent partRobot = new PartRobotAgent("PartRobot1", null);
		
		// Create a MockPartsRobot
		MockKitRobot kitRobot = new MockKitRobot("KitRobot1");
		Part p = new Part("TestPart1");
		List<Part> parts = new ArrayList<Part>();
		Part p2 = new Part("TestPart2");
		Part p3 = new Part("TestPart3");
		Part p4 = new Part("TestPart4");
		Part p5 = new Part("TestPart5");
		Part p6 = new Part("TestPart6");
		Part p7 = new Part("TestPart7");
		Part p8= new Part("TestPart8");
		parts.add(p);
		parts.add(p2);
		parts.add(p3);
		parts.add(p4);
		parts.add(p5);
		parts.add(p6);
		parts.add(p7);
		parts.add(p8);
		

		Kit kit = new Kit("Config1");

		partRobot.setKitRobot(kitRobot);
		MockVision vision = new MockVision("Vision1");
		partRobot.setVision(vision);
		partRobot.setNests(nests);

		partRobot.msgGiveConfig(parts, "config1",1);
		// This will check that you're not messaging the kitrobot in the
		// part robots's message reception.
		assertEquals(
				"Mock KitRobot should have an empty event log before the Vision's scheduler is called. Instead, the mock kitrobot's event log reads: " +
						kitRobot.log.toString(), 0, kitRobot.log.size());

		// Call the vision's scheduler
		partRobot.pickAndExecuteAnAction();
		
		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.

		assertTrue(
				"Mock KitRobot should have received msgNeedEmptyKit with config, and the config name. Event log: " +
						kitRobot.log.toString(),
				kitRobot.log.containsString("msgNeedEmptyKit recevied with name config1."));
		assertEquals("Only 1 message should have been sent to the kit robot. Event log: " +
				kitRobot.log.toString(), 1, kitRobot.log.size());

		// public void msgGiveConfig(List<Part> config, String name) {
		// kitrobot.msgNeedEmptyKit(c.config, c.name); should be called
		// test this message 8 times, nest0 should have 2 messages and rest
		// should have 1
		partRobot.msgHereIsEmptyKit(kit);
		
		partRobot.pickAndExecuteAnAction();
		
		partRobot.msgPickUpParts(p, nests.get(0), 0);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgPickUpParts(p2, nests.get(1), 1);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgPickUpParts(p3, nests.get(2), 2);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgPickUpParts(p4, nests.get(3), 3);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgPickUpParts(p5, nests.get(4), 4);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgPickUpParts(p6, nests.get(5), 5);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgPickUpParts(p7, nests.get(6), 6);
		partRobot.pickAndExecuteAnAction();
		partRobot.msgPickUpParts(p8, nests.get(7), 7);
		partRobot.pickAndExecuteAnAction();
		assertTrue(kitRobot.log
				.containsString("msgKitReadyForInspection recevied with kit: " + kit));
	}
	
	@Test
	public void testMsgHereIsEmptyKit()
	{
		// Create a partRobot Agent
		PartRobotAgent partRobot = new PartRobotAgent("PartRobot1", null);
		
		// Create a MockPartsRobot
		MockKitRobot kitRobot = new MockKitRobot("KitRobot1");
		Part p = new Part("TestPart1");
		List<Part> parts = new ArrayList<Part>();
		Part p2 = new Part("TestPart2");
		Part p3 = new Part("TestPart3");
		parts.add(p);
		parts.add(p2);
		parts.add(p3);
		parts.add(p);
		parts.add(p2);
		parts.add(p3);

		Kit kit = new Kit("Config1");

		partRobot.setKitRobot(kitRobot);
		MockVision vision = new MockVision("Vision1");
		partRobot.setVision(vision);
		partRobot.setNests(nests);

		partRobot.msgGiveConfig(parts, "config1",1);
		// This will check that you're not messaging the kitrobot in the
		// part robots's message reception.
		assertEquals(
				"Mock KitRobot should have an empty event log before the Vision's scheduler is called. Instead, the mock kitrobot's event log reads: " +
						kitRobot.log.toString(), 0, kitRobot.log.size());

		// Call the vision's scheduler
		partRobot.pickAndExecuteAnAction();

		
		}
	}

	

