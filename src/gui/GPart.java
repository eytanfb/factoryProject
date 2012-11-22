package gui;

import java.io.Serializable;

public class GPart extends GObject implements Serializable{
	
	
	int typeNumber;
	String typeName;
	boolean in_nest_socket;
	String typeDescription;


	public GPart(int x, int y,String imageAddress, int typeNumber, String typeName)
	{
		super (x,y,imageAddress);
		this.typeNumber=typeNumber;
		this.typeName = typeName;
	}
	
	public GPart(int x, int y,String imageAddress)
	{
		super(x,y,imageAddress);
	}
	
	public GPart(int x, int y,String imageAddress, int typeNumber, String typeName, String description)
	{
		super (x,y,imageAddress);
		this.typeNumber=typeNumber;
		this.typeName = typeName;
		this.typeDescription = description;
	}
	
	public GPart(GPart myPart)
	{
		this(myPart.getX(),myPart.getY(),myPart.getImageAddress(),myPart.getTypeNumber(), myPart.getTypeName());
	}
	
	public GPart(String partType)
	{
		this(0,0,""+partType+".png",0,partType);
	}
	
	public String getTypeName()
	{
		return typeName;
	}

	public int getTypeNumber()
	{
		return typeNumber;
	}
	
	public String getImageAddress()
	{
		return imageAddress;
	}
		
	public String getTypeDescription()
	{
		return typeDescription;
	}

	public void setInNestSocket(boolean in_nest_socket_or_not)
	{
		in_nest_socket=in_nest_socket_or_not;
	}
	
	public boolean ifInNestSocket()
	{
		return in_nest_socket;
	}

}
