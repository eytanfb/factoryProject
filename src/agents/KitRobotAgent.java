package agents;
import gui.GKitRobot;
import interfaces.ConveyorSystem;
import interfaces.IKitRobotController;
import interfaces.KitRobot;
import interfaces.Kittable;
import interfaces.PartRobot;
import interfaces.Vision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.Iterator;

import mocks.MockServer;

//import com.sun.xml.internal.xsom.impl.scd.Iterators;

import agent.Agent;

import NonAgent.Kit;
import NonAgent.Stand;
import NonAgent.Kit.KitState;
import NonAgent.Part;

public class KitRobotAgent extends Agent implements KitRobot
{
	private List<Kit> kits;
	private ConveyorSystem conveyorSystem;
	private Vision vision;
	private PartRobot partRobot;
	private List<Stand> workingStands;
	private Stand inspectionStand;
	private GKitRobot gui;
	private Semaphore animationSemaphore = new Semaphore(0);
	private IKitRobotController controller;
	private Map<String, Integer> kitRequests;
	private int kitsAskedFromConveyor = 0;
	
	public KitRobotAgent()
	{
		kits = Collections.synchronizedList(new ArrayList<Kit>());
		workingStands = new ArrayList<Stand>();
		kitRequests = new LinkedHashMap<String, Integer>();
	}
	
	public KitRobotAgent(ConveyorSystem conveyorSystem, Vision vision, PartRobot partRobot, Stand ws1, Stand ws2, Stand inspectionStand)
	{
		this();
		this.conveyorSystem = conveyorSystem;
		this.vision = vision;
		this.partRobot = partRobot;
		workingStands.add(ws1);
		workingStands.add(ws2);
		this.inspectionStand = inspectionStand;
	}

	/****	MESSAGES	****/
	public void msgHereIsEmptyKit(Kit kit)
	{
		print("msgHereIsEmpty received with kit: " + kit);
		kits.add(kit);
		kit.setState(KitState.MOVING_OUT);
		stateChanged();
	}
	
	public void msgKitIsGood(Kit kit)
	{
		print("msgKitIsGood received with kit: " + kit);
		kit.setState(KitState.DONE);
		stateChanged();
	}
	
	public void msgNeedEmptyKit(String nameOfConfiguration)
	{
		print("msgNeedEmptyKit received");
		int numberOfRequests = 0;
		if(kitRequests.get(nameOfConfiguration) != null)
		{
			numberOfRequests = kitRequests.get(nameOfConfiguration);
		}
		kitRequests.put(nameOfConfiguration, numberOfRequests+1);
		stateChanged();
	}
	
	public void msgKitReadyForInspection(Kit kit)
	{
		print("msgKitReadyForInspection received with kit: " + kit);
		kit.setState(KitState.WAITING_FOR_INSPECTION);
		stateChanged();
	}
	
	public void msgAnimDone()
	{
		animationSemaphore.release();
	}
	
	
	/****	SCHEDULER	****/
	
	@Override
	protected boolean pickAndExecuteAnAction()
	{
//		foreach kit in kits:
//			if kit.status is done
//				putGoodKitToConveyor(kit);
//			if kit.status is waiting_for_inspection
//				tellCameraToInspect(kit);
//			if kit.status is empty && thereIsAFreeStand
//				putEmptyKitToWorkingStand(kit);
//			if emptyKitRequests > 0 && thereAreNoEmptyKitsOnTheConveyorAlready
//				askConveyorForKit();
		
		if (!kits.isEmpty())
		{
			synchronized (kits)
			{
				for (Kit kit : kits)
				{
					if (kit.getState().equals(KitState.DONE))
					{
						putGoodKitToConveyor(kit);
						return true;
					}
				}
			}
			synchronized (kits)
			{
				for (Kit kit : kits)
				{
					if (kit.getState().equals(KitState.WAITING_FOR_INSPECTION))
					{
						tellCameraInspect(kit);
						return true;
					}
				}
			}
			synchronized (kits)
			{
				for (Kit kit : kits)
				{
					if (kit.getState().equals(KitState.MOVING_OUT) && !hasNoFreeWorkingStands())
					{
						putEmptyKitToWorkingStand(kit);
						return true;
					}
				}
			}
		}
		if(kitRequests.size() > 0 && !hasNoFreeWorkingStands() && kitsAskedFromConveyor < 2)
		{
			askConveyorForKit();
			return true;
		}
		
		return false;
	}

