package integrationTests;

import static org.junit.Assert.*;

import helper.TestHelper;
import interfaces.Nest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mocks.MockConveyorSystemController;
import mocks.MockGuiConveyorIn;
import mocks.MockGuiConveyorOut;
import mocks.MockGuiKitRobot;
import mocks.MockKitRobotController;
import mocks.MockNest;
import mocks.MockVision;

import org.junit.Before;
import org.junit.Test;

import agents.ConveyorSystemAgent;
import agents.KitRobotAgent;
import agents.PartRobotAgent;
import agents.VisionAgent;

import AgentsTests.BasicTest;
import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;
import NonAgent.Part;
import NonAgent.Stand;
import NonAgent.Kit.KitState;

public class PartsAndKitRobotTests extends BasicTest
{

	KitRobotAgent kitRobot;
	PartRobotAgent partRobot;
	ConveyorSystemAgent conveyorSystem;
	MockVision vision;
	MockGuiKitRobot gui;
	MockKitRobotController controller;
	List<Part> parts, parts2;
	List<Nest> nests;
	MockGuiConveyorIn inGui;
	MockGuiConveyorOut outGui;
	MockConveyorSystemController conveyorController;
	EnteringConveyor enteringConveyor;
	ExitingConveyor exitingConveyor;

	@Before
	public void setUp()
	{
		vision = new MockVision("Vision");
		partRobot = new PartRobotAgent("PartsRobot", null);

		// setting up conveyor
		enteringConveyor = new EnteringConveyor();
		exitingConveyor = new ExitingConveyor();
		conveyorController = new MockConveyorSystemController(conveyorSystem);
		conveyorSystem = new ConveyorSystemAgent(enteringConveyor, exitingConveyor);
		conveyorController.setConveyorSystem(conveyorSystem);
		conveyorSystem.setController(conveyorController);
		inGui = new MockGuiConveyorIn(conveyorSystem);
		outGui = new MockGuiConveyorOut(conveyorSystem);
		enteringConveyor.setGui(inGui);
		exitingConveyor.setGui(outGui);

		// setting up kitRobot
		kitRobot = new KitRobotAgent(conveyorSystem, vision, partRobot, new Stand(), new Stand(),
				new Stand());
		gui = new MockGuiKitRobot(kitRobot);
		kitRobot.setGui(gui);
		controller = new MockKitRobotController(kitRobot);
		kitRobot.setController(controller);
		conveyorSystem.setKitRobot(kitRobot);

		// setting up partRobot
		nests = new ArrayList<Nest>();
		partRobot.setVision(vision);
		partRobot.setNests(nests);
		partRobot.setKitRobot(kitRobot);

		for (int i = 0; i < 8; i++)
		{
			nests.add(new MockNest("Nest " + i));
		}

		parts = new ArrayList<Part>();
		parts2 = new ArrayList<Part>();
		Part p = new Part("testPart");
		Part p2 = new Part("TestPart2");
		Part p3 = new Part("TestPart3");
		Part p4 = new Part("TestPart4");
		Part p5 = new Part("TestPart5");
		Part p6 = new Part("TestPart6");
		Part p7 = new Part("TestPart7");
		Part p8 = new Part("TestPart8");
		parts.add(p);
		parts.add(p2);
		parts.add(p3);
		parts.add(p4);
		parts.add(p5);
		parts.add(p6);
		parts.add(p7);
		parts.add(p8);

		parts2.add(p);
		parts2.add(p2);
		parts2.add(p3);
		parts2.add(p4);
		parts2.add(p5);
		parts2.add(p6);
		parts2.add(p7);
		parts2.add(p8);
	}

	@Test
	public void testNormativeOneKit()
	{
		partRobot.msgGiveConfig(parts, "config1", 1);

		partRobot.pickAndExecuteAnAction();

		assertTrue(kitRobot.getKitRequests().get("config1") == 1);
		assertEquals(1, kitRobot.getKitRequests().size());

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		Kit kit = conveyorSystem.getEnteringConveyor().getCurrentKit();

		assertEquals(0, kitRobot.getKitRequests().size());
		assertNotNull(conveyorSystem.getEnteringConveyor().getCurrentKit());
		assertEquals("config1",
				conveyorSystem.getEnteringConveyor().getCurrentKit().configAssociated);

		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent",
				"pickAndExecuteAnAction", conveyorSystem);

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		assertEquals(1, partRobot.getKits().size());

		partRobot.pickAndExecuteAnAction();

		for (int i = 0; i < 8; i++)
			vision.msgNestHas(nests.get(i), parts.get(i), i);

