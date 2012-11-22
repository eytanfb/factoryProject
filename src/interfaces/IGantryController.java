package interfaces;

public interface IGantryController {

	public void DoPickUpNewBin(String partType);
	public void DoDeliverBinToFeeder(int feederNumber);
	public void DoDropBin();
	public void DoPickUpPurgedBin(int feederNumber);
	public void DoReleaseBinToGantry(int feederNumber);
	public void DoDeliverBinToRefill();
	public void animDone();
	public void DoPlaceBin(int feederNumber);
}
