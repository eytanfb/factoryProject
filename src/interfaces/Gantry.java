package interfaces;
import NonAgent.Part;

public interface Gantry {
	public abstract void msgNeedPart(Feeder f, Part p);
	public abstract void msgDoneWithAnim();
	
}