		assertTrue(vision.log.containsString("msgNestHas"));

		for (int i = 0; i < 8; i++)
			assertTrue(vision.log.containsString(parts.get(i).toString()));

		for (int i = 0; i < 8; i++)
			partRobot.msgPickUpParts(parts.get(i), nests.get(i), i);

		partRobot.pickAndExecuteAnAction();

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		assertTrue(vision.log.containsString("msgTellCameraInspect"));
		assertTrue(vision.log.containsString(kit.toString()));

		kitRobot.msgKitIsGood(kit);

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent",
				"pickAndExecuteAnAction", conveyorSystem);

	}

	@Test
	public void testNormativeTwoKits()
	{
		partRobot.msgGiveConfig(parts, "config1", 1);
		partRobot.msgGiveConfig(parts2, "config2", 1);

		partRobot.pickAndExecuteAnAction();
		partRobot.pickAndExecuteAnAction();

		assertTrue(kitRobot.getKitRequests().get("config1") == 1);
		assertTrue(kitRobot.getKitRequests().get("config2") == 1);
		assertEquals(2, kitRobot.getKitRequests().size());

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);
		Kit kit = conveyorSystem.getEnteringConveyor().getCurrentKit();
		assertEquals(1, kitRobot.getKitRequests().size());
		assertNotNull(conveyorSystem.getEnteringConveyor().getCurrentKit());
		assertEquals("config1", kit.configAssociated);

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);
		Kit kit2 = conveyorSystem.getEnteringKits().get(1);
		assertEquals("config2", conveyorSystem.getEnteringKits().get(1).configAssociated);
		assertEquals(0, kitRobot.getKitRequests().size());

		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent",
				"pickAndExecuteAnAction", conveyorSystem);
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);
		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent",
				"pickAndExecuteAnAction", conveyorSystem);
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		assertEquals(2, partRobot.getKits().size());

		partRobot.pickAndExecuteAnAction();
		partRobot.pickAndExecuteAnAction();

		for (int i = 0; i < 8; i++)
			vision.msgNestHas(nests.get(i), parts.get(i), i);

		assertTrue(vision.log.containsString("msgNestHas"));

		for (int i = 0; i < 8; i++)
			assertTrue(vision.log.containsString(parts.get(i).toString()));

		for (int i = 0; i < 8; i++)
		{
			partRobot.msgPickUpParts(parts.get(i), nests.get(i), i);
		}
		for (int i = 0; i < 8; i++)
		{
			partRobot.msgPickUpParts(parts2.get(i), nests.get(i), i);
		}

		for (int i = 0; i < 16; i++)
		{
			partRobot.pickAndExecuteAnAction();
		}

		assertEquals(KitState.WAITING_FOR_INSPECTION, kit.getState());
		assertEquals(KitState.WAITING_FOR_INSPECTION, kit2.getState());

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		assertTrue(vision.log.containsString("msgTellCameraInspect"));
		assertTrue(vision.log.containsString(kit.toString()));

		kitRobot.msgKitIsGood(kit);

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent",
				"pickAndExecuteAnAction", conveyorSystem);

		conveyorSystem.msgKitOutOfCell(kit);
		assertEquals(0, conveyorSystem.getExitingKits().size());
		assertEquals(1, kitRobot.getKits().size());
		assertEquals(0, conveyorSystem.getEnteringKits().size());

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		assertTrue(vision.log.containsString("msgTellCameraInspect"));
		assertTrue(vision.log.containsString(kit.toString()));

		kitRobot.msgKitIsGood(kit2);

		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction",
				kitRobot);

		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent",
				"pickAndExecuteAnAction", conveyorSystem);

		conveyorSystem.msgKitOutOfCell(kit2);
		assertEquals(0, conveyorSystem.getExitingKits().size());
		assertEquals(0, kitRobot.getKits().size());
		assertEquals(0, conveyorSystem.getEnteringKits().size());
	}



//	@Test
//	public void testWithThreads()
//	{
//		final VisionAgent vision = new VisionAgent("Vision", null);
//		kitRobot.startThread();
//		partRobot.startThread();
//		conveyorSystem.startThread();
//		vision.startThread();
//		vision.setPartRobot(partRobot);
//		vision.setKitRobot(kitRobot);
//		
//		partRobot.msgGiveConfig(parts, "config1", 1);
//		
//		new Timer().schedule(new TimerTask(){
//			public void run()
//			{
//				for (int i = 0; i < 8; i++)
//					vision.msgNestHas(nests.get(i), parts.get(i), i);
//			}
//		}, 3000);
//		
//		while(true);
//		
//	}

}
