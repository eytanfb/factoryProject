package interfaces;


import NonAgent.Kit;
import NonAgent.Part;

public interface Vision {
		public abstract void msgNestHas(Nest nest, Part part, int ownerId);
		public abstract void msgTellCameraInspect(Kit kit);
		public abstract void setPartRobot(PartRobot partrobot);
		
		public abstract void setKitRobot(KitRobot kitrobot);
		public abstract void didAnimation();
		public void setController(IVisionController controller);
}
