package mocks;

import NonAgent.Part;
import interfaces.Nest;
import interfaces.Vision;

public class MockNest extends MockAgent implements Nest {
	public EventLog log = new EventLog();
	
	public MockNest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void msgPutPart(Part part) {
		log.add(new LoggedEvent("msgPutPart recevied with part: " + part));
	}

	public void msgNeedParts(Part part) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("msgNeedPart recevied with part: " + part));
	}

	public void msgPartRemoved() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("msgPartRemoved recevied."));
	}

	public void SetVision(Vision vision) {
		// TODO Auto-generated method stub
		
	}

}
