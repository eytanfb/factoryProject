package gui;

import java.util.ArrayList;


public class GPartBin extends GObject{


	//public ArrayList<GPart> partsInBin;
	//final int binSize =14;
	String myPartType;

	
//	public GPartBin(int x, int y, GPart myPart){
//		super(x,y,"src/resources/bin.png");
//		partsInBin = new ArrayList<GPart>();
//			for(int i=0; i< binSize;i++)
//				partsInBin.add(new GPart(myPart)); //we might want to change the myPart into an partNumber in the future 
//	}
	
//	public GPartBin(int x, int y, GPart myPart){
//	super(x,y,"src/resources/bin.png");
//	partsInBin = new ArrayList<GPart>();
//		for(int i=0; i< binSize;i++)
//			partsInBin.add(new GPart(myPart)); //we might want to change the myPart into an partNumber in the future 
//	}
	
	public GPartBin(String partType){
		super(0,0,"src/resources/bin.png");
		myPartType = partType;
//		partsInBin = new ArrayList<GPart>();
//			for(int i=0; i< binSize;i++)
//				partsInBin.add(new GPart(partType)); //we might want to change the myPart into an partNumber in the future
	}
	
	public GPartBin(int x,int y)
	{
		super(x,y,"src/resources/bin.png");
	}
	
	public GPartBin(int x,int y, String partType)
	{
		super(x,y,"src/resources/bin.png");
		myPartType = partType;
	}
	
	
//	public ArrayList<GPart> getPartsInBin()
//	{
//			return partsInBin;
//	}
	
//	public ArrayList<GPart> deliverToFeeder()
//	{
//		 ArrayList<GPart> temp = new ArrayList<GPart>();
//		 int size = partsLeft();
//		 for(int i=0;i<size;i++)
//			 temp.add(partsInBin.remove(0));
//		 
//		 return temp;	 
//	}

}
