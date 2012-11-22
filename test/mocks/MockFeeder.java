package mocks;

import NonAgent.Bin;
import NonAgent.Part;
import interfaces.Feeder;
import interfaces.Lane;

public class MockFeeder implements Feeder {
	public EventLog log = new EventLog();
	public String name;

	public MockFeeder(String name) {
		this.name = name;
	}

	public void msgHereIsBin(Bin bin) {
		log.add(new LoggedEvent("Received msgHereIsBin of " + bin.partType));
	}

	public void msgNeedParts(Lane lane, Part part) {
		// TODO Auto-generated method stub

	}

	public void msgDoneFeeding(Lane lane) {
		// TODO Auto-generated method stub

	}

}
