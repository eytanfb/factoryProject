//By Sean Sharma
package interfaces;

public interface IVisionController{
public abstract void doShoot(int nestNumber);
public abstract void doShootKit();
public abstract void animDone();


public abstract void setVision(Vision vision);
public abstract void setServer(Server server);
}