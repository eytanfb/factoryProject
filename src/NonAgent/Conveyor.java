package NonAgent;

import gui.GConveyor;
import gui.GConveyorIn;
import gui.GKittable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Conveyor implements Serializable
{
	private List<Kit> kits;
	
	public Conveyor()
	{
		kits = new ArrayList<Kit>();
	}

	public void addKit(Kit kit)
	{
		kits.add(kit);
	}
	
	public void removeKit(Kit kit)
	{
		kits.remove(kit);
	}

	public List<Kit> getKits()
	{
		return kits;
	}
	
	public abstract GConveyor getGui();

}
