package controllers;

//By Sean Sharma
import NonAgent.Kit;
import gui.GKitRobot;
import gui.GPartRobotGraphicsPanel;
import interfaces.IKitRobotController;
import interfaces.IPartRobotController;
import interfaces.IVisionController;
import interfaces.PartRobot;
import interfaces.Server;
import interfaces.Vision;

public class VisionController implements IVisionController
{
	private Server server;
	private Vision vision;
	

	public VisionController(Vision vision, Server server)
	{
		this.vision=vision;
		this.server=server;
	}

	public void doShoot(int nestNumber)
	{
		//server.doShoot(nestNumber);
		server.doShoot(nestNumber);
	}
	
	public void animDone()
	{
		vision.didAnimation();
	}
	
	public void doShootKit(){
		//gui.doShootKit();
		server.doShootKit();
	}

	public void setVision(Vision vision){
		this.vision=vision;
	}
	

	public void setServer(Server server) {
		// TODO Auto-generated method stub
		this.server=server;
	}

}
