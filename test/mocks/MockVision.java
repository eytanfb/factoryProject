package mocks;

import NonAgent.Kit;
import NonAgent.Part;
import interfaces.IVisionController;
import interfaces.KitRobot;
import interfaces.Nest;
//import interfaces.KitRobot;
//import interfaces.Nest;
import interfaces.PartRobot;
import interfaces.Vision;

public class MockVision extends MockAgent implements Vision{
	public EventLog log = new EventLog();
	
	public MockVision(String name){
		super(name);
	}

	public void msgNestHas(Nest nest, Part part, int Id) {
		log.add(new LoggedEvent(
				"Received message msgNestHas from Nest "+nest.toString()+" with part"+part.toString()+"."));
	}
	

	public void msgTellCameraInspect(Kit kit) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Received message msgTellCameraInspect "+kit.toString()));
	}

	public void setPartRobot(PartRobot partrobot) {
		// TODO Auto-generated method stub
		
	}
/*
	@Override
	public void setKitRobot(KitRobot kitrobot) {
		// TODO Auto-generated method stub
		
	}
*/

	public void setKitRobot(KitRobot kitrobot) {
		// TODO Auto-generated method stub
		
	}

	public void didAnimation() {
		// TODO Auto-generated method stub
		
	}

	public void setController(IVisionController controller) {
		// TODO Auto-generated method stub
		
	}
}
