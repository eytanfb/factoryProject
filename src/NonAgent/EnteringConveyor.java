package NonAgent;

import gui.GConveyorIn;
import gui.GKittable;
import interfaces.Kittable;

public class EnteringConveyor extends Conveyor implements Kittable
{
	private Kit currentKit;
	private GConveyorIn gui;
	
	public EnteringConveyor()
	{
		currentKit = null;
	}
	
	public EnteringConveyor(GConveyorIn gui)
	{
		this.gui = gui;
	}

	public Kit getCurrentKit()
	{
		return currentKit;
	}
	
	public void setCurrentKit(Kit currentKit)
	{
		this.currentKit = currentKit;
	}
	
	public void addKit(Kit kit)
	{
		if(getKits().isEmpty())
			currentKit = kit;
		super.addKit(kit);
	}
	
	public void removeKit(Kit kit)
	{
		super.removeKit(kit);
		if(getKits().isEmpty())
			currentKit = null;
		else
			currentKit = getKits().get(0);
	}

	public GConveyorIn getGui()
	{
		return gui;
	}

	public void setGui(GKittable gui)
	{
		this.gui = (GConveyorIn) gui;
	}

}
