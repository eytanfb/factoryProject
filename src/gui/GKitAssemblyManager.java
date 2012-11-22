/* by Tian(Sky) Lan & Mingyu(Heidi) Qiao
 */

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import NonAgent.Conveyor;
import NonAgent.EnteringConveyor;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;
import NonAgent.Part;
import gui.GKitAssemblyGraphicsPanel;
import interfaces.Kittable;

public class GKitAssemblyManager extends JFrame implements ActionListener
{
	private int offset_X;
	private int offset_Y;
	GKitAssemblyGraphicsPanel KAMPanel;

	ArrayList<JButton> testButtons;
	JPanel controlPanel;
	Object conveyorPassedIn;
	Kit kitPassedIn;
	Timer timer; // this is basically just to check the animation done state
	GKit tempKit;

	// //////// CLIENT NETWORK VARIABLES //////////
	/* Copied from the CLient Code Snippet */
	Socket s;
	//Socket r;
	UpdateChecker updateChecker;
	ObjectOutputStream oos;
	ObjectInputStream ois;

	public class UpdateChecker implements Runnable
	{
		private Socket mySocket;
		private ObjectInputStream ois;

		public UpdateChecker(Socket s, ObjectInputStream obis)
		{
			mySocket = s;
			ois = obis;
		}

