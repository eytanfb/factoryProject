package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FPM_Manager extends JFrame implements ActionListener{
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
	String chosenKitString;
	
	FPM_FactoryPanel fp;
	JPanel swingPanel;
	JButton produceButton;
	JTextField amountInput;
	JComboBox kitDropDownList;
	
	
	public FPM_Manager(){
		setupSockets(); //sets up sockets with server
		setLayout(new BoxLayout(this.getContentPane(),BoxLayout.X_AXIS));
		//kitList = new ArrayList<GKit>();
		fp = new FPM_FactoryPanel(this);	
		add(fp);
		try{
			oos.writeObject("FPM_Kits"); 
		}
		catch (IOException ie){
		}
		//setupSwingPanel();
	}
	
	public static void main(String[] args){
		FPM_Manager fpm = new FPM_Manager();
		fpm.setSize(1200,800);
		fpm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fpm.setVisible(true);
	}
	
	private void setupSwingPanel() {
		System.out.println("setupSwingPanel get Called");
		swingPanel = new JPanel();
		swingPanel.setLayout(new BoxLayout(swingPanel,BoxLayout.Y_AXIS));
		kitDropDownList = new JComboBox();
		for (int i=0; i<kitList.size(); i++) {
			kitDropDownList.addItem(kitList.get(i).kitName);
		}
		amountInput = new JTextField();
		produceButton = new JButton("Produce");
		produceButton.addActionListener(this);
		kitDropDownList.addActionListener(this);
		
		swingPanel.add(kitDropDownList);
		swingPanel.add(amountInput);
		swingPanel.add(produceButton);
		swingPanel.setMaximumSize(new Dimension(200,100));
		swingPanel.setMinimumSize(new Dimension(200,100));
		add(swingPanel);
		validate();
		repaint();
	}
	
	public void updateList()
	{
		kitDropDownList.removeAllItems();
		for (int i=0; i<kitList.size(); i++) {
			kitDropDownList.addItem(kitList.get(i).kitName);
		}
		
		swingPanel.validate();
		swingPanel.repaint();
		System.out.println("updateList finished");
	}

	
	public FPM_FactoryPanel returnFactoryPanel(){
		return fp;
	}
	
	public ObjectOutputStream getOOS(){
		return oos;
	}
	
	public ObjectInputStream getOIS(){
		return ois;
	}
	
	public void setupSockets(){
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
	}
	
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
					// FACTORY COMMANDS //
					String command = ois.readObject().toString();
					if(command.equals("FPM_Kits"))
					{
						kitList = (ArrayList<GKit>) ois.readObject();
						setupSwingPanel();
					} 
					else if(command.equals("UpdateKitList"))
					{
						kitList = (ArrayList<GKit>) ois.readObject();
						updateList();
					} 
					else if(command.equals("Parts"))
					{
						partList = (ArrayList<GPart>) ois.readObject();
					} 
					else if(command.equals("ChosenKit")) {
						chosenKit = (GKit) ois.readObject();
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

	public void actionPerformed(ActionEvent ae)
	{
		int temp=0;
		if(ae.getSource()==produceButton)
		{
			boolean can_go=false;
			try
			{
				temp=Integer.parseInt(amountInput.getText());
				can_go=true;
			}
			catch(NumberFormatException e)
			{
				amountInput.setText("Invalid Input");
				can_go=false;
			}
			

			if(can_go)
			{
				chosenKit.kitNumber = temp;
				try
				{
					oos.writeObject(new String("Kit_Chosen"));
					oos.reset();
					oos.writeObject(chosenKit);
					oos.reset();

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if(ae.getSource()==kitDropDownList)
		{
			if(kitDropDownList.getSelectedIndex()!=-1)
				chosenKit=kitList.get(kitDropDownList.getSelectedIndex());
			else
			{
				System.out.println("The Combobox is rough");
			}
		}
	}	
}
