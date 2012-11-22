package mocks;

import NonAgent.Part;
import interfaces.Feeder;
import interfaces.Gantry;

public class MockGantry implements Gantry{

	public EventLog log = new EventLog();

	public void msgNeedPart(Feeder f, Part p) {
		log.add(new LoggedEvent("Received msgNeedParts of " + p.partType));
	}

	
	public void msgDoneWithAnim() {
		log.add(new LoggedEvent ("Received msgDoneWithAnim from gui"));
	}

}
