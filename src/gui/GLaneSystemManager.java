package gui;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class GLaneSystemManager extends JFrame implements ActionListener, ChangeListener
{
	
	private int offset_X;	//offset to shift the position of the animation
	private int offset_Y;
	private GLaneSystemGraphicsPanel graphicsPanel;
	private JPanel controlPanel;
	//Current unit number
	private int Unit_Number=0;
		
	//frame rate control parameters
	private final int MIN_FRAME_RATE=30;
	private final int MAX_FRAME_RATE=300;
	private final int INITIAL_FRAME_RATE=30;
		
	//all the components in the button panel
	private JSlider frameRateSlider;
	private JComboBox unitCombox;
	private String[] comboBoxOptions={"Unit#1","Unit#2","Unit#3","Unit#4"};
	
	private JButton purgeToBin;
	private JButton placeBin;
	private JButton turnDiverter_Up;
	private JButton turnDiverter_Down;
	private JButton runLane1;
	private JButton runLane2;
	private JButton purgeNest1;
	private JButton purgeNest2;
	private JButton takePic1;
	private JButton takePic2;
	private JButton givePart1;
	private JButton givePart2;
	//////////CLIENT NETWORK VARIABLES //////////
	private Socket s;
	//private Socket r;
	private UpdateChecker updateChecker;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public GLaneSystemManager(int offset_X, int offset_Y, boolean in_lane_manager)
	{
		this.offset_X=offset_X;
		this.offset_Y=offset_Y;
		try 
		{
			s = new Socket("localhost", 63432);
			System.out.println( "s connected");
			//r = new Socket("localhost", 63432);
			System.out.println( "r connected");
			oos = new ObjectOutputStream(s.getOutputStream());
			System.out.println( "oos created");
			ois = new ObjectInputStream(s.getInputStream());
			System.out.println("Client Ready");	
			updateChecker = new UpdateChecker(s, ois);
			new Thread(updateChecker).start();		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(in_lane_manager)
			graphicsPanel = new GLaneSystemGraphicsPanel(this, offset_X, offset_Y);
		else
			graphicsPanel = new GLaneSystemGraphicsPanel(null, offset_X, offset_Y);
		
		setLayout(new BoxLayout(this.getContentPane(),BoxLayout.X_AXIS));
		
		validate();
		controlPanel=new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
		frameRateSlider=new JSlider(JSlider.HORIZONTAL,MIN_FRAME_RATE,MAX_FRAME_RATE,INITIAL_FRAME_RATE);
		unitCombox=new JComboBox(comboBoxOptions);
		purgeToBin=new JButton("Purge Bin");
		placeBin=new JButton("Place Bin");
		turnDiverter_Up=new JButton("Turn Diverter_Up");
		turnDiverter_Down=new JButton("Turn Diverter_Down");
		runLane1=new JButton("Run Lane #1");
		runLane2=new JButton("Run Lane #2");
		purgeNest1=new JButton("Purge Nest #1");
		purgeNest2=new JButton("Purge Nest #2");
		takePic1=new JButton("Take Picture #1");
		takePic2=new JButton("Take Picture #2");
		givePart1=new JButton("Give Part #1");
		givePart2=new JButton("Give Part #2");
		initialize();
		
		
		
		//add everything to the swing panel
		add(graphicsPanel);
		//add(controlPanel);	
				
	}
	
	public void initialize()
	{
		//Determine the layout and add listeners to the buttons
		unitCombox.addActionListener(this);
		purgeToBin.addActionListener(this);
		placeBin.addActionListener(this);
		turnDiverter_Up.addActionListener(this);
		turnDiverter_Down.addActionListener(this);
		runLane1.addActionListener(this);
		runLane2.addActionListener(this);
		purgeNest1.addActionListener(this);
		purgeNest2.addActionListener(this);
		takePic1.addActionListener(this);
		takePic2.addActionListener(this);
		givePart1.addActionListener(this);
		givePart2.addActionListener(this);
		//set up the JSlider
		//frameRateSlider.setMajorTickSpacing(90);
		//frameRateSlider.setPaintTicks(true);
		//frameRateSlider.setPaintLabels(true);
		//frameRateSlider.addChangeListener(this);
		Font font = new Font("Serif", Font.BOLD, 18);
		frameRateSlider.setFont(font);
		frameRateSlider.addChangeListener(this);

		
		//add everything to the button panel
		//gbc.fill=GridBagConstraints.HORIZONTAL;
		controlPanel.add(frameRateSlider);
		controlPanel.add(unitCombox);
		controlPanel.add(purgeToBin);
		controlPanel.add(placeBin);
		controlPanel.add(turnDiverter_Up);
		controlPanel.add(turnDiverter_Down);
		controlPanel.add(runLane1);
		controlPanel.add(runLane2);
		controlPanel.add(purgeNest1);
		controlPanel.add(purgeNest2);
		controlPanel.add(takePic1);
		controlPanel.add(takePic2);
		controlPanel.add(givePart1);
		controlPanel.add(givePart2);
	}
	
	public ObjectOutputStream get_oos()
	{
		return oos;
	}
	
public static void main(String[] args){
		
	int offset_X=0;
	int offset_Y=0;
		GLaneSystemManager window = new GLaneSystemManager(offset_X, offset_Y,false);
		window.setSize(800, 800);
		window.setTitle("GLaneSystemManager");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	
	
	//the inner class that handles the communication
	class UpdateChecker implements Runnable
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
			try
			{
				while(true)
				{
					String command = ois.readObject().toString();

					//checks what command is sent from the server
					if(command.equals("GantryRobot_ReleaseBinToGantry"))
					{
						Integer feeder_index=(Integer)ois.readObject();
						graphicsPanel.getSystem().DoPurgeToBin(feeder_index);
						oos.writeObject((Object) new String("GantryRobot_ReleaseBinToGantry_Done"));
						oos.reset();
					}
					else if(command.equals("Feeder_PlaceBin"))
					{
						Integer feeder_index=(Integer)ois.readObject();
						graphicsPanel.getSystem().DoPlaceBin(feeder_index);
						oos.writeObject((Object) new String("Feeder_PlaceBin_Done"));
						oos.reset();
					}
					else if(command.equals("Feeder_ChangeDiverter"))
					{
						Integer feeder_index=(Integer)ois.readObject();
						Boolean up_or_down=(Boolean)ois.readObject();
						graphicsPanel.DoDiverter(feeder_index, up_or_down.booleanValue());
						oos.writeObject((Object) new String("Feeder_ChangeDiverter_Done"));
						oos.reset();
					}
					else if(command.equals("Lane_RunLane"))
					{
						Integer lane_index=(Integer)ois.readObject();
						String part_type=(String) ois.readObject();
						Integer num=(Integer) ois.readObject();
						graphicsPanel.feedLane(lane_index.intValue(), part_type, num.intValue());
						oos.writeObject((Object) new String("Lane_RunLane_Done"));
						oos.reset();
						oos.writeObject((Object) lane_index);
						oos.reset();
					}
					else if(command.equals("Nest_PurgeNest"))
					{
						Integer nest_index=(Integer)ois.readObject();
						graphicsPanel.getSystem().DoPurgeNest(nest_index);
						oos.writeObject((Object) new String("Nest_PurgeNest_Done"));
						oos.reset();
					}
					else if(command.equals("Camera_Shoot"))
					{
						Integer nest_index=(Integer) ois.readObject();
						int remainder = nest_index%2;
						if(remainder==0){
//							graphicsPanel.getSystem().getLanes().get(nest_index.intValue()).get_nest().takePicture();
//							graphicsPanel.getSystem().getLanes().get(nest_index.intValue()+1).get_nest().takePicture();
							
						}
						else{
//							graphicsPanel.getSystem().getLanes().get(nest_index.intValue()).get_nest().takePicture();
//							graphicsPanel.getSystem().getLanes().get(nest_index.intValue()-1).get_nest().takePicture();
							
						}
						//graphicsPanel.getSystem().getLanes().get(nest_index.intValue()).get_nest().takePicture();
					}
					else if(command.equals("Nest_GivePart"))
					{
						int nest_index=(Integer) ois.readObject();
						GPart temp=graphicsPanel.getSystem().DoTakePart(nest_index);
						oos.writeObject((Object) new String("Nest_GivePart_Done"));
						oos.reset();
					}
				}
			} 
			catch(EOFException e) 
			{
			} 
			catch(IOException e) 
			{
				e.printStackTrace();
			} 
			catch(Exception e) 
			{
				e.printStackTrace();
			} 
			finally 
			{
				try 
				{
					mySocket.close();
					s.close();
					//r.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	//the listener for the JSlider
	public void stateChanged(ChangeEvent ce)
	{
		JSlider source = (JSlider)ce.getSource();
		int fps=(int)source.getValue();
		graphicsPanel.frameRateChanged(fps);
	}

	//no longer in use since the agents would send the signals, not the buttons
	public void actionPerformed(ActionEvent ae)
	{
		/*
		if(ae.getSource()==unitCombox)
		{
			if(unitCombox.getSelectedItem().equals("Unit#1"))
			{
				Unit_Number=0;
			}
			else if(unitCombox.getSelectedItem().equals("Unit#2"))
			{
				Unit_Number=1;
			}
			else if(unitCombox.getSelectedItem().equals("Unit#3"))
			{
				Unit_Number=2;
			}
			else if(unitCombox.getSelectedItem().equals("Unit#4"))
			{
				Unit_Number=3;
			}
		}
		else if(ae.getSource()==purgeToBin)
		{
			try
			{
				oos.writeObject((Object) new String("Feeder_DoReleaseBinToGantry"));
				oos.reset();
				oos.writeObject((Object)new Integer(Unit_Number));
				oos.reset();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(ae.getSource()==placeBin)
		{
			try
			{
				oos.writeObject((Object) new String("Feeder_PlaceBin"));
				oos.reset();
				oos.writeObject((Object)new Integer(Unit_Number));
				oos.reset();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(ae.getSource()==turnDiverter_Up)
		{
			try
			{
				oos.writeObject((Object) new String("Feeder_Diverter"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number));
				oos.reset();
				oos.writeObject((Object) new Boolean(true));
				oos.reset();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(ae.getSource()==turnDiverter_Down)
		{
			try
			{
				oos.writeObject((Object) new String("Feeder_DoDiverter"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number));
				oos.reset();
				oos.writeObject((Object) new Boolean(false));
				oos.reset();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(ae.getSource()==runLane1)
		{
			try
			{
				oos.writeObject((Object) new String("Lane_DoRunLane"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number*2));
				oos.reset();
				oos.writeObject((Object) new String("enemy1"));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
		}
		else if(ae.getSource()==runLane2)
		{
			try
			{
				oos.writeObject((Object) new String("Lane_DoRunLane"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number*2+1));
				oos.reset();
				oos.writeObject((Object) new String("enemy1"));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(ae.getSource()==purgeNest1)
		{
			try
			{
				oos.writeObject(new String("Nest_DoPurgeNest"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number*2));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(ae.getSource()==purgeNest2)
		{
			try
			{
				oos.writeObject(new String("Nest_DoPurgeNest"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number*2+1));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(ae.getSource()==takePic1)
		{
			try
			{
				oos.writeObject(new String("Nest_DoTakePicture"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number*2));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(ae.getSource()==takePic2)
		{
			try
			{
				oos.writeObject(new String("Nest_DoTakePicture"));
				oos.reset();
				oos.writeObject((Object) new Integer(Unit_Number*2+1));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(ae.getSource()==givePart1)
		{
			try
			{
				oos.writeObject(new String("Nest_DoGivePart"));
				oos.reset();
				oos.writeObject(new Integer(Unit_Number*2));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(ae.getSource()==givePart2)
		{
			try
			{
				oos.writeObject(new String("Nest_DoGivePart"));
				oos.reset();
				oos.writeObject(new Integer(Unit_Number*2+1));
				oos.reset();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		*/
	}
	
	public GLaneSystemGraphicsPanel getGraphicsPanel(){
		return graphicsPanel;
	}
	
}
