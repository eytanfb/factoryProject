package interfaces;
import NonAgent.*;

public interface Nest {

	void msgPutPart(Part part);
	
	void msgNeedParts(Part part);
	
	void msgPartRemoved();
	
	void SetVision(Vision vision);
	
	
	
}
