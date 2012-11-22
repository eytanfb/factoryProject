package interfaces;

import agents.KitRobotAgent;
import NonAgent.Kit;

public interface ConveyorSystem
{
	public void msgINeedEmptyKit(String config);
	public void msgKitPickedUp(Kit kit);
	public void msgKitDone(Kit kit);
	public Kittable getExitingConveyor();
	public Kittable getEnteringConveyor();
	public void msgAnimDone();
}
