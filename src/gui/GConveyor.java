package gui;

import NonAgent.Kit;

// New interface to unify GConveyorIn and GConveyorOut to make it easy on Controller -- Eytan
public interface GConveyor
{
	public void DoMove(Kit kit);
}
