package NonAgent;

import java.io.Serializable;

import gui.GPart;

public class Part implements Serializable{
	 public String partType; 
	 enum myState {good, bad}; 
	 myState state; 
	 public Part(Part p){
		 this.partType=p.partType;
		 this.state=p.state;
	 }
	 public Part(){
		 
	 }
	 public Part(String string) {
		partType=string;
	}
	GPart gui;
	 
}

