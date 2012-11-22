//GUI by Kevin Nguyen
//Server commands and additions by Heidi Qiao and Michael Borke

package gui;

import gui.ClientCodeSnippits.UpdateChecker; //List of imports.

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GPartManager extends JFrame implements ActionListener, ListSelectionListener{
	
	////////// CLIENT NETWORK VARIABLES //////////
	Socket s;
	//Socket r;
	UpdateChecker updateChecker;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	////////// FACTORY VARIABLES //////////
	ArrayList<GKit> kitList;
	ArrayList<GPart> partList;
	GKit chosenKit;
	
	
	JList partsList;
	DefaultListModel list;
	JButton submit, delete, saveexit;
	JLabel nameL, numberL, descriptionL, imageL;
	JTextField name, number, description;
	JComboBox image;
	ArrayList<GPart> myGParts;
	JLabel picture;
	String partNameSelected;
	GPart tempPart;
	Boolean check = false;
	int partArrayIndex = 0;
	boolean initialized = false;
	
	public GPartManager(){
		
	///// CLIENT NETWORKING SETUP /////
			try 
			{
				s = new Socket("localhost", 63432);
				//r = new Socket("localhost", 63432);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
				System.out.println("Client Ready");	
				updateChecker = new UpdateChecker(s, ois);
				new Thread(updateChecker).start();		
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			this.setLayout(new GridBagLayout());
			//GPARTS ARRAYLIST - Passed to the server, read from the server, updated in the server.
			myGParts = new ArrayList<GPart>();
			
			list = new DefaultListModel();
			
			try{
				oos.writeObject("RequestPartsList"); //String sent to server to pull saved ArrayList<GPart> myGParts.
				/*GPart item = null; //Used only for local testing, not for server testing.
				FileInputStream fis = new FileInputStream("Parts_Database.sav");
				ObjectInputStream ois = new ObjectInputStream(fis);
				while ((item = (GPart)ois.readObject()) != null){
					myGParts.add(item);
				}
				ois.close();*/
			}
			catch (IOException ie){
			}
	}
	
	public void initializePanel() 
	{
		if(!initialized)
		{
			picture = new JLabel();
			submit = new JButton("Submit");
			delete = new JButton("Delete");
			saveexit = new JButton("Exit");
			nameL = new JLabel("Name: ");
			numberL = new JLabel("Number: ");
			descriptionL = new JLabel("Description: ");
			imageL = new JLabel("Image Path: ");
			name = new JTextField(10);
			number = new JTextField(10);
			description = new JTextField(20);
			image = new JComboBox();
			image.addItem("src/resources/enemy1.png"); //Image choices and image path called.
			image.addItem("src/resources/enemy2.png");
			image.addItem("src/resources/enemy3.png");
			image.addItem("src/resources/enemy4.png");
			image.addItem("src/resources/enemy5.png");
			image.addItem("src/resources/enemy6.png");
			image.addItem("src/resources/enemy7.png");
			image.addItem("src/resources/enemy8.png");
			GridBagConstraints c = new GridBagConstraints(); //Use gridbaglayout for more User interaction and easy layout
			list.clear();
			list.addElement("New Part"); //Initial list option so that user knows how to create a new part.
			for (int i = 0; i < myGParts.size(); i++)
			{
				String partName = myGParts.get(i).getTypeName();
				list.addElement(partName);
			}	
			
			partsList = new JList(list); //Creates the list and saves it.
			partsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			partsList.setSelectedIndex(-1);
			partsList.addListSelectionListener(this);
			JScrollPane partsListScrollPane = new JScrollPane(partsList);
			partsListScrollPane.setPreferredSize(new Dimension(200,200));
			partsListScrollPane.getVerticalScrollBar().setUnitIncrement(10);
			partsListScrollPane.getHorizontalScrollBar().setUnitIncrement(10);
			
			submit.addActionListener(this);
			delete.addActionListener(this);
			saveexit.addActionListener(this);
			image.addActionListener(this);
			
			//DIMENSIONS
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.LINE_START;
			this.add(partsListScrollPane,c);
			c.gridx = 2;
			c.gridy = 0;
			c.gridwidth = 1;
			this.add(picture,c);
			c.gridx = 0;
			c.gridy = 1;
			this.add(nameL,c);
			c.gridx = 1;
			c.gridy = 1;
			this.add(name,c);
			c.gridx = 0;
			c.gridy = 2;
			this.add(numberL,c);
			c.gridx = 1;
			c.gridy = 2;
			this.add(number,c);
			c.gridx = 0;
			c.gridy = 3;
			this.add(descriptionL,c);
			c.gridx = 1;
			c.gridy = 3;
			c.gridwidth = 2;
			this.add(description,c);
			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 1;
			this.add(imageL,c);
			c.gridx = 1;
			c.gridy = 4;
			this.add(image,c);
			c.gridx = 0;
			c.gridy = 5;
			c.anchor = GridBagConstraints.CENTER;
			this.add(submit,c);
			c.gridx = 1;
			c.gridy = 5;
			this.add(delete,c);
			c.gridx = 2;
			c.gridy = 5;
			this.add(saveexit,c);
			System.out.println("everything should be created");
			validate();
			initialized = true;
		}
	}
	
	// Need to refresh window
	private void refreshJFrame()
	{
		validate();
		repaint();
	}
	
	//Creates the panel and displays.
	public static void main (String[] args){
		GPartManager manager = new GPartManager();
		manager.setTitle("Bowser's Enemy Creator");
		manager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		manager.setSize(400,400);
		manager.setLocationRelativeTo(null);
		manager.setVisible(true);
		manager.setResizable(false);
	}
	
	//Actions for all the buttons and all of the events of lists to edit, create new, exit, and update images.
	public void actionPerformed (ActionEvent ae){
		if (ae.getSource() == submit)
		{
			if (partsList.getSelectedIndex() == 0) //Senses if a new part is to be created
			{
				/*
				 * This selection will add a new part to the parts list.
				 * The user must have selected the New Part in the list in order to add a new part
				 */
				if (myGParts.size() > 0) //Checks to make sure that no repeats occur.
				{
					for(int i = 0; i < myGParts.size(); i++)
					{
						System.out.println(name.getText());
						System.out.println(myGParts.get(i).getTypeName());
						if(Integer.parseInt(number.getText()) == myGParts.get(i).getTypeNumber())
						{
							System.out.println("Error: Number already exists.");
							check = false;
							break;
						}
						else if(Integer.parseInt(number.getText()) > 1000000000)
						{
							System.out.println("Error: Please choose a number with 9 digits or less.");
							check = false;
							break;
						}
						else if(name.getText().equals(myGParts.get(i).getTypeName())) //Strings match, but don't go into this loop.
						{
							System.out.println("Error: Name already exists.");
							check = false;
							break;
						}	
						else
						{
							check = true;
						}
					}
				}
				else
				{
					check = true;
				}
				if (check == true) //If passes, create a new part in tempPart, add to list.
				{	
					tempPart = new GPart(0, 0, (String)image.getSelectedItem(), Integer.parseInt(number.getText()), name.getText(), description.getText());
					myGParts.add(tempPart); //arraylist
			    	list.addElement(tempPart.getTypeName());
			    	partsList.setModel(list); //Update list in GUI
			    	check = false;
			    	tempPart = null;
			    	System.out.println("Part saved.");
					try {
						oos.writeObject("UpdatePartsList"); //Update list in server.
						oos.reset();
						oos.writeObject(myGParts);
						oos.reset();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else if (partsList.getSelectedIndex() > 0) //Checks to make sure that editing an old part.
			{
				/*
				 * This is to help edit GParts that were already created
				 */
				list.removeAllElements(); //Repopulates the list and creates it again with edits.
				tempPart = new GPart(0, 0, (String)image.getSelectedItem(), Integer.parseInt(number.getText()), name.getText(), description.getText());
				myGParts.set(partArrayIndex, tempPart);
				tempPart = null;
				System.out.println("Part saved.");
				list.addElement("New Part");
				for (int i = 0; i < myGParts.size(); i++)
				{
					list.addElement(myGParts.get(i).getTypeName());
				}
				partsList.setModel(list);
				try {
					oos.writeObject("UpdatePartsList"); //Sends updated part to server.
					oos.reset();
					oos.writeObject(myGParts);
					oos.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if (ae.getSource() == image) //Changes the image based on the selection of image path.
		{
			String imagePath = (String)image.getSelectedItem();
			ImageIcon icon = new ImageIcon(imagePath);
			icon.getImage().flush();
			picture.setIcon(icon);
		}
		else if (ae.getSource() == delete) //Deletes the part, refreshes the list, and updates to server.
		{
			for (int i = 0; i < myGParts.size(); i++)
			{
				if(myGParts.get(i).getTypeName().equals(partNameSelected))
				{
					myGParts.remove(i);
					list.remove(partsList.getSelectedIndex());
					partsList.setModel(list);
					break;
				}
			}
			try {
				oos.writeObject("UpdatePartsList");
				oos.reset();
				oos.writeObject(myGParts);
				oos.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (ae.getSource() == saveexit)
			{
//				try{ //For local testing only.
//					ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream("Parts_Database.sav")); 
//					for(int i = 0; i < myGParts.size(); i++){
//						oos1.writeObject(myGParts.get(i));
//					} 
//					oos1.close(); 
//				} 
//				catch (IOException ie) {}
				try {
					oos.writeObject("close");
					oos.reset();
					s.close();
					//r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
	}

	public void valueChanged(ListSelectionEvent e) { //Senses any changes in the list selection.
		if (e.getValueIsAdjusting() == false)
		{
			if (partsList.getSelectedIndex() == 0) //Disables the delete button in the first option to create a new part and sets all fields to empty or default.
			{
				delete.setEnabled(false);
				name.setText("");
				number.setText("");
				description.setText("");
				image.setSelectedIndex(0);
			}
			else if (partsList.getSelectedIndex() > 0)
			{
				delete.setEnabled(true);
				partNameSelected = partsList.getSelectedValue().toString();
				System.out.println("Part Name: " + partNameSelected);
				for (int i = 0; i < myGParts.size(); i++)
				{
					if(myGParts.get(i).getTypeName().equals(partNameSelected))
					{
						tempPart = myGParts.get(i);
						name.setText(tempPart.getTypeName());
						number.setText(Integer.toString(tempPart.getTypeNumber()));
						description.setText(tempPart.getTypeDescription());
						image.setSelectedItem(myGParts.get(i).getImageAddress());
						partArrayIndex = i;
						break;
					}
				}
			}
							
		}
		
	}
	
	/*
	 * THIS CLASS IS THE THREAD THAT SITS AND WAITS FOR UPDATES FROM THE SERVER.
	 */
	public class UpdateChecker implements Runnable{
		private Socket mySocket;
		private ObjectInputStream ois;

		public UpdateChecker(Socket s, ObjectInputStream obis){
		    mySocket = s;
		    ois = obis;
		}
		
		public void run() {
			try{
				while(true)
				{
					System.out.println("I am here");
					// FACTORY COMMANDS //
					String command = ois.readObject().toString();
					if(command.equals("RequestPartsList"))
					{	
						myGParts = (ArrayList<GPart>) ois.readObject();
						for(int i =0; i<myGParts.size();i++)
							System.out.println("manager: initial"+myGParts.get(i).typeName);
						System.out.println("update mgparts on gpartmanager");
						initializePanel();
						list.removeAllElements(); 
						list.addElement("New Part"); //Initial list option so that user knows how to create a new part.
						for (int i = 0; i < myGParts.size(); i++)
						{
							list.addElement(myGParts.get(i).getTypeName());
						}
						partsList.setModel(list);
					} 
					else if(command.equals("UpdatePartsList"))
					{	
						myGParts = (ArrayList<GPart>) ois.readObject();
						list.removeAllElements(); 
						list.addElement("New Part"); //Initial list option so that user knows how to create a new part.
						for (int i = 0; i < myGParts.size(); i++)
						{
							list.addElement(myGParts.get(i).getTypeName());
						}
						partsList.setModel(list);
						//refreshJFrame();
					}
					// CATCH IRRELEVANT COMMANDS //
					else {
						System.out.println(command);
					}
				}
			} 
			catch(EOFException e) {
			} 
			catch(IOException e) {
				e.printStackTrace();
			} 
			catch(Exception e) {
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
	
}