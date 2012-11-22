////////// CREATED BY MICHAEL BORKE //////////
package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

public class ClientCodeSnippits 
{
	////////// CLIENT NETWORK VARIABLES //////////
	Socket s;
	Socket r;
	UpdateChecker updateChecker;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	////////// FACTORY VARIABLES //////////
	ArrayList<GKit> kitList;
	ArrayList<GPart> partList;
	GKit chosenKit;
	
	/*
	 * THIS PREPARES YOUR CLASS TO CONNECT TO THE SERVER SO INFORMATION
	 * CAN BE SENT BACK AND FORTH.
	 */
	public ClientCodeSnippits()
	{
		///// CLIENT NETWORKING SETUP /////
		try 
		{
			s = new Socket("localhost", 63432);
			r = new Socket("localhost", 63432);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(r.getInputStream());
			System.out.println("Client Ready");	
			updateChecker = new UpdateChecker(r, ois);
			new Thread(updateChecker).start();		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * WHENEVER YOU UPDATE SOMETHING AND THE REST OF THE FACTORY
	 * NEEDS TO KNOW ABOUT IT, CALL THIS:
	 * try {
	 *		oos.writeObject("COMMAND");
	 *		oos.reset();
	 *		oos.writeObject(THING TO UPDATE);
	 *		oos.reset();
	 *	} catch (IOException e) {
	 *		e.printStackTrace();
	 *	}
	 */
	
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
					// FACTORY COMMANDS //
					String command = ois.readObject().toString();
					if(command.equals("Kits"))
					{
						kitList = (ArrayList<GKit>) ois.readObject();
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
					r.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}			
		}
	}	
}
