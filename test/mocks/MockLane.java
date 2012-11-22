package mocks;

import java.util.List;

import NonAgent.Part;
import interfaces.Lane;
import interfaces.Server;

public class MockLane implements Lane {

	public EventLog log = new EventLog();
	
	public MockLane() {
		// TODO Auto-generated constructor stub
	}

	public void msgHereAreParts(List<Part> parts) {
		log.add(new LoggedEvent("Received msgHereAreParts"));
		
	}

	public void msgNeedParts(Part part) {
		// TODO Auto-generated method stub

	}

	public void msgPartPutInNest() {
		// TODO Auto-generated method stub

	}

	public void SetServer(Server sever) {
		// TODO Auto-generated method stub
		
	}

}
