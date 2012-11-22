package agentIntegrationForV1;

import gui.GConveyorIn;
import gui.GConveyorOut;
import gui.GKitRobot;
import interfaces.Nest;

import java.util.ArrayList;
import java.util.List;

import controllers.ConveyorSystemController;
import controllers.GantryController;
import controllers.KitRobotController;

import mocks.MockNestGUI;
import mocks.MockServer;
import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Part;
import NonAgent.Stand;
import agents.*;
import interfaces.*;

public class AgentIntegrationForV1
{
	MockServer server = new MockServer();
	//GUIServer server = new GUIServer();
	KitRobotAgent kitRobot;
	static PartRobotAgent partRobot;
	ConveyorSystemAgent conveyorSystem;
	VisionAgent vision;
	GKitRobot gKitRobot;
	KitRobotController kitRobotController;
	List<Part> parts, parts2;
	List<Nest> nests = new ArrayList<Nest>();
	List<MockNestGUI> mocknestguis = new ArrayList<MockNestGUI>();
	List<Lane> lanes = new ArrayList<Lane>();
	List<Feeder> feeders = new ArrayList<Feeder>();
	GConveyorIn inGui;
	GConveyorOut outGui;
	ConveyorSystemController conveyorController;
	EnteringConveyor enteringConveyor;
	ExitingConveyor exitingConveyor;
	GantryAgent gantry;
	GantryController gantryController;

	public AgentIntegrationForV1()
	{

		// gantry, nests, lanes, feeders
		gantry = new GantryAgent("Gantry");
		gantryController = new GantryController(gantry);
		gantry.setController(gantryController);
		gantryController.setServer(server);

		FeederAgent tempfeeder = null;

		for (int i = 0; i < 8; i++)
		{
			if (i % 2 == 0)
			{
				tempfeeder = new FeederAgent(gantry);
				feeders.add(tempfeeder);
			}

			LaneAgent lane = new LaneAgent();
			NestAgent nest = new NestAgent();
			nest.nestID = i;
			lane.laneID = i;

			lane.SetFeeder(tempfeeder);
			lane.SetNest(nest);
			nest.SetLane(lane);

			lane.SetServer(server);

			lanes.add(lane);
			nests.add(nest);

			if (i % 2 == 0)
			{
				tempfeeder.SetTopLane(lane);
			}
			if (i % 2 == 1)
			{
				tempfeeder.SetBottomLane(lane);
			}
		}
		gantry.setFeeders(feeders);

		vision = new VisionAgent("Vision", null);
		partRobot = new PartRobotAgent("PartsRobot", null);

		// setting up conveyor
		enteringConveyor = new EnteringConveyor();
		exitingConveyor = new ExitingConveyor();
		conveyorController = new ConveyorSystemController(conveyorSystem);
		conveyorSystem = new ConveyorSystemAgent(enteringConveyor, exitingConveyor);
		conveyorController.setConveyorSystem(conveyorSystem);
		conveyorSystem.setController(conveyorController);
		inGui = new GConveyorIn();
		outGui = new GConveyorOut();
		enteringConveyor.setGui(inGui);
		exitingConveyor.setGui(outGui);
		conveyorController.setServer(server);

		// setting up kitRobot
		kitRobot = new KitRobotAgent(conveyorSystem, vision, partRobot, new Stand(), new Stand(),
				new Stand());
		gKitRobot = new GKitRobot();
		kitRobot.setGui(gKitRobot);
		kitRobotController = new KitRobotController(kitRobot, gKitRobot);
		kitRobot.setController(kitRobotController);
		conveyorSystem.setKitRobot(kitRobot);
		kitRobotController.setServer(server);

		server.setKitRobotController(kitRobotController);
		server.setConveyorSystemController(conveyorController);
		server.setGantryController(gantryController);
		//server.setLaneAgent(lane);
		//server.setPartRobotController(partRobotController);

		// setting up partRobot
		// nests = new ArrayList<Nest>();
		partRobot.setVision(vision);
		partRobot.setNests(nests);
		partRobot.setKitRobot(kitRobot);

		// TODO need to add Nest objects to nests arraylist

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

		// setting nest, vision, and parts robot references

		partRobot.setNests(nests);
		for (Nest n : nests)
		{
			n.SetVision(vision);
		}

		kitRobot.startThread();
		conveyorSystem.startThread();
		partRobot.startThread();
		gantry.startThread();
		vision.startThread();
		for (Nest nest : nests)
		{
			((NestAgent) nest).startThread();
		}
		for (Lane lane : lanes)
		{
			((LaneAgent) lane).startThread();
		}
		for (Feeder feeder : feeders)
		{
			((FeederAgent) feeder).startThread();
		}

		partRobot.msgGiveConfig(parts, "config", 2);

		// GRAHAM'S MOCK STUFF - WILL BE GONE IN REAL MAIN
		// setting up mock nest guis
		for (int i = 0; i < 8; i++)
		{
			mocknestguis.add(new MockNestGUI());
			mocknestguis.get(i).nest = (NestAgent) nests.get(i);
			mocknestguis.get(i).lane = (LaneAgent) lanes.get(i);
			((NestAgent) nests.get(i)).mockgui = mocknestguis.get(i);
		}
	}
	
	public static void main(String[] args)
	{
		AgentIntegrationForV1 v = new AgentIntegrationForV1();
	}
}
