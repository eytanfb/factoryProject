package gui;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GRobot extends GObject{
	

	boolean ifMoving;
	
	BufferedImage[] images;
  int currentImage;
  
  boolean flipped;
	
	/* Don't allow the default constructor */
	private GRobot()
	{
		super(0,0,"mariobag.png");		
		ifMoving=false;
		flipped = false;
	}
	
	public GRobot(int x, int y, String[] imageAddresses ){
	  super(x,y,imageAddresses[0]);
	  
	  ifMoving=false;
	  
    images = new BufferedImage[imageAddresses.length];
    
    // Load each image into the images array 
    for (int i = 0; i < images.length; i++)
    {
      try {
        images[i] = ImageIO.read(new File(imageAddresses[i]));
      } catch (IOException e) {
        System.out.println("Image (" + imageAddresses[i] + ") does not exist");
        e.printStackTrace();
      }
    }
    currentImage = 0;
    flipped = false;
  }
	
	public GRobot(int x, int y,String pictureAddress)
	{
		super (x,y,pictureAddress);
		
		ifMoving=false;
		
		images = new BufferedImage[1];
    
    // Load each image into the images array 
    try {
      images[0] = ImageIO.read(new File(pictureAddress));
    } catch (IOException e) {
      System.out.println("Image (" + pictureAddress + ") does not exist");
      e.printStackTrace();
    }
    
    currentImage = 0;
    flipped = false;
	}
	
	public void paintObject(Graphics g, int offset_X, int offset_Y)
	{
	  Graphics2D g2 = (Graphics2D)g;
    //pic.paintIcon(null,g,x,y);	  
    
	  /* Check the horizontal orientation of the image before painting */
    if (!flipped)
      g2.drawImage(images[currentImage],null,x+offset_X,y+offset_Y);
    else {
      int h = images[currentImage].getHeight();
      int w = images[currentImage].getWidth();
      
      // Paint the image flipped
      g2.drawImage(images[currentImage], x+offset_X, y+offset_Y,
          w+x+offset_X, h+y+offset_Y, w, 0, 0, h, null);	
    }
	}
}
