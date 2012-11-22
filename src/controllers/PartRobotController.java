//By Sean Sharma
package controllers;

import java.util.List;

import NonAgent.Kit;
import NonAgent.Part;
import gui.GKitRobot;
import gui.GPartRobotGraphicsPanel;
import interfaces.IKitRobotController;
import interfaces.IPartRobotController;
import interfaces.PartRobot;
import interfaces.Server;

public class PartRobotController implements IPartRobotController
{
	private PartRobot partRobot;
	private GPartRobotGraphicsPanel gui;
	private Server server;

	public PartRobotController(PartRobot partRobot, Server server)
	{
		//this.partRobot=partRobot;
		//this.gui = gui;
		this.partRobot=partRobot;
		this.server=server;
	}
//add 
	public void pickUpFromNest(int nestNumber)
	{
		//gui.doPickUpFromNest(nestNumber);
		server.doPickUpFromNest(nestNumber);
	}
	
	public void moveToNest(int nestNumber)
	{
		//gui.doMoveToNest(nestNumber);
		server.doMoveToNest(nestNumber);
	}

	public void moveToKit(Kit kit){
		//gui.doMoveToKit();
		server.doMoveToKit(kit);
	}
	
	public void dropDownPartsInKit(){
		//gui.moveToKit();
		//gui.doDropDownPartsToKit();
		server.doDropPartsInKit();
	}
	
	public void animDone()
	{
		partRobot.didAnimation();
	}
	

	public void setPartRobot(PartRobot partRobot){
		this.partRobot=partRobot;
	}
	public void setGui(GPartRobotGraphicsPanel gui){
		this.gui=gui;
	}

	public void dropDownPartsToKit() {
		// TODO Auto-generated method stub
		server.doDropPartsInKit();
	}

	public void setServer(Server server) {
		// TODO Auto-generated method stub
		this.server=server;
	}
	public void msgGiveConfig(List<Part> config, String name, int number)
	{
		this.partRobot.msgGiveConfig(config, name, number);
	}
	

}