		public void run()
		{
			// This catches the command messages sent by the server
			try
			{
				while (true)
				{
					String command = ois.readObject().toString();

					// Part Robot Commands
					if (command.equals("PartRobot_MoveToNest"))
					{
						// Integer passed in to determine which nest to move to
						KAMPanel.DoMoveToNest((Integer) ois.readObject());
					}

					else if (command.equals("PartRobot_MoveToKit"))
					{
						// Kit passed in to determine which kit to move to
//						Kit temp = (Kit) ois.readObject();
						Integer standNumber = (Integer)ois.readObject();
						// KAMPanel.rightWorkingStand.myGKit = temp.getGui();
						// KAMPanel.DoMove(KAMPanel.rightWorkingStand.myGKit);
						// System.out.println("kit's x coordinate:"+temp.getGui().getX());
						// System.out.println("kit's y coordinate:"+temp.getGui().getY());
						// 0 conveyorin, 1 left stand, 2 right stand, 3
						// inspection stand, 4 conveyor out
						KAMPanel.DoMove(standNumber);
					}

					else if (command.equals("PartRobot_PickUpParts"))
					{
						// Integer passed in as the nest number to pick from

						KAMPanel.DoPickUp((Integer) ois.readObject());
						// sendUpdate("PartRobot_PickedUp");
					}

					else if (command.equals("PartRobot_DropPartsInKit"))
					{
						// Part Robot puts parts down to kit
						KAMPanel.DoPutDownToKit();
					}

					// Nest Commands
					else if (command.equals("Camera_Shoot"))
					{
						// Take a picture of the nest according to integer
						// passed in as the nest number
						KAMPanel.DoShoot((Integer) ois.readObject());
						sendUpdate("Nest_TakePicture_Done");
					}
					else if (command.equals("Nest_PartFed"))
					{
						Integer temp = (Integer) ois.readObject();
						String temp2 = (String) ois.readObject();
						KAMPanel.nest.get(temp.intValue()).addPartToNest(temp2);
					}

					// Conveyor Commands
					else if (command.equals("Conveyor_MoveKit"))
					{
						/*
						// Perform moving kit into and away from the factory
						conveyorPassedIn = new EnteringConveyor();//(Conveyor) ois.readObject();
						kitPassedIn = new Kit();//(Kit) ois.readObject();
						System.out.println("FUCKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK"+ kitPassedIn.getConfig() +"KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
						*/
						String in_or_out=(String) ois.readObject();
						Integer number=(Integer) ois.readObject();
						Kit tempKit=new Kit();
						for(int i=0;i<number.intValue();i++)
						{
							tempKit.add(new Part((String) ois.readObject()));
						}
						
						if(in_or_out.equals("Entering"))
						{
							KAMPanel.DoKitIn(tempKit);
						}
						else if(in_or_out.equals("Exiting"))
						{
							KAMPanel.DoKitOut(tempKit);
						}
					}

					// Kit Robot Commands
					else if (command.equals("KitRobot_PutKit"))
					{
						// KitRobot moves a kit from an origin position to the
						// destination positions
						String origin;
						String destination;
						Integer numberOfParts;
						
						GKittable GOrigin;
						GKittable GDestination;
						
						origin = (String)ois.readObject();
						destination = (String)ois.readObject();
						numberOfParts = (Integer)ois.readObject();
						Kit tempKit=new Kit();
						for(int i=0;i<numberOfParts.intValue();i++)
						{
							tempKit.add(new Part((String) ois.readObject()));
						}
						
						
						if (origin.equals("ConveyorIn")) {
							GOrigin = KAMPanel.conveyorIn;
						}
						else if (origin.equals("leftWorkingStand")) {
							GOrigin = KAMPanel.leftWorkingStand;
						}
						else if (origin.equals("rightWorkingStand")) {
							GOrigin = KAMPanel.rightWorkingStand;
						}
						else  {
							GOrigin = KAMPanel.inspectionStand;
						}
						
						if (destination.equals("ConveyorOut")) {
							GDestination = KAMPanel.conveyorOut;
						}
						else if (destination.equals("leftWorkingStand")) {
							GDestination = KAMPanel.leftWorkingStand;
						}
						else if (destination.equals("rightWorkingStand")) {
							GDestination = KAMPanel.rightWorkingStand;
						}
						else {
							GDestination = KAMPanel.inspectionStand;
						}

						
						KAMPanel.doPutKit(GOrigin,GDestination,tempKit);
//						temp1 = (Kittable) ois.readObject();
//						System.out.println("+++++++++++++++++++++++++" + temp1);
//						System.out.println("+++++++++++++++++++++++++" + temp1.getGui());

//						if (temp1.getGui() instanceof GConveyorIn)
//						{
//							((GConveyorIn) temp1.getGui()).myGKit = KAMPanel.conveyorIn.myGKit;
//						}
//						else if (temp1.getGui() instanceof GKittingStand)
//						{
//							if (temp1.getGui().getY() == 550)
//							{
//								((GKittingStand) temp1.getGui()).myGKit = KAMPanel.rightWorkingStand.myGKit;
//							}
//							else if (temp1.getGui().getY() == 350)
//							{
//								((GKittingStand) temp1.getGui()).myGKit = KAMPanel.leftWorkingStand.myGKit;
//							}
//							else if (temp1.getGui().getY() == 100)
//							{
//								((GKittingStand) temp1.getGui()).myGKit = KAMPanel.inspectionStand.myGKit;
//							}
//
//						}

//						temp2 = (Kittable) ois.readObject();
//						if (temp2.getGui() instanceof GKittingStand)
//						{
//							if (temp2.getGui().getY() == 550)
//							{
//								KAMPanel.rightWorkingStand = (GKittingStand) temp2.getGui();
//								((GKittingStand) temp2.getGui()).myGKit = KAMPanel.rightWorkingStand.myGKit;
//							}
//							else if (temp2.getGui().getY() == 350)
//							{
//								KAMPanel.leftWorkingStand = (GKittingStand) temp2.getGui();
//								((GKittingStand) temp2.getGui()).myGKit = KAMPanel.leftWorkingStand.myGKit;
//							}
//							else if (temp2.getGui().getY() == 100)
//							{
//								KAMPanel.inspectionStand = (GKittingStand) temp2.getGui();
//								((GKittingStand) temp2.getGui()).myGKit = KAMPanel.inspectionStand.myGKit;
//							}
//						}
//						else if (temp2.getGui() instanceof GConveyorOut)
//						{
//							KAMPanel.conveyorOut = (GConveyorOut) temp2.getGui();
//							((GConveyorOut) temp2.getGui()).leavingGKit = KAMPanel.conveyorOut.leavingGKit;
//						}
						
						// System.out.println("++++++++++++++++++++++++++++" +
						// temp2);
						// System.out.println("+++++++++++++++++++++++++" +
						// temp2.getGui());
//						temp3 = (Kit) ois.readObject();
//						System.out.println("temp3" + temp3);
//						KAMPanel.kitRobot.DoPutKit(temp1, temp2, temp3);
					}

					else if (command.equals("Camera_ShootKit"))
					{
						KAMPanel.shootKit();
						sendUpdate("Kit_TakePicture_Done");
					}
					else
					{
						System.out.println(command);
					}
				}
			} catch (EOFException e)
			{
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					mySocket.close();
					s.close();
					//r.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	// Constructor
	public GKitAssemblyManager(int offset_X, int offset_Y)
	{

		this.offset_X = offset_X;
		this.offset_Y = offset_Y;
		// Establish Connection
		try
		{
			s = new Socket("localhost", 63432);
			System.out.println("s connected");
			//r = new Socket("localhost", 63432);
			System.out.println("r connected");
			oos = new ObjectOutputStream(s.getOutputStream());
			System.out.println("oos created");
			ois = new ObjectInputStream(s.getInputStream());
			System.out.println("Client Ready");
			updateChecker = new UpdateChecker(s, ois);
			new Thread(updateChecker).start();
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		KAMPanel = new GKitAssemblyGraphicsPanel(offset_X, offset_Y);
		setSize(500, 800);

		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));

		add(KAMPanel);
		validate();

		timer = new Timer(1000, this);
		timer.start();

	}

	public static void main(String[] args)
	{

		int offset_X = 0;
		int offset_Y = 0;
		GKitAssemblyManager window = new GKitAssemblyManager(offset_X, offset_Y);
		window.setSize(500, 800);
		window.setTitle("KitAssemblyManager");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public void sendUpdate(String messageToServer)
	{
		try
		{
			if (messageToServer.equals("UpdateActiveKitList"))
			{
				oos.writeObject(messageToServer);
				oos.reset();
				// oos.writeObject(KAMPanel.activeKitList);
				// oos.reset();
			}
			else if (messageToServer.equals("KitRobot_AnimationDone"))
			{
				oos.writeObject(messageToServer);
				oos.reset();
			}
			else if (messageToServer.equals("ConveyorIn_AnimationDone"))
			{
				System.out.println("Sending udpate to server: ConveyorIn_AnimationDone");
				oos.writeObject(messageToServer);
				oos.reset();
				// oos.writeObject(KAMPanel.conveyorIn.myGKit);
				// oos.reset();
			}
			else if (messageToServer.equals("ConveyorOut_AnimationDone"))
			{
				oos.writeObject(messageToServer);
				oos.reset();
				// oos.writeObject(KAMPanel.activeKitList);
				// oos.reset();
			}
			else if (messageToServer.equals("PartRobot_AnimationDone"))
			{
				oos.writeObject(messageToServer);
				oos.reset();
			}
			else if (messageToServer.equals("PartRobot_PickedUp"))
			{
				oos.writeObject(messageToServer);
				oos.reset();
			}
			else if (messageToServer.equals("Nest_TakePicture_Done"))
			{
				oos.writeObject(messageToServer);
				oos.reset();
			}
			else if (messageToServer.equals("Kit_TakePicture_Done")) 
			{
				oos.writeObject(messageToServer);
				oos.reset();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent ae)
	{
		
		try
		{
			oos.writeObject("Skip");
			oos.reset();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (KAMPanel.conveyorIn.conveyorInAnimationDone)
		{
			sendUpdate("ConveyorIn_AnimationDone");
			KAMPanel.conveyorIn.conveyorInAnimationDone = false;

		}
		if (KAMPanel.conveyorOut.conveyorOutAnimationDone)
		{

			sendUpdate("ConveyorOut_AnimationDone");
			KAMPanel.conveyorOut.conveyorOutAnimationDone = false;

		}
		if (KAMPanel.kitRobot.movingState == 4)
		{
			sendUpdate("KitRobot_AnimationDone");
			KAMPanel.kitRobot.movingState = 0;
		}
		if (KAMPanel.partRobot.ifArrive)
		{
			sendUpdate("PartRobot_AnimationDone");
			KAMPanel.partRobot.ifArrive = false;
		}

	}

	public GKitAssemblyGraphicsPanel getGraphicsPanel()
	{
		return KAMPanel;
	}
}