	/****	ACTIONS	****/
	private void askConveyorForKit()
	{
		print("askConveyorForKit");
		String config = nextConfig();
		conveyorSystem.msgINeedEmptyKit(config);
		kitsAskedFromConveyor++;
		stateChanged();
	}
	
	private void tellCameraInspect(Kit kit)
	{
		print("tellCameraInspect " + kit);
		if(inspectionStand.isEmpty())
		{
			Stand stand = getStandWithKit(kit);
			controller.doAnim(stand, inspectionStand, kit);
			try
			{
				animationSemaphore.acquire();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			vision.msgTellCameraInspect(kit);
			stand.removeKit();
			inspectionStand.addKit(kit);
			kit.setStand(3);
			kit.setState(KitState.BEING_INSPECTED);
			kitsAskedFromConveyor--;
			stateChanged();
		}
	}
	
	private void putEmptyKitToWorkingStand(Kit kit)
	{
		print("putEmptyKitToWorkingStand " + kit);
		
		print("Printing: " + conveyorSystem.getEnteringConveyor() + " Table: " + getFreeTable() + " Kit: " + kit);
		controller.doAnim(conveyorSystem.getEnteringConveyor(), getFreeTable(), kit);
		try
		{
			animationSemaphore.acquire();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		kit.setState(KitState.BEING_WORKED_ON);		
		addKitToFreeTable(kit);		
		partRobot.msgHereIsEmptyKit(kit);
		conveyorSystem.msgKitPickedUp(kit);
		stateChanged();
	}
	
	private void putGoodKitToConveyor(Kit kit)
	{
		print("putGoodKitToConveyor " + kit);
		//fcs.msgKitDone();
//		gui.DoPutKit(inspectionStand, conveyorSystem.getExitingConveyor(), kit);
		controller.doAnim(inspectionStand, conveyorSystem.getExitingConveyor(), kit);
		try
		{
			animationSemaphore.acquire();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		kit.setStand(4);
		conveyorSystem.msgKitDone(kit);
		inspectionStand.removeKit();
		kits.remove(kit);
	}

	
	/****	OTHER	****/

	public List<Kit> getKits()
	{
		return kits;
	}
	
	public boolean hasNoKits()
	{
		return kits.isEmpty();
	}
	public GKitRobot getGui()
	{
		return gui;
	}

	public boolean hasNoFreeWorkingStands()
	{
		for(Stand stand: workingStands)
		{
			if(stand.isEmpty())
				return false;
		}
		return true;
	}
	
	private void addKitToFreeTable(Kit kit)
	{
		for(Stand stand: workingStands)
		{
			if(stand.isEmpty())
			{
				stand.addKit(kit);
				kit.setStand(workingStands.indexOf(stand)+1);
				return;
			}
		}
	}
	
	private Kittable getFreeTable()
	{
		Kittable kittable = null;
		
		for(Stand stand: workingStands)
		{
			if(stand.isEmpty())
				return stand;
		}
		return kittable;
	}
	
	private Stand getStandWithKit(Kit kit)
	{
		for(Stand stand: workingStands)
		{
			if((stand.getCurrentKit() != null) && stand.getCurrentKit().equals(kit))
			{
				return stand;
			}
		}
		return null;
	}
	
	public void setGui(GKitRobot gui)
	{
		this.gui = gui;
	}

	public void setController(IKitRobotController controller)
	{
		this.controller = controller;
	}

	public Map<String, Integer> getKitRequests()
	{
		return kitRequests;
	}
	
	private String nextConfig()
	{
		for(Iterator<String> it = kitRequests.keySet().iterator(); it.hasNext();)
		{
			String config = it.next();
			if(kitRequests.containsKey(config))
			{
				int value = kitRequests.get(config);
				kitRequests.put(config, value-1);
				if(kitRequests.get(config) == 0)
				{
					kitRequests.remove(config);
				}
				return config;
			}
		}
		return null;
	}

	public int kitsAskedFromConveyor()
	{
		return kitsAskedFromConveyor;
	}
}

