package AgentsTests;

import static org.junit.Assert.*;
import helper.TestHelper;

import mocks.MockGuiConveyorIn;
import mocks.MockGuiConveyorOut;
import mocks.MockGuiKitRobot;
import mocks.MockPartRobot;
import mocks.MockVision;

import org.junit.Before;
import org.junit.Test;

import controllers.ConveyorSystemController;
import controllers.KitRobotController;

import sun.org.mozilla.javascript.internal.Kit;

import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit.KitState;
import NonAgent.Stand;
import agents.ConveyorSystemAgent;
import agents.KitRobotAgent;

public class Task1AgentsOnly
{
	
	MockVision vision;
	MockPartRobot partRobot;
	ConveyorSystemAgent conveyorSystemAgent;
	KitRobotAgent kitRobotAgent;
	MockGuiKitRobot gui;
	EnteringConveyor enteringConveyor;
	ExitingConveyor exitingConveyor;
	MockGuiConveyorIn inGui;
	MockGuiConveyorOut outGui;
	KitRobotController kitRobotController;
	ConveyorSystemController conveyorSystemController;
	
	@Before
	public void setUp()
	{
		enteringConveyor = new EnteringConveyor();
		exitingConveyor = new ExitingConveyor();
		vision = new MockVision("vision");
		partRobot = new MockPartRobot("partRobot");
		conveyorSystemAgent = new ConveyorSystemAgent(enteringConveyor, exitingConveyor);
		kitRobotAgent = new KitRobotAgent(conveyorSystemAgent, vision, partRobot, new Stand() , new Stand(), new Stand());
		conveyorSystemAgent.setKitRobot(kitRobotAgent);
		gui = new MockGuiKitRobot(kitRobotAgent);
		kitRobotAgent.setGui(gui);
		inGui = new MockGuiConveyorIn(conveyorSystemAgent);
		outGui = new MockGuiConveyorOut(conveyorSystemAgent);
		enteringConveyor.setGui(inGui);
		exitingConveyor.setGui(outGui);
		kitRobotController = new KitRobotController(kitRobotAgent, kitRobotAgent.getGui());
		conveyorSystemController = new ConveyorSystemController();
		kitRobotAgent.setController(kitRobotController);
		conveyorSystemAgent.setController(conveyorSystemController);
	}
	
	@Test
	public void testTask1()
	{
		kitRobotAgent.msgNeedEmptyKit(null);
		
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertNotNull(conveyorSystemAgent.getCurrentEnteringKit());
		NonAgent.Kit kit = conveyorSystemAgent.getCurrentEnteringKit();
		
		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent", "pickAndExecuteAnAction", conveyorSystemAgent);
	
		assertEquals(1, kitRobotAgent.getKits().size());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
	
		assertNull(conveyorSystemAgent.getCurrentEnteringKit());
		assertTrue(partRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(partRobot.log.containsString(kit.toString()));
		
		kit.setState(KitState.WAITING_FOR_INSPECTION);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(vision.log.containsString("msgTellCameraInspect"));
		assertTrue(vision.log.containsString(kit.toString()));
		
		kitRobotAgent.msgKitIsGood(kit);
		assertEquals(KitState.DONE, kit.getState());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertFalse(kitRobotAgent.getKits().contains(kit));
		
		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent", "pickAndExecuteAnAction", conveyorSystemAgent);
		
		assertEquals(KitState.EXITING_CELL, kit.getState());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent", "pickAndExecuteAnAction", conveyorSystemAgent);
		
		assertEquals(KitState.EXITING_CELL, kit.getState());
		
		conveyorSystemAgent.msgKitOutOfCell(kit);
		
		assertFalse(conveyorSystemAgent.getEnteringKits().contains(kit));
		assertFalse(conveyorSystemAgent.getExitingKits().contains(kit));
		assertFalse(kitRobotAgent.getKits().contains(kit));
		
	}

}
