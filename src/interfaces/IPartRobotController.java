//By Sean Sharma
package interfaces;

import java.util.List;

import gui.GPartRobotGraphicsPanel;
import NonAgent.Kit;
import NonAgent.Part;

public interface IPartRobotController
{
	public abstract void pickUpFromNest(int nestNumber);
	
	public abstract void moveToNest(int nestNumber);

	public abstract void moveToKit(Kit kit);
	
	public abstract void dropDownPartsInKit();
	
	public abstract void animDone();
	
	public abstract void setPartRobot(PartRobot partRobot);
	public abstract void setGui(GPartRobotGraphicsPanel gui);

	public abstract void dropDownPartsToKit();
	public abstract void setServer(Server server);
	public abstract void msgGiveConfig(List<Part> config, String name, int number);
}

