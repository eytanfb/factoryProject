package controllerTests;

import static org.junit.Assert.*;

import mocks.MockConveyorSystem;
import mocks.MockGuiConveyorIn;
import mocks.MockGuiConveyorOut;
import mocks.MockGuiKitRobot;
import mocks.MockKitRobot;

import org.junit.Before;
import org.junit.Test;

import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;
import NonAgent.Stand;

import controllers.KitRobotController;

public class KitRobotControllerTest
{
	
	KitRobotController controller;
	MockKitRobot kitRobot;
	MockGuiKitRobot gKitRobot;
	
	@Before
	public void setUp()
	{
		kitRobot = new MockKitRobot("KitRobot");
		gKitRobot = new MockGuiKitRobot(kitRobot);
		controller = new KitRobotController(kitRobot, gKitRobot);
	}

	@Test
	public void testDoAnim()
	{
		MockConveyorSystem conveyor = new MockConveyorSystem("Conveyor System");
		EnteringConveyor ent = new EnteringConveyor(new MockGuiConveyorIn(conveyor));
		ExitingConveyor exit = new ExitingConveyor(new MockGuiConveyorOut(conveyor));
		Stand stand = new Stand();
		Stand inspectionStand = new Stand();
		Kit kit = new Kit();
		
		controller.doAnim(ent, stand, kit);
		
		assertTrue(gKitRobot.log.containsString("DoPutKit"));
		assertTrue(gKitRobot.log.containsString(kit.toString()));
		assertTrue(gKitRobot.log.containsString(ent.toString()));
		assertTrue(gKitRobot.log.containsString(stand.toString()));
		
		controller.doAnim(stand, inspectionStand, kit);
		
		assertTrue(gKitRobot.log.containsString("DoPutKit"));
		assertTrue(gKitRobot.log.containsString(kit.toString()));
		assertTrue(gKitRobot.log.containsString(inspectionStand.toString()));
		assertTrue(gKitRobot.log.containsString(stand.toString()));
		
		controller.doAnim(inspectionStand, exit, kit);
		
		assertTrue(gKitRobot.log.containsString("DoPutKit"));
		assertTrue(gKitRobot.log.containsString(kit.toString()));
		assertTrue(gKitRobot.log.containsString(exit.toString()));
		assertTrue(gKitRobot.log.containsString(inspectionStand.toString()));
	}
	
	@Test
	public void testDoneAnim()
	{
		controller.animDone();
		
		assertTrue(kitRobot.log.containsString("msgAnimDone"));
	}

}
