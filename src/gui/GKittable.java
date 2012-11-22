package gui;

public interface GKittable {

	public void addKit(GKit k);
	// This is to add a Kit instance to a kitting stand or conveyor
	public GKit giveKit();
	// This is to remove a kit from a kitting stand or conveyor and pass it to the git robot
}