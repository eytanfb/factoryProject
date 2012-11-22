package agentIntegrationForV1;

import gui.GConveyorIn;
import gui.GConveyorOut;
import gui.GKitRobot;
import gui.GKittingStand;
import gui.GUIServer;
import interfaces.Feeder;
import interfaces.Lane;
import interfaces.Nest;

import java.util.ArrayList;
import java.util.List;

import controllers.ConveyorSystemController;
import controllers.GantryController;
import controllers.KitRobotController;
import controllers.LanesysController;
import controllers.PartRobotController;
import controllers.VisionController;

import mocks.MockNestGUI;
import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Part;
import NonAgent.Stand;
import agents.*;


public class FullIntegrationTest {

	//MockServer server = new MockServer();
	
	GUIServer server;
	KitRobotAgent kitRobot;
	PartRobotAgent partRobot;
	ConveyorSystemAgent conveyorSystem;
	VisionAgent vision;
	GKitRobot gKitRobot;
	PartRobotController partRobotController;
	VisionController visionController;
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
	LanesysController laneSysController = new LanesysController();	
	Stand workingStand1, workingStand2, inspectionStand;
	GKittingStand gWorkingStand1, gWorkingStand2, gInspectionStand;
	
	
	public FullIntegrationTest(){
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
					
					laneSysController.SetLane(lane, i);

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

				vision = new VisionAgent("Vision", visionController);
				visionController = new VisionController(vision, server);
				vision.setController(visionController);
				partRobot = new PartRobotAgent("PartsRobot", partRobotController);
				partRobotController = new PartRobotController(partRobot, server);
				partRobot.setController(partRobotController);

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
				gWorkingStand1 = new GKittingStand(210,550);
				gWorkingStand2 = new GKittingStand(210,350);
				gInspectionStand = new GKittingStand(210,100);
				workingStand1 = new Stand(gWorkingStand1);
				workingStand2 = new Stand(gWorkingStand2);
				inspectionStand = new Stand(gInspectionStand);
				kitRobot = new KitRobotAgent(conveyorSystem, vision, partRobot, workingStand1, workingStand2,
						inspectionStand);
				gKitRobot = new GKitRobot();
				kitRobot.setGui(gKitRobot);
				kitRobotController = new KitRobotController(kitRobot, gKitRobot);
				kitRobot.setController(kitRobotController);
				conveyorSystem.setKitRobot(kitRobot);
				kitRobotController.setServer(server);

				// setting up partRobot
				//nests = new ArrayList<Nest>();
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
				for(Nest nest: nests)
				{
					((NestAgent) nest).startThread();
				}
				for(Lane lane: lanes)
				{
					((LaneAgent) lane).startThread();
				}
				for(Feeder feeder: feeders)
				{
					((FeederAgent) feeder).startThread();
				}
				
				server = new GUIServer(kitRobotController, conveyorController, gantryController, laneSysController, partRobotController, visionController);
//				server = new GUIServer();
				server.setKitRobotController(kitRobotController);
				server.setConveyorSystemController(conveyorController);
				server.setGantryController(gantryController);
				server.setLaneAgent(laneSysController);
				server.setPartRobotController(partRobotController);
				server.setVisionController(visionController);
				
				
//				laneSysController.server = server;
//				System.out.println(server + " WOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
//				laneSysController.SetServerRefs();
				
				
				//GRAHAM'S MOCK STUFF - WILL BE GONE IN REAL MAIN
				// setting up mock nest guis
//						for (int i = 0; i < 8; i++)
//						{
//							mocknestguis.add(new MockNestGUI());
//							mocknestguis.get(i).nest = (NestAgent) nests.get(i);
//							mocknestguis.get(i).lane = (LaneAgent) lanes.get(i);
//							((NestAgent) nests.get(i)).mockgui = mocknestguis.get(i);
//						}
				
				
				
	}
	
	
	
	public static void main(String[] args)
	{
		FullIntegrationTest fit = new FullIntegrationTest();
		/*fpm.setSize(1200,800);
		fpm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fpm.setVisible(true);*/
	}
	
}
