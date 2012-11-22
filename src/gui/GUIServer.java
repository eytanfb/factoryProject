///// DONE BY MICHAEL BORKE /////
package gui;

import interfaces.Feeder;
import interfaces.Kittable;
import interfaces.Server;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;

import javax.swing.*;

import NonAgent.Conveyor;
import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;
import NonAgent.Part;
import NonAgent.Stand;
import agents.KitRobotAgent;
import agents.LaneAgent;
import controllers.ConveyorSystemController;
import controllers.GantryController;
import controllers.KitRobotController;
import controllers.LanesysController;
import controllers.PartRobotController;
import controllers.VisionController;

import java.io.*;
import java.net.*;

public class GUIServer implements Server
{
	boolean flag = false; // TODO this is a fucking hack
	// //////// SAVE FILE VARIABLES //////////
	FileOutputStream fOut;
	FileInputStream fIn;
	ObjectOutputStream obOut;
	ObjectInputStream obIn;

	// //////// NETWORKING VARIABLES //////////
	ServerSocket ss;
	//ArrayList<ServerReceiveThread> clientToServerThreads = new ArrayList<ServerReceiveThread>();
	List<Handler> Handlers = Collections.synchronizedList(new ArrayList<Handler>());
	volatile String command = new String();
	volatile ArrayList<String> agentMessages=new ArrayList<String>();

	// //////// FACTORY GUI VARIABLES //////////
	volatile ArrayList<GKit> kitTypeList;
	volatile ArrayList<GKit> activeKitList;
	volatile ArrayList<GPart> partList;
	volatile GKit chosenKit;
	volatile Integer nestNumberToTakePicture;

	// //////// FACTORY BACKEND VARIABLES //////////
	volatile KitRobotAgent kitRobotAgent;
	volatile KitRobotController kitRobotController;
	volatile PartRobotController partRobotController;
	volatile GantryController gantryController;

	// NestController nestController;
	volatile VisionController visionController;
	volatile ConveyorSystemController conveyorSystemController;
	volatile Kit activeKit;
	volatile Conveyor currentConveyor;
	volatile Kittable activeKitOrigin;
	volatile Kittable activeKitDestination;
	volatile Feeder targetFeeder;
	volatile LanesysController lanesysController;

	// //////// KIT ASSEMBLY MANAGER VARIABLES //////////
	// Stand lws, rws, is;
	// EnteringConveyor entConveyor;
	// ExitingConveyor exitConveyor;
	// Kit myKit;

	// //////// FEEDER MANAGER VARIABLES //////////
	volatile Integer targetFeederNumber;
	volatile Boolean diverterDirection;

	// ///////GANTRY ROBOT MANAGER VARIABLES///////////
	volatile Integer lastFeederAt;
	volatile String newPartType;
	volatile String dropBinType;

	// /////////LANE MANAGER VARIABLES////////////
	volatile Integer targetLaneNumber;
	volatile Integer numberOfParts;
	volatile Part partToUse;

	// /////////NEST MANAGER VARIABLES////////////
	volatile ArrayList<Integer> partRobot_targetNests=new ArrayList<Integer>();
	volatile Integer targetNestNumber;
	volatile String nestPartType;

