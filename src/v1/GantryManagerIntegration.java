package v1;

import interfaces.Feeder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gui.GUIServer;
import controllers.GantryController;
import NonAgent.Part;
import agents.FeederAgent;
import agents.GantryAgent;

public class GantryManagerIntegration{
	public static GantryAgent gantry;
	public static FeederAgent feeder1;
	public FeederAgent feeder2;
	public FeederAgent feeder3;
	public FeederAgent feeder4;
	public List<Feeder> feeders = new ArrayList<Feeder>();
	public GantryController gController;
	public GUIServer server;
	public static Part p1;
	public static Part p2;
	
	
	public GantryManagerIntegration(){
		System.out.println("Creating test");
		gantry = new GantryAgent("gantry");
		feeder1 = new FeederAgent(gantry);
		feeders.add(feeder1);

		gController = new GantryController(gantry);

		server = new GUIServer();
		p1 = new Part("p1");
		p2 = new Part("p2");
		server.setGantryController(gController);
		gantry.setFeeders(feeders);
		gantry.setController(gController);
		
		gController.setServer(server);
		System.out.println("finish constructor");
		
	}
	
	public static void main(String[] args){
		GantryManagerIntegration gmi = new GantryManagerIntegration();
		gantry.startThread();
		feeder1.startThread();
		
		System.out.println("Started threads for test");
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				System.out.println("Calling msgNeedPart");
				gantry.msgNeedPart(feeder1, p1);
			}
		}, 3000);
		
		
		
		new Timer().schedule(new TimerTask(){
			public void run()
			{
				System.out.println("Calling msgNeedPart");
				gantry.msgNeedPart(feeder1, p2);
			}
		}, 10000);


	}
	
}