package CPU;

import java.awt.Point;

import javafx.scene.shape.Cylinder;

public abstract class Zombie
{
  protected final Point spawnPoint; //(x,y) coordinate of spawn Point
  protected Cylinder model;

  public Zombie(int x, int y, Cylinder m)
  {
    spawnPoint = new Point(x,y); 
    model = m;
  }
  
  abstract public void determineNextMove();
  
}
