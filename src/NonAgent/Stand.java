package NonAgent;

import java.io.Serializable;

import gui.GKittingStand;
import gui.GObject;
import interfaces.Kittable;

public class Stand implements Kittable,Serializable
{
	private Kit kit;
	private GKittingStand gui;

	public Stand()
	{
		kit = null;
	}
	
	public Stand(GKittingStand gui)
	{
		this();
		this.gui = gui;
	}

	public Kit getCurrentKit()
	{
		return kit;
	}

	public void setCurrentKit(Kit kit)
	{
		this.kit = kit;
	}
	
	public void addKit(Kit kit)
	{
		setCurrentKit(kit);
	}
	
	public void removeKit()
	{
		setCurrentKit(null);
	}
	
	public boolean isEmpty()
	{
		return kit == null;
	}

	public GObject getGui()
	{
		return gui;
	}


}
