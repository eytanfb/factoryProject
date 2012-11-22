package integrationTests;

import static org.junit.Assert.*;
import interfaces.Nest;

import java.util.ArrayList;
import java.util.List;

import mocks.MockConveyorSystemController;
import mocks.MockGuiConveyorIn;
import mocks.MockGuiConveyorOut;
import mocks.MockGuiKitRobot;
import mocks.MockKitRobotController;
import mocks.MockNest;

import org.junit.Before;
import org.junit.Test;

import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Part;
import NonAgent.Stand;
import agents.ConveyorSystemAgent;
import agents.KitRobotAgent;
import agents.PartRobotAgent;
import agents.VisionAgent;

public class finalIntegrationTest
{
	KitRobotAgent kitRobot;
	PartRobotAgent partRobot;
	ConveyorSystemAgent conveyorSystem;
	VisionAgent vision;
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
		vision = new VisionAgent("Vision", null);
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
		
		// setting up vision
		vision.setKitRobot(kitRobot);
		vision.setPartRobot(partRobot);
	}

	@Test
	public void test()
	{
		fail("Not yet implemented");
	}

}
