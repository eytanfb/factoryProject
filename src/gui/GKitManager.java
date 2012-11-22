package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class GKitManager extends JFrame implements ActionListener
{

	// //////// CLASS VARIABLES //////////

	// CLIENT NETWORK VARIABLES
	Socket s;
	//Socket r;
	UpdateChecker updateChecker;
	ObjectOutputStream oos;
	ObjectInputStream ois;

	// FACTORY VARIABLES
	ArrayList<GPart> partList;	//to store all the parts in the kit we are currently making
	ArrayList<GKit> kitTypeList;	//to store all the different kits we have created since histroy
	GKit chosenKit;		//refer to the kit we choose to send to the factory

	// GKITMANAGER VARIABLES
	ArrayList<JComboBox> cbxArrayList; // a container to hold all the comboboxes
										// used in the manager
	ArrayList<JLabel> jlabelList;
	JButton setPartsButton, sendButton;
	JTextArea jta;
	JComboBox partABox, partBBox, partCBox, partDBox, partEBox, partFBox, partGBox, partHBox;
	JLabel partALabel, partBLabel, partCLabel, partDLabel, partELabel, partFLabel, partGLabel,
			partHLabel;

	// NEW SWING VARIABLES
	JComboBox partsBox, howManyBox;
	JButton selectButton, send2ServerButton;
	JTextArea textArea;
	StringBuffer TAString;
	JTextField configNametf, configNumtf;
	int partsInKit = 0;

	// //////// CONSTRUCTOR //////////
	public GKitManager()
	{
		initializeFactory(); // initializes Factory Variables
		initializeNewSwing();
		connectToServer(); // sets up sending and receiving sockets
		requestGPartsList(); // requests for the GParts list to build kit
	}

	// //////// MAIN //////////
	public static void main(String[] args)
	{
		GKitManager app = new GKitManager();
		app.setSize(400, 400);
		app.setTitle("Kit Manager");
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
	}
	
	// //////// INITIALIZES FACTORY VARIABLES //////////
	public void initializeFactory()
	{
		chosenKit = new GKit();
	}

	// //////// CREATE NEW SWING PANELS //////////
	public void initializeNewSwing()
	{
		// CREATE THE PANEL TO WORK ON//
		JPanel g = new JPanel();
		g.setLayout(new GridLayout(1, 2));
		add(g);

		// LEFT SIDE PANEL //
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		g.add(p);

		JLabel partsLabel = new JLabel("PARTS LIST");
		gbc.gridx = 0;
		gbc.gridy = 0;

		p.add(partsLabel, gbc);
		partsBox = new JComboBox();
		partsBox.setMaximumSize(new Dimension(150, 20));
		partsBox.setMinimumSize(new Dimension(100, 20));
		partsBox.setPreferredSize(new Dimension(100, 20));

		gbc.gridx = 0;
		gbc.gridy = 1;
		p.add(partsBox, gbc);

		JLabel howManyLabel = new JLabel("NUMBER OF SELETED PART");
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.weighty = 0;
		p.add(howManyLabel, gbc);
		howManyBox = new JComboBox();
		howManyBox.setMaximumSize(new Dimension(150, 20));
		howManyBox.setMinimumSize(new Dimension(100, 20));
		howManyBox.setPreferredSize(new Dimension(100, 20));
		for (int i = 0; i < 9; i++)
		{
			howManyBox.addItem(i);
		}
		gbc.gridx = 0;
		gbc.gridy = 3;
		p.add(howManyBox, gbc);

		JLabel configName = new JLabel("CONFIGURATION NAME");
		gbc.gridx = 0;
		gbc.gridy = 4;
		p.add(configName, gbc);
		configNametf = new JTextField();
		configNametf.setMaximumSize(new Dimension(150, 20));
		configNametf.setMinimumSize(new Dimension(100, 20));
		configNametf.setPreferredSize(new Dimension(100, 20));
		gbc.gridx = 0;
		gbc.gridy = 5;
		p.add(configNametf, gbc);

		JLabel configNum = new JLabel("CONFIGURATION NUMBER");
		gbc.gridx = 0;
		gbc.gridy = 6;
		p.add(configNum, gbc);
		configNumtf = new JTextField();
		configNumtf.setMaximumSize(new Dimension(150, 20));
		configNumtf.setMinimumSize(new Dimension(100, 20));
		configNumtf.setPreferredSize(new Dimension(100, 20));
		gbc.gridx = 0;
		gbc.gridy = 7;
		p.add(configNumtf, gbc);

		selectButton = new JButton("Set Selection");
		gbc.gridx = 0;
		gbc.gridy = 8;
		// gbc.weightx = 0; gbc.weighty = 1;
		p.add(selectButton, gbc);
		selectButton.addActionListener(this);

		// RIGHT SIDE PANEL //
		JPanel q = new JPanel(); // right side panel.
		q.setLayout(new GridBagLayout());
		g.add(q);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setMaximumSize(new Dimension(200, 250));
		textArea.setMinimumSize(new Dimension(200, 250));
		textArea.setPreferredSize(new Dimension(200, 250));
		q.add(textArea);
		send2ServerButton = new JButton("Send to Server");
		send2ServerButton.addActionListener(this);
		gbc.gridx = 0;
		gbc.gridy = 1;
		q.add(send2ServerButton, gbc);
		send2ServerButton.setEnabled(false);
		TAString = new StringBuffer("Chosen Parts \n\n");
		textArea.setText(TAString.toString());
	}
	
	// //////// CONNECTS TO SERVER //////////
		public void connectToServer()
		{
			try
			{
				s = new Socket("localhost", 63432);
				//r = new Socket("localhost", 63432);
				oos = new ObjectOutputStream(s.getOutputStream());
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
		}

		// //////// REQUESTS FOR GPARTSLIST FROM SERVER //////////
		public void requestGPartsList()
		{
			try
			{
				oos.writeObject("RequestPartsList");
				oos.reset();
				oos.writeObject("Kits");
				oos.reset();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// //////// GETS GPARTSLIST FROM SERVER //////////
		/*
		 * This is in runnable? public void obtainGPartsList(){ try { partList =
		 * (ArrayList<GPart>) ois.readObject(); } catch (EOFException e){
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } catch (ClassNotFoundException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } }
		 */

		// //////// CHANGES JLABELS ABOVE COMBOBOXES //////////
		public void changeJLabels()
		{
			/*
			 * This portion will essentially change the placeholder names to the
			 * actual names of the part passed to the KitManager
			 */
			for (int i = 0; i < partList.size(); i++)
			{
				jlabelList.get(i).setText("Quantity of " + partList.get(i).getTypeName());
			}
		}

		// //////// SEND KIT CONFIGURATION TO SERVER //////////
		public void sendListToServer()
		{
			chosenKit.kitName = configNametf.getText();
			chosenKit.kitNumber = Integer.parseInt(configNumtf.getText());
			kitTypeList.add(chosenKit); // adds the kit the KM was working on
			try
			{
				oos.writeObject("UpdateKitList");
				oos.reset();
				oos.writeObject(kitTypeList);
				oos.reset();
			} catch (IOException e)
			{
				e.getStackTrace();
			}
//			chosenKit.partsInKit.clear();
			partsInKit = 0;
			textArea.setText(TAString.toString());
			configNametf.setText("");
			configNumtf.setText("");
			TAString = new StringBuffer("Chosen Parts \n\n");
			chosenKit=new GKit();
//			chosenKit.kitName = "";
//			chosenKit.kitNumber = 0;
		}

	// //////// ACTION PERFORMED //////////
	public void actionPerformed(ActionEvent ae)
	{
		/*
		 * Adds the numbers selected from all 8 combo boxes. If the total is
		 * greater than 8, then the manager will not allow the user to send.
		 */

		if (ae.getSource() == selectButton)
		{

			if (partsInKit + howManyBox.getSelectedIndex() <= 8 &&partsInKit + howManyBox.getSelectedIndex() >= 0)
			{
				if(howManyBox.getSelectedIndex()!=0)
					TAString.append(partsBox.getSelectedItem() + ": " + howManyBox.getSelectedIndex() +"\n");
				textArea.setText(TAString.toString());
				for (int i = 0; i < howManyBox.getSelectedIndex(); i++)
				{ // sets a kit to work with just in the KitManager
					chosenKit.partsInKit.add(new GPart(0, 0, partList.get(
							partsBox.getSelectedIndex()).getImageAddress(), partList.get(
							partsBox.getSelectedIndex()).getTypeNumber(), partList.get(
							partsBox.getSelectedIndex()).getTypeName()));
				}
				partsInKit += howManyBox.getSelectedIndex();
				if (partsInKit >= 4)
				{
					send2ServerButton.setEnabled(true);
				}
				else
				{
					send2ServerButton.setEnabled(false);
				}
			}
			else
			{
				send2ServerButton.setEnabled(false);
			}
		}
		else if (ae.getSource() == send2ServerButton)
		{
			sendListToServer();
		}
	}

	// //////// UPDATE CHECKER //////////
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
			try
			{
				while (true)
				{
					// FACTORY COMMANDS //
					String command = ois.readObject().toString();

					if (command.equals("RequestPartsList"))
					{
						partList = (ArrayList<GPart>) ois.readObject();
						partsBox.removeAllItems();
						for (int i = 0; i < partList.size(); i++)
						{
							partsBox.addItem(partList.get(i).getTypeName());
						}
					}
					else if (command.equals("UpdatePartsList"))
					{
						partList = (ArrayList<GPart>) ois.readObject();
						partsBox.removeAllItems();
						for (int i = 0; i < partList.size(); i++)
						{
							partsBox.addItem(partList.get(i).getTypeName());
						}
					}
					else if (command.equals("Kits"))
					{
						kitTypeList = (ArrayList<GKit>) ois.readObject();
					}

					// CATCH IRRELEVANT COMMANDS //
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

}
