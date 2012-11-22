package gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class GKittingStand extends GObject implements GKittable{
	GKit myGKit;
	int x, y;
	public GKittingStand()	{
		super(100,100,"src/resources/kittingstand.png");
	}
	

	
	public GKittingStand(int x, int y)	{
		super(x,y, "src/resources/kittingstand.png");
		this.x = x;
		this.y = y;

	}
	
	public void paintObject(Graphics g, int offset_X, int offset_Y) {
		super.paintObject(g, offset_X,offset_Y);
		if (myGKit != null) {
			myGKit.paintObject(g, offset_X,offset_Y);
		}
		
	}
	
	public void addKit(GKit k) {
		myGKit = k;
		myGKit.setX(this.getX());
		myGKit.setY(this.getY());
	}
	
	public GKit giveKit() {
		GKit temp = new GKit();
		temp = myGKit;
		myGKit = null;
		return temp;		
	}
	
	public GKit getGKit() {
		return myGKit;
	}
	
	public boolean ifContainsKit() {
		
		if (myGKit != null) {
			return true;
		}
		return false;
	}
	
	public int getX() {		
		return x;
	}
	
	public int getY() {		
		return y;
	}
	

}