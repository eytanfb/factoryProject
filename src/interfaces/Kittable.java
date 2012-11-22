package interfaces;

import java.io.Serializable;

import gui.GObject;
import NonAgent.Kit;

public interface Kittable
{
	public Kit getCurrentKit();
	public GObject getGui();
}
