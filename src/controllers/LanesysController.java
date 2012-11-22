package controllers;
import agents.*;
import gui.*;
import interfaces.*;


public class LanesysController {

	LaneAgent lane0;
	LaneAgent lane1;
	LaneAgent lane2;
	LaneAgent lane3;
	LaneAgent lane4;
	LaneAgent lane5;
	LaneAgent lane6;
	LaneAgent lane7;
	public Server server;
	
	//I realize a list would simplify this, but I don't entirely trust the ordering
	
	public void SetLane(LaneAgent lane, int i){
		
		if(i == 0){ lane0 = lane; }
		if(i == 1){ lane1 = lane; }
		if(i == 2){ lane2 = lane; }
		if(i == 3){ lane3 = lane; } 
		if(i == 4){ lane4 = lane; }
		if(i == 5){ lane5 = lane; }
		if(i == 6){ lane6 = lane; }
		if(i == 7){ lane7 = lane; }
		
	}
	
	public void MessageLane(int i){
		if(i == 0){ lane0.msgPartPutInNest(); }
		if(i == 1){ lane1.msgPartPutInNest(); }
		if(i == 2){ lane2.msgPartPutInNest(); }
		if(i == 3){ lane3.msgPartPutInNest(); } 
		if(i == 4){ lane4.msgPartPutInNest(); }
		if(i == 5){ lane5.msgPartPutInNest(); }
		if(i == 6){ lane6.msgPartPutInNest(); }
		if(i == 7){ lane7.msgPartPutInNest(); }
	}
	
	public void SetServerRefs(){
		lane0.SetServer(server);
		lane1.SetServer(server);
		lane2.SetServer(server);
		lane3.SetServer(server);
		lane4.SetServer(server);
		lane5.SetServer(server);
		lane6.SetServer(server);
		lane7.SetServer(server);
		
	}
	
}