	// //////// FACTORY AND SERVER CONSTRUCTOR //////////
	public GUIServer()
	{
		// /// DATA INITIALIZATION /////
		activeKit = new Kit();
		kitTypeList = new ArrayList<GKit>();
		activeKitList = new ArrayList<GKit>();
		partList = new ArrayList<GPart>();
		chosenKit = new GKit();
		nestNumberToTakePicture = new Integer(-1);

		// Gantry//
		lastFeederAt = new Integer(-1);
		newPartType = new String("");

		// /// SAVED DATA SETUP /////
		try
		{
			fIn = new FileInputStream("FACTORY_SAVE_FILE.sav");
			obIn = new ObjectInputStream(fIn);
			kitTypeList = (ArrayList<GKit>) obIn.readObject();
			partList = (ArrayList<GPart>) obIn.readObject();
		} catch (FileNotFoundException e)
		{
			System.out.println("GUIServer: No save file found");
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fIn.close();
				obIn.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// /// SERVER NETWORKING SETUP /////
		try
		{
			ss = new ServerSocket(63432);
			ss.setReuseAddress(true);
			System.out.println("GUIServer: Waiting for connections...");
			for (int i = 0; i < 1; i++)
			{
				// Modify the above number to test
				//Socket r = ss.accept();
				Socket s = ss.accept();
				System.out.println("GUIServer: Connection Successful");
				//clientToServerThreads.add(new ServerReceiveThread(r));
				Handlers.add(new Handler(s));
				//new Thread(clientToServerThreads.get(clientToServerThreads.size() - 1)).start();
				new Thread(Handlers.get(Handlers.size() - 1)).start();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public GUIServer(KitRobotController kitRobotController,
			ConveyorSystemController conveyorController, GantryController gantryController,
			LanesysController laneSysController, PartRobotController partRobotController,
			VisionController visionController)
	{
		this.kitRobotController = kitRobotController;
		conveyorSystemController = conveyorController;
		this.gantryController = gantryController;
		this.visionController = visionController;
		this.lanesysController = laneSysController;
		this.partRobotController = partRobotController;

//		this.kitRobotController.setServer(this);
		this.conveyorSystemController.setServer(this);
		this.gantryController.setServer(this);
		this.visionController.setServer(this);
		this.partRobotController.setServer(this);
		this.lanesysController.server = this;
		this.lanesysController.SetServerRefs();

		// /// DATA INITIALIZATION /////
		activeKit = new Kit();
		kitTypeList = new ArrayList<GKit>();
		activeKitList = new ArrayList<GKit>();
		partList = new ArrayList<GPart>();
		chosenKit = new GKit();
		nestNumberToTakePicture = new Integer(-1);

		// Gantry//
		lastFeederAt = new Integer(-1);
		newPartType = new String("");

		// /// SAVED DATA SETUP /////
		try
		{

			fIn = new FileInputStream("FACTORY_SAVE_FILE.sav");
			obIn = new ObjectInputStream(fIn);
			kitTypeList = (ArrayList<GKit>) obIn.readObject();
			partList = (ArrayList<GPart>) obIn.readObject();
		} catch (FileNotFoundException e)
		{
			System.out.println("GUIServer: No save file found");
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		// /// SERVER NETWORKING SETUP /////
		try
		{
			ss = new ServerSocket(63432);
			ss.setReuseAddress(true);
			System.out.println("GUIServer: Waiting for connections...");
			for (int i = 0; i < 20; i++)
			{
				// Modify the above number to test
				//Socket r = ss.accept();
				Socket s = ss.accept();
				System.out.println("GUIServer: Connection Successful");
				//clientToServerThreads.add(new ServerReceiveThread(r));
				Handlers.add(new Handler(s));
				//new Thread(clientToServerThreads.get(clientToServerThreads.size() - 1)).start();
				new Thread(Handlers.get(Handlers.size() - 1)).start();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// //////// AGENT METHODS //////////
	// CONVEYOR //
	public void doMove(Kit kit, Conveyor conveyor)
	{
		activeKit = kit;
		currentConveyor = conveyor;
		System.out.println("GUIServer: Kit in Server doMove " + kit);
		agentMessages.add("Conveyor_MoveKit");
		
//		for (Handler sst : Handlers)
//			sst.update("Conveyor_MoveKit");
		
	}

	// KITS//
	public void doPutKit(Kittable origin, Kittable destination, Kit kit)
	{
		activeKit = kit;
		activeKitOrigin = origin;
		activeKitDestination = destination;
		System.out.println("GUIServer: Kit in Server doPutKit " + kit);
		agentMessages.add("KitRobot_PutKit");
		
//		for (Handler sst : Handlers)
//			sst.update("KitRobot_PutKit");
	}

	// PARTS //
	public void doPickUpFromNest(int nestNumber)
	{
		//targetNestNumber = new Integer(nestNumber);
		partRobot_targetNests.add(new Integer(nestNumber));
		agentMessages.add("PartRobot_PickUpParts");
		
//		for (Handler sst : Handlers)
//			sst.update("PartRobot_PickUpParts");
	}

	public void doMoveToNest(int nestNumber)
	{
		
		//targetNestNumber = new Integer(nestNumber);
		partRobot_targetNests.add(new Integer(nestNumber));
		agentMessages.add("PartRobot_MoveToNest");
		
//		for (Handler sst : Handlers)
//			sst.update("PartRobot_MoveToNest");
	}

	public void doMoveToKit(Kit kit)
	{
		activeKit = kit;
		agentMessages.add("PartRobot_MoveToKit");
		
//		for (Handler sst : Handlers)
//			sst.update("PartRobot_MoveToKit");
	}

	public void doDropPartsInKit()
	{
		agentMessages.add("PartRobot_DropPartsInKit");
		
//		for (Handler sst : Handlers)
//			sst.update("PartRobot_DropPartsInKit");
	}

	// FEEDER //
	public void doReleaseBinToGantry(int feederNumber)
	{
		targetFeederNumber = feederNumber;
		agentMessages.add("GantryRobot_ReleaseBinToGantry");
		
//		for (Handler sst : Handlers)
//			sst.update("GantryRobot_ReleaseBinToGantry");
	}

	public void doPurgeToBin(int feederNumber)
	{
		targetFeederNumber = new Integer(feederNumber);
		agentMessages.add("Feeder_PurgeToBin");
		
		
//		for (Handler sst : Handlers)
//			sst.update("Feeder_PurgeToBin");
	}

	public void doDiverter(int feederNumber, boolean divert)
	{
		targetFeederNumber = new Integer(feederNumber);
		diverterDirection = new Boolean(divert);
		agentMessages.add("Feeder_Diverter");
		
//		for (Handler sst : Handlers)
//			sst.update("Feeder_Diverter");
	}

	public void doPlaceBin(int feederNumber)
	{
		targetFeederNumber = new Integer(feederNumber);
		agentMessages.add("Feeder_PlaceBin");
		
//		for (Handler sst : Handlers)
//			sst.update("Feeder_PlaceBin");
	}

	// LANE //
	public void doRunLane(int laneNumber, Part part, int numParts)
	{
		targetLaneNumber = new Integer(laneNumber);
		partToUse = part;
		numberOfParts = numParts;
		agentMessages.add("Lane_RunLane");
		
//		for (Handler sst : Handlers)
//			sst.update("Lane_RunLane");
	}

	// NEST //
	public void doPurgeNest(int nestNumber)
	{
		targetNestNumber = new Integer(nestNumber);
		agentMessages.add("Nest_PurgeNest");
		
//		for (Handler sst : Handlers)
//			sst.update("Nest_PurgeNest");
	}

	public void doGivePart(int nestNumber)
	{
		targetNestNumber = new Integer(nestNumber);
		agentMessages.add("Nest_GivePart");
		
//		for (Handler sst : Handlers)
//			sst.update("Nest_GivePart");
	}

	/*
	 * public void doRaiseNest(int nestNumber) { targetNestNumber = new
	 * Integer(nestNumber); for(ServerSendThread sst : serverToClientThreads)
	 * sst.update("Nest_RaiseNest"); }
	 * 
	 * public void doLowerNest(int nestNumber) { targetNestNumber = new
	 * Integer(nestNumber); for(ServerSendThread sst : serverToClientThreads)
	 * sst.update("Nest_LowerNest"); }
	 */

	// GANTRY //
	public void doDeliverBinToFeeder(int feederNumber)
	{
		targetFeederNumber = new Integer(feederNumber);
		agentMessages.add("GantryRobot_DoDeliverBinToFeeder");
		
//		for (Handler sst : Handlers)
//			sst.update("GantryRobot_DoDeliverBinToFeeder");
	}

	public void doDropBin()
	{
		agentMessages.add("GantryRobot_DoDropBin");
		
//		for (Handler sst : Handlers)
//			sst.update("GantryRobot_DoDropBin");
	}

	public void doPickUpNewBin(String partType)
	{
		newPartType = partType;
		agentMessages.add("GantryRobot_DoPickUpNewBin");
//		for (Handler sst : Handlers)
//			sst.update("GantryRobot_DoPickUpNewBin");
	}

	public void doRemoveBin(int feederNumber)
	{
		targetFeederNumber = new Integer(feederNumber);
		agentMessages.add("GantryRobot_DoRemoveBin");
//		for (Handler sst : Handlers)
//			sst.update("GantryRobot_DoRemoveBin");
	}

	public void doPickUpPurgedBin(int feederNumber)
	{
		targetFeederNumber = new Integer(feederNumber);
		agentMessages.add("GantryRobot_DoPickUpPurgedBin");
//		for (Handler sst : Handlers)
//			sst.update("GantryRobot_DoPickUpPurgedBin");
	}

	public void doDeliverBinToRefill()
	{
		agentMessages.add("GantryRobot_DoDeliverBinToRefill");
//		for (Handler sst : Handlers)
//			sst.update("GantryRobot_DoDeliverBinToRefill");
	}

	// VISION //
	public void doShoot(int nestNumber)
	{
		targetNestNumber = new Integer(nestNumber);
		agentMessages.add("Camera_Shoot");
//		for (Handler sst : Handlers)
//			sst.update("Camera_Shoot");
	}

	public void doShootKit()
	{
		agentMessages.add("Camera_ShootKit");
//		for (Handler sst : Handlers)
//			sst.update("Camera_ShootKit");
	}

	// SETTERS //
	public void setKitRobotController(KitRobotController kitRobotController)
	{
		this.kitRobotController = kitRobotController;
	}

	public void setPartRobotController(PartRobotController partRobotController)
	{
		System.out.println("GUIServer: PartRobotController is : " + partRobotController);
		this.partRobotController = partRobotController;
	}

	public void setVisionController(VisionController visionController)
	{
		this.visionController = visionController;
	}

	public void setConveyorSystemController(ConveyorSystemController conveyorSystemController)
	{
		this.conveyorSystemController = conveyorSystemController;
	}

	public void setGantryController(GantryController gantryController)
	{
		this.gantryController = gantryController;
	}

	public void setLaneAgent(LanesysController laneAgent)
	{
		this.lanesysController = laneAgent;
	}

	// //////// CLASSES //////////
	public class Handler implements Runnable
	{
		private Socket mySocket;
		private ObjectOutputStream oos;
		private ObjectInputStream ois;

		public Handler(Socket s)
		{
			mySocket = s;
			try
			{
				oos = new ObjectOutputStream(mySocket.getOutputStream());
				ois = new ObjectInputStream(mySocket.getInputStream());
			} catch (Exception e)
			{
				//System.out.println("GUIServer: Problems");
			}
		}

		public void run()
		{
			try
			{
				while (true)
				{
					if(agentMessages.size()!=0)
					{
						if(!flag)
						{
							kitRobotController.connect();
							flag = true;
						}
						String currentMessage=agentMessages.remove(0);
						
						if (currentMessage.equals("Kits"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(kitTypeList);
								h.oos.reset();
							}
						}
						else if (currentMessage.equals("FPM_Kits"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(kitTypeList);
								h.oos.reset();
							}
						}
						else if (currentMessage.equals("Parts"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(partList);
								h.oos.reset();
							}
							
						}
						else if (currentMessage.equals("Kit_Chosen"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(chosenKit);
								h.oos.reset();
							}
						}
						else if (currentMessage.equals("ChosenKit"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(chosenKit);
								h.oos.reset();
							}
							
						}
						else if (currentMessage.equals("RequestPartsList"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(partList);
								h.oos.reset();
							}

						}
						else if (currentMessage.equals("UpdatePartsList"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(partList);
								h.oos.reset();
							}
							
						}
						// KIT MANAGER currentMessageS //
						else if (currentMessage.equals("UpdateKitList"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							for(Handler h:Handlers)
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(kitTypeList);
								h.oos.reset();
							}
							
						}
						// PART ROBOT currentMessageS //
						else if (currentMessage.equals("PartRobot_MoveToNest"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							Integer temp=partRobot_targetNests.remove(0);
							for (Handler h: Handlers) 
							{
								h.oos.writeObject(currentMessage);
								h.oos.reset();
								h.oos.writeObject(temp);
								h.oos.reset();
							}
							
							
						}
						else if (currentMessage.equals("PartRobot_MoveToKit"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();

									if (activeKit.getStand() == 1)
									{
										h.oos.writeObject(new Integer(1));
										h.oos.reset();
									} else if (activeKit.getStand() == 2)
									{
										h.oos.writeObject(new Integer(2));
										h.oos.reset();
									}
								}
							}
							
//							oos.writeObject(activeKit);
//							oos.reset();
						}
						else if (currentMessage.equals("PartRobot_PickUpParts"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							Integer temp=partRobot_targetNests.remove(0);
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(temp);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("PartRobot_PickedUp"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("PartRobot_DropPartsInKit"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
						}
						// NEST currentMessageS //
						else if (currentMessage.equals("Nest_TakePicture"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(nestNumberToTakePicture);
									h.oos.reset();
								}
							}
							
						}
						// CONVEYOR currentMessageS //
						else if (currentMessage.equals("Conveyor_MoveKit"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();

									if (currentConveyor instanceof EnteringConveyor)
									{
										h.oos.writeObject("Entering");
										h.oos.reset();
									} else if (currentConveyor instanceof ExitingConveyor)
									{
										h.oos.writeObject("Exiting");
										h.oos.reset();
									}

									h.oos.writeObject(new Integer(
											activeKit.parts.size()));
									h.oos.reset();
									for (int i = 0; i < activeKit.parts.size(); i++)
									{
										h.oos.writeObject(activeKit.parts
												.get(i).partType);
										h.oos.reset();
									}
								}
							}
							
							
							
							/*
							h.oos.writeObject(activeKit);
							h.oos.reset();
							*/
						}
						// else if(currentMessage.equals("ConveyorIn_AnimationDone"))
						// {
						// System.out.println("GUIServer: Pushing Updates: "+currentMessage);
						// h.oos.writeObject(currentMessage);
						// h.oos.reset();
						// h.oos.writeObject(activeKitList);
						// h.oos.reset();
						// }
						else if (currentMessage.equals("KitRobot_MoveKitToLeftWorkingStand"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
							// h.oos.writeObject(activeKitList);
							// h.oos.reset();
						}
						else if (currentMessage.equals("ConveyorOut_KitOutFinished"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
							// h.oos.writeObject(activeKitList);
							// h.oos.reset();
						}

						// FEEDER currentMessageS //
						else if (currentMessage.equals("Feeder_ReleaseBinToGantry"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetFeederNumber);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("Feeder_Diverter"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetFeederNumber);
									h.oos.reset();
									h.oos.writeObject(diverterDirection);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("Feeder_PlaceBin"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetFeederNumber);
									h.oos.reset();
								}
							}
							
						}

						// LANE currentMessageS //
						else if (currentMessage.equals("Lane_RunLane"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetLaneNumber);
									h.oos.reset();
									String typeAddress = convertToAddress(partToUse.partType);
									h.oos.writeObject(typeAddress);
									h.oos.reset();
									h.oos.writeObject(numberOfParts);
									h.oos.reset();
								}
							}
							
						}

						// NEST currentMessageS //
						else if (currentMessage.equals("Nest_PurgeNest"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetNestNumber);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("Nest_GivePart"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetNestNumber);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("Nest_PartFed"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							//System.out.println("GUIServer: TargetNestNuber: " + targetNestNumber);
							//System.out.println("GUIServer: nestPartType: " + nestPartType);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetNestNumber);
									h.oos.reset();
									h.oos.writeObject(nestPartType);
									h.oos.reset();
								}
							}
							
						}

						/*
						 * else if (currentMessage.equals("Nest_RaiseNest")) {
						 * System.out.println("GUIServer: Pushing Updates: "+currentMessage);
						 * h.oos.writeObject(currentMessage); h.oos.reset();
						 * h.oos.writeObject(targetNestNumber); h.oos.reset(); }
						 * 
						 * else if (currentMessage.equals("Nest_LowerNest")) {
						 * System.out.println("GUIServer: Pushing Updates: "+currentMessage);
						 * h.oos.writeObject(currentMessage); h.oos.reset();
						 * h.oos.writeObject(targetNestNumber); h.oos.reset(); }
						 */

						// GANTRY currentMessageS //
						else if (currentMessage.equals("GantryRobot_ReleaseBinToGantry"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetFeederNumber);
									h.oos.reset();
								}
							}
						
						}
						else if (currentMessage.equals("GantryRobot_DoPickUpNewBin"))
						{
							synchronized (Handlers)
							{
								//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(newPartType);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("GantryRobot_DoDeliverBinToFeeder"))
						{

							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetFeederNumber);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("GantryRobot_DoDropBin"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							//System.out.println("GUIServer: DoDropBin Server sent");
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("GantryRobot_DoPickUpPurgedBin"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetFeederNumber);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("GantryRobot_DoDeliverBinToRefill"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("GantryRobot_DoRemoveBin"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									h.oos.writeObject(targetFeederNumber);
									h.oos.reset();
								}
							}
							
						}

						// CAMERA currentMessageS //
						else if (currentMessage.equals("Camera_Shoot"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
									//System.out.println("GUIServer: targetNestNumber = " + targetNestNumber);
									h.oos.writeObject(targetNestNumber);
									h.oos.reset();
								}
							}
							
						}
						else if (currentMessage.equals("Camera_ShootKit"))
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
						}

						else
						{
							//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
							
							synchronized (Handlers)
							{
								for (Handler h : Handlers)
								{
									h.oos.writeObject(currentMessage);
									h.oos.reset();
								}
							}
							
						}
					}

					
					
					//here is the message receiving part
					
					//System.out.println("############################## GUI Server command(Before) ########################");
					command = ois.readObject().toString();
					//System.out.println("############################## GUI Server command: "+command+"########################");

					//System.out.println("GUIServer: " + command);
					// FACTORY COMMANDS //
					if (command.equals("UpdatePartsList"))
					{
						partList = (ArrayList<GPart>) ois.readObject();
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(partList);
							sst.oos.reset();
						}
					}
//					else if(command.equals("DoMove"))
//					{
//						for(Handler sst: Handlers)
//						{
//							sst.oos.writeObject(ois.readObject());
//							
//						}
//					}
					else if (command.equals("Kits"))
					{
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(kitTypeList);
							sst.oos.reset();
						}
					}
					else if (command.equals("Kit_Chosen"))
					{
						//System.out.println("GUIServer: THis should be the kit name.");
						chosenKit = (GKit) ois.readObject();
						chosenKit.setPartsList();
						partRobotController.msgGiveConfig(chosenKit.partsKit,
								chosenKit.kitName, chosenKit.kitNumber);
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(chosenKit);
							sst.oos.reset();
						}
					}
					
					else if (command.equals("Kit_TakePicture_Done")) {
						visionController.animDone();
					}
					
					else if (command.equals("FPM_Kits"))
					{
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(kitTypeList);
							sst.oos.reset();
						}
					}
					else if (command.equals("RequestPartsList"))
					{
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(partList);
							//System.out.println("================= GUIServer-RequestPartList-PartList: " + partList + "Size: " + partList.size());
							sst.oos.reset();
						}
					}
					// KIT MANAGER COMMANDS //
					else if (command.equals("UpdateKitList"))
					{
						kitTypeList = (ArrayList<GKit>) ois.readObject();
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(kitTypeList);
							sst.oos.reset();
						}
					}
					// KIT ROBOT COMMANDS //
					
					// KIT ROBOT currentMessageS //
					else if (command.equals("KitRobot_PutKit"))
					{
						Kittable activeKitOrigin_temp=(Kittable) ois.readObject();
						Kittable activeKitDestination_temp=(Kittable) ois.readObject();
						Kit activeKit_temp=(Kit) ois.readObject();
						
						//System.out.println("GUIServer: Pushing Updates: " + currentMessage);
						for(Handler h:Handlers)
						{
							h.oos.writeObject(command);
							h.oos.reset();
							
							
							// Determine the origin type
							if (activeKitOrigin_temp.getGui() instanceof GConveyorIn) {
								h.oos.writeObject("ConveyorIn");
								h.oos.reset();
							}
							else if (activeKitOrigin_temp.getGui() instanceof GKittingStand) {
								if (activeKitOrigin_temp.getGui().getY() == 550)
								{
									h.oos.writeObject("rightWorkingStand");
									h.oos.reset();
								}
								else if (activeKitOrigin_temp.getGui().getY() == 350)
								{
									h.oos.writeObject("leftWorkingStand");
									h.oos.reset();
								}
								else if (activeKitOrigin_temp.getGui().getY() == 100)
								{
									h.oos.writeObject("inspectionStand");
									h.oos.reset();
								}
							}
							
							// Determine the destination type
							if (activeKitDestination_temp.getGui() instanceof GConveyorOut) {
								h.oos.writeObject("ConveyorOut");
								h.oos.reset();
							}
							else if (activeKitDestination_temp.getGui() instanceof GKittingStand) {
								if (activeKitDestination_temp.getGui().getY() == 550)
								{
									h.oos.writeObject("rightWorkingStand");
									h.oos.reset();
								}
								else if (activeKitDestination_temp.getGui().getY() == 350)
								{
									h.oos.writeObject("leftWorkingStand");
									h.oos.reset();
								}
								else if (activeKitDestination_temp.getGui().getY() == 100)
								{
									h.oos.writeObject("inspectionStand");
									h.oos.reset();
								}
							}
							// Determine the parts in kit
							h.oos.writeObject(new Integer(activeKit_temp.parts.size()));
							h.oos.reset();
							for(int i=0;i<activeKit_temp.parts.size();i++)
							{
								h.oos.writeObject(activeKit_temp.parts.get(i).partType);
								h.oos.reset();
							}
						}
					}
					else if (command.equals("KitRobot_AnimationDone"))
					{
						kitRobotController.animDone();
					}

					// CONVEYOR COMMANDS //
					else if (command.equals("ConveyorIn_AnimationDone"))
					{
						//System.out.println("GUIServer: Before Calling conveyorin animation done");
						// chosenKit = (GKit) ois.readObject();
						conveyorSystemController.animDone();
						// activeKitList = (ArrayList<GKit>)
						// ois.readObject();
						// for(ServerSendThread sst :
						// serverToClientThreads)
						// sst.update(command);
					}

					// PART ROBOT COMMANDS //
					else if (command.equals("PartRobot_PickUpParts"))
					{
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(targetNestNumber);
							sst.oos.reset();
						}
					}
//					else if (command.equals("PartRobot_PutDownToKit"))
//					{
//						for (Handler sst : Handlers)
//						{
//							sst.sst.oos.writeObject(command);
//							sst.sst.oos.reset();
//						}
//					}
					else if (command.equals("PartRobot_PickedUp"))
					{
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
						}
					}
					else if (command.equals("PartRobot_AnimationDone"))
					{
						partRobotController.animDone();
					}

