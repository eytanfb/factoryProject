package mocks;

import java.util.List;

import NonAgent.Kit;
import NonAgent.Part;
import interfaces.KitRobot;

public class MockKitRobot extends MockAgent implements KitRobot
{
	public EventLog log;

	public MockKitRobot(String name)
	{
		super(name);
		log = new EventLog();
	}

	public void msgHereIsEmptyKit(Kit kit)
	{
		log.add(new LoggedEvent("msgHereIsEmptyKit recevied with kit: " + kit));
	}

	public void msgKitIsGood(Kit kit)
	{
		log.add(new LoggedEvent("msgKitIsGood recevied with kit: " + kit));
	}

	public void msgNeedEmptyKit(String name)
	{
		log.add(new LoggedEvent("msgNeedEmptyKit recevied with name "+name+"."));
		
	}

	public void msgKitReadyForInspection(Kit kit)
	{
		log.add(new LoggedEvent("msgKitReadyForInspection recevied with kit: " + kit));	
	}

	public void msgAnimDone()
	{
		log.add(new LoggedEvent("msgAnimDone received"));
	}

	

}
