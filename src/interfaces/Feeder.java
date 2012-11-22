package interfaces;
import NonAgent.*;

public interface Feeder {
	
	void msgHereIsBin(Bin bin);
	
	void msgNeedParts(Lane lane, Part part);
	
	void msgDoneFeeding(Lane lane);
	
}