					// FEEDER COMMANDS //
					else if (command.equals("Feeder_ReleaseBinToGantry_Done"))
					{
						// partRobotController.animDone();
					}
					else if (command.equals("Feeder_PlaceBin_Done"))
					{
						// partRobotController.animDone();
					}
					else if (command.equals("Feeder_ChangeDiverter_Done"))
					{
						// partRobotController.animDone();
					}

					// LANE COMMANDS //
					else if (command.equals("Lane_RunLane_Done"))
					{
						// lanesysController.MessageLane((Integer)
						// ois.readObject());
					}

					// NEST COMMANDS //
					else if (command.equals("Nest_PurgeNest_Done"))
					{
						// NestController.msgPartPutInNest()
					}
					else if (command.equals("Nest_TakePicture_Done"))
					{
						//System.out.println("GUIServer: AnimDoneVision");
						visionController.animDone();
					}
					else if (command.equals("Nest_GivePart_Done"))
					{
						// NestController.msgDidFeedPart()
					}
					else if (command.equals("Nest_PartFed"))
					{
						targetNestNumber = (Integer) ois.readObject();
						nestPartType = (String) ois.readObject();

						lanesysController.MessageLane(targetNestNumber);
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(targetNestNumber);
							sst.oos.reset();
							sst.oos.writeObject(nestPartType);
							sst.oos.reset();
						}
						// laneAgent.msgPartPutInNest((Integer)
						// ois.readObject());
					}
					// GANTRY COMMANDS //
					else if (command.equals("GantryRobot_DoPickUpNewBin"))
					{
						newPartType = (String) ois.readObject();
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(newPartType);
							sst.oos.reset();
						}
					}
					else if (command.equals("GantryRobot_DoPickUpNewBin_Done"))
					{
						gantryController.animDone();
					}
					else if (command.equals("GantryRobot_DoDeliverBinToFeeder"))
					{
						lastFeederAt = (Integer) ois.readObject();
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(targetFeederNumber);
							sst.oos.reset();
						}
					}
					else if (command.equals("GantryRobot_DoDeliverBinToFeeder_Done"))
					{
						//System.out.println("command DoDeliverBinToFeeder_Done");
						gantryController.animDone();
					}
					else if (command.equals("GantryRobot_DoDropBin"))
					{
						//System.out.println("GUIServer: DoDropBin Server received");

						dropBinType = (String) ois.readObject();
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
						}
					}
					else if (command.equals("GantryRobot_DoDropBin_Done"))
					{
						gantryController.animDone();
					}
					else if (command.equals("GantryRobot_DoPickUpPurgedBin"))
					{
						lastFeederAt = (Integer) ois.readObject();
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
							sst.oos.writeObject(targetFeederNumber);
							sst.oos.reset();
						}
					}
					else if (command.equals("GantryRobot_DoPickUpPurgedBin_Done"))
					{
						gantryController.animDone();
					}
					else if (command.equals("GantryRobot_DoDeliverBinToRefill"))
					{
						for (Handler sst : Handlers)
						{
							sst.oos.writeObject(command);
							sst.oos.reset();
						}
					}
					else if (command.equals("GantryRobot_DoDeliverBinToRefill_Done"))
					{
						gantryController.animDone();
					}

					// CLOSE COMMAND //
					else if (command.equals("close"))
					{
						try
						{
							fOut = new FileOutputStream("FACTORY_SAVE_FILE.sav");
							obOut = new ObjectOutputStream(fOut);
							obOut.writeObject(kitTypeList);
							obOut.reset();
							obOut.writeObject(partList);
							obOut.reset();
							obOut.close();
							//System.out.println("Factory saved");
						} catch (FileNotFoundException e)
						{
							e.printStackTrace();
						} catch (IOException e)
						{
							e.printStackTrace();
						}
						break;
					}
				}
				
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		
	}


		public void close()
		{
			try
			{
				mySocket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

//		@Override
//		public void actionPerformed(ActionEvent e)
//		{
//			if(Thread.interrupted())
//			{
//				
//			}
//			
//		}

	}

	String convertToAddress(String partType)
	{
		String temp = "";
		for (int i = 0; i < partList.size(); i++)
			if (partList.get(i).typeName.equals(partType))
			{

				temp = partList.get(i).imageAddress;
			}
		return temp;
	}

	// //////// MAIN //////////
	public static void main(String[] args)
	{
		GUIServer s = new GUIServer();
	}

}
