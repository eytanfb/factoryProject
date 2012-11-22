package v1;

import gui.GConveyorIn;
import gui.GConveyorOut;
import gui.GKitAssemblyManager;
import gui.GKitRobot;
import gui.GKittingStand;
import gui.GUIServer;
import interfaces.Nest;
import interfaces.Vision;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import mocks.MockNest;
import mocks.MockVision;

import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;
import NonAgent.Part;
import NonAgent.Stand;
import agents.ConveyorSystemAgent;
import agents.KitRobotAgent;
import agents.PartRobotAgent;
import agents.VisionAgent;
import controllers.ConveyorSystemController;
import controllers.KitRobotController;
import controllers.PartRobotController;
import controllers.VisionController;

public class KitAssemblyManagerIntegration {
	
	static KitRobotAgent kitRobot;
	static PartRobotAgent partRobot;
	static ConveyorSystemAgent conveyorSystem;
	static VisionAgent vision;
	GKitRobot gKitRobot;
	KitRobotController kitRobotController;
	PartRobotController partRobotController;
	VisionController visionController;
	static List<Part> parts;
	static List<Part> parts2;
	List<Nest> nests;
	GConveyorIn inGui;
	GConveyorOut outGui;
	ConveyorSystemController conveyorController;
	EnteringConveyor enteringConveyor;
	ExitingConveyor exitingConveyor;
	GUIServer myServer;
	Stand workingStand1, workingStand2, inspectionStand;
	GKittingStand gWorkingStand1, gWorkingStand2, gInspectionStand;
	
	public KitAssemblyManagerIntegration() {
			myServer = new GUIServer();
			visionController = new VisionController(vision,myServer);
			partRobotController = new PartRobotController(partRobot,myServer);
		
			vision = new VisionAgent("Vision",visionController);
			partRobot = new PartRobotAgent("PartsRobot", partRobotController);
			
			
			// setting up conveyor
			enteringConveyor = new EnteringConveyor();
			exitingConveyor = new ExitingConveyor();
			conveyorController = new ConveyorSystemController(conveyorSystem);
			inGui = new GConveyorIn();
			outGui = new GConveyorOut();
			enteringConveyor.setGui(inGui);
			exitingConveyor.setGui(outGui);
			conveyorSystem = new ConveyorSystemAgent(enteringConveyor, exitingConveyor);
			conveyorController.setConveyorSystem(conveyorSystem);
			conveyorSystem.setController(conveyorController);
			

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
			
			
			

			// setting up partRobot
			
			nests = new ArrayList<Nest>();
			partRobot.setVision(vision);
			partRobot.setNests(nests);
			partRobot.setKitRobot(kitRobot);
			
			partRobot.setController(partRobotController);
			vision.setController(visionController);
			partRobotController.setPartRobot(partRobot);
			visionController.setVision(vision);
			
			for(int i=0; i<8;i++){
			nests.add(new MockNest("Nest"+i));	
			}
			// TODO need to add Nest objects to nests arraylist
			partRobot.setNests(nests);
			parts = new ArrayList<Part>();
			parts2 = new ArrayList<Part>();
			Part p = new Part("TestPart0");
			Part p2 = new Part("TestPart1");
			Part p3 = new Part("TestPart2");
			Part p4 = new Part("TestPart3");
			Part p5 = new Part("TestPart4");
			Part p6 = new Part("TestPart5");
			Part p7 = new Part("TestPart6");
			Part p8 = new Part("TestPart7");
			parts.add(p);
			parts.add(p2);
			parts.add(p3);
			parts.add(p4);
			parts.add(p5);
			parts.add(p6);
			parts.add(p7);
			//parts.add(p8);

			parts2.add(p);
			parts2.add(p2);
			parts2.add(p3);
			parts2.add(p4);
			

			// setting up vision
			vision.setKitRobot(kitRobot);
			vision.setPartRobot(partRobot);
			kitRobotController.setServer(myServer);
			conveyorController.setServer(myServer);
			myServer.setKitRobotController(kitRobotController);
			myServer.setConveyorSystemController(conveyorController);
			myServer.setPartRobotController(partRobotController);
			myServer.setVisionController(visionController);
			
			//setting up server
	}
	public static void main(String[] args) {
		System.out.println("Hello");
		KitAssemblyManagerIntegration kamI = new KitAssemblyManagerIntegration();
		// Schedule a task for 5 seconds later, allowing the user to run the GKitAssemblyManager class
		// But right now, the task is not performed somehow
		System.out.println("Before the timer");
		kitRobot.startThread();
		conveyorSystem.startThread();
		partRobot.startThread();
		vision.startThread();
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgGiveConfig(parts, "Config1", 1);
			}
		}, 3000);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgGiveConfig(parts2, "Config2", 1);
			}
		}, 4000);
		
		new Timer().schedule(new TimerTask(){
			public void run()
			{
			
				vision.msgNestHas(null,parts.get(0), 0);
				System.out.println("Called");
			}
		}, 25100);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				vision.msgNestHas(null,parts.get(1), 1);
				
			}
		}, 25200);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				vision.msgNestHas(null,parts.get(2), 2);
				
			}
		}, 25300);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				vision.msgNestHas(null,parts.get(3), 3);
				
			}
		}, 25400);
		
		
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgPickUpParts(parts.get(4), null, 4);
				
			}
		}, 25500);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgPickUpParts(parts.get(5), null, 5);
				
			}
		}, 25600);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgPickUpParts(parts.get(6), null, 6);
				
			}
		}, 25700);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgPickUpParts(parts2.get(0), null, 0);
				
			}
		}, 25800);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgPickUpParts(parts2.get(1), null, 1);
				
			}
		}, 25900);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgPickUpParts(parts2.get(2), null, 2);
				
			}
		}, 26000);
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				partRobot.msgPickUpParts(parts2.get(3), null, 3);
				
			}
		}, 26100);
		
		
//		new Timer().schedule(new TimerTask(){
//			public void run()
//			{
//				Kit kit = kitRobot.getKits().get(0);
//				kitRobot.msgKitReadyForInspection(kit);
//			}
//		}, 25000);
//		
//		new Timer().schedule(new TimerTask(){
//			public void run()
//			{
//				Kit kit = kitRobot.getKits().get(0);
//				kitRobot.msgKitIsGood(kit);
//			}
//		}, 25000);
	}
}
