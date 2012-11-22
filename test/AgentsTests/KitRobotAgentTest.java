package AgentsTests;

import static org.junit.Assert.*;
import helper.TestHelper;

import mocks.MockConveyorSystem;
import mocks.MockGuiKitRobot;
import mocks.MockKitRobotController;
import mocks.MockPartRobot;
import mocks.MockVision;

import org.junit.Before;
import org.junit.Test;

import NonAgent.Kit;
import NonAgent.Kit.KitState;
import NonAgent.Stand;
import agents.KitRobotAgent;

public class KitRobotAgentTest extends BasicTest
{
	KitRobotAgent kitRobotAgent;
	MockConveyorSystem conveyorSystem;
	MockVision vision;
	MockPartRobot partRobot;
	MockGuiKitRobot gui;
	MockKitRobotController controller;
	Stand workingStand1, workingStand2, inspectionStand;
	
	@Before
	public void setUp()
	{
		workingStand1 = new Stand();
		workingStand2 = new Stand();
		inspectionStand = new Stand();
		conveyorSystem = new MockConveyorSystem("Conveyor System");
		vision = new MockVision("Vision");
		partRobot = new MockPartRobot("PartRobot");
		kitRobotAgent = new KitRobotAgent(conveyorSystem, vision, partRobot, workingStand1, workingStand2, inspectionStand);
		gui = new MockGuiKitRobot(kitRobotAgent);
		kitRobotAgent.setGui(gui);
		controller = new MockKitRobotController(kitRobotAgent);
		kitRobotAgent.setController(controller);
	}
	
	@Test
	public void testMsgHereIsEmptyKit()
	{
		assertTrue(kitRobotAgent.hasNoKits());
		
		kitRobotAgent.msgHereIsEmptyKit(new Kit());
		
		assertEquals(1, kitRobotAgent.getKits().size());
	}

	@Test
	public void testMsgKitGood()
	{
		Kit kit = new Kit();
		
		kitRobotAgent.msgHereIsEmptyKit(kit);
		
		kitRobotAgent.msgKitIsGood(kit);
		
		assertTrue(kitRobotAgent.getKits().contains(kit));
		assertTrue(kit.getState().equals(KitState.DONE));
	}
	
	@Test
	public void testMsgNeedEmptyKit()
	{
		assertTrue(kitRobotAgent.getKitRequests().size() == 0);
		
		kitRobotAgent.msgNeedEmptyKit("config");
		
		assertTrue(kitRobotAgent.getKitRequests().get("config") == 1);
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		
		kitRobotAgent.msgNeedEmptyKit("config");
		
		assertTrue(kitRobotAgent.getKitRequests().get("config") == 2);
		
		kitRobotAgent.msgNeedEmptyKit("anotherConfig");
		
		assertEquals(2, kitRobotAgent.getKitRequests().size());
		assertTrue(kitRobotAgent.getKitRequests().get("anotherConfig") == 1);
		
	}
	
	@Test
	public void testMsgKitReadyForInspection()
	{
		kitRobotAgent.msgHereIsEmptyKit(new Kit());
		
		assertTrue(kitRobotAgent.getKits().get(0).getState() == KitState.MOVING_OUT);
		
		kitRobotAgent.msgKitReadyForInspection(kitRobotAgent.getKits().get(0));
		
		assertTrue(kitRobotAgent.getKits().get(0).getState() == KitState.WAITING_FOR_INSPECTION);
	}
	
	@Test
	public void testAskConveyorForKit()
	{
		kitRobotAgent.msgNeedEmptyKit("config");
		
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		assertNotNull(kitRobotAgent.getKitRequests().get("config"));
		assertEquals(1, (int) kitRobotAgent.getKitRequests().get("config"));
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "askConveyorForKit", kitRobotAgent);
		
