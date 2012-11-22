package gui;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;

public class FPM_FactoryPanel extends JPanel implements ActionListener
{

	/*
	 * This class will hold every JPanel, including the swing panel. The initial
	 * idea is to have instances of the three graphics panels and one instance
	 * of the swing panel starting from the left and flowing towards the right.
	 */
	// /////// VARIABLES /////////
	private FPM_Manager fpm; // passed in JFrame
	private ImageIcon backgroundImage = new ImageIcon("src/resources/potentialBackground.jpg");
	private Timer timer;

	// /////// MANAGERS /////////
	private GKitAssemblyManager kitAssemblyManager;
	private GLaneSystemManager laneSystemManager;
	private GantryRobotManager gantryRobotManager;

	// ////// GRAPHICS PANELS ////////
	private GKitAssemblyGraphicsPanel kitAssemblyGraphics;
	private GLaneSystemGraphicsPanel laneSystemGraphics;
	private GGantrySystem gantryGraphics;

	public FPM_FactoryPanel(FPM_Manager manager)
	{
		fpm = manager; // grabs the FPM_Manager JFrame
		setupManagers();
		setMaximumSize(new Dimension(1000, 800));
		setMinimumSize(new Dimension(1000, 800));
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);
		g.drawImage(backgroundImage.getImage(), 0, 0, 1000, 800, null);

		gantryGraphics.paintComponent(g);
		kitAssemblyGraphics.paintComponent(g); 
		laneSystemGraphics.paintComponent(g);
	}

	public void actionPerformed(ActionEvent ae)
	{
		repaint();
	}

	public void setupManagers()
	{
		timer = new Timer(50, this);

		kitAssemblyManager = new GKitAssemblyManager(0, 0);
		laneSystemManager = new GLaneSystemManager(235, 0, true);
		gantryRobotManager = new GantryRobotManager(343, 0, true);

		kitAssemblyGraphics = kitAssemblyManager.getGraphicsPanel();
		laneSystemGraphics = laneSystemManager.getGraphicsPanel();
		gantryGraphics = gantryRobotManager.getGraphicsPanel();
		/*
		 * kitManager = new GKitManager(); partsManager = new GPartManager();
		 */
		add(kitAssemblyGraphics);
		add(laneSystemGraphics);
		add(gantryGraphics);
		timer.start();
	}

}