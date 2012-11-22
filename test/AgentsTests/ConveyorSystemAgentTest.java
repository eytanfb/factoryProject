package AgentsTests;

import static org.junit.Assert.*;
import helper.TestHelper;

import mocks.MockConveyorSystemController;
import mocks.MockGuiConveyorIn;
import mocks.MockGuiConveyorOut;
import mocks.MockKitRobot;

import org.junit.Before;
import org.junit.Test;

import controllers.ConveyorSystemController;

import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;
import agents.ConveyorSystemAgent;

public class ConveyorSystemAgentTest extends BasicTest
{
	ConveyorSystemAgent conveyorSystemAgent;
	EnteringConveyor enteringConveyor;
	ExitingConveyor exitingConveyor;
	private MockKitRobot kitRobot;
	MockGuiConveyorIn inGui;
	MockGuiConveyorOut outGui;
	MockConveyorSystemController controller;
	
	@Before
	public void setUp()
	{
		enteringConveyor = new EnteringConveyor();
		exitingConveyor = new ExitingConveyor();
		kitRobot = new MockKitRobot("KitRobot");
		conveyorSystemAgent = new ConveyorSystemAgent(enteringConveyor, exitingConveyor);
		conveyorSystemAgent.setKitRobot(kitRobot);
		inGui = new MockGuiConveyorIn(conveyorSystemAgent);
		outGui = new MockGuiConveyorOut(conveyorSystemAgent);
		enteringConveyor.setGui(inGui);
		exitingConveyor.setGui(outGui);
		controller = new MockConveyorSystemController(conveyorSystemAgent);
		conveyorSystemAgent.setController(controller);
	}
	
	@Test
	public void testMsgINeedEmptyKit()
	{
		assertNull(conveyorSystemAgent.getCurrentEnteringKit());
		
		conveyorSystemAgent.msgINeedEmptyKit("config");
		
		assertNotNull(conveyorSystemAgent.getCurrentEnteringKit());
		assertEquals("config", conveyorSystemAgent.getCurrentEnteringKit().getConfig());
		assertEquals(1, conveyorSystemAgent.getEnteringKits().size());
	}

	@Test
	public void testMsgKitPickedUp()
	{
		String config = "config";
		conveyorSystemAgent.msgINeedEmptyKit(config);
		
		Kit kit = conveyorSystemAgent.getCurrentEnteringKit();
		
		conveyorSystemAgent.msgKitPickedUp(kit);
		
		assertNull(conveyorSystemAgent.getCurrentEnteringKit());
		
		conveyorSystemAgent.msgINeedEmptyKit(config);
		conveyorSystemAgent.msgINeedEmptyKit(config);
		
		Kit kit2 = conveyorSystemAgent.getCurrentEnteringKit();
		Kit kit3 = conveyorSystemAgent.getEnteringKits().get(1);
		
		conveyorSystemAgent.msgKitPickedUp(kit2);
		
		assertFalse(conveyorSystemAgent.getEnteringKits().contains(kit2));
		assertEquals(1, conveyorSystemAgent.getEnteringKits().size());
		assertNotNull(conveyorSystemAgent.getCurrentEnteringKit());
		assertEquals(kit3, conveyorSystemAgent.getCurrentEnteringKit());
		
	}
	
	@Test
	public void testMsgKitDone()
	{
		Kit kit = new Kit();
		
		conveyorSystemAgent.msgKitDone(kit);
		
		int index = conveyorSystemAgent.getExitingKits().indexOf(kit);
		
		assertEquals(kit, conveyorSystemAgent.getExitingKits().get(index));
		assertEquals(1, conveyorSystemAgent.getExitingKits().size());
	}
	
	@Test
	public void testMsgKitOutOfCell()
	{
		Kit kit = new Kit();
		
		conveyorSystemAgent.msgKitDone(kit);		
		conveyorSystemAgent.msgKitOutOfCell(kit);
		
		assertFalse(conveyorSystemAgent.getExitingKits().contains(kit));
	}
	
	@Test
	public void testMoveEmptyKitForRobotToPickUp()
	{
		conveyorSystemAgent.msgINeedEmptyKit("config");
		
		assertNotNull(conveyorSystemAgent.getCurrentEnteringKit());
		assertEquals(1, conveyorSystemAgent.getEnteringKits().size());
		
		Kit kit = conveyorSystemAgent.getCurrentEnteringKit();
		
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.ConveyorSystemAgent", "moveEmptyKitForRobotToPickUp", conveyorSystemAgent, kit);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit.toString()));
		assertTrue(kitRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(kitRobot.log.containsString(kit.toString()));
		
		controller.log.clear();
		kitRobot.log.clear();
		
		conveyorSystemAgent.msgINeedEmptyKit("config");
		
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.ConveyorSystemAgent", "moveEmptyKitForRobotToPickUp", conveyorSystemAgent, kit);
		
		assertFalse(controller.log.containsString("doAnim"));
		assertFalse(kitRobot.log.containsString("msgHereIsEmptyKit"));
		assertEquals(2, conveyorSystemAgent.getEnteringKits().size());
		assertTrue(conveyorSystemAgent.getEnteringKits().get(0).equals(kit));
		assertFalse(conveyorSystemAgent.getEnteringKits().get(1).equals(kit));
		
		Kit kit2 = conveyorSystemAgent.getEnteringKits().get(1);
		
		conveyorSystemAgent.msgKitPickedUp(kit);
		
		assertNotNull(conveyorSystemAgent.getCurrentEnteringKit());
		assertEquals(kit2, conveyorSystemAgent.getCurrentEnteringKit());
		assertEquals(1, conveyorSystemAgent.getEnteringKits().size());
		
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.ConveyorSystemAgent", "moveEmptyKitForRobotToPickUp", conveyorSystemAgent, kit2);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit2.toString()));
		assertTrue(kitRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(kitRobot.log.containsString(kit2.toString()));
	
	}
	
	@Test
	public void testPickAndExecuteAnAction()
	{
		conveyorSystemAgent.msgINeedEmptyKit("config");
		
		assertNotNull(conveyorSystemAgent.getCurrentEnteringKit());
		assertEquals(1, conveyorSystemAgent.getEnteringKits().size());
		
		Kit kit = conveyorSystemAgent.getCurrentEnteringKit();
		
		TestHelper.INSTANCE.callPrivateMethod("agents.ConveyorSystemAgent", "pickAndExecuteAnAction", conveyorSystemAgent);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit.toString()));
		assertTrue(kitRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(kitRobot.log.containsString(kit.toString()));
	
	}
	
}
