package agents;

import interfaces.ConveyorSystem;
import interfaces.IConveyorSystemController;
import interfaces.KitRobot;

import java.util.List;
import java.util.concurrent.Semaphore;

import controllers.ConveyorSystemController;

import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;
import NonAgent.Kit.KitState;
import agent.Agent;

public class ConveyorSystemAgent extends Agent implements ConveyorSystem
{

	private EnteringConveyor enteringConveyor;
	private ExitingConveyor exitingConveyor;
	private KitRobot kitRobot;
	private Semaphore animationSemaphore = new Semaphore(0);
	private IConveyorSystemController controller;
	
	public ConveyorSystemAgent(EnteringConveyor enteringConveyor, ExitingConveyor exitingConveyor)
	{
		this.enteringConveyor = enteringConveyor;
		this.exitingConveyor = exitingConveyor;
	}
	
	public ConveyorSystemAgent(EnteringConveyor enteringConveyor, ExitingConveyor exitingConveyor, KitRobot kitRobot, IConveyorSystemController controller)
	{
		this.enteringConveyor = enteringConveyor;
		this.exitingConveyor = exitingConveyor;
		this.kitRobot = kitRobot;
		this.controller = controller;
	}
	
	/****	MESSAGES	****/
	public void msgINeedEmptyKit(String config)
	{
		print("msgINeedEmptyKit received");
		enteringConveyor.addKit(new Kit(config));
		stateChanged();
	}
	
	public void msgKitPickedUp(Kit kit)
	{
		print("msgKitPickedUp received with kit: " + kit);
		enteringConveyor.removeKit(kit);
		if(!enteringConveyor.getKits().isEmpty())
			enteringConveyor.setCurrentKit(enteringConveyor.getKits().get(0));
		stateChanged();
	}
	
	public void msgKitDone(Kit kit)
	{
		print("msgKitDone received with kit: " + kit);
		exitingConveyor.addKit(kit);
		stateChanged();
	}
	
	// Message from gui indicating that the kit left the cell
	public void msgKitOutOfCell(Kit kit)
	{
		print("msgKitOutOfCell received with kit: " + kit);
		exitingConveyor.removeKit(kit);
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
		/*
		 if enteringConveyor.hasKits and enteringConveyor.currentKit is empty
			moveEmptyKitForRobotToPickUp(enteringConveyor.kits.get(0))
		if exitingConveyor.hasKits
			getTheGoodKitsOutOfTheCell();
		*/
		if(enteringConveyor.getCurrentKit() != null && !enteringConveyor.getCurrentKit().getState().equals(KitState.MOVING_OUT))
		{
			moveEmptyKitForRobotToPickUp(enteringConveyor.getCurrentKit());
			return true;
		}
		if(exitingConveyor.getKits().size() > 0)
		{
			getTheGoodKitOutOfTheCell(exitingConveyor.getKits().get(0));
			return true;
		}
		return false;
	}
	
	/****	ACTIONS	****/
	private void moveEmptyKitForRobotToPickUp(Kit kit)
	{
		if (!kit.getState().equals(KitState.MOVING_OUT))
		{
			print("moveEmptyKitForRobotToPickUp with kit: " + kit);
			controller.doAnim(enteringConveyor, kit);
			try
			{
				animationSemaphore.acquire();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			kit.setState(KitState.MOVING_OUT);
			kitRobot.msgHereIsEmptyKit(kit);
			stateChanged();
		}
	}
	
	private void getTheGoodKitOutOfTheCell(Kit kit)
	{
		if(!kit.getState().equals(KitState.EXITING_CELL))
		{
			print("getTheGoodKitOutOfTheCell with kit: " + kit);
			kit.setState(KitState.EXITING_CELL);
			controller.doAnim(exitingConveyor, kit);
			try
			{
				animationSemaphore.acquire();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			exitingConveyor.removeKit(kit);
		}
		stateChanged();
	}
	
	/****	OTHER	****/

	public Kit getCurrentEnteringKit()
	{
		return enteringConveyor.getCurrentKit();
	}

	public List<Kit> getExitingKits()
	{
		return exitingConveyor.getKits();
	}
	
	public List<Kit> getEnteringKits()
	{
		return enteringConveyor.getKits();
	}
	
	public void setKitRobot(KitRobot kitRobot)
	{
		this.kitRobot = kitRobot;
	}

	public EnteringConveyor getEnteringConveyor()
	{
		return enteringConveyor;
	}

	public ExitingConveyor getExitingConveyor()
	{
		return exitingConveyor;
	}

	public void setController(IConveyorSystemController controller)
	{
		this.controller = controller;
	}

	public void setKitRobot(KitRobotAgent kitRobot)
	{
		this.kitRobot = kitRobot;
	}
}
