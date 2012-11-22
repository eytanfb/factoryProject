package interfaces;
import java.util.*;
import NonAgent.*;

public interface Lane {

	void msgHereAreParts(List<Part> parts);
	
	void msgNeedParts(Part part);

	void msgPartPutInNest();
	
	void SetServer(Server sever);
}
