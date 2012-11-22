package controllerTests;

import static org.junit.Assert.*;

import mocks.MockConveyorSystem;
import mocks.MockGuiConveyorIn;
import mocks.MockGuiConveyorOut;

import org.junit.Before;
import org.junit.Test;

import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;

import controllers.ConveyorSystemController;

public class ConveyorSystemControllerTest
{

	ConveyorSystemController controller;
	MockConveyorSystem conveyorSystem;
	
	@Before
	public void setUp()
	{
		controller = new ConveyorSystemController();
		conveyorSystem = new MockConveyorSystem("Conveyor System");
		controller.setConveyorSystem(conveyorSystem);
	}
	
	@Test
	public void testDoAnim()
	{
		
		MockGuiConveyorIn in = new MockGuiConveyorIn(conveyorSystem);
		MockGuiConveyorOut out = new MockGuiConveyorOut(conveyorSystem);
		EnteringConveyor ent = new EnteringConveyor(in);
		ExitingConveyor exit = new ExitingConveyor(out);
		Kit kit = new Kit();
		
		controller.doAnim(ent, kit);
		
		assertTrue(in.log.containsString("DoMove"));
		assertTrue(in.log.containsString(kit.toString()));
		
		controller.doAnim(exit, kit);
		
		assertTrue(out.log.containsString("DoMove"));
		assertTrue(out.log.containsString(kit.toString()));
	}
	
	@Test
	public void testDoneAnim()
	{
		controller.animDone();
		
		assertTrue(conveyorSystem.log.containsString("msgAnimDone"));
	}

}