		assertTrue(conveyorSystem.log.containsString("msgINeedEmptyKit"));
		assertTrue(conveyorSystem.log.containsString("config"));
		assertEquals(0, kitRobotAgent.getKitRequests().size());
		assertNull(kitRobotAgent.getKitRequests().get("config"));
		assertEquals(1, kitRobotAgent.kitsAskedFromConveyor());
		
	}
	
	@Test
	public void testTellCameraInspect()
	{
		Kit kit = new Kit();
		workingStand1.setCurrentKit(kit);
		int kitsAsked = kitRobotAgent.kitsAskedFromConveyor();
		
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.KitRobotAgent", "tellCameraInspect", kitRobotAgent, kit);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit.toString()));
		assertTrue(vision.log.containsString("msgTellCameraInspect"));
		assertTrue(vision.log.containsString(kit.toString()));
		assertNull(workingStand1.getCurrentKit());
		assertNull(workingStand2.getCurrentKit());
		assertNotNull(inspectionStand.getCurrentKit());
		assertEquals(kitsAsked-1, kitRobotAgent.kitsAskedFromConveyor());
		assertEquals(new Integer(3), kit.getStand());
		
		// checking if a second kit can be put before the first one is removed
		Kit kit2 = new Kit();
		workingStand2.setCurrentKit(kit2);
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.KitRobotAgent", "tellCameraInspect", kitRobotAgent, kit2);
		
		assertFalse(controller.log.containsString(kit2.toString()));
		assertFalse(vision.log.containsString(kit2.toString()));
		assertEquals(kit, inspectionStand.getCurrentKit());
		assertEquals(kit2, workingStand2.getCurrentKit());
		assertEquals(kitsAsked-1, kitRobotAgent.kitsAskedFromConveyor());
	}
	
	@Test
	public void testPutEmptyKitToWorkingStand()
	{
		Kit kit = new Kit();
		
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.KitRobotAgent", "putEmptyKitToWorkingStand", kitRobotAgent, kit);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit.toString()));
		assertTrue(partRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(partRobot.log.containsString(kit.toString()));
		assertEquals(KitState.BEING_WORKED_ON, kit.getState());
		assertTrue(conveyorSystem.log.containsString("msgKitPickedUp"));
		assertTrue(conveyorSystem.log.containsString(kit.toString()));
		assertFalse(workingStand1.isEmpty());
		assertTrue(workingStand2.isEmpty());
		assertTrue(inspectionStand.isEmpty());
		assertEquals(new Integer(1), kit.getStand());
		
		controller.log.clear();
		partRobot.log.clear();
		conveyorSystem.log.clear();
		
		Kit kit2 = new Kit();
		
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.KitRobotAgent", "putEmptyKitToWorkingStand", kitRobotAgent, kit2);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit2.toString()));
		assertTrue(partRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(partRobot.log.containsString(kit2.toString()));
		assertEquals(KitState.BEING_WORKED_ON, kit2.getState());
		assertTrue(conveyorSystem.log.containsString("msgKitPickedUp"));
		assertTrue(conveyorSystem.log.containsString(kit2.toString()));
		assertFalse(workingStand1.isEmpty());
		assertFalse(workingStand2.isEmpty());
		assertTrue(inspectionStand.isEmpty());
		assertEquals(new Integer(2), kit2.getStand());
	}
	
	@Test
	public void testPutGoodKitToConveyor()
	{
		Kit kit = new Kit();
		
		kitRobotAgent.msgHereIsEmptyKit(kit);
		
		TestHelper.INSTANCE.callPrivateMethodWithArguments("agents.KitRobotAgent", "putGoodKitToConveyor", kitRobotAgent, kit);
	
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit.toString()));
		assertEquals(0, kitRobotAgent.getKits().size());	
		assertTrue(conveyorSystem.log.containsString("msgKitDone"));
		assertTrue(conveyorSystem.log.containsString(kit.toString()));
		assertNull(inspectionStand.getCurrentKit());
		assertEquals(new Integer(4), kit.getStand());
	}
	
	@Test
	public void testPickAndExecuteAnActionInOrder()
	{
		Kit kit = new Kit();
		Kit kit2 = new Kit();
		Kit kit3 = new Kit();
		Kit kit4 = new Kit();
		Kit kit5 = new Kit();
		
		kitRobotAgent.msgHereIsEmptyKit(kit);
		
		assertEquals(1, kitRobotAgent.getKits().size());
		
		kitRobotAgent.msgHereIsEmptyKit(kit2);
		kitRobotAgent.msgHereIsEmptyKit(kit3);
		kitRobotAgent.msgHereIsEmptyKit(kit4);
		kitRobotAgent.msgHereIsEmptyKit(kit5);
		
		assertEquals(5, kitRobotAgent.getKits().size());
		
		kit.setState(KitState.DONE);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(4, kitRobotAgent.getKits().size());
		assertFalse(kitRobotAgent.getKits().contains(kit));
		
		kit2.setState(KitState.WAITING_FOR_INSPECTION);
		workingStand1.setCurrentKit(kit2);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(vision.log.containsString("msgTellCameraInspect"));
		assertTrue(vision.log.containsString(kit2.toString()));
		
		kit2.setState(KitState.DONE);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		assertFalse(kitRobotAgent.getKits().contains(kit2));
		
		kit3.setState(KitState.MOVING_OUT);		
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(partRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(partRobot.log.containsString(kit3.toString()));
		assertEquals(KitState.BEING_WORKED_ON, kit3.getState());
		assertTrue(conveyorSystem.log.containsString("msgKitPickedUp"));
		assertTrue(conveyorSystem.log.containsString(kit3.toString()));
		assertFalse(kitRobotAgent.hasNoFreeWorkingStands());
		
		partRobot.log.clear();
		conveyorSystem.log.clear();
		
		kit4.setState(KitState.MOVING_OUT);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(partRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(partRobot.log.containsString(kit4.toString()));
		assertEquals(KitState.BEING_WORKED_ON, kit4.getState());
		assertTrue(conveyorSystem.log.containsString("msgKitPickedUp"));
		assertTrue(conveyorSystem.log.containsString(kit4.toString()));
		assertTrue(kitRobotAgent.hasNoFreeWorkingStands());
		
		partRobot.log.clear();
		conveyorSystem.log.clear();
		
		kit5.setState(KitState.EMPTY);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertFalse(partRobot.log.containsString("msgHereIsEmptyKit"));
		assertFalse(partRobot.log.containsString(kit5.toString()));
		assertEquals(KitState.EMPTY, kit5.getState());
		assertFalse(conveyorSystem.log.containsString("msgKitPickedUp"));
		assertFalse(conveyorSystem.log.containsString(kit5.toString()));
		assertTrue(kitRobotAgent.hasNoFreeWorkingStands());
		
		// this step checks if stands are emptied
		kit3.setState(KitState.WAITING_FOR_INSPECTION);
		kit4.setState(KitState.WAITING_FOR_INSPECTION);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertFalse(kitRobotAgent.hasNoFreeWorkingStands());
		
		
		// this step is to get rid of all kits
		kit3.setState(KitState.DONE);
		kit4.setState(KitState.DONE);
		kit5.setState(KitState.DONE);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertFalse(kitRobotAgent.getKits().contains(kit3));
		assertFalse(kitRobotAgent.getKits().contains(kit4));
		assertFalse(kitRobotAgent.getKits().contains(kit5));
		assertEquals(0, kitRobotAgent.getKits().size());
		assertFalse(kitRobotAgent.hasNoFreeWorkingStands());
		
		kitRobotAgent.msgNeedEmptyKit(null);
		kitRobotAgent.msgNeedEmptyKit(null);
		
		assertEquals(2, (int)kitRobotAgent.getKitRequests().get(null));
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(1, (int)kitRobotAgent.getKitRequests().get(null));
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(0, kitRobotAgent.getKitRequests().size());
	}
	
	@Test
	public void testNormativeCaseForOneKit()
	{
		Kit kit = new Kit();
		
		kitRobotAgent.msgNeedEmptyKit(null);
		
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(conveyorSystem.log.containsString("msgINeedEmptyKit"));
		assertEquals(0, kitRobotAgent.getKitRequests().size());
		
		kitRobotAgent.msgHereIsEmptyKit(kit);
		
		assertEquals(1, kitRobotAgent.getKits().size());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(KitState.BEING_WORKED_ON, kit.getState());
		assertTrue(partRobot.log.containsString("msgHereIsEmptyKit"));
		assertTrue(partRobot.log.containsString(kit.toString()));
		assertTrue(conveyorSystem.log.containsString("msgKitPickedUp"));
		assertTrue(conveyorSystem.log.containsString(kit.toString()));
		
		kitRobotAgent.msgKitReadyForInspection(kit);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(vision.log.containsString("msgTellCameraInspect"));
		assertTrue(vision.log.containsString(kit.toString()));
		
		kitRobotAgent.msgKitIsGood(kit);
		
		assertTrue(kitRobotAgent.getKits().contains(kit));
		assertTrue(kit.getState().equals(KitState.DONE));
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(0, kitRobotAgent.getKits().size());
		assertTrue(conveyorSystem.log.containsString("msgKitDone"));
		assertTrue(conveyorSystem.log.containsString(kit.toString()));
	}
	
	@Test
	public void testIfRobotAsksConveyorForKitsIfStandsAreFull()
	{

		Kit kit = new Kit();
		Kit kit2 =  new Kit();
		Kit kit3 = new Kit();
		
		kitRobotAgent.msgNeedEmptyKit("config");
		kitRobotAgent.msgNeedEmptyKit("config");
		kitRobotAgent.msgNeedEmptyKit("config");
		kitRobotAgent.msgNeedEmptyKit("config");
		
		// asking the conveyor for first kit
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(conveyorSystem.log.containsString("msgINeedEmptyKit"));
		conveyorSystem.log.clear();
		
		// expected message reception
		kitRobotAgent.msgHereIsEmptyKit(kit);
		
		// moving the kit to workingStand1
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit.toString()));
		assertFalse(workingStand1.isEmpty());
		assertEquals(1, kitRobotAgent.getKits().size());
		assertEquals(kit, kitRobotAgent.getKits().get(0));
		assertEquals(kit, workingStand1.getCurrentKit());
		
		// asking the conveyor for second kit
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(conveyorSystem.log.containsString("msgINeedEmptyKit"));
		conveyorSystem.log.clear();
		
		// expected message reception
		kitRobotAgent.msgHereIsEmptyKit(kit2);
		
		// moving the kit to workingStand2
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(controller.log.containsString("doAnim"));
		assertTrue(controller.log.containsString(kit2.toString()));
		assertFalse(workingStand2.isEmpty());
		assertEquals(2, kitRobotAgent.getKits().size());
		assertEquals(kit2, kitRobotAgent.getKits().get(1));
		assertEquals(kit2, workingStand2.getCurrentKit());
		assertTrue(kitRobotAgent.hasNoFreeWorkingStands());
		
		// asking the conveyor for third kit -- should not ask
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertFalse(conveyorSystem.log.containsString("msgINeedEmptyKit"));
	}
	
	@Test
	public void testIfRobotAsksConveyorForKitsAfter2Requests()
	{
		// testing for multiple kits
		// kitRobot shouldn't ask for more than 2 kits at a time
		// should ask again when there free stands
		kitRobotAgent.msgNeedEmptyKit("config");
		
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		assertNotNull(kitRobotAgent.getKitRequests().get("config"));
		assertEquals(1, (int) kitRobotAgent.getKitRequests().get("config"));
		
		kitRobotAgent.msgNeedEmptyKit("config");
		
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		assertNotNull(kitRobotAgent.getKitRequests().get("config"));
		assertEquals(2, (int) kitRobotAgent.getKitRequests().get("config"));
		
		kitRobotAgent.msgNeedEmptyKit("config2");
		
		assertEquals(2, kitRobotAgent.getKitRequests().size());
		assertNotNull(kitRobotAgent.getKitRequests().get("config2"));
		assertEquals(1, (int) kitRobotAgent.getKitRequests().get("config2"));
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(conveyorSystem.log.containsString("msgINeedEmptyKit"));
		assertTrue(conveyorSystem.log.containsString("config"));
		assertEquals(2, kitRobotAgent.getKitRequests().size());
		assertNotNull(kitRobotAgent.getKitRequests().get("config"));
		assertEquals(1, (int) kitRobotAgent.getKitRequests().get("config"));
		assertEquals(1, (int) kitRobotAgent.getKitRequests().get("config2"));
		assertEquals(1, kitRobotAgent.kitsAskedFromConveyor());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(conveyorSystem.log.containsString("msgINeedEmptyKit"));
		assertTrue(conveyorSystem.log.containsString("config"));
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		assertNull(kitRobotAgent.getKitRequests().get("config"));
		assertEquals(1, (int) kitRobotAgent.getKitRequests().get("config2"));
		assertEquals(2, kitRobotAgent.kitsAskedFromConveyor());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertFalse(conveyorSystem.log.containsString("config2"));
		assertEquals(1, kitRobotAgent.getKitRequests().size());
		assertEquals(1, (int) kitRobotAgent.getKitRequests().get("config2"));
		
		// removing a kit from a working stand to check if kitsAsked decreases
		// and a new request is made
		Kit kit = new Kit();
		Kit kit2 = new Kit();
		kitRobotAgent.msgHereIsEmptyKit(kit);
		kitRobotAgent.msgHereIsEmptyKit(kit2);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(kit, workingStand1.getCurrentKit());
		assertNull(workingStand2.getCurrentKit());
		assertNull(inspectionStand.getCurrentKit());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(kit2, workingStand2.getCurrentKit());
		assertEquals(kit, workingStand1.getCurrentKit());
		assertNull(inspectionStand.getCurrentKit());
		
		kitRobotAgent.msgKitReadyForInspection(kit2);
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertEquals(kit, workingStand1.getCurrentKit());
		assertNull(workingStand2.getCurrentKit());
		assertEquals(kit2, inspectionStand.getCurrentKit());
		assertEquals(1, kitRobotAgent.kitsAskedFromConveyor());
		
		TestHelper.INSTANCE.callPrivateMethod("agents.KitRobotAgent", "pickAndExecuteAnAction", kitRobotAgent);
		
		assertTrue(conveyorSystem.log.containsString("config2"));
		assertEquals(0, kitRobotAgent.getKitRequests().size());
		assertNull(kitRobotAgent.getKitRequests().get("config2"));
		assertNull(kitRobotAgent.getKitRequests().get("config"));
		
	}
	
}
