package interfaces;

import java.util.List;


import NonAgent.Kit;
import NonAgent.Part;

public interface PartRobot {
		public abstract void msgGiveConfig(List<Part> config, String name, int number);

		public abstract void msgHereIsEmptyKit(Kit kit);
		public abstract void msgPickUpParts(Part part, Nest nest, int nestNumber);
		//public abstract void setN1(Nest n1);

		//public abstract void setN2(Nest n2);

		public abstract void setKitRobot(KitRobot kitrobot);

		public abstract void setVision(Vision vision);
		public abstract void setNests(List<Nest> nests);
		public abstract void didAnimation();
		public void setController(IPartRobotController controller);
}
