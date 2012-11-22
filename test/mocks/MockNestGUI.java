package mocks;
import interfaces.*;
import NonAgent.Part;
import agents.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Instantiate one for each lane, set the nest

public class MockNestGUI {

	int i = 0;
	int numparts = 0;
	Timer t = new Timer(1000, null);
	public NestAgent nest;
	public LaneAgent lane;
	
	public MockNestGUI(){
		t.addActionListener(new mocknestAL());
	}
	
	public void runStuff(int numparts){
		this.numparts = numparts;
		t.start();
	}
	
	void sendNestMsg(){
		if(i < numparts)
		{
			lane.msgPartPutInNest();
			i++;
		}
	}
	
	public class mocknestAL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			sendNestMsg();
		}
		
	}
	
	
}
