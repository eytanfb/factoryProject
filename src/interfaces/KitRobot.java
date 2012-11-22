package interfaces;

import java.util.List;

import NonAgent.Kit;
import NonAgent.Part;

public interface KitRobot
{
	public void msgHereIsEmptyKit(Kit kit);
	public void msgKitIsGood(Kit kit);
	public void msgNeedEmptyKit(String configuration_name);
	public void msgKitReadyForInspection(Kit kit);
	public void msgAnimDone();
}
