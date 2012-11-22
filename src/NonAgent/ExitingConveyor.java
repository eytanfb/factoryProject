package NonAgent;

import gui.GConveyorOut;
import gui.GKittable;
import interfaces.Kittable;

public class ExitingConveyor extends Conveyor implements Kittable
{
	private GConveyorOut gui;

	public ExitingConveyor()
	{
		super();
	}
	
	public ExitingConveyor(GConveyorOut gui)
	{
		super();
		this.gui = gui;
	}
	
	public Kit getCurrentKit()
	{
		return getKits().get(0);
	}

	public GConveyorOut getGui()
	{
		return gui;
	}

	public void setGui(GKittable gui)
	{
		this.gui = (GConveyorOut) gui;
	}

}
